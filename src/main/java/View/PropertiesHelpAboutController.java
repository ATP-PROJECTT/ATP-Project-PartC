package View;

import ViewModel.MyViewModel;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class PropertiesHelpAboutController implements Initializable {

    public ImageView zoomImage;
    public ComboBox comboMazeGenerator;
    public ComboBox comboMazeSolver;
    public Button ApplyGenerator;
    public Button ApplySearch;
    SoundController soundController;
    private Main mainApp;

    public static boolean openedFromMazeWindow;





    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void goBack() {
        soundController.playChooseSound();
        if(openedFromMazeWindow)
            openMazeWindow();
        else
            mainApp.goBackToMainMenu();
    }
    @FXML
    private void Hover() {
        soundController.playHoverSound();
    }

    public void stopHoverSound() {
        soundController.stopPlayHoverSound();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        soundController = SoundController.getInstance();
    }


    public void mouseOnImage(MouseEvent mouseEvent) {
        Hover();
        ImageView imageView = (ImageView) mouseEvent.getSource();
        ScaleTransition scaleInTransition = new ScaleTransition(Duration.millis(200), imageView);
        scaleInTransition.setByX(1.3);
        scaleInTransition.setByY(1.3);
        scaleInTransition.play();

    }

    public void mouseExitFromImage(MouseEvent mouseEvent) {
        stopHoverSound();
        ImageView imageView = (ImageView) mouseEvent.getSource();
        ScaleTransition scaleOutTransition = new ScaleTransition(Duration.millis(200), imageView);
        scaleOutTransition.setByX(-1.3);
        scaleOutTransition.setByY(-1.3);
        scaleOutTransition.play();

    }

    public void changeSearchProperty(ActionEvent actionEvent) {
        String searchingAlgorithm = (String) comboMazeSolver.getValue();
        if(searchingAlgorithm != null) {
            MyViewModel.getInstance().changeSearchProperty(searchingAlgorithm);
            MyViewModel.getInstance().makeAlert("Property changed successfully");
        }
        else
            MyViewModel.getInstance().makeAlert("Choose searching algorithm before");

    }

    public void changeGeneratorProperty(ActionEvent actionEvent) {
        String mazeGenerator = (String) comboMazeGenerator.getValue();
        if(mazeGenerator != null) {

            MyViewModel.getInstance().changeGeneratorProperty(mazeGenerator);
            MyViewModel.getInstance().makeAlert("Property changed successfully");
        }
        else
            MyViewModel.getInstance().makeAlert("Choose Maze Generator before");

    }

    public void openMazeWindow(){
        openedFromMazeWindow = false;
        mainApp.openMazeWindowFromWhileGameRunning();
    }


}
