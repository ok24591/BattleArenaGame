package projectiles;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Bloom;
import physics.Vector2D;
public class LaserBeam extends Projectile {
    private static final double BEAM_LENGTH = 40;
    private static final double BEAM_WIDTH = 4;

    public LaserBeam(double x, double y, double direction, double damage, double speed, boolean isPlayer1) {
        super(x, y, direction, damage, speed, isPlayer1);

        double dirRadians = Math.toRadians(direction);
        double forwardOffset = BEAM_LENGTH / 2;
        this.position = new Vector2D(
                x + Math.cos(dirRadians) * forwardOffset,
                y + Math.sin(dirRadians) * forwardOffset
        );

        updateRotation();
    }

    @Override
    public Rectangle createVisual() {
        Rectangle laser = new Rectangle(BEAM_LENGTH, BEAM_WIDTH);

        LinearGradient laserGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.2, isPlayer1Projectile ? Color.CYAN : Color.MAGENTA),
                new Stop(0.5, Color.WHITE),
                new Stop(0.8, isPlayer1Projectile ? Color.CYAN : Color.MAGENTA),
                new Stop(1, Color.TRANSPARENT)
        );

        laser.setFill(laserGradient);

        laser.setTranslateX(-BEAM_LENGTH / 2);
        laser.setTranslateY(-BEAM_WIDTH / 2);

        Glow glow = new Glow();
        glow.setLevel(0.9);

        laser.setEffect(glow);

        return laser;
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