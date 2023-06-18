package View;

import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundController {

    private static SoundController soundController;
    private MediaPlayer backgroundMusic;

    private MediaPlayer hoverSound;

    private MediaPlayer chooseSound;

    private MediaPlayer winningSound;

    private MediaPlayer regularMoveSound;
    private MediaPlayer diagonalMoveSound;

    private MediaPlayer gameMusic;

    private SoundController(){
        hoverSound = getMediaPlayer("Sounds/menu_change.mp3");
        chooseSound = getMediaPlayer("Sounds/menu_choose.mp3");
        winningSound = getMediaPlayer("Sounds/cheer.mp3");
        regularMoveSound = getMediaPlayer("Sounds/regularMove.mp3");
        diagonalMoveSound = getMediaPlayer("Sounds/diagonalMove.mp3");
        gameMusic = getMediaPlayer("Sounds/gameBackgroundMusic.mp3");
        playBackgroundMusic();
    }

    public static SoundController getInstance(){
        if(soundController == null)
            soundController = new SoundController();
        return soundController;
    }

    private MediaPlayer getMediaPlayer(String path){
        Media sound = new Media(getClass().getResource(path).toExternalForm());
        return new MediaPlayer(sound);
    }

    @FXML
    public void playHoverSound() {
        hoverSound.play();
    }
    @FXML
    public void stopPlayHoverSound() {
        if (hoverSound != null && hoverSound.getStatus() == MediaPlayer.Status.PLAYING) {
            hoverSound.stop();
        }
    }

    @FXML
    public void playChooseSound() {
        Thread thread = new Thread(() -> playAndStopChooseSound());
        thread.start();

    }

    @FXML
    public void playWinSound() {
        Thread thread = new Thread(this::playAndStopWinSound);
        thread.start();

    }

    private void playAndStopWinSound() {
        winningSound.play();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (winningSound != null && winningSound.getStatus() == MediaPlayer.Status.PLAYING) {
            winningSound.stop();
        }
    }

    @FXML
    private void playAndStopChooseSound() {
        chooseSound.play();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (chooseSound != null && chooseSound.getStatus() == MediaPlayer.Status.PLAYING) {
            chooseSound.stop();
        }
    }

    private void playBackgroundMusic() {
        backgroundMusic = getMediaPlayer("Sounds/IcyTowerSong.mp3");
        backgroundMusic.setAutoPlay(true);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();
    }

    public void changeToGameMusic(){
        backgroundMusic.stop();
        gameMusic.setAutoPlay(true);
        gameMusic.setCycleCount(MediaPlayer.INDEFINITE);
        gameMusic.play();
    }

    @FXML
    public void playRegularMoveSound() {
        if (regularMoveSound != null && regularMoveSound.getStatus() == MediaPlayer.Status.PLAYING)
            regularMoveSound.stop();
        regularMoveSound.play();
    }



    @FXML
    public void playDiagonalMoveSound() {
        if (diagonalMoveSound != null && diagonalMoveSound.getStatus() == MediaPlayer.Status.PLAYING) {
            diagonalMoveSound.stop();
        }
        diagonalMoveSound.play();

    }



}
