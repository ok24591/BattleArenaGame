package audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.net.URL;

public class BackgroundMusic {
    private MediaPlayer mediaPlayer;
    private boolean musicEnabled;
    private double volume = 0.3;
    private boolean isPlaying = false;
    private String musicFile = "background_music.mp3";

    public BackgroundMusic() {
        this.musicEnabled = true;
        loadBackgroundMusic();
    }

    public BackgroundMusic(String musicFileName) {
        this.musicEnabled = true;
        this.musicFile = musicFileName;
        loadBackgroundMusic();
    }

    private void loadBackgroundMusic() {
        try {
            System.out.println("Loading background music...");
            System.out.println("Looking for: " + musicFile);

            URL musicURL = getClass().getResource("/sounds/" + musicFile);
            System.out.println("Resource URL: " + musicURL);

            if (musicURL == null) {
                System.out.println("Trying file system path...");
                File musicFileObj = new File("src/main/resources/sounds/" + musicFile);
                System.out.println("File path: " + musicFileObj.getAbsolutePath());
                System.out.println("File exists: " + musicFileObj.exists());

                if (musicFileObj.exists()) {
                    musicURL = musicFileObj.toURI().toURL();
                    System.out.println("Found file at: " + musicURL);
                } else {
                    System.out.println("Trying alternative paths...");

                    musicFileObj = new File("sounds/" + musicFile);
                    System.out.println("Alt path 1: " + musicFileObj.getAbsolutePath() +
                            " exists: " + musicFileObj.exists());

                    if (!musicFileObj.exists()) {
                        musicFileObj = new File("resources/sounds/" + musicFile);
                        System.out.println("Alt path 2: " + musicFileObj.getAbsolutePath() +
                                " exists: " + musicFileObj.exists());
                    }

                    if (musicFileObj.exists()) {
                        musicURL = musicFileObj.toURI().toURL();
                    }
                }
            }

            if (musicURL != null) {
                System.out.println("Successfully loaded music from: " + musicURL);
                Media media = new Media(musicURL.toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setVolume(volume);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

                mediaPlayer.setOnError(() -> {
                    System.out.println("Error playing background music: " + mediaPlayer.getError());
                });

                mediaPlayer.setOnEndOfMedia(() -> {
                    if (musicEnabled) {
                        mediaPlayer.play();
                    }
                });
            } else {
                System.out.println("ERROR: Could not find background music file: " + musicFile);
                System.out.println("Please ensure the file exists in: src/main/resources/sounds/");
            }
        } catch (Exception e) {
            System.out.println("Exception loading background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void play() {
        if (musicEnabled && mediaPlayer != null) {
            mediaPlayer.play();
            isPlaying = true;
            System.out.println("Background music started playing");
        } else {
            System.out.println("Cannot play: music not loaded or disabled");
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
            System.out.println("Background music stopped");
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            System.out.println("Background music paused");
        }
    }

    public void setVolume(double volume) {
        this.volume = Math.max(0, Math.min(1, volume));
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(this.volume);
            System.out.println("Volume set to: " + this.volume);
        }
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) stop();
        else play();
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public double getVolume() {
        return volume;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setMusicFile(String musicFile) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        this.musicFile = musicFile;
        loadBackgroundMusic();

        if (musicEnabled) {
            play();
        }
    }
}