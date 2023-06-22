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
    private Scene mazeWindow;

    private Scene helpWindowScene;

    private Scene AboutWindowScene;

    private Scene propertiesWindowScene;

    private SoundController soundController;

    private PropertiesHelpAboutController propertiesHelpAboutController;




    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        // Create the main menu scene
        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("fxmlAndCSSFiles/MyView2.fxml"));
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
        FXMLLoader helpWindowLoader = new FXMLLoader(getClass().getResource("fxmlAndCSSFiles/HelpWindow.fxml"));
        Parent myViewRoot = helpWindowLoader.load();
        propertiesHelpAboutController = helpWindowLoader.getController();
        propertiesHelpAboutController.setMainApp(this);

        helpWindowScene = new Scene(myViewRoot, 800, 750);

        primaryStage.setScene(helpWindowScene);
        primaryStage.setTitle("Help Window");
    }

    public void openAboutWindowScene() throws IOException {
        FXMLLoader aboutWindowLoader = new FXMLLoader(getClass().getResource("fxmlAndCSSFiles/AboutWindow.fxml"));
        Parent myViewRoot = aboutWindowLoader.load();

        propertiesHelpAboutController = aboutWindowLoader.getController();
        propertiesHelpAboutController.setMainApp(this);

        AboutWindowScene = new Scene(myViewRoot, 800, 750);

        primaryStage.setScene(AboutWindowScene);
        primaryStage.setTitle("About Window");
    }


    public void openMyViewScene() throws IOException {
        FXMLLoader myViewLoader = new FXMLLoader(getClass().getResource("fxmlAndCSSFiles/MyView.fxml"));
        Parent myViewRoot = myViewLoader.load();
        MyViewController myViewController = myViewLoader.getController();
        myViewController.setMainApp(this);

        mazeWindow = new Scene(myViewRoot, 900, 750);

        primaryStage.setScene(mazeWindow);
        primaryStage.setTitle("Maze Window");
        myViewController.setResize(mazeWindow);
    }

    public void goBackToMainMenu() {
        primaryStage.setScene(mainMenuScene);
        primaryStage.setTitle("Main Menu");
    }



    public static void main(String[] args) {
        launch(args);
    }

    public void openPropertiesWindowScene() throws IOException {
        FXMLLoader aboutWindowLoader = new FXMLLoader(getClass().getResource("fxmlAndCSSFiles/PropertiesWindow.fxml"));
        Parent myViewRoot = aboutWindowLoader.load();

        propertiesHelpAboutController = aboutWindowLoader.getController();
        propertiesHelpAboutController.setMainApp(this);

        propertiesWindowScene = new Scene(myViewRoot, 800, 750);

        primaryStage.setScene(propertiesWindowScene);
        primaryStage.setTitle("Properties Window");
    }

    public void openMazeWindowFromWhileGameRunning(){
        primaryStage.setScene(mazeWindow);
        primaryStage.setTitle("Maze Window");
    }
}
