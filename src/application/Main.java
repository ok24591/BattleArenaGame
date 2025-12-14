package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import audio.BackgroundMusic;

public class Main extends Application {
    private static BackgroundMusic backgroundMusic;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Starting Battle Arena...");

        try {
            backgroundMusic = new BackgroundMusic();
            backgroundMusic.setVolume(0.3);
            backgroundMusic.play();
            System.out.println("Background music initialized");
        } catch (Exception e) {
            System.out.println("Failed to initialize background music: " + e.getMessage());
            e.printStackTrace();
        }

        primaryStage.setTitle("Battle Arena");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();

        SceneManager sceneManager = new SceneManager(primaryStage, backgroundMusic);
        sceneManager.showCharacterSelection();
        primaryStage.show();

        System.out.println("Application started successfully");
    }

    @Override
    public void stop() {
        System.out.println("Stopping application...");
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            System.out.println("Background music stopped");
        }
    }

    public static void main(String[] args) {
        System.out.println("Launching Battle Arena...");
        launch(args);
    }

    public static BackgroundMusic getBackgroundMusic() {
        return backgroundMusic;
    }
}