package ViewModel;

import Model.MyModel;
import Model.SavableGame;
import View.SoundController;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;

public class MyViewModel extends Observable implements IViewModel {

    private SoundController soundController;
    MyModel myModel;
    private boolean playerAskForHint;

    public Maze myMaze;

    private Solution solution;

    private ArrayList<AState> fullSolutionPathCopy;
    private ArrayList<AState> solutionPath;

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
            playerAskForHint = false;
            solution = null;
        }
        catch (ClassCastException exception){
            try {
                solution = (Solution) arg;
                solutionPath = solution.getSolutionPath();
                fullSolutionPathCopy = new ArrayList<>();
                fullSolutionPathCopy.addAll(solutionPath);
                solutionPath.clear();
                event = "set solution";
            }
            catch (ClassCastException exception2){
                try {
                    SavableGame myGame = (SavableGame) arg;
                    myMaze = (Maze) myGame.getGame();
                    int[] position = (int[]) myGame.getPosition();
                    playerRow = position[0];
                    playerCol = position[1];
                    goalRow = myMaze.getGoalPosition().getRowIndex();
                    goalCol = myMaze.getGoalPosition().getColumnIndex();
                    event = "maze generated";
                    playerAskForHint = false;
                    solution = null;
                }
                catch (ClassCastException exception3){
                    event = "file not found";
                }
            }
        }
        this.notifyObservers(event);
    }

    public void save(String name){
        int[] playerPosition = {playerRow,playerCol};
        myModel.save(new SavableGame(myMaze,playerPosition,name));
    }

    @Override
    public void solve() {
        if(myMaze == null){
            makeAlert("Generate maze first");
            return;
        }
        if(solution != null){
            playerAskForHint = false;
            this.setChanged();
            this.notifyObservers("set solution");
            return;
        }

        playerAskForHint = false;
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

    public char keyPressed(KeyEvent keyEvent) {
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

    public Solution getSolution() {
        if(playerAskForHint){
            if(solutionPath.size() == fullSolutionPathCopy.size()) return solution;

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
        return solution;
    }

    public int getGoalRow() {
        return goalRow;
    }

    public int getGoalCol() {
        return goalCol;
    }

    public void makeHint() {

        if(myMaze == null){
            makeAlert("Generate maze first");
            return;
        }
        if(solution != null){
            this.setChanged();
            this.notifyObservers("set solution");
            return;
        }

        playerAskForHint = true;
        myModel.solve(myMaze);

    }


    public void updateCharacterLocationByMouse(int row, int col) {
        boolean moreThenOneStep = Math.abs(row - playerRow) > 1 || Math.abs(col - playerCol) > 1;
        boolean playerDoesntMove = row == playerRow && col == playerCol;
        if(moreThenOneStep || playerDoesntMove) return;

        boolean diagonal = Math.abs(row - playerRow) == 1 && Math.abs(col - playerCol) == 1;
        setPlayerPosition(row, col,diagonal);
    }

    public void changeSearchProperty(String searchingAlgorithm) {
        myModel.setSearchingAlgorithm(searchingAlgorithm);
    }

    public void changeGeneratorProperty(String mazeGenerator) {
        myModel.setMazeGenerator(mazeGenerator);
    }

    public void makeAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
