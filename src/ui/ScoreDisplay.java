package ui;

import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.animation.*;
import javafx.util.Duration;

public class ScoreDisplay extends HBox {
    private Label player1Score;
    private Label vsLabel;
    private Label player2Score;
    private Timeline scoreAnimation;
    private DropShadow glowEffect;
    private LinearGradient player1Gradient;
    private LinearGradient player2Gradient;

    public ScoreDisplay() {
        setSpacing(30);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(15, 25, 15, 25));
        setStyle("-fx-background-color: rgba(26, 29, 40, 0.9); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-radius: 15; " +
                "-fx-border-width: 1;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(10);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        setEffect(shadow);

        glowEffect = new DropShadow();
        glowEffect.setColor(Color.TRANSPARENT);
        glowEffect.setRadius(20);
        glowEffect.setSpread(0.3);

        player1Gradient = createGradient("#00D4FF", "#00B8E6");
        player2Gradient = createGradient("#FF2A6D", "#FF1A5C");

        player1Score = createScoreLabel(player1Gradient);
        vsLabel = createVSLabel();
        player2Score = createScoreLabel(player2Gradient);

        getChildren().addAll(player1Score, vsLabel, player2Score);
        setupAnimations();
    }

    private LinearGradient createGradient(String color1, String color2) {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(color1)),
                new Stop(1, Color.web(color2))
        );
    }

    private Label createScoreLabel(LinearGradient gradient) {
        Label label = new Label("0");
        label.setStyle("-fx-font-family: 'Arial'; " +
                "-fx-font-size: 32; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-background-color: rgba(42, 47, 64, 0.8); " +
                "-fx-background-radius: 8;");

        label.setTextFill(gradient);

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        innerShadow.setRadius(2);
        innerShadow.setOffsetX(1);
        innerShadow.setOffsetY(1);
        label.setEffect(innerShadow);

        return label;
    }

    private Label createVSLabel() {
        Label label = new Label("VS");
        label.setStyle("-fx-font-family: 'Arial'; " +
                "-fx-font-size: 18; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 5 15 5 15;");

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FFD700")),
                new Stop(0.5, Color.web("#FFA726")),
                new Stop(1, Color.web("#FF9800"))
        );
        label.setTextFill(gradient);

        return label;
    }

    private void setupAnimations() {
        scoreAnimation = new Timeline();
    }

    public void updateScores(int score1, int score2) {
        playScoreAnimation(player1Score, score1);
        playScoreAnimation(player2Score, score2);

        if (score1 > score2) {
            highlightLeadingScore(player1Score, player2Score, player1Gradient);
        } else if (score2 > score1) {
            highlightLeadingScore(player2Score, player1Score, player2Gradient);
        } else {
            resetScoreEffects();
        }
    }

    private void playScoreAnimation(Label scoreLabel, int newScore) {
        int oldScore = Integer.parseInt(scoreLabel.getText());

        if (newScore > oldScore) {
            Timeline animation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(scoreLabel.scaleXProperty(), 1.0),
                            new KeyValue(scoreLabel.scaleYProperty(), 1.0)
                    ),
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(scoreLabel.scaleXProperty(), 1.3),
                            new KeyValue(scoreLabel.scaleYProperty(), 1.3)
                    ),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(scoreLabel.scaleXProperty(), 1.0),
                            new KeyValue(scoreLabel.scaleYProperty(), 1.0)
                    )
            );

            animation.setOnFinished(e -> {
                scoreLabel.setText(String.valueOf(newScore));
            });

            animation.play();
        } else {
            scoreLabel.setText(String.valueOf(newScore));
        }
    }

    private void highlightLeadingScore(Label leadingScore, Label trailingScore, LinearGradient gradient) {
        Color baseColor = Color.web(gradient.getStops().get(0).getColor().toString());
        glowEffect.setColor(Color.rgb(
                (int)(baseColor.getRed() * 255),
                (int)(baseColor.getGreen() * 255),
                (int)(baseColor.getBlue() * 255),
                0.5
        ));
        leadingScore.setEffect(glowEffect);

        trailingScore.setOpacity(0.8);
        trailingScore.setEffect(null);
    }

    private void resetScoreEffects() {
        player1Score.setOpacity(1.0);
        player2Score.setOpacity(1.0);

        InnerShadow innerShadow1 = new InnerShadow();
        innerShadow1.setColor(Color.rgb(0, 0, 0, 0.3));
        innerShadow1.setRadius(2);
        innerShadow1.setOffsetX(1);
        innerShadow1.setOffsetY(1);

        InnerShadow innerShadow2 = new InnerShadow();
        innerShadow2.setColor(Color.rgb(0, 0, 0, 0.3));
        innerShadow2.setRadius(2);
        innerShadow2.setOffsetX(1);
        innerShadow2.setOffsetY(1);

        player1Score.setEffect(innerShadow1);
        player2Score.setEffect(innerShadow2);
    }

    public void playVictoryAnimation(boolean player1Wins) {
        Label winnerScore = player1Wins ? player1Score : player2Score;

        Timeline victoryAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(winnerScore.scaleXProperty(), 1.0),
                        new KeyValue(winnerScore.scaleYProperty(), 1.0),
                        new KeyValue(winnerScore.rotateProperty(), 0)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(winnerScore.scaleXProperty(), 1.5),
                        new KeyValue(winnerScore.scaleYProperty(), 1.5),
                        new KeyValue(winnerScore.rotateProperty(), -5)
                ),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(winnerScore.scaleXProperty(), 1.0),
                        new KeyValue(winnerScore.scaleYProperty(), 1.0),
                        new KeyValue(winnerScore.rotateProperty(), 5)
                ),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(winnerScore.scaleXProperty(), 1.3),
                        new KeyValue(winnerScore.scaleYProperty(), 1.3),
                        new KeyValue(winnerScore.rotateProperty(), 0)
                ),
                new KeyFrame(Duration.millis(800),
                        new KeyValue(winnerScore.scaleXProperty(), 1.0),
                        new KeyValue(winnerScore.scaleYProperty(), 1.0),
                        new KeyValue(winnerScore.rotateProperty(), 0)
                )
        );

        victoryAnimation.setCycleCount(3);
        victoryAnimation.setAutoReverse(true);
        victoryAnimation.play();
    }

    public void reset() {
        player1Score.setText("0");
        player2Score.setText("0");
        resetScoreEffects();

        player1Score.setScaleX(1.0);
        player1Score.setScaleY(1.0);
        player1Score.setRotate(0);
        player2Score.setScaleX(1.0);
        player2Score.setScaleY(1.0);
        player2Score.setRotate(0);
        player1Score.setOpacity(1.0);
        player2Score.setOpacity(1.0);
    }
}