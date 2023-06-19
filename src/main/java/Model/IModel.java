package Model;

import ViewModel.IViewModel;
import algorithms.search.ISearchable;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    void requestBoard(Object details);

    void addViewModel(IViewModel viewModel);

    void notifyViewModel(Object o);

    void solve(Object game);

    void save(SavableGame savableGame);

    void load(String gameName);
}
