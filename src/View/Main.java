package View;
import Utils.*;

public class Main {

   public static void main(String[] args) {
        boolean running = true;
 
        while (running) {
            printMainMenu();
            int choice = ConsoleIO.readMenuChoice("Select an option: ", 0, 5);
 
            switch (choice) {
                case 1:
                    BookView.showMenu();
                    break;
                case 2:
                    CustomerView.showMenu();
                    break;
                case 3:
                    ReservationView.showMenu();
                    break;
                case 4:
                    OverdueView.showMenu();
                    break;
                case 5:
                    ReportView.showMenu();
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
            }
        }
    }
 
    private static void printMainMenu() {
        System.out.println("\n=================================================");
        System.out.println("             LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=================================================");
        System.out.println("1. Book Management");
        System.out.println("2. Customer Management");
        System.out.println("3. Reservations & Borrowing");
        System.out.println("4. Fines & Overdues");
        System.out.println("5. Reports");
        System.out.println("0. Exit");
        System.out.println("=================================================");
    }
}
