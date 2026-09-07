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
        if (!Files.exists(FILE_PATH)) {
            return map;
        }

        try {
            for (String line : Files.readAllLines(FILE_PATH)) {
                if (line.trim().isEmpty()) {
                    continue;
                }
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

    // helper function to create a Queue waitlist 
     private static Queue<Reservations> buildWaitlistQueue(Map<String, Reservations> map, String bookId) {
        Queue<Reservations> memberQueue = new ArrayDeque<>();
        Queue<Reservations> nonMemberQueue = new ArrayDeque<>();
 
        for (Reservations res : map.values()) {
            if (res.getBookId().toString().equals(bookId) && res.getStatus().equals("WAITING")) {
                if (CustomerManager.isCustomerMember(res.getCustomerId().toString())) {
                    memberQueue.add(res);
                } else {
                    nonMemberQueue.add(res);
                }
            }
        }
 
        Queue<Reservations> waitlist = new ArrayDeque<>(memberQueue);
        waitlist.addAll(nonMemberQueue);
        return waitlist;
    }
    
    // reservation functions
    public static void requestBook(String customerId, String bookId) {
        Map<String, Reservations> map = loadReservationsToMap();

        // prevent same customer borrowing same book
        if (!Validation.isValidRequest(customerId, bookId, map)) {
            return; // execution stops if validation fails
        }

        //  check if ANYONE currently has this book borrowed
        boolean isCurrentlyBorrowed = false;
        for (Reservations res : map.values()) {
            if (res.getBookId().toString().equals(bookId) && res.getStatus().equalsIgnoreCase(Reservations.STATUS_BORROWED)) {
                isCurrentlyBorrowed = true;
                break;
            }
        }
 
        String initialStatus;
        if (isCurrentlyBorrowed != false) {
            initialStatus = Reservations.STATUS_WAITING;
        } else {
            initialStatus = Reservations.STATUS_BORROWED;
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
 
        // find the person who currently have the book and mark the borrow status as COMPLETED and enter a buffer period
        for (Reservations res : map.values()) {
            if (res.getBookId().toString().equals(bookId) && res.getStatus().equalsIgnoreCase(Reservations.STATUS_BORROWED)) {
 
                res.setStatus(Reservations.STATUS_COMPLETED);
                res.setReturnDate(LocalDate.now());
                System.out.println("Success: Book successfully returned.");
                System.out.println("Notice: Book is in the 2-day processing buffer before queue promotion.");
 
                // overdue detection
                long daysBorrowed = ChronoUnit.DAYS.between(res.getBorrowDate(), LocalDate.now());
                int MAX_DAYS = 14;
 
                if (daysBorrowed > MAX_DAYS) {
                    int daysOverdue = (int) (daysBorrowed - MAX_DAYS);
                    // issue fine
                    OverdueManager.issueFine(res.getCustomerId().toString(), res.getReserveId().toString(), daysOverdue);
                } else {
                    System.out.println("Book returned on time.");
                }
 
                bookReturned = true;
                break;
            }
        }
 
        if (bookReturned) {
        saveMapToFile(map);
    } else {
        System.out.println("Error: No active 'BORROWED' record found for this Book ID.");
    }
    }
    
    
    // passing the book to the next person in wait list if book is retunred and buffer days are over
    public static void processWaitlistQueue() {
    Map<String, Reservations> map = loadReservationsToMap();
    boolean changesMade = false;
    
    // Get a list of all book IDs currently in the system
    Set<String> allBookIds = new HashSet<>();
    for (Reservations r : map.values()) allBookIds.add(r.getBookId().toString());
 
    System.out.println("\n--- RUNNING DAILY WAITLIST PROCESSOR ---");
 
    for (String bookId : allBookIds) {
        boolean isBorrowed = false;
        LocalDate latestReturnDate = LocalDate.MIN;
        boolean hasWaitlist = false;
 
        // Scan the history of this specific book
        for (Reservations res : map.values()) {
            if (!res.getBookId().toString().equals(bookId)) continue;
            if (res.getStatus().equalsIgnoreCase(Reservations.STATUS_BORROWED)) isBorrowed = true;
            if (res.getStatus().equalsIgnoreCase(Reservations.STATUS_WAITING)) hasWaitlist = true;
            
            // Find the most recent date it was returned or voided
            if ((res.getStatus().equals("COMPLETED") || res.getStatus().equals("VOIDED")) 
                 && res.getReturnDate() != null) {
                if (res.getReturnDate().isAfter(latestReturnDate)) {
                    latestReturnDate = res.getReturnDate();
                }
            }
        }
 
        // Only process if NO ONE has it, AND there are people waiting
        if (!isBorrowed && hasWaitlist) {
            long daysSinceReturned = ChronoUnit.DAYS.between(latestReturnDate, LocalDate.now());
 
            // 2 day buffer check
            if (daysSinceReturned >= 2 || latestReturnDate.equals(LocalDate.MIN)) {
 
                // Buffer is clear -> pull the next person in line off the waitlist queue.
                // Members are queued ahead of non-members; poll() gives us the front.
                Queue<Reservations> waitlist = buildWaitlistQueue(map, bookId);
                Reservations winner = waitlist.poll();
 
                if (winner != null) {
                    winner.setStatus("BORROWED");
                    winner.setReturnDate(null); // Reset return date just in case
                    changesMade = true;
 
                    String bookTitle = BookManager.getBookTitle(bookId);
                    System.out.printf("Buffer period for '%s' ended!\nAutomatically assigned to Customer ID: %s%n", bookTitle, winner.getCustomerId());
                }
            }
        }
    }
 
    if (changesMade) saveMapToFile(map);
    else System.out.println("No waitlist promotions needed at this time.");
    }
 
    // look up the borrow history of a customer and display which books thay have borrowed
    // and when did they borrowed those book
    public static void viewCustomerHistory(String customerId) {
        if (!CustomerManager.customerExists(customerId)) {
            System.out.println("Error: Customer ID does not exist in the system.");
            return;
        }
 
        Map<String, Reservations> map = loadReservationsToMap();
        boolean foundHistory = false;
 
        System.out.printf("\n=================================================");
        System.out.printf("   BORROWING HISTORY FOR CUSTOMER: %s", customerId);
        System.out.printf("=================================================\n");
 
        for (Reservations res : map.values()) {
 
            if (res.getCustomerId().toString().equals(customerId)) {
 
                String bookTitle = BookManager.getBookTitle(res.getBookId().toString());
 
                System.out.printf("Date: %s | Status: %-10s | Book: %s%n", res.getBorrowDate(), res.getStatus(), bookTitle);
                foundHistory = true;
            }
        }
 
        if (!foundHistory) {
            System.out.println("This customer has not borrowed any books yet.");
        }
        System.out.printf("\n=================================================\n");
    }
 
    // show the actual wait list queue of a book
    public static void viewWaitlistQueue(String bookId) {
        if (!BookManager.bookExists(bookId)) {
            System.out.println("Error: Book ID does not exist in the system.");
            return;
        }
 
        Map<String, Reservations> map = loadReservationsToMap();
        Queue<Reservations> waitlist = buildWaitlistQueue(map, bookId);
        String bookTitle = BookManager.getBookTitle(bookId);
 
        System.out.println("\n=================================================");
        System.out.println("   WAITLIST QUEUE FOR: " + bookTitle);
        System.out.println("=================================================");
 
        if (waitlist.isEmpty()) {
            System.out.println("No one is currently waiting for this book.");
        } else {
            int position = 1;
            for (Reservations res : waitlist) {
                String customerName = CustomerManager.getCustomerName(res.getCustomerId().toString());
                boolean isMember = CustomerManager.isCustomerMember(res.getCustomerId().toString());
                String tag = isMember ? "[MEMBER - priority]" : "[non-member]";
                System.out.printf("%d. %-10s %s | Requested: %s%n", position, customerName, tag, res.getBorrowDate());
                position++;
            }
            System.out.println("-------------------------------------------------");
            System.out.println("Total waiting: " + waitlist.size());
        }
        System.out.println("=================================================\n");
    }
    
    // select a book id to look it reservation history up, will how how many people borrowed 
    // and how many are currently waiting to borrow it
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
        System.out.printf("   RESERVATION HISTORY FOR: %s", bookTitle);
        System.out.printf("\n=================================================\n");
 
       for (Reservations res : map.values()) {
 
            if (res.getBookId().toString().equals(bookId)) {
 
                totalRequests++;
                if (res.getStatus().equalsIgnoreCase(Reservations.STATUS_WAITING)) {
                    currentlyWaiting++;
                }
 
                String customerName = CustomerManager.getCustomerName(res.getCustomerId().toString());
                System.out.printf("Date: %s | Status: %-10s | Customer: %s%n", res.getBorrowDate(), res.getStatus(), customerName);
            }
        }
 
        if (totalRequests == 0) {
            System.out.println("This book has never been reserved.");
        } else {
            System.out.println("-------------------------------------------------");
            System.out.printf("Total lifetime requests: %d\n" , totalRequests);
            System.out.printf("Customers currently in waitlist: %d\n", currentlyWaiting);
        }
        System.out.println("=================================================");
    }
 
    // show all reservation ids
    public static void viewAllReservations() {
        Map<String, Reservations> map = loadReservationsToMap();
 
        System.out.println("\n=================================================");
        System.out.println("   ALL RESERVATIONS (ID REFERENCE)");
        System.out.println("=================================================");
 
        if (map.isEmpty()) {
            System.out.println("No reservations found in the system.");
        } else {
            for (Reservations res : map.values()) {
                String bookTitle = BookManager.getBookTitle(res.getBookId().toString());
                String customerName = CustomerManager.getCustomerName(res.getCustomerId().toString());
                System.out.printf("Reservation ID: %s | Status: %-10s | Book: %-20s | Customer: %-20s | Date: %s%n",res.getReserveId(), res.getStatus(), bookTitle, customerName, res.getBorrowDate());
            }
        }
        System.out.println("=================================================\n");
    }
    
    // a function to calculate and display the statistic of how often the book is borrow
    // and how often the book is avaiable vs borrowed
    public static void bookAvailabilityStats(String bookId) {
        if (!BookManager.bookExists(bookId)) {
            System.out.println("Error: Book ID does not exist.");
            return;
        }
 
        Map<String, Reservations> map = loadReservationsToMap();
 
        int timesBorrowed = 0;
        long totalDaysBorrowed = 0;
        LocalDate earliestActivity = LocalDate.now(); // baseline from when the tally start
 
        for (Reservations res : map.values()) {
            if (res.getBookId().toString().equals(bookId)) {
 
                // find the oldest record to establish starting baseline
                if (res.getBorrowDate().isBefore(earliestActivity)) {
                    earliestActivity = res.getBorrowDate();
                }
 
                // tally completed returns
                if (res.getStatus().equalsIgnoreCase(Reservations.STATUS_COMPLETED)) {
                    timesBorrowed++;
                    LocalDate end = (res.getReturnDate() != null) ? res.getReturnDate() : res.getBorrowDate();
                    totalDaysBorrowed += ChronoUnit.DAYS.between(res.getBorrowDate(), end);
                }
 
                // tally currently borrowed book
                if (res.getStatus().equalsIgnoreCase(Reservations.STATUS_BORROWED)) {
                    timesBorrowed++;
                    totalDaysBorrowed += ChronoUnit.DAYS.between(res.getBorrowDate(), LocalDate.now());
                }
            }
        }
 
        long totalDaysSinceFirstRequest = ChronoUnit.DAYS.between(earliestActivity, LocalDate.now());
 
        // prevent divide-by-zero if it was added today
        if (totalDaysSinceFirstRequest == 0) {
            totalDaysSinceFirstRequest = 1;
        }
 
        long daysFree = totalDaysSinceFirstRequest - totalDaysBorrowed;
 
        // prevent negative free days from same-day borrow/returns overlapping
        if (daysFree < 0) {
            daysFree = 0;
        }
 
        double percentBorrowed = ((double) totalDaysBorrowed / totalDaysSinceFirstRequest) * 100;
        double percentFree = 100.0 - percentBorrowed;
 
        // display
        String title = BookManager.getBookTitle(bookId);
        System.out.printf("\n=================================================\n");
        System.out.println("          STATISTICS FOR: " + title);
        System.out.println("=================================================");
        System.out.println("Total Times Borrowed : " + timesBorrowed);
        System.out.println("Days in Circulation  : " + totalDaysBorrowed + " day(s)");
        System.out.println("Days Idle on Shelf   : " + daysFree + " day(s)");
        System.out.printf("Circulation Rate     : %.1f%% Borrowed | %.1f%% Free%n", percentBorrowed, percentFree);
        System.out.println("=================================================\n");
    }
 
    // reservation manipulation methods (cancel a reserve or reinstall a resever, ect)
    public static void cancelWaitlistRequest(String customerId, String bookId) {
        Map<String, Reservations> map = loadReservationsToMap();
        boolean found = false;
 
        for (Reservations res : map.values()) {
            // find the exact active waitlist record
            if (res.getCustomerId().toString().equals(customerId) && res.getBookId().toString().equals(bookId) && res.getStatus().equalsIgnoreCase(Reservations.STATUS_WAITING)) {
 
                res.setStatus(Reservations.STATUS_CANCELLED);
                res.setReturnDate(LocalDate.now()); // record the time it was cancelled
 
                found = true;
                saveMapToFile(map);
 
                String customerName = CustomerManager.getCustomerName(customerId);
                String bookTitle = BookManager.getBookTitle(bookId);
                System.out.printf("Success: %s has been removed from the waitlist for '%s'.%n", customerName, bookTitle);
                break;
            }
        }
 
        if (!found) {
            System.out.println("Error: Could not find an active waitlist request for this customer and book.");
        }
    }
 
    // select a book that need to be taken down for some reasion and dequeue those who reserve it
    public static void clearBookQueue(String bookId, String reason) {
        if (!BookManager.bookExists(bookId)) {
            System.out.println("Error: Book ID not found.");
            return;
        }
 
        Map<String, Reservations> map = loadReservationsToMap();
        String bookTitle = BookManager.getBookTitle(bookId);
        int cancelCount = 0;
 
        System.out.printf("\n=================================================\n");
        System.out.printf(" \n[%s] has been taken down for this period of time\n", bookTitle);
        System.out.printf(" Reason: %s", reason);
        System.out.printf("\n=================================================\n");
        System.out.printf("Cancelling queue list customers...\n");
 
        for (Reservations res : map.values()) {
 
            // Look for customer who current have WAITING status for this specific book
            if (res.getBookId().toString().equals(bookId) && res.getStatus().equals("WAITING")) {
 
                res.setStatus(Reservations.STATUS_CANCELLED);
                res.setReturnDate(LocalDate.now());
 
                String customerName = CustomerManager.getCustomerName(res.getCustomerId().toString());
                System.out.println("- " + customerName + " (Cancelled)");
 
                cancelCount++;
            }
        }
 
        // Save and summarize the result of ending a waitlist
        if (cancelCount > 0) {
            saveMapToFile(map);
            System.out.printf("\n-------------------------------------------------\n");
            System.out.printf("Successfully cancelled %d reservation request(s).", cancelCount);
        } else {
            System.out.println("No customers were on the waitlist for this book.");
        }
        System.out.printf("\n=================================================\n");
    }
 
    public static void voidReservation(String reservationId, String reason) {
        Map<String, Reservations> map = loadReservationsToMap();
        Reservations res = map.get(reservationId);
 
        if (res == null) {
            System.out.println("Error: Reservation ID not found.");
            return;
        }
 
        String oldStatus = res.getStatus();
 
        if (oldStatus.equals(Reservations.STATUS_VOIDED) || oldStatus.equals(Reservations.STATUS_COMPLETED)) {
            System.out.println("Error: Only active (BORROWED or WAITING) reservations can be voided.");
            return;
        }
 
        // mark the reservation as voided and record when it was voided
        res.setStatus(Reservations.STATUS_VOIDED);
        res.setReturnDate(LocalDate.now());
        System.out.printf("\nReservation with the id of %s has successfully been voided\n",reservationId);
        System.out.println("Reason logged: " + reason);
 
        // trigger the waitlist check to pass on the book to the next person
        if (oldStatus.equals("BORROWED")) {
            triggerWaitlistPromotion(map, res.getBookId().toString());
        }
 
        saveMapToFile(map);
    }
 
    // helper function that promotes the next customer in line (member-priority,
    // same rule as processWaitlistQueue) when retracting a reservation
    private static void triggerWaitlistPromotion(Map<String, Reservations> map, String bookId) {
        Queue<Reservations> waitlist = buildWaitlistQueue(map, bookId);
        Reservations winner = waitlist.poll();
 
        if (winner != null) {
            winner.setStatus(Reservations.STATUS_BORROWED);
            winner.setReturnDate(null);
            System.out.printf("\nWaitlist has been bumped up!\nNext in line is now BORROWED.");
        }
    }
    
    public static void revertAccidentalReturn(String reservationId) {
    Map<String, Reservations> map = loadReservationsToMap();
    Reservations res = map.get(reservationId);
 
    if (res == null || !res.getStatus().equalsIgnoreCase(Reservations.STATUS_COMPLETED)) {
        System.out.println("Error: Invalid ID or reservation is not COMPLETED.");
        return;
    }
 
    String bookId = res.getBookId().toString();
 
    // Check if the 2-day buffer already expired and the system gave it away
    for (Reservations checkRes : map.values()) {
        if (checkRes.getBookId().toString().equals(bookId) && checkRes.getStatus().equalsIgnoreCase(Reservations.STATUS_BORROWED)) {
            System.out.println("The 2-day buffer expired and this book was already passed to the next person.");
            return; 
        }
    }
 
    // Still within the buffer! Revert safely.
    res.setStatus(Reservations.STATUS_BORROWED);
    res.setReturnDate(null); // Erases the accidental return date
    saveMapToFile(map);
    
    System.out.printf("The return request has been successfully retracted.\nWaitlist was not affected because of the 2-day buffer safety net.\n");
}
}
