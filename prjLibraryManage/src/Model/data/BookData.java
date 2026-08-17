
package Model.data;

import java.util.UUID;


public class BookData {
    protected UUID id;
    protected String title;
    protected String author;
    protected double price;
    protected String publishDate;

    public BookData(UUID id, String title, String author, double price, String publishDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.publishDate = publishDate;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
    
    
}
