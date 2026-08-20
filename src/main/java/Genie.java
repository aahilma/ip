import java.util.Scanner;

public class Genie {
    public static void main(String[] args) {
        String banner = "Hello! I'm Genie\n"
                + "What can I do for ?\n";
        System.out.println(banner);

        Scanner scanner = new Scanner(System.in);
        String userInput;

        while(true) {
            userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("bye")) {
                break;
            }


            System.out.println("Genie says: " + userInput);
            System.out.println("\n");

        }

        System.out.println("Bye! See you again soon!");

        scanner.close();
    }
}
