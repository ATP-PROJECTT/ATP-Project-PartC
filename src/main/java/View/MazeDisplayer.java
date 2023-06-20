package View;

import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
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

    private int goalRow;

    private int goalCol;

    private double zoomFactor = 1;

    private int screenShiftX = 0;
    private int screenShiftY = 0;


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

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
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



    public String imageFileNamePlayerProperty() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void setImageFileNameGoal(String imageFileNamePlayer) {
        this.imageFileNameGoal.set(imageFileNamePlayer);
    }

    public void setImageFileNamePlayerInGoalPose(String imageFileNamePlayer) {
        this.imageFileNamePlayerInGoalPose.set(imageFileNamePlayer);
    }

    public void drawMaze(int[][] maze, int goalRow, int goalCol) {
        this.maze = deepCopyMaze(maze);
        this.goalRow = goalRow;
        this.goalCol = goalCol;
        this.maze[goalRow][goalCol] = 2;
        zoomFactor = 1;
        screenShiftX = 0;
        screenShiftY = 0;
        draw();
    }

    public void playerWin(){
        setPlayerPosition(this.goalRow,this.goalCol);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("You Win!");
        alert.show();
    }

    private void draw() {
        if(maze != null){
            double canvasHeight = getHeight() * zoomFactor; // Apply zoomFactor
            double canvasWidth = getWidth() * zoomFactor; // Apply zoomFactor
            int rows = maze.length;
            int cols = maze[0].length;

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);

            String playerImageStr = getImageFileNamePlayer();
            if(maze[playerRow][playerCol] == 2)
                playerImageStr = getImageFileNamePlayerInGoalPose();

            drawPlayer(graphicsContext, cellHeight, cellWidth, playerImageStr);

        }
    }



    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED);

        Image wallImage = null;
        Image passImage = null;
        Image solutionImage = null;
        try{
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
            passImage = new Image(new FileInputStream(getImageFileNamePass()));
            solutionImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall/pass/goal image file");
        }


        for (int i = 0; i < rows - screenShiftY; i++) {
            for (int j = 0; j < cols - screenShiftX; j++) {
                double x = j * cellWidth;
                double y = i * cellHeight;
                switch (maze[i+screenShiftY][j+screenShiftX]){

                    case 0:
                        if(passImage != null)
                            graphicsContext.drawImage(passImage, x, y, cellWidth, cellHeight);
                        break;

                    case 1:
                        //if it is a wall:
                        if(wallImage == null)
                            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                        else
                            graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                        break;

                    case 2:
                        if(solutionImage != null)
                            graphicsContext.drawImage(solutionImage, x, y, cellWidth, cellHeight);

                }
            }
        }

    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth, String playerImageStr) {
        int playerPosAfterShiftX = playerCol - screenShiftX;
        int playerPosAfterShiftY = playerRow - screenShiftY;
        if(playerPosAfterShiftX < 0 || playerPosAfterShiftY < 0)
            return;
        double x = playerPosAfterShiftX * cellWidth;
        double y = playerPosAfterShiftY * cellHeight;
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

        Position solutionPosition;
        for(int i = 0; i < mazeSolutionSteps.size(); ++i) {
            solutionPosition = ((MazeState)mazeSolutionSteps.get(i)).getPosition();
            maze[solutionPosition.getRowIndex()][solutionPosition.getColumnIndex()] = 2;
        }
        draw();
    }

    private int[][] deepCopyMaze(int[][] originalMaze) {

        int[][] copy = new int[originalMaze.length][];

        for (int i = 0; i < originalMaze.length; i++) {
            copy[i] = new int[originalMaze[i].length];
            for (int j = 0; j < originalMaze[i].length; j++) {
                copy[i][j] = originalMaze[i][j];
            }
        }

        return copy;
    }


    public void zoomIn() {
        zoomFactor *= 1.1;
        draw();
    }

    public void zoomOut() {
        if (zoomFactor/1.1 < 1)
            zoomFactor = 1;
        else
            zoomFactor /= 1.1;
        draw();
    }

    public void moveScreenDown(){
        screenShiftY ++;
        if(screenShiftY == maze[0].length)
            screenShiftY--;
        draw();
    }

    public void moveScreenUp(){
        screenShiftY --;
        if(screenShiftY < 0)
            screenShiftY++;
        draw();
    }

    public void moveScreenRight(){
        screenShiftX ++;
        if(screenShiftX == maze.length)
            screenShiftX--;
        draw();
    }

    public void moveScreenLeft(){
        screenShiftX --;
        if(screenShiftX < 0)
            screenShiftX++;
        draw();
    }
}
