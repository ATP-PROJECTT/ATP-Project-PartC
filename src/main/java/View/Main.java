package View;

import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Application;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private Scene mainMenuScene;
    private Scene myViewScene;




    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        // Create the main menu scene
        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("main_menu.fxml"));
        Parent mainMenuRoot = mainMenuLoader.load();
        MainMenuController mainMenuController = mainMenuLoader.getController();
        mainMenuController.setMainApp(this);

        mainMenuScene = new Scene(mainMenuRoot, 900, 800);

        SoundController.getInstance();
        primaryStage.setScene(mainMenuScene);
        primaryStage.setTitle("Main Menu");
        primaryStage.show();

    }




    public void openMyViewScene() throws IOException {
        FXMLLoader myViewLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent myViewRoot = myViewLoader.load();
        MyViewController myViewController = myViewLoader.getController();
        myViewController.setMainApp(this);

        myViewScene = new Scene(myViewRoot, 900, 800);

        primaryStage.setScene(myViewScene);
        primaryStage.setTitle("My View");
    }

    public void goBackToMainMenu() {
        primaryStage.setScene(mainMenuScene);
        primaryStage.setTitle("Main Menu");
    }



    public static void main(String[] args) {
        launch(args);
    }
}
