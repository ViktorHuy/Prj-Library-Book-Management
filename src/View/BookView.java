
package View;
import Utils.ConsoleIO;
import Controller.BookManager;

public class BookView {
    
    public static void showMenu() {
        boolean back = false;
        while (!back) {
            printMenu();
            int choice = ConsoleIO.readMenuChoice("Select an option: ", 0, 7);
 
            switch (choice) {
                case 1:
                    BookManager.bookInput();
                    break;
                case 2:
                    BookManager.displayAllBooks();
                    break;
                case 3:
                    searchBook();
                    break;
                case 4:
                    updateBook();
                    break;
                case 5:
                    removeBook();
                    break;
                case 6:
                    toggleAvailability();
                    break;
                case 7:
                    sortBooks();
                    break;
                case 0:
                    back = true;
                    break;
            }
            if (!back) ConsoleIO.pause();
        }
    }
 
    private static void printMenu() {
        System.out.println("\n--- BOOK MANAGEMENT ---");
        System.out.println("1. Add New Book");
        System.out.println("2. Display All Books");
        System.out.println("3. Search Book by Title");
        System.out.println("4. Update Book");
        System.out.println("5. Remove Book");
        System.out.println("6. Suspend / Restore Book Availability");
        System.out.println("7. Sort Books by Title / Author / Publish Year");
        System.out.println("0. Back to Main Menu");
    }

    private static void sortBooks() {
        System.out.println("\n--- SORT BOOKS ---");
        System.out.println("1. Sort by Title");
        System.out.println("2. Sort by Author");
        System.out.println("3. Sort by Publish Year");
        int sortOption = ConsoleIO.readMenuChoice("Choose sorting option: ", 1, 3);
        BookManager.displayAllBooks(sortOption);
    }
 
    private static void searchBook() {
        String keyword = ConsoleIO.readLine("Enter title keyword: ");
        BookManager.searchBookByTitle(keyword);
    }
 
    private static void updateBook() {
        BookManager.displayPartialBook();
        String id = ConsoleIO.readLine("Enter Book ID to update: ");
        BookManager.updateBook(id);
    }
 
    private static void removeBook() {
        BookManager.displayPartialBook();
        String id = ConsoleIO.readLine("Enter Book ID to remove: ");
        BookManager.removeBook(id);
    }
 
    private static void toggleAvailability() {
        BookManager.displayPartialBook();
        String id = ConsoleIO.readLine("Enter Book ID: ");
        String action = ConsoleIO.readLine("Type 'suspend' or 'restore': ").toLowerCase();
 
        boolean makeAvailable;
        if (action.equals("restore")) {
            makeAvailable = true;
        } else if (action.equals("suspend")) {
            makeAvailable = false;
        } else {
            System.out.println("Invalid option. Please type 'suspend' or 'restore'.");
            return;
        }
 
        String reason = "";
        if (!makeAvailable) {
            reason = ConsoleIO.readLine("Reason for suspension: ");
        }
 
        BookManager.toggleBookAvailability(id, makeAvailable, reason);
    }
}
