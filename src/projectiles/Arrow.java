package projectiles;

import physics.Vector2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.effect.Glow;

public class Arrow extends Projectile {
    private double rotation;

    public Arrow(double x, double y, double direction, double damage, double speed, boolean isPlayer1) {
        super(x, y, direction, damage, speed, isPlayer1);
        this.collisionRadius = 6;
        this.lifetime = 4000;
        this.rotation = direction;
    }

    @Override
    public Shape createVisual() {
        Polygon arrow = new Polygon();

        double arrowLength = 25;
        double arrowWidth = 6;
        double tipLength = 8;
        double fletchingLength = 6;

        arrow.getPoints().addAll(
                0.0, 0.0,
                arrowLength - tipLength, arrowWidth / 2,
                arrowLength - tipLength, arrowWidth / 3,
                arrowLength, 0.0,
                arrowLength - tipLength, -arrowWidth / 3,
                arrowLength - tipLength, -arrowWidth / 2,
                0.0, 0.0,
                fletchingLength, arrowWidth / 1.5,
                0.0, arrowWidth / 2,
                fletchingLength, arrowWidth / 1.5,
                0.0, 0.0,
                fletchingLength, -arrowWidth / 1.5,
                0.0, -arrowWidth / 2,
                fletchingLength, -arrowWidth / 1.5
        );

        return arrow;
    }

    @Override
    protected void setupVisualProperties() {
        if (isPlayer1Projectile) {
            visual.setFill(Color.rgb(180, 120, 60));
            visual.setStroke(Color.rgb(220, 180, 100));
        } else {
            visual.setFill(Color.rgb(160, 100, 50));
            visual.setStroke(Color.rgb(200, 160, 90));
        }
        visual.setStrokeWidth(1);

        Glow glow = new Glow();
        glow.setLevel(0.2);
        visual.setEffect(glow);
    }

    @Override
    public void update() {
        if (!active) return;

        if (System.currentTimeMillis() - creationTime > lifetime) {
            active = false;
            return;
        }

        position = position.add(velocity);

        if (visual != null) {
            visual.setTranslateX(position.getX());
            visual.setTranslateY(position.getY());

            double angle = Math.atan2(velocity.getY(), velocity.getX());
            visual.setRotate(Math.toDegrees(angle));
        }
    }

    @Override
    public boolean isOutOfBounds(double width, double height) {
        return position.getX() < -50 || position.getX() > width + 50 ||
                position.getY() < -50 || position.getY() > height + 50;
    }
}