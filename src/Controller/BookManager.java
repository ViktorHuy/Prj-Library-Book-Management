package Controller;

import Model.data.*;
import java.nio.file.*;
import java.io.IOException;
import Utils.Validation;
import java.time.LocalDate;
import java.util.*;

public class BookManager {

    public static final Path FILE_PATH = Paths.get("book.txt");

    // HashMap helper
    private static Map<String, BookData> loadBooksToMap() {
        Map<String, BookData> bookMap = new LinkedHashMap<>();

        if (!Files.exists(FILE_PATH)) {
            return bookMap;
        }

        try {
            List<String> lines = Files.readAllLines(FILE_PATH);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    BookData book = BookData.fromTxtLine(line);
                    bookMap.put(book.getId().toString(), book);
                } catch (Exception e) {
                    // skip a row if it is unreadable or corrupted
                    System.err.println("Skipping unreadable book record: " + line);
                    System.err.println("  Reason: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading books.txt: " + e.getMessage());
        }

        return bookMap;
    }

    private static void saveMapToFile(Map<String, BookData> bookMap) {
        List<String> lines = new ArrayList<>();
        for (BookData b : bookMap.values()) {
            lines.add(b.toTxtLine());
        }

        try {
            Files.write(FILE_PATH, lines);
        } catch (IOException e) {
            System.err.println("Error writing to books.txt: " + e.getMessage());
        }
    }

    // CRUD functions
    public static void bookInput() {
        String title = Validation.getValidString("[title]:", "Title cannot be blank!\n");
        String author = Validation.getValidString("[Author]:", "Author field cannot be blank!\n");
        String category = Validation.getValidString("[Category]:", "field cannot be empty\n");
        String genre = Validation.getValidString("[Genre/Subject]:", "Field cannot be blank!\n");
        Double price = Validation.getValidPrice();
        LocalDate pubDate = Validation.getValidDate("[Publish Date]:", "Please ensure the input is strictly [DD-MM-YYYY]\n");

        BookData newBook = new BookData(title, author, genre, category, price, pubDate);

        Map<String, BookData> map = loadBooksToMap();
        map.put(newBook.getId().toString(), newBook);
        saveMapToFile(map);

        System.out.println("Success! Book saved with ID: " + newBook.getId());
    }

    public static void displayAllBooks() {
        displayAllBooks(0);
    }

    public static void displayAllBooks(int sortOption) {
        Map<String, BookData> map = loadBooksToMap();
        if (map.isEmpty()) {
            System.out.println("\nNo books found in the library.");
            return;
        }

        List<BookData> books = new ArrayList<>(map.values());
        switch (sortOption) {
            case 1:
                books.sort(Comparator.comparing(BookData::getTitle, String.CASE_INSENSITIVE_ORDER));
                System.out.println("\n---- LIBRARY INVENTORY (SORTED BY TITLE) ----");
                break;
            case 2:
                books.sort(Comparator.comparing(BookData::getAuthor, String.CASE_INSENSITIVE_ORDER));
                System.out.println("\n---- LIBRARY INVENTORY (SORTED BY AUTHOR) ----");
                break;
            case 3:
                books.sort(Comparator.comparing(BookData::getPublishDate));
                System.out.println("\n---- LIBRARY INVENTORY (SORTED BY PUBLISH YEAR) ----");
                break;
            default:
                System.out.println("\n---- LIBRARY INVENTORY ----");
                break;
        }

        for (BookData b : books) {
            System.out.printf("Title: %s\n", b.getTitle());
            System.out.printf("ID: %s\n", b.getId());
            System.out.printf("Author: %s\n", b.getAuthor());
            System.out.printf("Cateogry: %s\n", b.getCategory());
            System.out.printf("Genre: %s\n", b.getGenre());
            System.out.printf("Price: %s$\n", b.getPrice());
            System.out.printf("Publish Date: %s%n\n", b.getPublishDate());
            System.out.println("------------------------");
        }
    }

    // show a partial information so the user can choose a book id
    public static void displayPartialBook() {
        Map<String, BookData> map = loadBooksToMap();
        if (map.isEmpty()) {
            System.out.println("\nNo books found in the library.");
            return;
        }

        System.out.println("\n---- LIBRARY INVENTORY ----");
        for (BookData b : map.values()) {
            System.out.printf("Title: %s\n", b.getTitle());
            System.out.printf("ID: %s\n", b.getId());
            System.out.printf("Author: %s\n", b.getAuthor());
            System.out.println("------------------------");
        }
    }

    public static void searchBookByTitle(String keyword) {
        Map<String, BookData> map = loadBooksToMap();
        boolean found = false;

        System.out.println("\n--- SEARCH RESULTS ---");
        for (BookData b : map.values()) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.printf("Title: %s\n", b.getTitle());
                System.out.printf("ID: %s\n", b.getId());
                System.out.printf("Author: %s\n", b.getAuthor());
                System.out.printf("Cateogry: %s\n", b.getCategory());
                System.out.printf("Genre: %s\n", b.getGenre());
                System.out.printf("Price: %s$\n", b.getPrice());
                System.out.printf("Publish Date: %s%n\n", b.getPublishDate());
                System.out.println("------------------------");

                found = true;
            }
        }

        if (!found) {
            System.out.println("No books found matching title: " + keyword);
        }
    }

    public static void updateBook(String targetId) {
        Map<String, BookData> map = loadBooksToMap();

        if (!map.containsKey(targetId)) {
            System.out.println("Error: Book ID not found.");
            return;
        }

        System.out.println("\nBook found! Enter new details:");
        String newTitle = Validation.getValidString("[New Title]:", "Title cannot be blank!");
        String newAuthor = Validation.getValidString("[New Author]:", "Author field cannot be blank!");
        String newGenre = Validation.getValidString("[New Genre/Subject]:", "Field cannot be blank!");
        LocalDate newPubDate = Validation.getValidDate("[New Publish Date]:", "Please ensure the input is strictly [DD-MM-YYYY]");
        Double newPrice = Validation.getValidPrice();
        String newCategory = Validation.getValidString("[New Category]:", "Field cannot be empty");
        boolean newStatus = Validation.getValidBoolean("Is this book available for reserve?", "book must either be available or not");

        // Keep the existing ID
        UUID existingId = UUID.fromString(targetId);
        BookData updatedBook = new BookData(existingId, newTitle, newAuthor, newGenre, newCategory, newPrice, newPubDate, newStatus);

        map.put(targetId, updatedBook);
        saveMapToFile(map);

        System.out.println("Book updated successfully.");
    }

    public static void removeBook(String targetId) {
        Map<String, BookData> map = loadBooksToMap();

        BookData removed = map.remove(targetId);

        if (removed != null) {
            saveMapToFile(map);
            System.out.println("Book '" + removed.getTitle() + "' removed successfully.");
        } else {
            System.out.println("Error: Book ID not found.");
        }
    }

    // helper function
    public static boolean isBookAvailable(String bookId) {
        Map<String, BookData> map = loadBooksToMap();
        BookData book = map.get(bookId);
        return book != null && book.isAvailable();
    }

    public static boolean bookExists(String bookId) {
        Map<String, BookData> map = loadBooksToMap();
        return map.containsKey(bookId);
    }

    public static String getBookTitle(String bookId) {
        Map<String, BookData> map = loadBooksToMap();
        BookData book = map.get(bookId);
        return (book != null) ? book.getTitle() : "Unknown Book (Deleted)";
    }

    // switch the availability of the book
    public static void toggleBookAvailability(String bookId, boolean makeAvailable, String reason) {
        Map<String, BookData> map = loadBooksToMap();
        BookData book = map.get(bookId);

        if (book == null) {
            System.out.println("Error: Book ID not found.");
            return;
        }

        if (book.isAvailable() == makeAvailable) {
            System.out.println("Notice: Book is already set to that availability status.");
            return;
        }

        // Update and save
        book.setIsAvailable(makeAvailable);
        saveMapToFile(map);

        String status = makeAvailable ? "AVAILABLE" : "SUSPENDED";
        System.out.printf("Success: '%s' has been marked as %s.%n", book.getTitle(), status);
        if (!makeAvailable) {
            System.out.println("Reason logged: " + reason);
            System.out.println("Tip: Remember to clear the queue of the book incase there customers are waiting!");
        }
    }

}
