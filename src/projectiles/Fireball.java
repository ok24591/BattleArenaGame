package projectiles;

import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.effect.Glow;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class Fireball extends Projectile {
    public Fireball(double x, double y, double direction, double damage, double speed, boolean isPlayer1) {
        super(x, y, direction, damage, speed, isPlayer1);
        startPulseAnimation();
    }

    @Override
    public Circle createVisual() {
        Circle fireball = new Circle(8);

        RadialGradient fireGradient = new RadialGradient(
                0, 0, 0.2, 0.2, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.YELLOW),
                new Stop(0.3, Color.ORANGE),
                new Stop(0.6, Color.RED),
                new Stop(0.8, Color.DARKRED),
                new Stop(1, Color.BLACK)
        );

        fireball.setFill(fireGradient);

        Glow glow = new Glow();
        glow.setLevel(0.8);
        fireball.setEffect(glow);

        return fireball;
    }

    private void startPulseAnimation() {
        if (visual != null) {
            ScaleTransition pulse = new ScaleTransition(Duration.millis(300), visual);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.3);
            pulse.setToY(1.3);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(ScaleTransition.INDEFINITE);
            pulse.play();
        }
    }
}