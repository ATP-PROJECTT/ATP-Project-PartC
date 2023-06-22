package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import ViewModel.IViewModel;
import algorithms.mazeGenerators.Maze;
import Server.*;
import IO.*;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MyModel extends Observable implements IModel {

    private static final Logger logger = LogManager.getLogger(MyModel.class);
    Server mazeGeneratingServer;
    Server solveSearchProblemServer;
    int[] curMazeDimensions;

    private boolean playerAskForHint;

    private Maze myMaze;

    private Solution solution;



    public void setPlayerRow(int playerRow) {
        this.playerRow = playerRow;
    }

    public void setPlayerCol(int playerCol) {
        this.playerCol = playerCol;
    }

    private int playerRow;
    private int playerCol;
    private int goalRow;

    private int goalCol;



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
                        logger.info("A request to create a maze");
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
                        myMaze = new Maze(decompressedMaze);
                        playerRow = myMaze.getStartPosition().getRowIndex();
                        playerCol = myMaze.getStartPosition().getColumnIndex();
                        goalRow = myMaze.getGoalPosition().getRowIndex();
                        goalCol = myMaze.getGoalPosition().getColumnIndex();
                        playerAskForHint = false;
                        solution = null;
                        notifyViewModel("maze generated");

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
    public void solve() {

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        logger.info("A request to solve a maze");
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.writeObject(myMaze);
                        toServer.flush();
                        solution = (Solution)fromServer.readObject();
                        notifyViewModel("set solution");
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

    @Override
    public void save(String mazeName) {

        byte[] rowPose = toByteInfo(playerRow);
        byte[] colPose = toByteInfo(playerCol);
        byte[] combined = combineByteArrays(myMaze.toByteArray(),rowPose,colPose);
        byte[] compressedMaze = MyCompressorOutputStream.compressToBinary(combined);
        saveObject(compressedMaze, mazeName);

    }

    private void saveObject(byte[] arrForSave, String fileName){

        // Specify the desired file path within the resources package
        String resourcesPath = getPath(fileName);

        // Create the necessary directories if they don't exist
        File resourcesDir = new File(resourcesPath).getParentFile();
        resourcesDir.mkdirs();

        try (FileOutputStream fileOutputStream = new FileOutputStream(resourcesPath)) {
            // Write the file content
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(arrForSave);
            objectOutputStream.flush();
            fileOutputStream.close();
            objectOutputStream.close();

            System.out.println("File saved to: " + resourcesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getPath(String fileName){
        // Get the user's home directory
        String userHome = System.getProperty("user.home");

        // Specify the desired file path within the resources package

        return userHome + File.separator + "ATP-Project-PartC" + File.separator + "SavedMazes" + File.separator + fileName + ".txt";
    }

    private byte[] combineByteArrays(byte[] array1, byte[] array2, byte[] array3) {
        int totalLength = array1.length + array2.length + array3.length;

        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.put(array1);
        buffer.put(array2);
        buffer.put(array3);

        return buffer.array();
    }
    private byte[] toByteInfo(int value) {
        String binaryNum = String.format("%16s", Integer.toBinaryString(value)).replace(' ', '0');
        byte[] binArray = new byte[16];

        for(int i = 0; i < binaryNum.length(); ++i) {
            char c = binaryNum.charAt(i);
            byte val = (byte)Character.getNumericValue(c);
            binArray[i] = val;
        }

        return binArray;
    }

    @Override
    public void load(String gameName) {

        try {
            String path = getPath(gameName);
            File newFile = new File(path);
            if(!newFile.exists()){
                notifyViewModel("file not found");
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(newFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Byte> decompressed = MyDecompressorInputStream.decompress((byte[]) objectInputStream.readObject());
            byte[] mazeArr = new byte[decompressed.size() - 32];
            byte[] rowPoseArr = new byte[16];
            byte[] colPose = new byte[16];
            int rowPosIndex = 0;
            int colPoseIndex = 0;
            for (int i = 0; i<decompressed.size(); i++){
                if(i<decompressed.size()-32)
                    mazeArr[i] = decompressed.get(i);
                else if(i<decompressed.size()-16){
                    rowPoseArr[rowPosIndex] = decompressed.get(i);
                    rowPosIndex++;
                }
                else {
                    colPose[colPoseIndex] = decompressed.get(i);
                    colPoseIndex++;
                }
            }

            myMaze = new Maze(mazeArr);
            playerRow = toIntInfo(rowPoseArr);
            playerCol = toIntInfo(colPose);
            goalRow = myMaze.getGoalPosition().getRowIndex();
            goalCol = myMaze.getGoalPosition().getColumnIndex();
            playerAskForHint = false;
            solution = null;
            notifyViewModel("maze generated");


        } catch (IOException | ClassNotFoundException e) {
        }
    }

    @Override
    public Object getGame() {
        return myMaze;
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }


    private int toIntInfo(byte[] byteArray) {
        StringBuilder binaryString = new StringBuilder();
        byte[] var3 = byteArray;
        int var4 = byteArray.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            binaryString.append(b);
        }

        return Integer.parseInt(binaryString.toString(), 2);
    }

    public void stop(){
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }


    public void setSearchingAlgorithm(String searchingAlgorithm) {
        Configurations.getInstance().setISearchingAlgorithm(searchingAlgorithm);
    }

    public void setMazeGenerator(String mazeGenerator) {
        Configurations.getInstance().setIMazeGenerator(mazeGenerator);
    }

    public boolean isTherePassHere(int row, int col){
        return myMaze.isTherePassHere(row, col);
    }

    public int getMazeRows(){
        return myMaze.getRows();
    }

    public int getMazeCols(){
        return  myMaze.getColumns();
    }

    public int getGoalRow() {
        return goalRow;
    }

    public int getGoalCol() {
        return goalCol;
    }
}
