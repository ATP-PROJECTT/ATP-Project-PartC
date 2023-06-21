package View;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public interface IView {

    void generateGame(ActionEvent actionEvent);
    void save(ActionEvent actionEvent);

    void moveCharacter(Event event);

    void openProperties(ActionEvent actionEvent) throws IOException;

    void openHelp(ActionEvent actionEvent) throws IOException;

    void openAbout(ActionEvent actionEvent) throws IOException;
}
