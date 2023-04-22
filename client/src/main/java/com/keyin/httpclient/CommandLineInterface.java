package com.keyin.httpclient;

import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import org.json.simple.parser.ParseException;

/**
 * This command line interface uses a simple implementation of the Command Pattern. --> The Command Pattern is
 * a behavioral design pattern in which an object is used to represent and encapsulate all the information
 * needed to call a method at a later time. --> This pattern is used to decouple the object that invokes the
 * operation from the one that knows how to perform it.
 */
public class CommandLineInterface {
    HTTPClient httpClient = new HTTPClient();

    private Scanner scanner = new Scanner(System.in);

    // --------------------------------------------- (Main Menu) --------------------------------------------

    private Map<Integer, Command> commands = new HashMap<>();

    public CommandLineInterface() {
        // Initialize commands map
        commands.put(1, new EnterNumbersCommand()); // Sprint Questions
        commands.put(2, new ViewAllDataCommand()); // Query
    }


    // Starts the command line interface.
    public void start() {
        System.out.println("---------------------------------------");
        System.out.println("\nWelcome to the HTTP Client Application!\n         (To exit, enter 99)");
        while (true) {
            displayMainMenu();
            int choice = readIntInput();
            Command command = commands.get(choice);
            if (command != null) {
                command.execute();
            } else if (choice == 99) {
                scanner.close();
                System.out.println("\nGoodbye!");
                System.exit(0); // Exit program
            } else {
                System.out.println("\nInvalid choice.");
            }
        }
    }


    // Displays the main menu.
    private void displayMainMenu() {
        System.out.println("\n-------------- MAIN MENU --------------");
        System.out.println("1. Treeify");
        System.out.println("2. View all data");
        System.out.print("> ");
    }


    // Reads and validates user input.
    private int readIntInput() {
//        System.out.println("readIntInput()");
        while (!scanner.hasNextInt()) {
            scanner.nextLine();

            System.out.print("\nInvalid input. Please enter a number.\n");
            System.out.print("> ");
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // consume remaining newline character
        return input;
    }

    // Declares a method for executing a command.
    private interface Command {
        void execute();
    }

    private class EnterNumbersCommand implements Command {
        @Override
        public void execute() {
            System.out.print("\n--------------- ENTER NUMBERS --------------\n\n");
            System.out.println("How many numbers would you like to enter? ");

            int numberAmount;
            while (true) {
                numberAmount = readIntInput();
                if (numberAmount < 0) { // TODO: Change this? 0 may be a valid input
                    System.out.print("\nInvalid input. Please enter a valid whole number: ");
                } else {
                    break;
                }
            }

            int[] numbers = new int[numberAmount];
            for (int i = 0; i < numberAmount; i++) {
                System.out.printf("\nEnter number #%d: ", i + 1);
                while (true) {
                    int id = readIntInput();
                    if (id < 0) {
                        System.out.print("\nInvalid input. Please enter a valid whole number: ");
                    } else {
                        numbers[i] = id;
                        break;
                    }
                }
            }

            /* Showing input back to the user */
            System.out.print("You entered: ");
            for (int i = 0; i < numberAmount; i++) {
                System.out.print(numbers[i] + " ");
            }

            /* Formatting numbers into space-separated string for insertion into endpoint query parameter */
            String numbersString = "";
            for (int i = 0; i < numberAmount; i++) {
                if (i != numberAmount - 1){
                    numbersString = numbersString + numbers[i] + ",";
                }else{
                numbersString = numbersString + numbers[i];
                }
            }
            System.out.println("Numbersstring: " + numbersString);
            try {
                httpClient.runTask("http://localhost:3000/treeify?numbers=" + numbersString);
            } catch (Exception e) {
                System.out.println("    ↪ Error: " + e + "\n");
            }
        }
    }

    private class ViewAllDataCommand implements Command {
        @Override
        public void execute() {
            System.out.println("ViewAllDataCommand.execute()");
            try {
                httpClient.runTask("http://localhost:3000/treeify/logs");
            } catch (Exception e) {
                System.out.println("    ↪ Error: " + e + "\n");
            }
        }
    }


    // --------------------------------------------- Main Method --------------------------------------------

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ParseException {
        CommandLineInterface cli = new CommandLineInterface();
        cli.start();
    }
}