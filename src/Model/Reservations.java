
package Model;
import java.util.UUID;
import java.time.LocalDate;

public class Reservations {
    private UUID reserveId;
    private UUID customerId;
    private UUID bookId;
    private LocalDate borrowDate;
    private String status;
    private LocalDate returnDate;

    public Reservations(UUID customerId, UUID bookId, String status) {
        this.reserveId = UUID.randomUUID();
        this.customerId = customerId;
        this.bookId = bookId;
        this.borrowDate = LocalDate.now();
        this.status = status;
        this.returnDate = null;
    }

    public Reservations(UUID reserveId, UUID customerId, UUID bookId, LocalDate borrowDate, String status, LocalDate returnDate) {
        this.reserveId = reserveId;
        this.customerId = customerId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.status = status;
        this.returnDate = returnDate;
    }

    public UUID getReserveId() {
        return reserveId;
    }

    public void setReserveId(UUID reserveId) {
        this.reserveId = reserveId;
    }


    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    
    public String toTxtLine() {
        return reserveId.toString() + "|" + customerId.toString() + "|" + bookId.toString() + "|" + 
               borrowDate.toString() + "|" + status;
    }

    public static Reservations fromTxtLine(String line) {
        String[] parts = line.split("\\|");
        LocalDate parsedReturn = null;
        if (parts.length == 6 && !parts[5].equals("null")) {
            parsedReturn = LocalDate.parse(parts[5]);
        }
        return new Reservations(
            UUID.fromString(parts[0]), 
            UUID.fromString(parts[1]), 
            UUID.fromString(parts[2]), 
            LocalDate.parse(parts[3]), 
            parts[4], // status
            parsedReturn
        );
    }
    
}
