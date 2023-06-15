package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import ViewModel.IViewModel;
import algorithms.mazeGenerators.Maze;
import Server.*;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.ISearchable;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {

    Server mazeGeneratingServer;
    Server solveSearchProblemServer;
    Maze curMaze;
    int[] curMazeDimensions;

    public MyModel(){
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }
    @Override
    public void requestBoard(Object rowsAndColsArray) {

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400,  new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        int[] mazeDimensions = curMazeDimensions;
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[ mazeDimensions[0] * mazeDimensions[1] + 32 ];
                        is.read(decompressedMaze);
                        notifyViewModel(new Maze(decompressedMaze));

                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                }
            });
            curMazeDimensions = (int[]) rowsAndColsArray;
            client.communicateWithServer();
        } catch (UnknownHostException var1) {

        }
    }

    @Override
    public void addViewModel(IViewModel viewModel) {
        this.addObserver(viewModel);
    }

    @Override
    public void notifyViewModel(Object o) {
        this.setChanged();
        notifyObservers(o);
    }

    @Override
    public void solve(Object mazeObject) {
        Maze maze = (Maze) mazeObject;
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution)fromServer.readObject();
                        notifyViewModel(mazeSolution);
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
    }

    public void stop(){
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }



}
