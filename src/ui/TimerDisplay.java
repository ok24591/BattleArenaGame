package ui;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class TimerDisplay extends Label {
    private int timeRemaining;

    public TimerDisplay() {
        setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.7); -fx-padding: 10;");
        setTextFill(Color.WHITE);
        updateDisplay(0);
    }

    public void updateDisplay(int seconds) {
        this.timeRemaining = seconds;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        setText(String.format("%02d:%02d", minutes, remainingSeconds));

        if (seconds <= 30) {
            setTextFill(Color.RED);
        } else if (seconds <= 60) {
            setTextFill(Color.ORANGE);
        } else {
            setTextFill(Color.WHITE);
        }
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }
}