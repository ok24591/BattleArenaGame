package projectiles;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Bloom;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class SniperBullet extends Projectile {
    private static final double BULLET_LENGTH = 20;
    private static final double BULLET_WIDTH = 8;

    public SniperBullet(double x, double y, double direction, double damage, double speed, boolean isPlayer1) {
        super(x, y, direction, damage, speed, isPlayer1);
        updateRotation();
        startTrailEffect();
    }

    @Override
    public Rectangle createVisual() {
        Rectangle bullet = new Rectangle(BULLET_LENGTH, BULLET_WIDTH);

        LinearGradient bulletGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.SILVER),
                new Stop(0.1, Color.GOLD),
                new Stop(0.3, Color.GOLDENROD),
                new Stop(0.4, Color.DARKGOLDENROD),
                new Stop(0.41, Color.DARKRED),
                new Stop(0.6, Color.rgb(184, 115, 51)),
                new Stop(0.8, Color.rgb(150, 90, 40)),
                new Stop(0.95, Color.SADDLEBROWN),
                new Stop(1, Color.BLACK)
        );

        bullet.setFill(bulletGradient);
        bullet.setStroke(Color.BLACK);
        bullet.setStrokeWidth(0.8);

        bullet.setTranslateX(-BULLET_LENGTH / 2);
        bullet.setTranslateY(-BULLET_WIDTH / 2);

        Glow glow = new Glow();
        glow.setLevel(0.6);

        Bloom bloom = new Bloom();
        bloom.setThreshold(0.3);

        bullet.setEffect(glow);

        return bullet;
    }

    private void startTrailEffect() {
        if (visual != null) {
            ScaleTransition trail = new ScaleTransition(Duration.millis(100), visual);
            trail.setFromX(1.0);
            trail.setFromY(1.0);
            trail.setToX(1.2);
            trail.setToY(1.2);
            trail.setAutoReverse(true);
            trail.setCycleCount(2);
            trail.play();
        }
    }

    @Override
    public void update() {
        super.update();
        updateRotation();
    }

    private void updateRotation() {
        if (visual != null) {
            double angle = Math.toDegrees(Math.atan2(velocity.getY(), velocity.getX()));
            visual.setRotate(angle);
        }
    }
}