package projectiles;

import physics.Vector2D;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

public abstract class Projectile {
    protected Vector2D position;
    protected Vector2D velocity;
    protected double damage;
    protected boolean active;
    protected Shape visual;
    protected boolean isPlayer1Projectile;
    protected long creationTime;
    protected double collisionRadius = 8;
    protected double lifetime = 5000;

    public Projectile(double x, double y, double direction, double damage, double speed, boolean isPlayer1) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(Math.cos(direction) * speed, Math.sin(direction) * speed);
        this.damage = damage;
        this.active = true;
        this.isPlayer1Projectile = isPlayer1;
        this.visual = createVisual();
        this.creationTime = System.currentTimeMillis();

        if (visual != null) {
            setupVisualProperties();
        }
    }

    public abstract Shape createVisual();

    protected void setupVisualProperties() {
        if (isPlayer1Projectile) {
            visual.setFill(Color.CYAN);
        } else {
            visual.setFill(Color.ORANGE);
        }
        visual.setStroke(Color.WHITE);
        visual.setStrokeWidth(1);
    }

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

    public void update(double deltaTime) {
        update();
    }

    public boolean isOutOfBounds(double width, double height) {
        return position.getX() < -100 || position.getX() > width + 100 ||
                position.getY() < -100 || position.getY() > height + 100;
    }

    public Vector2D getCollisionCenter() {
        return position;
    }

    public double getCollisionRadius() {
        return collisionRadius;
    }

    public void setCollisionRadius(double radius) {
        this.collisionRadius = radius;
    }

    public double getLifetime() {
        return lifetime;
    }

    public void setLifetime(double lifetime) {
        this.lifetime = lifetime;
    }

    public double getRemainingLifetime() {
        return Math.max(0, lifetime - (System.currentTimeMillis() - creationTime));
    }

    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public double getDamage() { return damage; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Shape getVisual() { return visual; }
    public boolean isPlayer1Projectile() { return isPlayer1Projectile; }
    public double getX() { return position.getX(); }
    public double getY() { return position.getY(); }
    public long getCreationTime() { return creationTime; }

    public boolean shouldRemove() {
        return !active || isOutOfBounds(config.GameConfig.CURRENT_ARENA_WIDTH, config.GameConfig.CURRENT_ARENA_HEIGHT);
    }
}