
package Model.data;

import java.util.UUID;
import java.util.Scanner;


public class Book extends BookData{
    
    public Scanner sc = new Scanner(System.in);
    
    private String genre;
    private String category;

    public Book() {
    }

    public Book(UUID id, String title, double price, String publishDate, String genre, String author, String category ) {
        super(id, title,author, price, publishDate);
        this.genre = genre;
        this.category = category;
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
    
}
