package ViewModel;

import Model.MyModel;
import algorithms.mazeGenerators.Maze;

public class MyViewModel {

    public Maze generateMazeMatrix(int rows, int cols){
        int[] mazeDimensions = {rows, cols};
        MyModel myModel = new MyModel();
        Maze maze = (Maze) myModel.getBoard(mazeDimensions);
        myModel.stop();
        return maze;
    }

}
