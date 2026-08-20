import java.util.Scanner;

public class Genie {
    public static void main(String[] args) {
        String banner = "Hello! I'm Genie\n"
                + "What can I do for ?\n";
        System.out.println(banner);

        Scanner scanner = new Scanner(System.in);
        String userInput;
        String[] h = new String[100];
        int count = 0;

        while(true) {
            userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("bye")) {
                break;
            }

            if (userInput.equalsIgnoreCase("list")) {
                for (int i = 0; i < count; i++) {
                    System.out.println("     " + (i + 1) + ". " + h[i]);
                }
                System.out.println("\n");
                continue;
            }



            h[count] = userInput;
            count++;


            System.out.println("Genie added: " + userInput);
            System.out.println("\n");

        }

        System.out.println("Bye! See you again soon!");

        scanner.close();
    }
}
