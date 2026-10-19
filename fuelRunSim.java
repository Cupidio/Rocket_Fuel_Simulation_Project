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
import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class fuelRunSim extends Application{

    //C: Load the database of prelim and final states from a CSV file
    private loadDroneDB db;

    public fuelRunSim() {
        db = new loadDroneDB();

        db.addPrelim(new Prelim(1, 2, 3, 4));
        db.addPrelim(new Final());
    }

    //C: Run the main application loop of the program for menu input
    public static void main(String[] args) {
        launch(args);
        fuelRunSim app = new fuelRunSim();
        app.runApp();
    }

    //C: Use JavaFX later to create a GUI for the program with beginner friendly buttons and input fields for the user to interact with
    /*public void start(javafx.stage.Stage primaryStage) {
        VBox vbox = new VBox(10);

        javafx.scene.Scene scene = new javafx.scene.Scene(vbox, 400, 300);

        primaryStage.setScene(scene);

        primaryStage.setTitle("Drone Run Simulation");

        primaryStage.show();
    }*/

    //C: Run the main appllicaiton of the progam inccluding the menu
    public void runApp() {
        Scanner scan = new Scanner(System.in);
        loadWordsFromFile("families.csv");
        int choice = 0;
        do {
            choice = menu(scan);
            processChoice(scan, choice);
        } while (choice != 99);
        scan.close();
    }

    //C: Main menu function that displays the options of the menu and takes the user input
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

    //C: Process the user input from the menu and selects the correct option
    public void processChoice(Scanner scan, int choice) {
        switch (choice) {
            //C: This starts the fuel simulation
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

    public void saveInfo(Scanner scan) {
        String fileName;
        System.out.print(
                "Enter the file name you would like to save the information to (make sure to include .csv at the end): ");
        fileName = scan.nextLine();
        try {
            PrintWriter writer = new PrintWriter(fileName);
            for (Family f : db.getFams()) {
                String species = f.getClass().getSimpleName();
                writer.println(species + "," + f.getFamName() + "," + f.getOrigPlanet());
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

                String prelim = parts[0];
                String name = parts[1];
                String origin = parts[2];

                Family fam = null;

                if (species.equalsIgnoreCase("Nkisi")) {
                    fam = new Nkisi(name, origin);
                } else if (species.equalsIgnoreCase("Neohumans")) {
                    fam = new Neohumans(name, origin);
                } else if (species.equalsIgnoreCase("Saints")) {
                    fam = new Saints(name, origin);
                } else if (species.equalsIgnoreCase("Ethirans")) {
                    fam = new Ethirans(name, origin);
                }

                if (fam != null) {
                    db.addFamily(fam);
                } else {
                    System.out.println("Unknown species in file: " + species);
                }

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

    @Override
    public void start(Stage arg0) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }
}
