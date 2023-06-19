package View;

import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.fxml.FXML;

public class MyViewController implements Initializable, Observer {

    public Button saveMazeButton;
    public TextField saveMazeTextArea;
    public Label saveMazeLabel;
    private Main mainApp;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();
    SoundController soundController;

    MyViewModel myViewModel;
    public MyViewController(){
        soundController = SoundController.getInstance();
        myViewModel = MyViewModel.getInstance();
        myViewModel.addObserver(this);

    }

    public void setImages(){
        String wallImagePath = getClass().getResource("Images/wall.png").toExternalForm();
        wallImagePath = wallImagePath.substring("file:".length());// Remove the "file:" prefix present
        mazeDisplayer.setImageFileNameWall(wallImagePath);

        String passImagePath = getClass().getResource("Images/pass.png").toExternalForm();
        passImagePath = passImagePath.substring("file:".length()); // Remove the "file:" prefix present
        mazeDisplayer.setImageFileNamePass(passImagePath);

        String playerImagePath = getClass().getResource("Images/IcyTower.png").toExternalForm();
        playerImagePath = playerImagePath.substring("file:".length()); // Remove the "file:" prefix present
        mazeDisplayer.setImageFileNamePlayer(playerImagePath);

        String playerInGoalPath = getClass().getResource("Images/playerInSolutionPath.png").toExternalForm();
        playerInGoalPath = playerInGoalPath.substring("file:".length()); // Remove the "file:" prefix present
        mazeDisplayer.setImageFileNamePlayerInGoalPose(playerInGoalPath);

        String solutionPassPath = getClass().getResource("Images/solutionPass.png").toExternalForm();
        solutionPassPath = solutionPassPath.substring("file:".length()); // Remove the "file:" prefix present
        mazeDisplayer.setImageFileNameGoal(solutionPassPath);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void goBack() {
        soundController.playChooseSound();
        mainApp.goBackToMainMenu();
    }

    @FXML
    private void playHoverSound() {
        soundController.playHoverSound();
    }

    public void stopHoverSound(MouseEvent mouseEvent) {
        soundController.stopPlayHoverSound();
    }
    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveMazeTextArea.setVisible(false);
        saveMazeButton.setVisible(false);
        saveMazeLabel.setVisible(false);
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }

    public void generateMaze(ActionEvent actionEvent) {
        soundController.playChooseSound();

        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        setImages();
        int[] mazeDimensions = {rows, cols};
        myViewModel.generateSearchableGame(mazeDimensions);

        saveMazeButton.setVisible(true);
        saveMazeTextArea.setVisible(true);
        saveMazeLabel.setVisible(true);

        soundController.changeToGameMusic();
    }

    public void solveMaze(ActionEvent actionEvent) {
        soundController.playChooseSound();
        myViewModel.solve();
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        //...
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }



    private void setSolution(){
        mazeDisplayer.displaySolution(myViewModel.getSolution());
    }

    private void playerMoved() {
        if(myViewModel.gotToGoalPoint()) {
            mazeDisplayer.playerWin();
            soundController.playWinSound();
        }
        else {
            int row = myViewModel.getPlayerRow();
            int col = myViewModel.getPlayerCol();
            mazeDisplayer.setPlayerPosition(row, col);
            setUpdatePlayerRow(row);
            setUpdatePlayerCol(col);
        }

    }

    private void mazeGenerated() {
        setImages();
        mazeDisplayer.drawMaze(myViewModel.getMaze(), myViewModel.getGoalRow(), myViewModel.getGoalCol());
        playerMoved();
    }

    public void keyPressed(KeyEvent keyEvent){
        myViewModel.movePlayer(keyEvent);
        playerMoved();
    }

    public void Exit(ActionEvent actionEvent) {
        soundController.playChooseSound();
        System.exit(0);
    }

    public void save(ActionEvent actionEvent) {
        myViewModel.save(saveMazeTextArea.getText());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Maze saved successfully");
        alert.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated" -> mazeGenerated();
            case "set solution" -> setSolution();
            default -> System.out.println("Not implemented change: " + change);
        }
    }
}
