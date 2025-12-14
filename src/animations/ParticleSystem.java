package animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ParticleSystem {
    private Pane container;

    public ParticleSystem(Pane container) {
        this.container = container;
    }

    public void createExplosion(double x, double y, int particleCount, Color color) {
        for (int i = 0; i < particleCount; i++) {
            createParticle(x, y, color);
        }
    }

    private void createParticle(double x, double y, Color color) {
        Circle particle = new Circle(1 + Math.random() * 4);
        particle.setFill(color.deriveColor(0, 1, 1, 0.6 + Math.random() * 0.4));
        particle.setCenterX(x);
        particle.setCenterY(y);

        container.getChildren().add(particle);

        double angle = Math.random() * 360;
        double distance = 30 + Math.random() * 70;
        double targetX = x + Math.cos(Math.toRadians(angle)) * distance;
        double targetY = y + Math.sin(Math.toRadians(angle)) * distance;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(particle.centerXProperty(), x),
                        new KeyValue(particle.centerYProperty(), y),
                        new KeyValue(particle.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(0.5 + Math.random() * 0.5),
                        new KeyValue(particle.centerXProperty(), targetX),
                        new KeyValue(particle.centerYProperty(), targetY),
                        new KeyValue(particle.opacityProperty(), 0.0)
                )
        );

        timeline.setOnFinished(e -> container.getChildren().remove(particle));
        timeline.play();
    }
}