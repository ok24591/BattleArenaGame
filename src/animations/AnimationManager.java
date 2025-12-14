package animations;

import javafx.animation.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AnimationManager {
    private Pane effectsLayer;
    private ParticleSystem particleSystem;

    public AnimationManager(Pane effectsLayer) {
        this.effectsLayer = effectsLayer;
        this.particleSystem = new ParticleSystem(effectsLayer);
    }

    public void createExplosion(double x, double y, double radius, Color color) {
        particleSystem.createExplosion(x, y, (int)(radius * 3), color);
        createShockwave(x, y, radius, color);
    }

    public void createHitEffect(double x, double y, Color color) {
        // Create hit sparkles
        for (int i = 0; i < 8; i++) {
            createSparkle(x, y, color);
        }
    }

    public void createHealEffect(double x, double y) {
        // Create healing particles
        for (int i = 0; i < 12; i++) {
            createHealingParticle(x, y);
        }
    }

    public void createLevelUpEffect(double x, double y) {
        // Create golden particles in a spiral
        for (int i = 0; i < 16; i++) {
            double angle = (i * 22.5) * Math.PI / 180;
            double distance = 30 + i * 5;
            createGoldenParticle(
                    x + Math.cos(angle) * distance,
                    y + Math.sin(angle) * distance
            );
        }
    }

    private void createShockwave(double x, double y, double radius, Color color) {
        Circle shockwave = new Circle(radius);
        shockwave.setFill(Color.TRANSPARENT);
        shockwave.setStroke(color);
        shockwave.setStrokeWidth(3);
        shockwave.setCenterX(x);
        shockwave.setCenterY(y);
        effectsLayer.getChildren().add(shockwave);

        Timeline shockwaveAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(shockwave.radiusProperty(), radius),
                        new KeyValue(shockwave.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(0.6),
                        new KeyValue(shockwave.radiusProperty(), radius * 4),
                        new KeyValue(shockwave.opacityProperty(), 0.0)
                )
        );

        shockwaveAnimation.setOnFinished(e -> effectsLayer.getChildren().remove(shockwave));
        shockwaveAnimation.play();
    }

    private void createSparkle(double x, double y, Color color) {
        Circle sparkle = new Circle(2 + Math.random() * 3);
        sparkle.setFill(color);
        sparkle.setCenterX(x);
        sparkle.setCenterY(y);
        effectsLayer.getChildren().add(sparkle);

        double angle = Math.random() * 360;
        double distance = 20 + Math.random() * 30;
        double targetX = x + Math.cos(Math.toRadians(angle)) * distance;
        double targetY = y + Math.sin(Math.toRadians(angle)) * distance;

        Timeline sparkleAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(sparkle.centerXProperty(), x),
                        new KeyValue(sparkle.centerYProperty(), y),
                        new KeyValue(sparkle.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(0.4),
                        new KeyValue(sparkle.centerXProperty(), targetX),
                        new KeyValue(sparkle.centerYProperty(), targetY),
                        new KeyValue(sparkle.opacityProperty(), 0.0)
                )
        );

        sparkleAnimation.setOnFinished(e -> effectsLayer.getChildren().remove(sparkle));
        sparkleAnimation.play();
    }

    private void createHealingParticle(double x, double y) {
        Circle particle = new Circle(1 + Math.random() * 2);
        particle.setFill(Color.LIGHTGREEN);
        particle.setCenterX(x);
        particle.setCenterY(y);
        effectsLayer.getChildren().add(particle);

        double targetX = x + (Math.random() - 0.5) * 40;
        double targetY = y - 20 - Math.random() * 30;

        Timeline healAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(particle.centerXProperty(), x),
                        new KeyValue(particle.centerYProperty(), y),
                        new KeyValue(particle.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(0.8 + Math.random() * 0.4),
                        new KeyValue(particle.centerXProperty(), targetX),
                        new KeyValue(particle.centerYProperty(), targetY),
                        new KeyValue(particle.opacityProperty(), 0.0)
                )
        );

        healAnimation.setOnFinished(e -> effectsLayer.getChildren().remove(particle));
        healAnimation.play();
    }

    private void createGoldenParticle(double x, double y) {
        Rectangle particle = new Rectangle(3, 3);
        particle.setFill(Color.GOLD);
        particle.setRotate(45);
        particle.setX(x);
        particle.setY(y);
        effectsLayer.getChildren().add(particle);

        Timeline goldenAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(particle.opacityProperty(), 1.0),
                        new KeyValue(particle.scaleXProperty(), 1.0),
                        new KeyValue(particle.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(1.0),
                        new KeyValue(particle.opacityProperty(), 0.0),
                        new KeyValue(particle.scaleXProperty(), 2.0),
                        new KeyValue(particle.scaleYProperty(), 2.0)
                )
        );

        goldenAnimation.setOnFinished(e -> effectsLayer.getChildren().remove(particle));
        goldenAnimation.play();
    }

    public void clearAllEffects() {
        effectsLayer.getChildren().clear();
    }
}