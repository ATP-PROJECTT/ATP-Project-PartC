package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class MainMenuController {

    private Main mainApp;
    private SoundController soundController;

    public void setMainApp(Main mainApp) {
        soundController = SoundController.getInstance();
        this.mainApp = mainApp;
    }

    @FXML
    private void openMyViewWindow() throws IOException {
        soundController.playChooseSound();
        mainApp.openMyViewScene();
    }

    @FXML
    private void playHoverSound() {
        soundController.playHoverSound();
    }

    public void stopHoverSound(MouseEvent mouseEvent) {
        soundController.stopPlayHoverSound();
    }

    public void exit(ActionEvent actionEvent) {
        soundController.playChooseSound();
        System.exit(0);
    }
}
