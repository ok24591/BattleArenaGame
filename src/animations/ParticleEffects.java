package animations;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ParticleEffects {

    public static void createImpactParticles(Pane parent, double x, double y, int count) {
        for (int i = 0; i < count; i++) {
            Circle particle = new Circle(2 + Math.random() * 3);
            particle.setFill(Color.RED);
            particle.setTranslateX(x);
            particle.setTranslateY(y);
            parent.getChildren().add(particle);

            double angle = Math.random() * Math.PI * 2;
            double distance = 20 + Math.random() * 30;
            double targetX = x + Math.cos(angle) * distance;
            double targetY = y + Math.sin(angle) * distance;

            TranslateTransition move = new TranslateTransition(Duration.millis(500), particle);
            move.setToX(targetX);
            move.setToY(targetY);

            FadeTransition fade = new FadeTransition(Duration.millis(500), particle);
            fade.setToValue(0);
            fade.setOnFinished(e -> parent.getChildren().remove(particle));

            ParallelTransition animation = new ParallelTransition(move, fade);
            animation.play();
        }
    }

    public static void createExplosion(Pane parent, double x, double y, Color color) {
        for (int i = 0; i < 15; i++) {
            Circle particle = new Circle(3 + Math.random() * 4);
            particle.setFill(color);
            particle.setTranslateX(x);
            particle.setTranslateY(y);
            parent.getChildren().add(particle);

            double angle = Math.random() * Math.PI * 2;
            double speed = 3 + Math.random() * 7;
            double targetX = x + Math.cos(angle) * speed * 30;
            double targetY = y + Math.sin(angle) * speed * 30;

            TranslateTransition move = new TranslateTransition(Duration.millis(1000), particle);
            move.setToX(targetX);
            move.setToY(targetY);

            FadeTransition fade = new FadeTransition(Duration.millis(1000), particle);
            fade.setToValue(0);
            fade.setOnFinished(e -> parent.getChildren().remove(particle));

            ParallelTransition animation = new ParallelTransition(move, fade);
            animation.setDelay(Duration.millis(Math.random() * 200));
            animation.play();
        }
    }

    public static void createBloodSplatter(Pane parent, double x, double y) {
        for (int i = 0; i < 8; i++) {
            Circle blood = new Circle(1.5 + Math.random() * 2.5);
            blood.setFill(Color.DARKRED);
            blood.setTranslateX(x);
            blood.setTranslateY(y);
            parent.getChildren().add(blood);

            double angle = Math.random() * Math.PI * 2;
            double distance = 10 + Math.random() * 20;
            double targetX = x + Math.cos(angle) * distance;
            double targetY = y + Math.sin(angle) * distance;

            TranslateTransition move = new TranslateTransition(Duration.millis(400), blood);
            move.setToX(targetX);
            move.setToY(targetY);

            FadeTransition fade = new FadeTransition(Duration.millis(800), blood);
            fade.setToValue(0);
            fade.setOnFinished(e -> parent.getChildren().remove(blood));

            ParallelTransition animation = new ParallelTransition(move, fade);
            animation.play();
        }
    }
}