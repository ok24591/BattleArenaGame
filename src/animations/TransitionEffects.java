package animations;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class TransitionEffects {

    public static void fadeIn(Node node, Duration duration) {
        FadeTransition fadeIn = new FadeTransition(duration, node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    public static void fadeOut(Node node, Duration duration) {
        FadeTransition fadeOut = new FadeTransition(duration, node);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.play();
    }

    public static void pulse(Node node) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), node);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        SequentialTransition pulse = new SequentialTransition(scaleUp, scaleDown);
        pulse.play();
    }

    public static void shake(Node node) {
        javafx.animation.TranslateTransition shake = new javafx.animation.TranslateTransition(Duration.millis(50), node);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
}