package application;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import audio.BackgroundMusic;
import audio.SoundEffect;

public class SceneManager {
    private Stage stage;
    private CharacterSelectionScene characterSelection;
    private GameScene gameScene;
    private GameOverScene gameOverScene;
    private BackgroundMusic backgroundMusic;

    private int player1Wins = 0;
    private int player2Wins = 0;
    private String lastPlayer1Character = "Warrior";
    private String lastPlayer2Character = "Warrior";
    private int currentRound = 1;

    public SceneManager(Stage stage, BackgroundMusic backgroundMusic) {
        this.stage = stage;
        this.backgroundMusic = backgroundMusic;
        setupStageProperties();
    }

    private void setupStageProperties() {
        stage.setTitle("Battle Arena");
        stage.setResizable(true);
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        stage.setMaximized(true);

        stage.setOnCloseRequest(e -> {
            if (backgroundMusic != null) {
                backgroundMusic.stop();
            }
        });
    }

    public void showCharacterSelection() {
        cleanupGameScene();
        cleanupGameOverScene();
        currentRound = 1;

        if (characterSelection == null) {
            characterSelection = new CharacterSelectionScene(this);
        }

        if (backgroundMusic != null) {
            backgroundMusic.play();
            backgroundMusic.setVolume(0.3);
        }

        Scene scene = characterSelection.getScene();
        setupEscapeKeyHandler(scene);
        stage.setScene(scene);

        if (!stage.isShowing()) {
            stage.show();
        }

        javafx.application.Platform.runLater(() -> {
            if (scene != null && scene.getRoot() != null) {
                scene.getRoot().requestFocus();
            }
        });
    }

    public void startGame(String player1Character, String player2Character) {
        this.lastPlayer1Character = player1Character;
        this.lastPlayer2Character = player2Character;

        cleanupGameScene();
        cleanupCharacterSelection();

        if (backgroundMusic != null) {
            backgroundMusic.setVolume(0.15);
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.play();
            }
        }

        gameScene = new GameScene(this, player1Character, player2Character, currentRound);
        Scene scene = gameScene.getScene();
        setupEscapeKeyHandler(scene);
        stage.setScene(scene);

        if (!stage.isShowing()) {
            stage.show();
        }

        javafx.application.Platform.runLater(() -> {
            if (scene != null && scene.getRoot() != null) {
                scene.getRoot().requestFocus();
            }
        });
    }

    public void showGameOver(String winner, int player1Score, int player2Score) {
        if (winner != null) {
            if (winner.contains("1")) {
                player1Wins++;
                SoundEffect.VICTORY.play();
            } else if (winner.contains("2")) {
                player2Wins++;
                SoundEffect.VICTORY.play();
            }
        }

        cleanupGameScene();
        currentRound++;

        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }

        gameOverScene = new GameOverScene(this, winner, player1Wins, player2Wins, currentRound);
        Scene scene = gameOverScene.getScene();
        setupEscapeKeyHandler(scene);
        stage.setScene(scene);

        if (!stage.isShowing()) {
            stage.show();
        }

        javafx.application.Platform.runLater(() -> {
            if (scene != null && scene.getRoot() != null) {
                scene.getRoot().requestFocus();
            }
        });
    }

    private void setupEscapeKeyHandler(Scene scene) {
        if (scene != null) {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    if (gameScene == null || scene != gameScene.getScene()) {
                        stage.setIconified(true);
                        e.consume();
                    }
                }
            });
        }
    }

    public void playAgain() {
        if (lastPlayer1Character != null && lastPlayer2Character != null) {
            startGame(lastPlayer1Character, lastPlayer2Character);
        } else {
            startGame("Warrior", "Warrior");
        }
    }

    public void exitGame() {
        cleanupAllScenes();
        if (stage != null) {
            stage.close();
        }
        System.exit(0);
    }

    private void cleanupGameScene() {
        if (gameScene != null) {
            gameScene.cleanup();
            gameScene = null;
        }
    }

    private void cleanupGameOverScene() {
        if (gameOverScene != null) {
            gameOverScene = null;
        }
    }

    private void cleanupCharacterSelection() {
        if (characterSelection != null) {
            characterSelection.cleanup();
            characterSelection = null;
        }
    }

    private void cleanupAllScenes() {
        cleanupGameScene();
        cleanupGameOverScene();
        cleanupCharacterSelection();
    }

    public Stage getStage() {
        return stage;
    }

    public String getLastPlayer1Character() {
        return lastPlayer1Character != null ? lastPlayer1Character : "Warrior";
    }

    public String getLastPlayer2Character() {
        return lastPlayer2Character != null ? lastPlayer2Character : "Warrior";
    }

    public void resetStatistics() {
        player1Wins = 0;
        player2Wins = 0;
        currentRound = 1;
    }

    public BackgroundMusic getBackgroundMusic() {
        return backgroundMusic;
    }

    public void restartBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.play();
            backgroundMusic.setVolume(0.3);
        }
    }
}