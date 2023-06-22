package ViewModel;

import Model.MyModel;
import View.SoundController;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Random;

public class MyViewModel extends Observable implements IViewModel {

    private static final Logger logger = LogManager.getLogger(MyModel.class);
    private SoundController soundController;
    MyModel myModel;

    private ArrayList<AState> fullSolutionPathCopy;
    private ArrayList<AState> solutionPath;

    private boolean playerAskForHint;



    private static MyViewModel myViewModel;




    private MyViewModel(){
        myModel = new MyModel();
        myModel.addViewModel(this);
        soundController = SoundController.getInstance();
    }

    public static MyViewModel getInstance(){
        if(myViewModel == null)
            myViewModel = new MyViewModel();
        return myViewModel;
    }

    @Override
    public void generateSearchableGame(Object mazeDimensions){
        myModel.requestGame(mazeDimensions);
    }


    @Override
    public void update(Observable o, Object arg) {
        if(Objects.equals(arg, "set solution")){
            Solution solution = myModel.getSolution();
            solutionPath = solution.getSolutionPath();
            fullSolutionPathCopy = new ArrayList<>();
            fullSolutionPathCopy.addAll(solutionPath);
            solutionPath.clear();
        }

        this.setChanged();
        this.notifyObservers(arg);
    }

    @Override
    public void save(String name){
        myModel.save(name);
    }

    /**
     * solving the current maze, if exist.
     */
    @Override
    public void solve() {
        if(myModel.getGame() == null){
            makeAlert("Generate maze first");
            return;
        }
        if(myModel.getSolution() != null){
            playerAskForHint = false;
            this.setChanged();
            this.notifyObservers("set solution");
            return;
        }

        playerAskForHint = false;
        myModel.solve();
    }


    public int getPlayerRow() {
        return myModel.getPlayerRow();
    }


    public int getPlayerCol() {
        return myModel.getPlayerCol();
    }

    /**
     * setting the player's position. check if the step is vallid before
     * @param row
     * @param col
     * @param diagonalMove
     */
    private void setPlayerPosition(int row, int col, boolean diagonalMove){
        if( row >= 0 && col >=0 && row < myModel.getMazeRows() && col < myModel.getMazeCols() && myModel.isTherePassHere(row,col)){
            if(diagonalMove){
                if(!(myModel.isTherePassHere(myModel.getPlayerRow(), col) || myModel.isTherePassHere(row, myModel.getPlayerCol()))) // cant move diagonal
                    return;
                soundController.playDiagonalMoveSound();
            }
            else
                soundController.playRegularMoveSound();
            myModel.setPlayerRow(row);
            myModel.setPlayerCol(col);
        }
    }

    @Override
    public void load(String gameName){
        myModel.load(gameName);
    }

    public boolean gotToGoalPoint(){
        return getGoalCol() == getPlayerCol() && getGoalRow() == getPlayerRow();
    }

    public int[][] getMazeMatrix() {
        return ((Maze)myModel.getGame()).getMyMatrix();
    }

    /**
     * handle the key pres event in the maze
     * @param keyEvent
     * @return
     */
    public char keyPressed(KeyEvent keyEvent) {
        int row = getPlayerRow();
        int col = getPlayerCol();
        boolean diagonalMove = false;

        switch (keyEvent.getCode()) {
            case NUMPAD8 -> row -= 1;
            case NUMPAD2 -> row += 1;
            case NUMPAD6 -> col += 1;
            case NUMPAD4 -> col -= 1;
            case NUMPAD9 -> {row -= 1; col += 1; diagonalMove = true;}
            case NUMPAD7 -> {row -= 1; col -= 1; diagonalMove = true;}
            case NUMPAD1 -> {row += 1; col -= 1; diagonalMove = true;}
            case NUMPAD3 -> {row += 1; col += 1; diagonalMove = true;}
            case D -> {return  'R';}
            case A -> {return  'L';}
            case W -> {return  'U';}
            case S -> {return 'D';}
            default -> {
                return 'E';
            }

        }
        setPlayerPosition(row, col, diagonalMove);

        keyEvent.consume();
        return 'M';
    }

    /**
     * if the user ask for hint, we give him some positions from the given solution. If he ask for full solution we give the user a full solution
     * @return
     */
    public Solution getSolution() {
        if(playerAskForHint){
            if(solutionPath.size() == fullSolutionPathCopy.size()) return myModel.getSolution();

            Random random = new Random();
            int randomIndex = random.nextInt(fullSolutionPathCopy.size());
            AState aState = fullSolutionPathCopy.get(randomIndex);
            while (solutionPath.contains(aState)){
                randomIndex = random.nextInt(fullSolutionPathCopy.size());
                aState = fullSolutionPathCopy.get(randomIndex);
            }
            solutionPath.add(aState);
        }
        else {
            solutionPath.clear();
            solutionPath.addAll(fullSolutionPathCopy);
        }
        return myModel.getSolution();
    }

    public int getGoalRow() {
        return myModel.getGoalRow();
    }

    public int getGoalCol() {
        return myModel.getGoalCol();
    }

    /**
     * ask for a solution from the model if doesnt exist already. the observers will notify by hint
     */
    public void makeHint() {

        if(myModel.getGame() == null){
            makeAlert("Generate maze first");
            return;
        }
        if(myModel.getSolution() != null){
            this.setChanged();
            this.notifyObservers("set solution");
            return;
        }

        playerAskForHint = true;
        myModel.solve();

    }


    /**
     * getting a mouse location and set the character position if the step is valid
     * @param row
     * @param col
     */
    public void updateCharacterLocationByMouse(int row, int col) {
        boolean moreThenOneStep = Math.abs(row - getPlayerRow()) > 1 || Math.abs(col - getPlayerCol()) > 1;
        boolean playerDoesntMove = row == getPlayerRow() && col == getPlayerCol();
        if(moreThenOneStep || playerDoesntMove) return;

        boolean diagonal = Math.abs(row - getPlayerRow()) == 1 && Math.abs(col - getPlayerCol()) == 1;
        setPlayerPosition(row, col,diagonal);
    }

    public void changeSearchProperty(String searchingAlgorithm) {
        myModel.setSearchingAlgorithm(searchingAlgorithm);
    }

    public void changeGeneratorProperty(String mazeGenerator) {
        myModel.setMazeGenerator(mazeGenerator);
    }

    /**
     * helper functio. get message and show an alert, and write the message into the logger
     * @param message
     */
    public void makeAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
        logger.info(message);
    }
}
