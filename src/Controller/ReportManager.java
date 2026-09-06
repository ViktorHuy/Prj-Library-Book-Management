
package Controller;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import Model.Overdues;
import Model.Reservations;

public class ReportManager {
    private static final Path RESERVATIONS_FILE = Paths.get("reservations.txt");
    private static final Path FINES_FILE = Paths.get("fines.txt");

    public static void generateMonthlyReport(int year, int month) {
        System.out.printf("\n=================================================\n");
        System.out.printf("     MONTHLY REPORT: %s %d%n", Month.of(month).name(), year);
        System.out.println("=================================================");
        
        int totalBorrows = 0;
        int completedReturns = 0;
        Map<String, Integer> bookCounts = new HashMap<>();
        Map<String, Integer> customerCounts = new HashMap<>();

        // scan all the reservations
        if (Files.exists(RESERVATIONS_FILE)) {
            try {
                List<String> lines = Files.readAllLines(RESERVATIONS_FILE);
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    
                    Reservations res = Reservations.fromTxtLine(line);
                    LocalDate date = res.getBorrowDate();

                    // filter by Target Year & Month
                    if (date.getYear() == year && date.getMonthValue() == month) {
                        totalBorrows++;

                        if ("COMPLETED".equals(res.getStatus())) {
                            completedReturns++;
                        }

                        // track popular books & active customers
                        String bookId = res.getBookId().toString();
                        String customerId = res.getCustomerId().toString();

                        bookCounts.put(bookId, bookCounts.getOrDefault(bookId, 0) + 1);
                        customerCounts.put(customerId, customerCounts.getOrDefault(customerId, 0) + 1);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading reservations: " + e.getMessage());
            }
        }

        // scanning for finance metrics
        double totalFinesIssued = 0.0;
        double totalFinesCollected = 0.0;

        if (Files.exists(FINES_FILE)) {
            try {
                List<String> lines = Files.readAllLines(FINES_FILE);
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;

                    Overdues fine = Overdues.fromTxtLine(line);

                    // track Fines this month
                    if (fine.getFinedDate().getYear() == year && fine.getFinedDate().getMonthValue() == month) {
                        totalFinesIssued += fine.getFine();
                    }

                    // track Revenue PAID this month
                    if (fine.getPayDate()!= null && 
                        fine.getPayDate().getYear() == year && 
                        fine.getPayDate().getMonthValue() == month) {
                        totalFinesCollected += fine.getFine();
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading fines: " + e.getMessage());
            }
        }

        // finding best performers
        String topBookId = getTopKey(bookCounts);
        String topCustomerId = getTopKey(customerCounts);

        String topBookTitle = (topBookId != null) ? BookManager.getBookTitle(topBookId) : "N/A";
        String topCustomerName = (topCustomerId != null) ? CustomerManager.getCustomerName(topCustomerId) : "N/A";

        // report summary
        System.out.println("--- ACTIVITY SUMMARY ---");
        System.out.println("Total New Reservations : " + totalBorrows);
        System.out.println("Books Returned         : " + completedReturns);
        
        if (totalBorrows > 0) {
            double returnRate = ((double) completedReturns / totalBorrows) * 100;
            System.out.printf("Return Rate            : %.1f%%%n", returnRate);
        }

        System.out.printf("\n--- HIGHLIGHTS ---\n");
        System.out.printf("Most Borrowed Book    : %s (%d times)%n", 
                topBookTitle, bookCounts.getOrDefault(topBookId, 0));
        System.out.printf("Top Active Reader     : %s (%d borrows)%n", 
                topCustomerName, customerCounts.getOrDefault(topCustomerId, 0));

        System.out.println("\n--- FINANCIALS ---");
        System.out.printf("Total Fines Issued     : $%.2f%n", totalFinesIssued);
        System.out.printf("Total Fines Collected  : $%.2f%n", totalFinesCollected);
        System.out.printf("\n=================================================\n");
    }


    private static String getTopKey(Map<String, Integer> map) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    }
}
