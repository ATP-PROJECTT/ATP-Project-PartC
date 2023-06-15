package View;

import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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

public class MyViewController implements Initializable, Observer {

    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    MyViewModel myViewModel;
    public MyViewController(){
        myViewModel = new MyViewModel();
        myViewModel.addObserver(this);
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
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }

    public void generateMaze(ActionEvent actionEvent) {

        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        String wallImagePath = getClass().getResource("wall.png").toExternalForm();
        wallImagePath = wallImagePath.substring("file:".length());// Remove the "file:" prefix present
        mazeDisplayer.setImageFileNameWall(wallImagePath);

        String passImagePath = getClass().getResource("pass.png").toExternalForm();
        passImagePath = passImagePath.substring("file:".length()); // Remove the "file:" prefix present
        mazeDisplayer.setImageFileNamePass(passImagePath);

        String playerImagePath = getClass().getResource("IcyTower.png").toExternalForm();
        playerImagePath = playerImagePath.substring("file:".length()); // Remove the "file:" prefix present
        mazeDisplayer.setImageFileNamePlayer(playerImagePath);

        String playerInGoalPath = getClass().getResource("playerInGoal.png").toExternalForm();
        playerInGoalPath = playerInGoalPath.substring("file:".length()); // Remove the "file:" prefix present
        mazeDisplayer.setImageFileNameGoal(playerInGoalPath);

        int[] mazeDimensions = {rows, cols};
        myViewModel.generateSearchableGame(mazeDimensions);





    }

    public void solveMaze(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();
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

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated" -> mazeGenerated();
            case "set solution" -> setSolution();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void setSolution(){
        mazeDisplayer.displaySolution(myViewModel.getSolution());
    }

    private void playerMoved() {
        if(myViewModel.gotToGoalPoint())
            mazeDisplayer.playerWin();
        else {
            int row = myViewModel.getPlayerRow();
            int col = myViewModel.getPlayerCol();
            mazeDisplayer.setPlayerPosition(row, col);
            setUpdatePlayerRow(row);
            setUpdatePlayerCol(col);
        }

    }

    private void mazeGenerated() {
        mazeDisplayer.setGoalRow(myViewModel.getGoalRow());
        mazeDisplayer.setGoalCol(myViewModel.getGoalCol());
        mazeDisplayer.drawMaze(myViewModel.getMaze());
        playerMoved();
    }

    public void keyPressed(KeyEvent keyEvent){
        myViewModel.movePlayer(keyEvent);
        playerMoved();
    }
}
