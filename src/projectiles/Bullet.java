package projectiles;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;

public class Bullet extends Projectile {
    private static final double BULLET_LENGTH = 12;
    private static final double BULLET_WIDTH = 6;

    public Bullet(double x, double y, double direction, double damage, double speed, boolean isPlayer1) {
        super(x, y, direction, damage, speed, isPlayer1);
        updateRotation();
    }

    @Override
    public Rectangle createVisual() {
        Rectangle bullet = new Rectangle(BULLET_LENGTH, BULLET_WIDTH);

        LinearGradient bulletGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.GOLDENROD),
                new Stop(0.3, Color.GOLD),
                new Stop(0.4, Color.DARKGOLDENROD),
                new Stop(0.41, Color.DARKRED),
                new Stop(0.6, Color.rgb(184, 115, 51)),
                new Stop(0.9, Color.rgb(150, 90, 40)),
                new Stop(1, Color.BLACK)
        );

        bullet.setFill(bulletGradient);
        bullet.setStroke(Color.BLACK);
        bullet.setStrokeWidth(0.5);


        bullet.setX(-BULLET_LENGTH / 2);
        bullet.setY(-BULLET_WIDTH / 2);

        DropShadow shine = new DropShadow();
        shine.setColor(Color.WHITE);
        shine.setRadius(2);
        shine.setOffsetX(1);
        shine.setOffsetY(1);

        Glow glow = new Glow();
        glow.setLevel(0.3);

        bullet.setEffect(shine);

        return bullet;
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