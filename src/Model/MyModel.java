package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import Server.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyModel implements IModel, IClientStrategy {

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
    public Object getBoard(Object rowsAndColsArray) {

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, this);
            curMazeDimensions = (int[]) rowsAndColsArray;
            client.communicateWithServer();
            return curMaze;
        } catch (UnknownHostException var1) {
            return null;
        }
    }

    public void stop(){
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }
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
            curMaze = new Maze(decompressedMaze);

        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }
}
