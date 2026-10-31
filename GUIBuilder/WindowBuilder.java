import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class WindowBuilder extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Awesome Window");

        Pane pane = new Pane();
        pane.setPrefSize(790, 392);
        pane.setStyle("-fx-background-color: #0306c8;");

        Label element1 = new Label("Welcome to the rocket simulation");
        element1.setLayoutX(280.1822814941406);
        element1.setLayoutY(35.9921875);
        element1.setPrefWidth(205.82);
        element1.setPrefHeight(17.5);
        element1.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lato.ttf"), 14.00));
        element1.setStyle("-fx-text-fill: #D9D9D9;");
        pane.getChildren().add(element1);

        Button element2 = new Button("Start");
        element2.setLayoutX(342.18);
        element2.setLayoutY(269.83);
        element2.setPrefWidth(105.82);
        element2.setPrefHeight(29.17);
        element2.setDisable(false);
        element2.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lato.ttf"), 14.00));
        element2.setStyle(
                "-fx-background-color: #2e2e2e; -fx-text-fill: #D9D9D9; -fx-border-color: #979797; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;");
        element2.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            element2.setBackground(
                    new Background(new BackgroundFill(Color.web("#232323"), new CornerRadii(4.00), null)));
        });
        element2.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            element2.setBackground(
                    new Background(new BackgroundFill(Color.web("#2e2e2e"), new CornerRadii(4.00), null)));
        });
        pane.getChildren().add(element2);

        TextField element3 = new TextField("");
        element3.setLayoutX(341.50);
        element3.setLayoutY(142.53);
        element3.setPrefWidth(105.82);
        element3.setPrefHeight(21.48);
        element3.setPromptText("Your Input!");
        element3.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lato.ttf"), 14.00));
        element3.setStyle(
                "-fx-background-color: #b64f4f; -fx-text-fill: #353535; -fx-border-color: #979797; -fx-border-width: 0px; -fx-border-radius: 2px; -fx-prompt-text-fill: #656565;");
        pane.getChildren().add(element3);

        Scene scene = new Scene(pane, 790, 392);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}