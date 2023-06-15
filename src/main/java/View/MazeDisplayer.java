package View;

import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MazeDisplayer extends Canvas {
    private int[][] maze;
    // player position:
    private int playerRow = 0;
    private int playerCol = 0;
    // wall and player images:
    StringProperty imageFileNameWall = new SimpleStringProperty();

    StringProperty imageFileNamePass = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();

    StringProperty imageFileNameGoal = new SimpleStringProperty();

    StringProperty imageFileNamePlayerInGoalPose = new SimpleStringProperty();

    StringProperty imageFileNameSolutionPass = new SimpleStringProperty();


    boolean playerWon;

    boolean solutionDisplayed;

    private int goalRow;

    private int goalCol;


    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public String getImageFileNamePass() {
        return imageFileNamePass.get();
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public String getImageFileNamePlayerInGoalPose() {
        return imageFileNamePlayerInGoalPose.get();
    }

    public String getImageFileNameSolutionPass() {
        return imageFileNameSolutionPass.get();
    }

    public String imageFileNameWallProperty() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNamePass(String imageFileNamePass) {
        this.imageFileNamePass.set(imageFileNamePass);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public String imageFileNamePlayerProperty() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void setImageFileNameGoal(String imageFileNamePlayer) {
        this.imageFileNameGoal.set(imageFileNamePlayer);
    }

    public void drawMaze(int[][] maze) {
        this.maze = maze;
        draw();
    }

    public void playerWin(){
        playerWon = true;
        draw();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("You Win!");
        alert.show();
    }

    private void draw() {
        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.length;
            int cols = maze[0].length;

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);

            String playerImageStr = getImageFileNamePlayer();
            if(playerWon)
                playerImageStr = getImageFileNamePlayerInGoalPose();

            drawPlayer(graphicsContext, cellHeight, cellWidth, playerImageStr);

        }
    }



    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED);

        Image wallImage = null;
        Image passImage = null;
        try{
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
            passImage = new Image(new FileInputStream(getImageFileNamePass()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall/pass image file");
        }


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * cellWidth;
                double y = i * cellHeight;
                if(maze[i][j] == 1){
                    //if it is a wall:
                    if(wallImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
                else {
                    if(passImage != null)
                        graphicsContext.drawImage(passImage, x, y, cellWidth, cellHeight);
                }

            }
        }

        try {
            Image goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
            graphicsContext.drawImage(passImage, goalRow, goalCol, cellWidth, cellHeight);
        }catch (FileNotFoundException e) {
            System.out.println("There is no Goal image file");
        }

    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth, String playerImageStr) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(playerImageStr));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if(playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    public void displaySolution(Solution mazeSolution){
        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();

        String s = "";

        for(int i = 0; i < mazeSolutionSteps.size(); ++i)
            s += (String.format("%s. %s", i, ((AState)mazeSolutionSteps.get(i)).toString()) + "\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(s);
        alert.show();

    }

    public void setGoalRow(int goalRow) {
        this.goalRow = goalRow;
    }

    public void setGoalCol(int goalCol) {
        this.goalCol = goalCol;
    }
}
