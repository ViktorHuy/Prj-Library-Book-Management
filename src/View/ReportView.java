
package View;
import Utils.ConsoleIO;
import Controller.ReportManager;
import java.time.Year;

public class ReportView {
    public static void showMenu() {
        boolean back = false;
        while (!back) {
            printMenu();
            int choice = ConsoleIO.readMenuChoice("Select an option: ", 0, 1);
 
            switch (choice) {
                case 1:
                    generateReport();
                    break;
                case 0:
                    back = true;
                    break;
            }
            if (!back) ConsoleIO.pause();
        }
    }
 
    private static void printMenu() {
        System.out.println("\n--- REPORTS ---");
        System.out.println("1. Generate Monthly Report");
        System.out.println("0. Back to Main Menu");
    }
 
    private static void generateReport() {
        int year = readYear();
        int month = ConsoleIO.readMenuChoice("Enter month (1-12): ", 1, 12);
        ReportManager.generateMonthlyReport(year, month);
    }
 
    private static int readYear() {
        int currentYear = Year.now().getValue();
        while (true) {
            String raw = ConsoleIO.readLine("Enter year (e.g. " + currentYear + "): ");
            try {
                int year = Integer.parseInt(raw);
                if (year >= 2000 && year <= currentYear + 1) {
                    return year;
                }
                System.out.println("Please enter a realistic year between 2000 and " + (currentYear + 1) + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric year.");
            }
        }
    }
}
