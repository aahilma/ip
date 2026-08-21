import java.util.Scanner;

public class Genie {
    public static void main(String[] args) {

        String divider = "____________________________________________________________\n";
        String banner = "Hello! I'm Genie\n"
                + "What can I do for ?\n";
        System.out.println(banner);

        Scanner scanner = new Scanner(System.in);
        String userInput;
        String[] h = new String[100];
        boolean[] comp = new boolean[100];
        int count = 0;

        while(true) {
            userInput = scanner.nextLine();
            String[] commands = userInput.split(" ");

            System.out.println(divider);

            if (userInput.equalsIgnoreCase("bye")) {
                break;
            }

            if (userInput.equalsIgnoreCase("list")) {
                System.out.println("Here are your tasks:\n");
                for (int i = 0; i < count; i++) {
                    String line = "     " + (i + 1) + ". ";
                    if (comp[i]) {
                        line += "[ X ] ";
                    }
                    else {
                        line += "[ ] ";
                    }
                    line += h[i];
                    System.out.println(line);
                }
                System.out.println("\n");
                System.out.println(divider);
                continue;
            }

            if (commands[0].equals("mark")) {
                comp[Integer.parseInt(commands[1]) - 1] = true;
                String txt = "Nice! I have marked the following as done: \n";
                System.out.println(txt);
                String txt2 = "[ X ] ";
                txt2 += h[Integer.parseInt(commands[1]) - 1];
                System.out.println(txt2);
                System.out.println(divider);
                continue;
            }

            if (commands[0].equals("unmark")) {
                comp[Integer.parseInt(commands[1]) - 1] = false;
                String txt = "Nice! I have unmarked the following: \n";
                System.out.println(txt);
                String txt2 = "[  ] ";
                txt2 += h[Integer.parseInt(commands[1]) - 1];
                System.out.println(txt2);
                System.out.println(divider);
                continue;
            }



            h[count] = userInput;
            count++;


            System.out.println("Genie added: " + userInput);
            System.out.println(divider);


        }

        System.out.println("Bye! See you again soon!");

        scanner.close();
    }
}
