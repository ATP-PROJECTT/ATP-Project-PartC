package Model;

public class SavableGame {
    private Object game;
    private Object position;
    private String gameName;

    public SavableGame(Object game, Object position, String gameName) {
        this.game = game;
        this.position = position;
        this.gameName = gameName;
    }

    public Object getGame() {
        return game;
    }

    public Object getPosition() {
        return position;
    }

    public String getGameName() {
        return gameName;
    }
}
