package Model;

import ViewModel.IViewModel;
import algorithms.search.ISearchable;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    void requestBoard(Object details);

    void addViewModel(IViewModel viewModel);

    void notifyViewModel(Object o);

    void solve();

    void save(String gameName);

    void load(String gameName);

    Object getGame();
    Solution getSolution();

    int getPlayerRow();
    int getPlayerCol();



}
