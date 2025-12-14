package characters;

import weapons.Weapon;
import weapons.WeaponManager;
import physics.Vector2D;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

public abstract class Fighter {
    protected String name;
    protected double health;
    protected double maxHealth;
    protected Vector2D position;
    protected double speed;
    protected WeaponManager weaponManager;
    protected boolean isPlayer1;
    protected long lastShotTime;
    protected double rotation;
    protected boolean isAlive;
    protected Shape visual;
    protected double lastMoveX = 0;
    protected double lastMoveY = 0;
    protected double width = 60;
    protected double height = 60;
    protected double collisionRadius = 30;
    private Color originalColor;
    private Timeline currentFlashAnimation;

    public Fighter(String name, double health, double speed, boolean isPlayer1, String defaultWeapon,
                   double width, double height, double collisionRadius) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.isPlayer1 = isPlayer1;
        this.weaponManager = new WeaponManager();
        this.isAlive = true;
        this.lastShotTime = 0;
        this.rotation = isPlayer1 ? 0 : 180;
        this.lastMoveX = isPlayer1 ? 1 : -1;
        this.width = width;
        this.height = height;
        this.collisionRadius = collisionRadius;
        this.visual = getVisualRepresentation();

        if (isPlayer1) {
            this.position = new Vector2D(200, 300);
        } else {
            this.position = new Vector2D(1000, 300);
        }

        updateVisualPosition();

        if (visual != null && visual instanceof Shape) {
            Shape shape = (Shape) visual;
            this.originalColor = (Color) shape.getFill();
        }
    }

    public abstract Shape getVisualRepresentation();

    public void move(double dx, double dy) {
        if (dx != 0 || dy != 0) {
            lastMoveX = dx;
            lastMoveY = dy;
            double angle = Math.atan2(dy, dx);
            rotation = Math.toDegrees(angle);
        }

        double moveX = dx * speed * 3;
        double moveY = dy * speed * 3;

        double currentArenaWidth = config.GameConfig.CURRENT_ARENA_WIDTH;
        double currentArenaHeight = config.GameConfig.CURRENT_ARENA_HEIGHT;

        double newX = position.getX() + moveX;
        double newY = position.getY() + moveY;

        double halfWidth = width / 2;
        double halfHeight = height / 2;

        double minX = halfWidth;
        double maxX = currentArenaWidth - halfWidth;
        double minY = halfHeight;
        double maxY = currentArenaHeight - halfHeight;

        if (newX < minX) {
            newX = minX;
        } else if (newX > maxX) {
            newX = maxX;
        }

        if (newY < minY) {
            newY = minY;
        } else if (newY > maxY) {
            newY = maxY;
        }

        position.setX(newX);
        position.setY(newY);

        updateVisualPosition();
    }

    public void updateVisualPosition() {
        if (visual != null) {
            visual.setTranslateX(position.getX());
            visual.setTranslateY(position.getY());
            visual.setRotate(rotation);
        }
    }

    public projectiles.Projectile shoot() {
        if (canShoot()) {
            Weapon currentWeapon = weaponManager.getCurrentWeapon();
            double direction = Math.atan2(lastMoveY, lastMoveX);

            double halfWidth = width / 2;
            double offsetDistance = halfWidth + 5;

            double spawnX = position.getX() + Math.cos(direction) * offsetDistance;
            double spawnY = position.getY() + Math.sin(direction) * offsetDistance;

            projectiles.Projectile projectile = currentWeapon.fire(
                    spawnX, spawnY, direction, isPlayer1
            );

            if (projectile != null) {
                lastShotTime = System.currentTimeMillis();
                return projectile;
            }
        }
        return null;
    }

    public void takeDamage(double damage) {
        health = Math.max(0, health - damage);
        if (health <= 0) {
            isAlive = false;
            onDeath();
        }
    }

    protected void onDeath() {
        if (visual != null) {
            visual.setFill(Color.GRAY);
        }
    }

    public void switchWeapon() {
        weaponManager.switchToNextWeapon();
    }

    public boolean canShoot() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastShotTime >= getCurrentWeapon().getCooldown();
    }

    public Vector2D getCenterPosition() {
        return position;
    }

    public double getCollisionRadius() {
        return collisionRadius;
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public double getLeft() {
        return position.getX() - (width / 2);
    }

    public double getRight() {
        return position.getX() + (width / 2);
    }

    public double getTop() {
        return position.getY() - (height / 2);
    }

    public double getBottom() {
        return position.getY() + (height / 2);
    }

    public String getName() { return name; }
    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }
    public Vector2D getPosition() { return position; }
    public void setPosition(Vector2D position) {
        double currentArenaWidth = config.GameConfig.CURRENT_ARENA_WIDTH;
        double currentArenaHeight = config.GameConfig.CURRENT_ARENA_HEIGHT;

        double halfWidth = width / 2;
        double halfHeight = height / 2;

        double minX = halfWidth;
        double maxX = currentArenaWidth - halfWidth;
        double minY = halfHeight;
        double maxY = currentArenaHeight - halfHeight;

        double clampedX = Math.max(minX, Math.min(maxX, position.getX()));
        double clampedY = Math.max(minY, Math.min(maxY, position.getY()));

        this.position = new Vector2D(clampedX, clampedY);
        updateVisualPosition();
    }
    public double getSpeed() { return speed; }
    public Weapon getCurrentWeapon() { return weaponManager.getCurrentWeapon(); }
    public WeaponManager getWeaponManager() { return weaponManager; }
    public double getRotation() { return rotation; }
    public void setRotation(double rotation) {
        this.rotation = rotation;
        updateVisualPosition();
    }
    public boolean isPlayer1() { return isPlayer1; }
    public boolean isAlive() { return isAlive; }
    public long getLastShotTime() { return lastShotTime; }
    public Shape getVisual() { return visual; }
    public double getLastMoveX() { return lastMoveX; }
    public double getLastMoveY() { return lastMoveY; }

    public void heal(double amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public void reset() {
        health = maxHealth;
        isAlive = true;
        if (visual != null) {
            visual.setOpacity(1.0);
        }
    }

    public void flashHit(Color flashColor, int durationMs) {
        if (visual == null || !(visual instanceof Shape)) return;

        Shape shape = (Shape) visual;

        if (currentFlashAnimation != null) {
            currentFlashAnimation.stop();
        }

        currentFlashAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(shape.fillProperty(), flashColor)
                ),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(shape.fillProperty(), flashColor)
                ),
                new KeyFrame(Duration.millis(durationMs),
                        new KeyValue(shape.fillProperty(), originalColor)
                )
        );

        currentFlashAnimation.setOnFinished(e -> {
            shape.setFill(originalColor);
            currentFlashAnimation = null;
        });

        currentFlashAnimation.play();
    }
}