package ViewModel;

import algorithms.search.ISearchable;
import algorithms.search.Solution;

import java.util.Observer;

public interface IViewModel extends Observer {

    void generateSearchableGame(Object gameDetails); // getting a game dimensions and ask for a game from the model
    void solve(); // ask for solution from the model to the current searchable game

    void load(String fileName); // ask from the model to load the given game

    void save(String fileName); // ask for the model to save the given game

}
