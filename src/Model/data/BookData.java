package Model.data;

import java.util.UUID;
import java.time.LocalDate;

public class BookData {

    private UUID id;
    private String title;
    private String author;
    private double price;
    private String genre;
    private String category;
    private LocalDate publishDate;

    public BookData(String title, String author, String genre, String category, double price, LocalDate publishDate) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.price = price;
        this.genre = genre;
        this.category = category;
        this.publishDate = publishDate;
    }

    public BookData(UUID id, String title, String author, String genre, String category,double price, LocalDate publishDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.genre = genre;
        this.category = category;
        this.publishDate = publishDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BookData() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
    // Object -> Text Line
    public String toTxtLine() {
        return id.toString() + "|" + title + "|" + author + "|" + genre + "|" + publishDate.toString() + "|" + price + "|" + category;
    }

    // Convert Text Line -> Object
    public static BookData fromTxtLine(String line) {
        String[] parts = line.split("\\|");
        UUID loadedId = UUID.fromString(parts[0]);
        String title = parts[1];
        String author = parts[2];
        String genre = parts[3];
        String category = parts[4];
        Double price = Double.parseDouble(parts[5]);   
        LocalDate publishDate = LocalDate.parse(parts[6]);

        return new BookData(loadedId, title, author, genre, category, price, publishDate);
    }
}
