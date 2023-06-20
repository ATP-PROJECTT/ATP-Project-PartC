package View;

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

    private Scene helpWindowScene;

    private Scene AboutWindowScene;

    private SoundController soundController;




    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        // Create the main menu scene
        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("MyView2.fxml"));
        Parent mainMenuRoot = mainMenuLoader.load();
        MainMenuController mainMenuController = mainMenuLoader.getController();
        mainMenuController.setMainApp(this);

        mainMenuScene = new Scene(mainMenuRoot, 900, 750);

        soundController = SoundController.getInstance();
        primaryStage.setScene(mainMenuScene);
        primaryStage.setTitle("Main Menu");
        primaryStage.show();

    }

    public void openHelpWindowScene() throws IOException {
        FXMLLoader helpWindowLoader = new FXMLLoader(getClass().getResource("HelpWindow.fxml"));
        Parent myViewRoot = helpWindowLoader.load();
        HelpAboutWindowController helpAboutWindowController = helpWindowLoader.getController();
        helpAboutWindowController.setMainApp(this);

        helpWindowScene = new Scene(myViewRoot, 800, 750);

        primaryStage.setScene(helpWindowScene);
        primaryStage.setTitle("Help Window");
    }

    public void openAboutWindowScene() throws IOException {
        FXMLLoader aboutWindowLoader = new FXMLLoader(getClass().getResource("AboutWindow.fxml"));
        Parent myViewRoot = aboutWindowLoader.load();

        HelpAboutWindowController helpAboutWindowController = aboutWindowLoader.getController();
        helpAboutWindowController.setMainApp(this);

        AboutWindowScene = new Scene(myViewRoot, 800, 750);

        primaryStage.setScene(AboutWindowScene);
        primaryStage.setTitle("About Window");
    }


    public void openMyViewScene() throws IOException {
        FXMLLoader myViewLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent myViewRoot = myViewLoader.load();
        MyViewController myViewController = myViewLoader.getController();
        myViewController.setMainApp(this);

        myViewScene = new Scene(myViewRoot, 900, 750);

        primaryStage.setScene(myViewScene);
        primaryStage.setTitle("My View");
        myViewController.setResize(myViewScene);
    }

    public void goBackToMainMenu() {
        primaryStage.setScene(mainMenuScene);
        primaryStage.setTitle("Main Menu");
    }



    public static void main(String[] args) {
        launch(args);
    }
}
