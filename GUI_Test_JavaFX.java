import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class GUI_Test_JavaFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Label titleLabel = new Label("Welcome to the Solild Fuel Combustion Simulation!");
        Button startButton = new Button("Continue");

        VBox vbox = new VBox(10, titleLabel, startButton);

        vbox.setStyle("-fx-background-color: lightblue; -fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(vbox, 400, 300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Start Page Simulation");
        startButton.setOnAction(e -> menuStage(primaryStage));
        primaryStage.show();
    }

    public void menuStage(Stage secondStage) {

        Button startSimButton = new Button("1: Start Simulation");
        Button viewResultButton = new Button("2: View Previous Results");
        Button ExitButton = new Button("3: End Simulation Program");

        VBox menuOptions = new VBox(10, startSimButton, viewResultButton, ExitButton);

        menuOptions.setStyle("-fx-background-color: lightblue; -fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(menuOptions, 400, 300);

        secondStage.setScene(scene);
        secondStage.setTitle("Second Page Simulation");

        viewResultButton.setOnAction(e -> resultStage(secondStage));
        ExitButton.setOnAction(e -> secondStage.close());

        secondStage.show();

    }

    public void resultStage(Stage thirdStage) {

        Button saveResultNewFile = new Button("Save results in a new file?");
        Button returnButton = new Button("Back");

        VBox menuOptions = new VBox(10, returnButton, saveResultNewFile);

        menuOptions.setStyle("-fx-background-color: lightblue; -fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(menuOptions, 400, 300);

        thirdStage.setScene(scene);
        thirdStage.setTitle("Third Page Simulation");

        returnButton.setOnAction(e -> menuStage(thirdStage));

        thirdStage.show();

    }
}