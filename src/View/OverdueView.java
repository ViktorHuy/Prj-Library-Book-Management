
package View;
import Utils.ConsoleIO;
import Controller.OverdueManager;

public class OverdueView {
    public static void showMenu() {
        boolean back = false;
        while (!back) {
            printMenu();
            int choice = ConsoleIO.readMenuChoice("Select an option: ", 0, 3);
 
            switch (choice) {
                case 1:
                    payFine();
                    break;
                case 2:
                    viewActiveFines();
                    break;
                case 3:
                    viewFineArchive();
                    break;
                case 0:
                    back = true;
                    break;
            }
            if (!back) ConsoleIO.pause();
        }
    }
 
    private static void printMenu() {
        System.out.println("\n--- FINES & OVERDUES ---");
        System.out.println("1. Pay a Fine");
        System.out.println("2. View Active (Unpaid) Fines for a Customer");
        System.out.println("3. View Paid Fine Archive for a Customer");
        System.out.println("0. Back to Main Menu");
        System.out.println("(Fines are issued automatically on late returns - no manual option needed)");
    }
 
    private static void payFine() {
        String fineId = ConsoleIO.readLine("Enter Fine ID: ");
        OverdueManager.payFine(fineId);
    }
 
    private static void viewActiveFines() {
        String customerId = ConsoleIO.readLine("Enter Customer ID: ");
        OverdueManager.viewActiveFines(customerId);
    }
 
    private static void viewFineArchive() {
        String customerId = ConsoleIO.readLine("Enter Customer ID: ");
        OverdueManager.viewFineArchive(customerId);
    }
}
