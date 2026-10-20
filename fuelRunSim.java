/*
* The main purpose of this program is to simulate the slid fuel comsbution simulaiton of a rocket. 
* The main goal of this simulation is to reduce the cost of physically testing 
*
*
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
/*import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;*/

public class fuelRunSim {// extends Application {

    // C: Load the database of prelim and final states from a CSV file
    private loadFuelSimDB db;

    public fuelRunSim() {
        db = new loadFuelSimDB();
        // db.addPrelim(new Prelim(1, 2.0, 3.0, 4.0, 5.0));
        // db.addPrelim(new Prelim(1, 2, 3, 4));
        // db.addPrelim(new Final());
    }

    // C: Run the main application loop of the program for menu input
    public static void main(String[] args) {
        // launch(args);
        fuelRunSim app = new fuelRunSim();
        app.runApp();
    }

    // C: Use JavaFX later to create a GUI for the program with beginner friendly
    // buttons and input fields for the user to interact with
    /*
     * public void start(javafx.stage.Stage primaryStage) {
     * VBox vbox = new VBox(10);
     * 
     * javafx.scene.Scene scene = new javafx.scene.Scene(vbox, 400, 300);
     * 
     * primaryStage.setScene(scene);
     * 
     * primaryStage.setTitle("Drone Run Simulation");
     * 
     * primaryStage.show();
     * }
     */

    // C: Run the main appllicaiton of the progam inccluding the menu
    public void runApp() {
        Scanner scan = new Scanner(System.in);
        loadWordsFromFile("simResults.csv");
        int choice = 0;
        do {
            choice = menu(scan);
            processChoice(scan, choice);
        } while (choice != 99);
        scan.close();
    }

    // C: Main menu function that displays the options of the menu and takes the
    // user input
    public int menu(Scanner scan) {
        while (true) {
            String message = """
                    (1) Start simulation
                    (2) See previous fuel simulation results
                    (99) Exit out of the program
                    """;

            System.out.println(message);

            try {
                String choiceStr = scan.nextLine();
                if (choiceStr.isEmpty()) {
                    System.out.println("Error: Input is empty!");
                }
                int choice = Integer.parseInt(choiceStr);
                if (choice < 1 || (choice > 2 && choice != 99)) {
                    System.out.println("Error: Invalid menu option.");
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // C: Process the user input from the menu and selects the correct option
    public void processChoice(Scanner scan, int choice) {
        switch (choice) {
            // C: This starts the fuel simulation
            case 1:
                startSim(scan);
                break;
            case 2:
                displaySimResults();
                break;
            case 99:
                exitProgram(scan);
                break;
            default:
                System.out.println("Invalid choice. Please try again");
        }
    }

    // C: Supposed to start the simulation by asking the user to input the prelim
    // values so the optimal states can be calculated
    public void startSim(Scanner scan) {
        System.out.print("Enter Simulation ID (integer): ");
        int id = Integer.parseInt(scan.nextLine());

        System.out.print("Enter Nozzle Entrance Area (m^2, e.g., 0.15): ");
        double entranceArea = Double.parseDouble(scan.nextLine());

        System.out.print("Enter Nozzle Throat Area (m^2, e.g., 0.05): ");
        double throatArea = Double.parseDouble(scan.nextLine());

        System.out.print("Enter Nozzle Exit Area (m^2, e.g., 0.2115): ");
        double exitArea = Double.parseDouble(scan.nextLine());

        StateEstimation calculator = new StateEstimation();
        // C: Function has not been made yet
        Prelim result = calculator.runSimGetResult(id, entranceArea, throatArea, exitArea);

        db.addPrelim(result);
        System.out.println("\nSimulation completed successfully and added to the database!");
    }

    public void displaySimResults() {
        System.out.println("\n--- PREVIOUS FUEL SIMULATION RESULTS ---");
        if (db.getPrelimStates().isEmpty()) {
            System.out.println("No previous simulation records found.");
            return;
        }

        for (Prelim p : db.getPrelimStates()) {
            System.out.printf(
                    "ID: %d | State 1: %.2f | State 2: %.2f | State 3: %.2f | State 4: %.2f | State 5: %.2f \n",
                    p.getId(), p.getStateOne(), p.getStateTwo(), p.getStateThree(), p.getStateFour(), p.getStateFive());
        }
        System.out.println();
    }

    public void saveInfo(Scanner scan) {
        String fileName;
        System.out.print(
                "Enter the file name you would like to save the information to (make sure to include .csv at the end): ");
        fileName = scan.nextLine();
        try {
            PrintWriter writer = new PrintWriter(fileName);
            for (Prelim p : db.getPrelimStates()) {
                writer.println(p.getId() + "," + p.getStateOne() + "," + p.getStateTwo() + "," + p.getStateThree() + ","
                        + p.getStateFour() + p.getStateFive());
            }
            writer.close();
            System.out.println("Database saved to file!");
        } catch (FileNotFoundException e) {
            System.out.println("Error saving file.");
            return;
        }
    }

    public boolean loadWordsFromFile(String filename) {
        try {
            Scanner fileHandle = new Scanner(new File(filename));

            while (fileHandle.hasNextLine()) {

                String line = fileHandle.nextLine();
                String lineTrim = line.trim();

                if (lineTrim.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 6) {
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                double stateOne = Double.parseDouble(parts[1].trim());
                double stateTwo = Double.parseDouble(parts[2].trim());
                double stateThree = Double.parseDouble(parts[3].trim());
                double stateFour = Double.parseDouble(parts[4].trim());
                double stateFive = Double.parseDouble(parts[5].trim());

                Prelim prelim = new Prelim(id, stateOne, stateTwo, stateThree, stateFour, stateFive);
                db.addPrelim(prelim);

            }

            fileHandle.close();
            return true;

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public void exitProgram(Scanner scan) {
        saveInfo(scan);
        System.out.println("This program has now concluded. All data has been uploaded.");
    }
}
