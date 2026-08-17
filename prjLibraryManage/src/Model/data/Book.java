
package Model.data;

import java.util.UUID;


public class Book extends BookData{
    private String genre;
    private String author;
    private String category;

    public Book() {
    }

    public Book(String genre, String author, String category, UUID id, String title, double price, String publishDate) {
        super(id, title, price, publishDate);
        this.genre = genre;
        this.author = author;
        this.category = category;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    
}
