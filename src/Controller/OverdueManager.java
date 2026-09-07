
package Controller;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import Model.Overdues;

public class OverdueManager {
    private static final Path FILE_PATH = Paths.get("fines.txt");
    private static final double DAILY_LATE_FEE =  25.00;
    
    // help read from files
    private static Map<String, Overdues> loadFinesToMap() {
        Map<String, Overdues> map = new LinkedHashMap<>();
        if (!Files.exists(FILE_PATH)) return map;
        try {
            for (String line : Files.readAllLines(FILE_PATH)) {
                if (line.trim().isEmpty()) continue;
                Overdues fine = Overdues.fromTxtLine(line);
                map.put(fine.getOverdueId().toString(), fine);
            }
        } catch (IOException e) { System.err.println("Error: " + e.getMessage()); }
        return map;
    }

    private static void saveMapToFile(Map<String, Overdues> map) {
        List<String> lines = new ArrayList<>();
        for (Overdues f : map.values()) lines.add(f.toTxtLine());
        try { Files.write(FILE_PATH, lines); } 
        catch (IOException e) { System.err.println("Error: " + e.getMessage()); }
    }
    
    public static void issueFine(String customerId, String reserveId, int daysOverdue) {
        Map<String, Overdues> map = loadFinesToMap();
        
        Overdues newFine = new Overdues(UUID.fromString(customerId), UUID.fromString(reserveId), daysOverdue, DAILY_LATE_FEE);
        map.put(newFine.getOverdueId().toString(), newFine);
        saveMapToFile(map);

        System.out.printf("[Book Overdue Detected]\n Customer charged $%.2f for being %d days late.%n", newFine.getFine(), daysOverdue);
    }
    
    public static void payFine(String fineId) {
        Map<String, Overdues> map = loadFinesToMap();
        Overdues fine = map.get(fineId);

        if (fine == null) {
            System.out.println("[Error]: Fine ID not found.");
            return;
        }

        if (fine.getStatus().equals("PAID")) {
            System.out.println("[Notice]: This fine has already been paid.");
            return;
        }

        
        fine.setStatus("PAID");
        fine.setPayDate(LocalDate.now());
        saveMapToFile(map);

        System.out.printf("Success! Fine of $%.2f has been paid and archived.%n", fine.getFine());
    }
    
    //show active fines 
    public static void viewActiveFines(String customerId) {
        System.out.println("\n--- ACTIVE UNPAID FINES ---");
        displayFinesByStatus(customerId, "UNPAID");
    }

    // archived fines(ones that are already paid)
    public static void viewFineArchive(String customerId) {
        System.out.println("\n--- PAID FINES ARCHIVE ---");
        displayFinesByStatus(customerId, "PAID");
    }

    // methods that show all fines instead of just a selected user
     public static void viewAllActiveFines() {
        System.out.println("\n--- ALL ACTIVE UNPAID FINES (ALL CUSTOMERS) ---");
        displayFinesByStatus(null, "UNPAID");
    }
 
    public static void viewAllFineArchive() {
        System.out.println("\n--- ALL PAID FINES ARCHIVE (ALL CUSTOMERS) ---");
        displayFinesByStatus(null, "PAID");
    }
    
    // Helper for printing
    private static void displayFinesByStatus(String customerId, String targetStatus) {
        Map<String, Overdues> map = loadFinesToMap();
        boolean found = false;

        for (Overdues f : map.values()) {
            if (f.getCustomerId().toString().equals(customerId) && f.getStatus().equals(targetStatus)) {
                String name = CustomerManager.getCustomerName(customerId);
                System.out.printf("Fine ID: %s | Customer: %s | Amount: $%.2f | Issued: %s%n", f.getOverdueId(), name, f.getFine(), f.getFinedDate());
                found = true;
            }
        }
        if (!found) System.out.println("No records found in this category.");
    }
}