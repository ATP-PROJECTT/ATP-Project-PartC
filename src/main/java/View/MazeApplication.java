package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MazeApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MyView.fxml"));
            primaryStage.setTitle("Hello World");
            primaryStage.setScene(new Scene(root, 1000, 700));
            primaryStage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(getClass().getResource("MyView.fxml"));
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
