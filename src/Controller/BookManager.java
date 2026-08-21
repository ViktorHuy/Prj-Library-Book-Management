package Controller;

import model.data.*;
import Utils.InputValidation;
import java.time.LocalDate;

public class BookManager {

    public static void bookInput() {
        String title = InputValidation.getValidString("[title]:", "Title cannot be blank!");
        String author = InputValidation.getValidString("[Author]:", "Author field cannot be blank!");
        LocalDate pubDate = InputValidation.getValidDate("[Publish Date]:", "Please ensure the input is strictly [DD-MM-YYYY]");
        Double price = InputValidation.getValidPrice();
        String category = InputValidation.getValidString("[Category]:", "field cannot be empty");
        String genre = InputValidation.getValidString("[Genre/Subject]:", "Field cannot be blank!");
    }
    
    public static void magazineInput(){
        String magTitle = InputValidation.getValidString("[title]:", "Title cannot be blank!");
        String publisher = InputValidation.getValidString("[Publisher]:", "publisher field cannot be blank!");
        LocalDate pubDate = InputValidation.getValidDate("[Publish Date]:", "Please ensure the input is strictly [DD-MM-YYYY]");
        Double price = InputValidation.getValidPrice();
        int issue = InputValidation.getValidIssue();
        String type = InputValidation.getValidString("[Genre]:", "Field cannot be blank!");
    }
}
