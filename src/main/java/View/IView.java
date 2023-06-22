package View;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public interface IView {

    void generateGame(ActionEvent actionEvent); // ask from the view model for a game
    void save(ActionEvent actionEvent); // ask from the view model to save the game

    void solve(ActionEvent actionEvent); // ask from the view model to solve the game

    void moveCharacter(Event event); // ask the view model to move the character cause some event happened (mose moved, a key pressed)

    void openProperties(ActionEvent actionEvent) throws IOException; //  open the properties window

    void openHelp(ActionEvent actionEvent) throws IOException; //  open the help window

    void openAbout(ActionEvent actionEvent) throws IOException; //  open the about window
}
