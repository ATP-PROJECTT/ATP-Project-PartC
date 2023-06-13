package View;

import java.util.Arrays;

public class MazeGenerator {

    public int[][] generateRandomMaze(int rows, int cols){
        int[][] maze = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = (int) Math.round(Math.random());
            }
        }
        return maze;
    }
}
