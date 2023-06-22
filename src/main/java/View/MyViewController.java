package View;

import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.fxml.FXML;

public class MyViewController implements Initializable, Observer, IView {

    public Button saveMazeButton;
    public TextField saveMazeTextArea;
    public Label saveMazeLabel;
    public Pane stackPane;
    private Main mainApp;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;
    private boolean playerWon;
    private boolean isMazeGenerated;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();
    SoundController soundController;


    MyViewModel myViewModel;
    public MyViewController(){
        soundController = SoundController.getInstance();
        myViewModel = MyViewModel.getInstance();
        myViewModel.addObserver(this);

    }

    /**
     * set the images that will showed by the mazeDisplyer
     */
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
        soundController.playBackgroundMusic();
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

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @FXML
    private void handleScroll(ScrollEvent event) {
        double deltaY = event.getDeltaY();

        // Zoom in
        if (deltaY > 0) {
            mazeDisplayer.zoomIn();
        }
        // Zoom out
        else {
            mazeDisplayer.zoomOut();
        }

        event.consume();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveMazeTextArea.setVisible(false);
        saveMazeButton.setVisible(false);
        saveMazeLabel.setVisible(false);
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }

    /**
     * ask from the viem model for generating a maze with the inserted dimensions
     * @param actionEvent
     */
    public void generateGame(ActionEvent actionEvent) {
        soundController.playChooseSound();

        String rowsStr = textField_mazeRows.getText();
        String colsStr = textField_mazeColumns.getText();

        if(containsOnlyNumbers(rowsStr) && containsOnlyNumbers(colsStr)){
            int rows = Integer.valueOf(rowsStr);
            int cols = Integer.valueOf(colsStr);

            setImages();
            int[] mazeDimensions = {rows, cols};
            myViewModel.generateSearchableGame(mazeDimensions);

            saveMazeButton.setVisible(true);
            saveMazeTextArea.setVisible(true);
            saveMazeLabel.setVisible(true);
        }
        else{
            myViewModel.makeAlert("Maze dimensions need to contain only numbers");
        }

    }

    /**
     * ask th
     * @param actionEvent
     */
    @Override
    public void solve(ActionEvent actionEvent) {
        soundController.playChooseSound();
        myViewModel.solve();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }



    private void setSolution(){
        mazeDisplayer.displaySolution(myViewModel.getSolution());
    }

    /**
     * handle the event "player moved" if got to the goal point, the player wins. then update the visual location of the character
     */
    private void playerMoved() {
        if(myViewModel.gotToGoalPoint()) {
            playerWon = true;
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

    /**
     * handle the "maze generated" event. asking the maze displayer to display the maze
     */
    private void mazeGenerated() {
        setImages();
        playerWon = false;
        isMazeGenerated = true;
        soundController.changeToGameMusic();
        mazeDisplayer.drawMaze(myViewModel.getMazeMatrix(), myViewModel.getGoalRow(), myViewModel.getGoalCol());
        playerMoved();
    }

    /**
     * handle the key press situation by calling a function from the view model
     * @param keyEvent
     */
    public void keyPressed(KeyEvent keyEvent){
        if((!isMazeGenerated) || playerWon) return;
        char event = myViewModel.keyPressed(keyEvent);
        switch (event){
            case 'R' -> {mazeDisplayer.moveScreenRight();}
            case 'L' -> {mazeDisplayer.moveScreenLeft();}
            case 'U' -> {mazeDisplayer.moveScreenUp();}
            case 'D' -> {mazeDisplayer.moveScreenDown();}
            case 'M' -> {playerMoved();}
        }

    }

    /**
     * finish the program
     * @param actionEvent
     */
    public void Exit(ActionEvent actionEvent) {
        soundController.playChooseSound();
        System.exit(0);
    }

    /**
     * Ask view model to save the maze after botten "save" pushed
     * @param actionEvent
     */
    @Override
    public void save(ActionEvent actionEvent) {
        String fileName = saveMazeTextArea.getText();
        if(containsOnlyNumbersOrLetters(fileName)) {
            myViewModel.save(fileName);
            myViewModel.makeAlert("Maze saved successfully");
        }
        else
            myViewModel.makeAlert("Maze name need to contain only numbers and letters");
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated" -> mazeGenerated();
            case "set solution" -> setSolution();
            case "file not found" -> {myViewModel.makeAlert("Maze not found"); goBack();}
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    /**
     * handle the button click of "make hint button". ask view model for hint
     * @param actionEvent
     */
    public void makeHint(ActionEvent actionEvent) {
        soundController.playChooseSound();
        myViewModel.makeHint();
    }

    /**
     * resize the maze in case the window dimensions is getting bigger
     * @param scene
     */
    public void setResize(Scene scene) {
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            //mazeDisplayer.widthProperty().bind(stackpane.widthProperty());
            mazeDisplayer.updateCanvasWidth((double)oldValue,(double)newValue);

        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            //mazeDisplayer.heightProperty().bind(stackpane.heightProperty());
            mazeDisplayer.updateCanvasHeight((double)oldValue,(double)newValue);
        });
    }

    /**
     * ask from view model to move the character because of mouse click event
     * @param event
     */
    @Override
    public void moveCharacter(Event event)
    {
        MouseEvent mouseEvent = (MouseEvent) event;
        if((!isMazeGenerated) || playerWon) return;
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        int row = (int) ((mouseY + mazeDisplayer.getScreenShiftY() *  mazeDisplayer.getCellHeight())/ mazeDisplayer.getCellHeight());
        int col = (int) ((mouseX + mazeDisplayer.getScreenShiftX() * mazeDisplayer.getCellWidth()) / mazeDisplayer.getCellWidth());

        myViewModel.updateCharacterLocationByMouse(row,col);
        playerMoved();
    }

    public static boolean containsOnlyNumbersOrLetters(String input) {
        return input.matches("[a-zA-Z0-9]+");
    }

    public static boolean containsOnlyNumbers(String input) {
        return input.matches("[0-9]+");
    }

    public void openProperties(ActionEvent actionEvent) throws IOException {
        PropertiesHelpAboutController.openedFromMazeWindow = true;
        mainApp.openPropertiesWindowScene();
    }

    public void openHelp(ActionEvent actionEvent) throws IOException {
        PropertiesHelpAboutController.openedFromMazeWindow = true;
        mainApp.openHelpWindowScene();
    }

    public void openAbout(ActionEvent actionEvent) throws IOException {
        PropertiesHelpAboutController.openedFromMazeWindow = true;
        mainApp.openAboutWindowScene();
    }
}
