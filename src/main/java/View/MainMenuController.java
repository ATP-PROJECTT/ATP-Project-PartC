package View;

import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    public TextField nameForSave;
    public Label saveLabel;
    public Button loadButton;
    private Main mainApp;
    private SoundController soundController;

    public void setMainApp(Main mainApp) {
        soundController = SoundController.getInstance();
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveLabel.setVisible(false);
        nameForSave.setVisible(false);
        loadButton.setVisible(false);
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

    public void loadMaze(ActionEvent actionEvent) {
        soundController.playChooseSound();
        saveLabel.setVisible(true);
        nameForSave.setVisible(true);
        loadButton.setVisible(true);
    }

    public void loadMazeByName(ActionEvent actionEvent) {
        MyViewModel.getInstance().loadMaze(nameForSave.getText());
    }
}
