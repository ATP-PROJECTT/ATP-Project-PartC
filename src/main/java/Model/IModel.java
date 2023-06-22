package Model;

import ViewModel.IViewModel;
import algorithms.search.Solution;
import javafx.beans.Observable;

public interface IModel{
    void requestGame(Object details); // generate a game and notify the ViewModel

    void addViewModel(IViewModel viewModel); // add the given view model to the observers vector

    void notifyViewModel(String o); // notify the view model observer for the event that just happened

    void solve(); // solving the game and notify the ViewModel

    void save(String gameName); // saving the game on disk

    void load(String gameName); // loading the compatible game and notify the ViewModel

    Object getGame(); // return the current game that's running
    Solution getSolution(); // returning the solution of the current maze

    int getPlayerRow(); // returning the row index of the player in the game
    int getPlayerCol(); // returning the column index of the player in the game



}
