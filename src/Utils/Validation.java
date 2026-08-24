package Utils;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import Model.Reservations;
import Controller.*;

public class Validation {

    private static final Scanner sc = new Scanner(System.in);
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String EMAIL_REGEX = "^[A-Za-z0-9_+&*-]+(?:\\.[A-Za-z0-9_+&*-]+)*@" + "(?:[A-Za-z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean emailValidate(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public static double getValidPrice() {
        while (true) {
            try {
                System.out.print("[Price]:");
                double input = sc.nextDouble();
                if (input <= 0) {
                    System.out.println("Price cannot be 0$");
                    continue;
                }
            } catch (InputMismatchException e) {
                System.out.println("invalid pricing");
                sc.nextLine();
            }
        }
    }

    public static int getValidIssue() {
        while (true) {
            try {
                System.out.print("[Issue No.]:");
                return Integer.parseInt(sc.nextLine());
            } catch (InputMismatchException e) {
                System.out.println("invalid issue");
                sc.nextLine();
            }
        }
    }

    public static String getValidString(String promt, String error) {
        while (true) {
            try {
                System.out.print(promt);
                String input = sc.nextLine();
                if (!input.trim().isEmpty()) {
                    return input;
                }
            } catch (Exception e) {
                System.out.println("This field cannot be empty!");
            }
        }
    }

    public static boolean getMemberStatus() {
        while (true) {
            System.out.println("Is this customer a registered member?");
            System.out.print("[Y/N]:");
            String input = sc.nextLine();
            try {
                if (input.equalsIgnoreCase("y")||input.equalsIgnoreCase("yes")) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                System.out.println("A customer must either be a member or not!");
            }
        }
    }

    public static LocalDate getValidDate(String promt, String error) {
        while (true) {
            System.out.print("promt");
            String input = sc.nextLine().trim();
            try {

                LocalDate parsedDate = LocalDate.parse(input, DATE_FORMAT);

                if (parsedDate.isAfter(LocalDate.now())) {
                    System.out.println("This date hasn't happened yet");
                    continue;
                }
            } catch (Exception e) {
                System.out.printf(error);
            }
        }
    }

    public static String getValidMail() {
        while (true) {
            System.out.print("[Mail]:");
            String input = sc.nextLine();
            try {
                if (emailValidate(input) != false) {
                    return input;
                }
            } catch (Exception e) {
                if (input.isEmpty()) {
                    System.out.println("This field cannot be empty!");
                } else {
                    System.out.println("Invalid mail!");
                }
            }
        }
    }
    
    public static String getValidPhone(){
        while (true){
            System.out.print("[Phone Number]:");
            String input = sc.nextLine();
            try {
                if(input.length()<=10){
                    return input;
                }
            } catch (Exception e) {
                if(input.isEmpty()){
                    System.out.println("this field cannot be empty");
                } else {
                    System.out.println("invalid format");
                }
            }
        }
    }
 
    public static boolean isValidRequest(String customerId, String bookId, Map<String, Reservations> reservationMap) {
        
        // check if the customer ID exists in CustomerManager
        if (!CustomerManager.customerExists(customerId)) {
            System.out.println("Error: Customer ID does not exist in system.");
            return false;
        }

        // check if the book ID exists in BookManager
        if (!BookManager.bookExists(bookId)) {
            System.out.println("Error: Book ID does not exist in inventory.");
            return false;
        }

        // prevention for booking the same book from same customer
        for (Reservations res : reservationMap.values()) {
            if (res.getBookId().toString().equals(bookId) && 
                res.getCustomerId().toString().equals(customerId)) {

                if ("BORROWED".equals(res.getStatus())) {
                    System.out.println("Error: Customer currently holds a borrowed copy of this book.");
                    return false;
                }
                if ("WAITING".equals(res.getStatus())) {
                    System.out.println("Error: Customer is already on the waitlist for this book.");
                    return false;
                }
            }
        }

        return true; // All validation checks passed!
    }
}
