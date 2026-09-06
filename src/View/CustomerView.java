package View;

import Utils.ConsoleIO;
import Controller.CustomerManager;

public class CustomerView {

    public static void showMenu() {
        boolean back = false;
        while (!back) {
            printMenu();
            int choice = ConsoleIO.readMenuChoice("Select an option: ", 0, 5);

            switch (choice) {
                case 1:
                    CustomerManager.inputCustomer();
                    break;
                case 2:
                    CustomerManager.displayAllCustomers();
                    break;
                case 3:
                    searchCustomer();
                    break;
                case 4:
                    updateCustomer();
                    break;
                case 5:
                    removeCustomer();
                    break;
                case 0:
                    back = true;
                    break;
            }
            if (!back) {
                ConsoleIO.pause();
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- CUSTOMER MANAGEMENT ---");
        System.out.println("1. Add New Customer");
        System.out.println("2. Display All Customers");
        System.out.println("3. Search Customer by Name");
        System.out.println("4. Update Customer");
        System.out.println("5. Remove Customer");
        System.out.println("0. Back to Main Menu");
    }

    private static void searchCustomer() {
        String keyword = ConsoleIO.readLine("Enter name keyword: ");
        CustomerManager.searchCustomerByName(keyword);
    }

    private static void updateCustomer() {
        CustomerManager.displayPartial();
        String id = ConsoleIO.readLine("Enter Customer ID to update: ");
        CustomerManager.updateCustomer(id);
    }

    private static void removeCustomer() {
        CustomerManager.displayPartial();
        String id = ConsoleIO.readLine("Enter Customer ID to remove: ");
        CustomerManager.removeCustomer(id);
    }
}
