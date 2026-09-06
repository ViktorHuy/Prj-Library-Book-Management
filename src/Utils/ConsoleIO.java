
package Utils;


public class ConsoleIO {
    // helper function that help set a range over how many choice each menu have
    public static int readMenuChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = Validation.sc.nextLine().trim();
            try {
                int choice = Integer.parseInt(raw);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
 
    // reads one line of raw text with a prompt.
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return Validation.sc.nextLine().trim();
    }
 
    // simple pause so result screens don't get swallowed by the next menu print.
    public static void pause() {
        System.out.printf("\nPress ENTER to continue...");
        Validation.sc.nextLine();
    }
}
