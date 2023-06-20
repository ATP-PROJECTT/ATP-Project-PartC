package View;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HelpWindowController implements Initializable {

    public ImageView zoomImage;
    SoundController soundController;
    private Main mainApp;





    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void goBack() {
        soundController.playChooseSound();
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
}
