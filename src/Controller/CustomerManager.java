package Controller;

import java.nio.file.*;
import java.io.IOException;
import Model.customer.Customer;
import Utils.Validation;
import java.util.*;

public class CustomerManager {

    public static final Path FILE_PATH = Paths.get("customers.txt");

    /* hash map to txt file and reverse */
    private static Map<String, Customer> loadCustomersToMap() {
        Map<String, Customer> customerMap = new LinkedHashMap<>();

        if (!Files.exists(FILE_PATH)) {
            return customerMap;
        }

        try {
            List<String> lines = Files.readAllLines(FILE_PATH);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Customer customer = Customer.fromTxtLine(line);
                customerMap.put(customer.getId().toString(), customer);
            }
        } catch (IOException e) {
            System.err.println("Error reading customers.txt: " + e.getMessage());
        }

        return customerMap;
    }

    private static void saveMapToFile(Map<String, Customer> customerMap) {
        List<String> lines = new ArrayList<>();
        for (Customer c : customerMap.values()) {
            lines.add(c.toTxtLine());
        }

        try {
            Files.write(FILE_PATH, lines);
        } catch (IOException e) {
            System.err.println("Error writing to customers.txt: " + e.getMessage());
        }
    }

    /* CRUDs Functions*/
    public static void inputCustomer() {
        System.out.println("\n--- Please Input Customer Info ---");
        String name = Validation.getValidString("[Customer's Name]:", "This field cannot be empty");
        String phoneNum = Validation.getValidPhone();
        String mail = Validation.getValidMail();
        boolean isMem = Validation.getValidBoolean("Is this customer a member?", "Customer must either be a member or not(y/n)");

        Customer newCustomer = new Customer(name, phoneNum, mail, isMem);

        Map<String, Customer> map = loadCustomersToMap();
        map.put(newCustomer.getId().toString(), newCustomer);
        saveMapToFile(map);

        System.out.println("Success! Customer saved with ID: " + newCustomer.getId());
    }

    public static void displayAllCustomers() {
        Map<String, Customer> map = loadCustomersToMap();
        if (map.isEmpty()) {
            System.out.printf("\nNo customer records found.");
            return;
        }

        System.out.printf("\n---- ALL CUSTOMERS ----\n");
        for (Customer c : map.values()) {
            System.out.printf("Name: %s\n", c.getName());
            System.out.printf("ID: %s\n", c.getId());
            System.out.printf("Phone Number: %s\n", c.getPhoneNum());
            System.out.printf("Email: %s\n", c.getMail());
            System.out.printf("Member Status: %n\n", c.isMember());
            System.out.println("------------------------");
        }
    }

    public static void displayPartial() {
        Map<String, Customer> map = loadCustomersToMap();
        if (map.isEmpty()) {
            System.out.printf("\nNo customer records found.");
            return;
        }

        System.out.printf("\n---- ALL CUSTOMERS ----\n");
        for (Customer c : map.values()) {
            System.out.printf("Name: %s", c.getName());
            System.out.printf("\nID: %s", c.getId());
            System.out.printf("\nPhone Number: %s", c.getPhoneNum());
            System.out.printf("\n------------------------\n");
        }

    }

    public static void searchCustomerByName(String keyword) {
        Map<String, Customer> map = loadCustomersToMap();
        boolean found = false;

        System.out.printf("\n--- SEARCH RESULTS ---");
        for (Customer c : map.values()) {
            if (c.getName().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.printf("Name: %s\n", c.getName());
                System.out.printf("ID: %s\n", c.getId());
                System.out.printf("Phone Number: %s\n", c.getPhoneNum());
                System.out.printf("Email: %s\n", c.getMail());
                System.out.printf("Member Status: %n\n", c.isMember());
                System.out.println("------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No customer found matching name: " + keyword);
        }
    }

    public static void updateCustomer(String targetId) {
        Map<String, Customer> map = loadCustomersToMap();

        if (!map.containsKey(targetId)) {
            System.out.println("Error: Customer ID not found.");
            return;
        }

        System.out.printf("\nCustomer found! Enter new details:\n");
        String newName = Validation.getValidString("[New Name]:", "This field cannot be empty");
        String newPhone = Validation.getValidPhone();
        String newMail = Validation.getValidMail();
        boolean newIsMem = Validation.getValidBoolean("Is this customer a member?", "customer must either be a member or not");

        UUID existingId = UUID.fromString(targetId);
        Customer updatedCustomer = new Customer(existingId, newName, newPhone, newMail, newIsMem);

        map.put(targetId, updatedCustomer);
        saveMapToFile(map);

        System.out.println("Customer record updated successfully.");
    }

    public static void removeCustomer(String targetId) {
        Map<String, Customer> map = loadCustomersToMap();

        Customer removed = map.remove(targetId);

        if (removed != null) {
            saveMapToFile(map);
            System.out.println("Customer " + removed.getName() + " removed successfully.");
        } else {
            System.out.println("Error: Customer ID not found.");
        }
    }

    // helper function to let reservation know if the customer is a member or not
    public static boolean isCustomerMember(String customerId) {
        Map<String, Customer> map = loadCustomersToMap();
        Customer c = map.get(customerId);

        // returns true if the customer exists AND is a member
        return c != null && c.isMember();
    }

    // helper functions 
    public static boolean customerExists(String customerId) {
        Map<String, Customer> map = loadCustomersToMap();
        return map.containsKey(customerId);
    }

    public static String getCustomerName(String customerId) {
        Map<String, Customer> map = loadCustomersToMap();
        Customer c = map.get(customerId);

        // Returns the name, or a fallback if the customer was deleted from the system
        return (c != null) ? c.getName() : "Unknown Customer (Deleted)";
    }
}
