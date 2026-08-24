
package Controller;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import Model.Reservations;
import Utils.Validation;


public class ReservationManager {
    private static final Path FILE_PATH = Paths.get("reservations.txt");
    
    // Map -> file and vice versa
    private static Map<String, Reservations> loadReservationsToMap() {
        Map<String, Reservations> map = new LinkedHashMap<>();
        if (!Files.exists(FILE_PATH)) return map;

        try {
            for (String line : Files.readAllLines(FILE_PATH)) {
                if (line.trim().isEmpty()) continue;
                Reservations res = Reservations.fromTxtLine(line);
                map.put(res.getReserveId().toString(), res);
            }
        } catch (IOException e) {
            System.err.println("Error reading reservations: " + e.getMessage());
        }
        return map;
    }

    private static void saveMapToFile(Map<String, Reservations> map) {
        List<String> lines = new ArrayList<>();
        for (Reservations res : map.values()) {
            lines.add(res.toTxtLine());
        }
        try {
            Files.write(FILE_PATH, lines);
        } catch (IOException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }
    
    // reservation functions
    public static void requestBook(String customerId, String bookId) {
        Map<String, Reservations> map = loadReservationsToMap();


        // prevent same customer borrowing same book
        if (!Validation.isValidRequest(customerId, bookId, map)) {
            return; // Execution stops if validation fails
        }
        
        //  Check if ANYONE currently has this book borrowed
        boolean isCurrentlyBorrowed = false;
        for (Reservations res : map.values()) {
            if (res.getBookId().toString().equals(bookId) && res.getStatus().equals("BORROWED")) {
                isCurrentlyBorrowed = true;
                break;
            }
        }
        
        String initialStatus;
        if(isCurrentlyBorrowed!= false){
            initialStatus = "waiting";
        } else {
            initialStatus = "borrowed";
        }
        Reservations newRes = new Reservations(UUID.fromString(customerId), UUID.fromString(bookId), initialStatus);
        map.put(newRes.getReserveId().toString(), newRes);
        saveMapToFile(map);
        
        if (isCurrentlyBorrowed) {
            System.out.printf("Book has already been borrowed!\nMoved to waitlist\n");
        } else {
            System.out.println("Success! Book successfully BORROWED.");
        }
    }
    
    public static void returnBook(String bookId) {
    Map<String, Reservations> map = loadReservationsToMap();
    boolean bookReturned = false;

    // Find the person who currently have the book and mark the borrow status as COMPLETED
    for (Reservations res : map.values()) {
        if (res.getBookId().toString().equals(bookId) && res.getStatus().equals("BORROWED")) {
            res.setStatus("COMPLETED");
            bookReturned = true;
            System.out.println("Book successfully returned!");
            break;
        }
    }

    if (!bookReturned) {
        System.out.println("Error: This book is not currently borrowed by anyone.");
        return;
    }

    // Scan the Waitlist for priority
    Reservations priorityMember = null;
    Reservations firstNonMember = null;

    for (Reservations res : map.values()) {
        if (res.getBookId().toString().equals(bookId) && res.getStatus().equals("WAITING")) {
            
            // Ask CustomerManager if this person has a membership
            boolean isMember = CustomerManager.isCustomerMember(res.getCustomerId().toString());

            if (isMember) {
                priorityMember = res; // highest priority found
                break; 
            } else if (firstNonMember == null) {
                firstNonMember = res; // Save the oldest non-member as a fallback
            }
        }
    }

    // Assign the book to the winner
    Reservations winner = (priorityMember != null) ? priorityMember : firstNonMember;

    if (winner != null) {
        winner.setStatus("BORROWED");
        
        System.out.println("🔔 Waitlist Triggered!");
        if (priorityMember != null) {
            System.out.println("🌟 VIP PRIORITY! Book bypassed standard waitlist and passed to Member ID: " + winner.getCustomerId());
        } else {
            System.out.println("Book passed to standard customer: " + winner.getCustomerId());
        }
    }

    saveMapToFile(map);
}
}
