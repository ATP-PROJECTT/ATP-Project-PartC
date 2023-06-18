package ViewModel;

import Model.MyModel;
import Model.SavableGame;
import View.Main;
import View.SoundController;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;

public class MyViewModel extends Observable implements IViewModel {

    private SoundController soundController;
    MyModel myModel;

    public Maze myMaze;

    public Solution solution;

    private int playerRow;
    private int playerCol;
    private int goalRow;

    private int goalCol;

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

    public void generateSearchableGame(Object mazeDimensions){
        myModel.requestBoard(mazeDimensions);
    }


    @Override
    public void update(Observable o, Object arg) {
        this.setChanged();
        String event = "";
        try {
            myMaze = (Maze) arg;
            event = "maze generated";
            playerRow = myMaze.getStartPosition().getRowIndex();
            playerCol = myMaze.getStartPosition().getColumnIndex();
            goalRow = myMaze.getGoalPosition().getRowIndex();
            goalCol = myMaze.getGoalPosition().getColumnIndex();
        }
        catch (ClassCastException exception){
            solution = (Solution) arg;
            event = "set solution";
        }
        this.notifyObservers(event);
    }

    public void save(String name){
        int[] playerPosition = {playerRow,playerCol};
        myModel.save(new SavableGame(myMaze,playerPosition,name));
    }

    @Override
    public void solve() {
        if(myMaze != null)
            myModel.solve(myMaze);
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    private void setPlayerPosition(int row, int col, boolean diagonalMove){
        if( row >= 0 && col >=0 && row < myMaze.getRows() && col < myMaze.getColumns() && myMaze.isTherePassHere(row,col)){
            if(diagonalMove){
                if(!(myMaze.isTherePassHere(playerRow, col) || myMaze.isTherePassHere(row, playerCol))) // cant move diagonal
                    return;
                soundController.playDiagonalMoveSound();
            }
            else
                soundController.playRegularMoveSound();
            playerRow = row;
            playerCol = col;
        }
    }

    public void loadMaze(String gameName){
        myModel.load(gameName);
    }

    public boolean gotToGoalPoint(){
        return goalCol == playerCol && goalRow == playerRow;
    }

    public int[][] getMaze() {
        return myMaze.getMyMatrix();
    }

    public void movePlayer(KeyEvent keyEvent) {
        int row = playerRow;
        int col = playerCol;
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
        }
        setPlayerPosition(row, col, diagonalMove);

        keyEvent.consume();
    }

    public Solution getSolution() {
        return solution;
    }

    public int getGoalRow() {
        return goalRow;
    }

    public int getGoalCol() {
        return goalCol;
    }
}
