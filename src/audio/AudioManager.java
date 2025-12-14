package audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.net.URL;

public class AudioManager {
    private static AudioManager instance;
    private double soundEffectVolume = 0.7; 

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playSound(String soundName) {
        System.out.println("=== PLAY SOUND: " + soundName + " ===");

        String soundFile = soundName;
        if (soundFile.toLowerCase().endsWith(".wav")) {
            soundFile = soundFile.substring(0, soundFile.length() - 4);
        }

        try {
            System.out.println("Looking for sound file: " + soundFile);

            URL soundURL = getClass().getResource("/sounds/" + soundFile + ".mp3");
            System.out.println("Resource MP3 URL: " + soundURL);

            if (soundURL == null) {
                soundURL = getClass().getResource("/sounds/" + soundFile + ".wav");
                System.out.println("Resource WAV URL: " + soundURL);
            }

            if (soundURL == null) {
                System.out.println("Trying file system paths...");


                File soundFileObj = new File("src/main/resources/sounds/" + soundFile + ".mp3");
                System.out.println("Path 1: " + soundFileObj.getAbsolutePath());
                System.out.println("Exists: " + soundFileObj.exists());

                if (soundFileObj.exists()) {
                    soundURL = soundFileObj.toURI().toURL();
                } else {
                    soundFileObj = new File("src/main/resources/sounds/" + soundFile + ".wav");
                    System.out.println("Path 2: " + soundFileObj.getAbsolutePath());
                    System.out.println("Exists: " + soundFileObj.exists());

                    if (soundFileObj.exists()) {
                        soundURL = soundFileObj.toURI().toURL();
                    } else {
                        soundFileObj = new File("sounds/" + soundFile + ".mp3");
                        System.out.println("Path 3: " + soundFileObj.getAbsolutePath());
                        System.out.println("Exists: " + soundFileObj.exists());

                        if (soundFileObj.exists()) {
                            soundURL = soundFileObj.toURI().toURL();
                        }
                    }
                }
            }

            if (soundURL == null) {
                System.out.println("ERROR: Sound file not found: " + soundFile);
                System.out.println("Please ensure the file exists in: src/main/resources/sounds/");
                return;
            }

            System.out.println("Playing from: " + soundURL);
            Media sound = new Media(soundURL.toString());
            MediaPlayer player = new MediaPlayer(sound);
            player.setVolume(soundEffectVolume);

            final String errorSoundName = soundFile;
            player.setOnError(() -> {
                System.out.println("ERROR playing sound: " + errorSoundName);
                System.out.println("MediaPlayer error: " + player.getError());
            });

            player.setOnEndOfMedia(() -> {
                player.dispose();
                System.out.println("Sound finished: " + errorSoundName);
            });

            player.play();
            System.out.println("Sound playing successfully");

        } catch (Exception e) {
            System.out.println("EXCEPTION loading sound: " + soundFile);
            e.printStackTrace();
        }
    }

    public void setSoundEffectVolume(double volume) {
        this.soundEffectVolume = Math.max(0, Math.min(1, volume));
        System.out.println("Sound effect volume set to: " + this.soundEffectVolume);
    }

    public double getSoundEffectVolume() {
        return soundEffectVolume;
    }
}