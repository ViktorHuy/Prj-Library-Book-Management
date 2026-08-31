
package Controller;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import Model.Reservations;
import Utils.Validation;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


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
            res.setReturnDate(LocalDate.now());
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
    
    public static void viewCustomerHistory(String customerId) {
    if (!CustomerManager.customerExists(customerId)) {
        System.out.println("Error: Customer ID does not exist in the system.");
        return;
    }

    Map<String, Reservations> map = loadReservationsToMap();
    boolean foundHistory = false;

    System.out.printf("\n=================================================");
    System.out.println("   BORROWING HISTORY FOR CUSTOMER: " + customerId);
    System.out.printf("=================================================\n");

    for (Reservations res : map.values()) {
        
        if (res.getCustomerId().toString().equals(customerId)) {
            
            String bookTitle = BookManager.getBookTitle(res.getBookId().toString());

            System.out.printf("Date: %s | Status: %-10s | Book: %s%n", 
                    res.getBorrowDate(), res.getStatus(), bookTitle);       
            foundHistory = true;
        }
    }

    if (!foundHistory) {
        System.out.println("This customer has not borrowed any books yet.");
    }
    System.out.printf("\n=================================================\n");
}
    
    public static void viewBookRecord(String bookId) {
    if (!BookManager.bookExists(bookId)) {
        System.out.println("Error: Book ID does not exist in the system.");
        return;
    }

    Map<String, Reservations> map = loadReservationsToMap();
    int totalRequests = 0;
    int currentlyWaiting = 0;

    String bookTitle = BookManager.getBookTitle(bookId);

    System.out.printf("\n=================================================\n");
    System.out.println("   RESERVATION HISTORY FOR: " + bookTitle);
    System.out.println("=================================================");

    for (Reservations res : map.values()) {
        
        if (res.getBookId().toString().equals(bookId)) {
            
            totalRequests++;
            if (res.getStatus().equals("WAITING")) {
                currentlyWaiting++;
            }

            String customerName = CustomerManager.getCustomerName(res.getCustomerId().toString());
            System.out.printf("Date: %s | Status: %-10s | Customer: %s%n", 
                    res.getBorrowDate(), res.getStatus(), customerName);
        }
    }


    if (totalRequests == 0) {
        System.out.println("This book has never been reserved.");
    } else {
        System.out.println("-------------------------------------------------");
        System.out.println("Total lifetime requests: " + totalRequests);
        System.out.println("Customers currently in waitlist: " + currentlyWaiting);
    }
    System.out.println("=================================================");
}
    
    public static void bookAvailabilityStats(String bookId) {
    if (!BookManager.bookExists(bookId)) {
        System.out.println("Error: Book ID does not exist.");
        return;
    }

    Map<String, Reservations> map = loadReservationsToMap();
    
    int timesBorrowed = 0;
    long totalDaysBorrowed = 0;
    LocalDate earliestActivity = LocalDate.now(); // Baseline from when the tally start

    for (Reservations res : map.values()) {
        if (res.getBookId().toString().equals(bookId)) {
            
            // Find the oldest record to establish starting baseline
            if (res.getBorrowDate().isBefore(earliestActivity)) {
                earliestActivity = res.getBorrowDate();
            }

            // Tally Completed returns
            if (res.getStatus().equals("COMPLETED")) {
                timesBorrowed++;
                LocalDate end = (res.getReturnDate() != null) ? res.getReturnDate() : res.getBorrowDate();
                totalDaysBorrowed += ChronoUnit.DAYS.between(res.getBorrowDate(), end);
            }
            
            // Tally Currently Borrowed
            if (res.getStatus().equals("BORROWED")) {
                timesBorrowed++;
                totalDaysBorrowed += ChronoUnit.DAYS.between(res.getBorrowDate(), LocalDate.now());
            }
        }
    }

    // Math calculations
    long totalDaysSinceFirstRequest = ChronoUnit.DAYS.between(earliestActivity, LocalDate.now());
    
    // Prevent divide-by-zero if it was added today
    if (totalDaysSinceFirstRequest == 0) totalDaysSinceFirstRequest = 1; 

    long daysFree = totalDaysSinceFirstRequest - totalDaysBorrowed;
    
    // Prevent negative free days from same-day borrow/returns overlapping
    if (daysFree < 0) daysFree = 0; 

    double percentBorrowed = ((double) totalDaysBorrowed / totalDaysSinceFirstRequest) * 100;
    double percentFree = 100.0 - percentBorrowed;

    // Display
    String title = BookManager.getBookTitle(bookId);
    System.out.println("\n=================================================");
    System.out.println("          STATISTICS FOR: " + title);
    System.out.println("=================================================");
    System.out.println("Total Times Borrowed : " + timesBorrowed);
    System.out.println("Days in Circulation  : " + totalDaysBorrowed + " days");
    System.out.println("Days Idle on Shelf   : " + daysFree + " days");
    System.out.printf("Circulation Rate     : %.1f%% Borrowed | %.1f%% Free%n", percentBorrowed, percentFree);
    System.out.println("=================================================\n");
}
}
