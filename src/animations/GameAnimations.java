package animations;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.animation.*;
import javafx.util.Duration;

public class GameAnimations {

    public static void shakeScreen(Node target, double intensity, int durationMs) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), target);
        shake.setByX(intensity);
        shake.setByY(intensity * 0.7);
        shake.setCycleCount(3);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> {
            target.setTranslateX(0);
            target.setTranslateY(0);
        });
        shake.play();
    }

    public static void fadeIn(Node node, int durationMs) {
        FadeTransition fade = new FadeTransition(Duration.millis(durationMs), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public static void fadeOut(Node node, int durationMs) {
        FadeTransition fade = new FadeTransition(Duration.millis(durationMs), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
    }

    public static void scalePop(Node node, double scale, int durationMs) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(durationMs/2), node);
        scaleUp.setToX(scale);
        scaleUp.setToY(scale);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(durationMs/2), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        SequentialTransition pop = new SequentialTransition(scaleUp, scaleDown);
        pop.play();
    }

    public static void flashHit(Shape shape, Color flashColor, int durationMs) {
        if (shape == null) return;

        Color originalFill = (Color) shape.getFill();
        if (originalFill == null) return;

        shape.getProperties().put("flashAnimation", null);

        Timeline flash = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(shape.fillProperty(), flashColor)
                ),
                new KeyFrame(Duration.millis(durationMs/3),
                        new KeyValue(shape.fillProperty(), flashColor)
                ),
                new KeyFrame(Duration.millis(durationMs),
                        new KeyValue(shape.fillProperty(), originalFill)
                )
        );

        shape.getProperties().put("flashAnimation", flash);

        flash.setOnFinished(e -> {
            shape.setFill(originalFill);
            shape.getProperties().put("flashAnimation", null);
        });

        flash.play();
    }

    public static void floatDamageText(Pane parent, String text, double x, double y,
                                       Color color, int fontSize, int durationMs) {
        Text floatingText = new Text(text);
        floatingText.setFill(color);
        floatingText.setStyle("-fx-font-size: " + fontSize + "; -fx-font-weight: bold;");
        floatingText.setLayoutX(x);
        floatingText.setLayoutY(y);
        parent.getChildren().add(floatingText);

        TranslateTransition floatUp = new TranslateTransition(Duration.millis(durationMs), floatingText);
        floatUp.setByY(-60);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(durationMs), floatingText);
        fadeOut.setToValue(0);

        ParallelTransition animation = new ParallelTransition(floatUp, fadeOut);
        animation.setOnFinished(e -> parent.getChildren().remove(floatingText));
        animation.play();
    }

    public static void pulseEffect(Node node, int durationMs, int cycles) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(durationMs), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setCycleCount(cycles * 2);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    public static void bounce(Node node, int durationMs) {
        TranslateTransition bounce = new TranslateTransition(Duration.millis(durationMs/2), node);
        bounce.setByY(-20);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(2);
        bounce.play();
    }

    public static void rotate(Node node, double degrees, int durationMs) {
        RotateTransition rotate = new RotateTransition(Duration.millis(durationMs), node);
        rotate.setByAngle(degrees);
        rotate.setCycleCount(2);
        rotate.setAutoReverse(true);
        rotate.play();
    }
}