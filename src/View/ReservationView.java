
package View;
import Utils.ConsoleIO;
import Controller.ReservationManager;
import Controller.BookManager;
import Controller.CustomerManager;


public class ReservationView {
    public static void showMenu() {
        boolean back = false;
        while (!back) {
            printMenu();
            int choice = ConsoleIO.readMenuChoice("Select an option: ", 0, 12);
 
            switch (choice) {
                case 1:
                    requestBook();
                    break;
                case 2:
                    returnBook();
                    break;
                case 3:
                    ReservationManager.processWaitlistQueue();
                    break;
                case 4:
                    viewCustomerHistory();
                    break;
                case 5:
                    viewBookRecord();
                    break;
                case 6:
                    ReservationManager.viewAllReservations();
                    break;
                case 7:
                    viewWaitListQueue();
                    break;
                case 8:
                    bookAvailabilityStats();
                    break;
                case 9:
                    cancelWaitlistRequest();
                    break;
                case 10:
                    clearBookQueue();
                    break;
                case 11:
                    voidReservation();
                    break;
                case 12:
                    revertAccidentalReturn();
                    break;
                case 0:
                    back = true;
                    break;
            }
            if (!back) ConsoleIO.pause();
        }
    }
 
    private static void printMenu() {
        System.out.println("\n--- RESERVATIONS & BORROWING ---");
        System.out.println("1. Borrow a Book");
        System.out.println("2. Return a Book");
        System.out.println("3. Run Waitlist Processor (daily job)");
        System.out.println("4. View Customer Borrowing History");
        System.out.println("5. View Book Reservation Record");
        System.out.println("6. View All Current Reservation");
        System.out.println("7. View Book waitlist");
        System.out.println("8. View Book Circulation Stats");
        System.out.println("9. Cancel a Waitlist Request");
        System.out.println("10. Emergency: Clear a Book's Waitlist");
        System.out.println("11. Void a Reservation");
        System.out.println("12. Revert an Accidental Return");
        System.out.println("0. Back to Main Menu");
    }
 
    private static void requestBook() {
        CustomerManager.displayPartial();
        String customerId = ConsoleIO.readLine("Enter Customer ID: ");
        BookManager.displayPartialBook();
        String bookId = ConsoleIO.readLine("Enter Book ID: ");
        ReservationManager.requestBook(customerId, bookId);
    }
 
    private static void returnBook() {
        BookManager.displayPartialBook();
        String bookId = ConsoleIO.readLine("Enter Book ID being returned: ");
        ReservationManager.returnBook(bookId);
    }
 
    private static void viewCustomerHistory() {
        CustomerManager.displayPartial();
        String customerId = ConsoleIO.readLine("Enter Customer ID: ");
        ReservationManager.viewCustomerHistory(customerId);
    }
 
    private static void viewBookRecord() {
        BookManager.displayPartialBook();
        String bookId = ConsoleIO.readLine("Enter Book ID: ");
        ReservationManager.viewBookRecord(bookId);
    }
    
    private static void viewWaitListQueue(){
        BookManager.displayPartialBook();
        String bookId = ConsoleIO.readLine("enter Book ID:");
        ReservationManager.viewWaitlistQueue(bookId);
    }
 
    private static void bookAvailabilityStats() {
        BookManager.displayPartialBook();
        String bookId = ConsoleIO.readLine("Enter Book ID: ");
        ReservationManager.bookAvailabilityStats(bookId);
    }
 
    private static void cancelWaitlistRequest() {
        CustomerManager.displayPartial();
        String customerId = ConsoleIO.readLine("Enter Customer ID: ");
        BookManager.displayPartialBook();
        String bookId = ConsoleIO.readLine("Enter Book ID: ");
        ReservationManager.cancelWaitlistRequest(customerId, bookId);
    }
 
    private static void clearBookQueue() {
        BookManager.displayPartialBook();
        String bookId = ConsoleIO.readLine("Enter Book ID: ");
        String reason = ConsoleIO.readLine("Reason for clearing queue: ");
        ReservationManager.clearBookQueue(bookId, reason);
    }
 
    private static void voidReservation() {
        ReservationManager.viewAllReservations();
        String reservationId = ConsoleIO.readLine("Enter Reservation ID: ");
        String reason = ConsoleIO.readLine("Reason for voiding: ");
        ReservationManager.voidReservation(reservationId, reason);
    }
 
    private static void revertAccidentalReturn() {
        ReservationManager.viewAllReservations();
        String reservationId = ConsoleIO.readLine("Enter Reservation ID: ");
        ReservationManager.revertAccidentalReturn(reservationId);
    }
}
