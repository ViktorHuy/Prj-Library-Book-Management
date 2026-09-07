package Model;

import java.util.UUID;
import java.time.LocalDate;

public class Overdues {

    private UUID overdueId;
    private UUID customerId;
    private UUID reserveId;
    private double fine;
    private String status;
    private LocalDate finedDate;
    private LocalDate payDate;

    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_UNPAID = "UNPAID";

    // Constructor for new fines
    public Overdues(UUID customerId, UUID reserveId, int daysOverdue, double overdueInterest) {
        this.overdueId = UUID.randomUUID();
        this.customerId = customerId;
        this.reserveId = reserveId;
        this.fine = daysOverdue * overdueInterest;
        this.status = "Unpaid";
        this.finedDate = LocalDate.now();
        this.payDate = null;
    }

    // Constructor to load data from file
    public Overdues(UUID overdueId, UUID customerId, UUID reserveId, double fine, String status, LocalDate finedDate, LocalDate payDate) {
        this.overdueId = overdueId;
        this.customerId = customerId;
        this.reserveId = reserveId;
        this.fine = fine;
        this.status = status;
        this.finedDate = finedDate;
        this.payDate = payDate;
    }

    // Translating from and to text files
    public String toTxtLine() {
        String paidStr = (payDate != null) ? payDate.toString() : "null";
        return overdueId + "|" + customerId + "|" + reserveId + "|"
                + fine + "|" + status + "|" + finedDate + "|" + paidStr;
    }

    public static Overdues fromTxtLine(String line) {
        String[] parts = line.split("\\|");
        LocalDate parsedPaid = parts[6].equals("null") ? null : LocalDate.parse(parts[6]);

        return new Overdues(
                UUID.fromString(parts[0]), UUID.fromString(parts[1]), UUID.fromString(parts[2]),  Double.parseDouble(parts[3]), parts[4], LocalDate.parse(parts[5]), parsedPaid
        );
    }

    public UUID getOverdueId() {
        return overdueId;
    }

    public void setOverdueId(UUID overdueId) {
        this.overdueId = overdueId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getReserveId() {
        return reserveId;
    }

    public void setReserveId(UUID reserveId) {
        this.reserveId = reserveId;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getFinedDate() {
        return finedDate;
    }

    public void setFinedDate(LocalDate finedDate) {
        this.finedDate = finedDate;
    }

    public LocalDate getPayDate() {
        return payDate;
    }

    public void setPayDate(LocalDate payDate) {
        this.payDate = payDate;
    }

}
