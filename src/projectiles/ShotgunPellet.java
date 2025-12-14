package projectiles;

import physics.Vector2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.effect.Glow;

public class ShotgunPellet extends Projectile {
    private static final double SIZE_VARIATION = 0.3;

    public ShotgunPellet(double x, double y, double direction, double damage, double speed, boolean isPlayer1) {
        super(x, y, direction, damage, speed, isPlayer1);
        this.collisionRadius = 3 + Math.random() * 2;
        this.lifetime = 1500;
    }

    @Override
    public Shape createVisual() {
        double size = 3 + Math.random() * 3;
        Circle pellet = new Circle(size);
        return pellet;
    }

    @Override
    protected void setupVisualProperties() {
        if (isPlayer1Projectile) {
            visual.setFill(Color.rgb(255, 200, 50));
        } else {
            visual.setFill(Color.rgb(255, 150, 30));
        }
        visual.setStroke(Color.rgb(255, 255, 200, 0.7));
        visual.setStrokeWidth(0.5);

        Glow glow = new Glow();
        glow.setLevel(0.4 + Math.random() * 0.3);
        visual.setEffect(glow);
    }

    @Override
    public void update() {
        if (!active) return;

        if (System.currentTimeMillis() - creationTime > lifetime) {
            active = false;
            return;
        }

        double speedDecay = 0.99;
        velocity = velocity.multiply(speedDecay);

        position = position.add(velocity);

        if (visual != null) {
            visual.setTranslateX(position.getX());
            visual.setTranslateY(position.getY());

            double trailEffect = 1.0 - (System.currentTimeMillis() - creationTime) / lifetime;
            visual.setOpacity(0.5 + trailEffect * 0.5);
        }
    }

    @Override
    public boolean isOutOfBounds(double width, double height) {
        return position.getX() < -20 || position.getX() > width + 20 ||
                position.getY() < -20 || position.getY() > height + 20;
    }
}