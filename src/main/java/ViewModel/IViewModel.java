package ViewModel;

import algorithms.search.ISearchable;
import algorithms.search.Solution;

import java.util.Observer;

public interface IViewModel extends Observer {

    void generateSearchableGame(Object gameDetails);
    void solve();

    int getPlayerRow();

    int getPlayerCol();
}
