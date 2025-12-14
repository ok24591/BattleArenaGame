package physics;

import characters.Fighter;
import characters.Warrior;
import characters.Archer;
import characters.Assassin;
import characters.Mage;
import projectiles.Projectile;
import javafx.scene.shape.Shape;

public class CollisionDetector {

    public static boolean checkProjectileHit(Projectile projectile, Fighter fighter) {
        if (projectile == null || fighter == null || !projectile.isActive() || !fighter.isAlive()) {
            return false;
        }


        if (fighter instanceof Warrior) {

            return checkAdjustedRectangleCollision(projectile, fighter, 0.9);
        } else if (fighter instanceof Archer) {

            return checkAdjustedCircularCollision(projectile, fighter, 0.8);
        } else if (fighter instanceof Assassin) {

            return checkAdjustedCircularCollision(projectile, fighter, 0.75);
        } else if (fighter instanceof Mage) {

            return checkAdjustedCircularCollision(projectile, fighter, 0.85);
        } else {

            return checkAdjustedCircularCollision(projectile, fighter, 0.85);
        }
    }

    private static boolean checkAdjustedRectangleCollision(Projectile projectile, Fighter fighter, double fighterAdjustment) {

        double projX = projectile.getPosition().getX();
        double projY = projectile.getPosition().getY();
        double projRadius = projectile.getCollisionRadius();


        double adjustedProjRadius = projRadius * 0.7;


        double projLeft = projX - adjustedProjRadius;
        double projRight = projX + adjustedProjRadius;
        double projTop = projY - adjustedProjRadius;
        double projBottom = projY + adjustedProjRadius;


        double fighterVisualWidth = fighter.getWidth();
        double fighterVisualHeight = fighter.getHeight();
        double fighterHitboxWidth = fighterVisualWidth * fighterAdjustment;
        double fighterHitboxHeight = fighterVisualHeight * fighterAdjustment;

        double fighterCenterX = fighter.getPosition().getX();
        double fighterCenterY = fighter.getPosition().getY();

        double fighterLeft = fighterCenterX - (fighterHitboxWidth / 2);
        double fighterRight = fighterCenterX + (fighterHitboxWidth / 2);
        double fighterTop = fighterCenterY - (fighterHitboxHeight / 2);
        double fighterBottom = fighterCenterY + (fighterHitboxHeight / 2);


        boolean collision = projLeft <= fighterRight &&
                projRight >= fighterLeft &&
                projTop <= fighterBottom &&
                projBottom >= fighterTop;

        return collision;
    }

    private static boolean checkAdjustedCircularCollision(Projectile projectile, Fighter fighter, double fighterAdjustment) {

        double projX = projectile.getPosition().getX();
        double projY = projectile.getPosition().getY();
        double fighterX = fighter.getPosition().getX();
        double fighterY = fighter.getPosition().getY();

        double dx = projX - fighterX;
        double dy = projY - fighterY;
        double distance = Math.sqrt(dx * dx + dy * dy);


        double visualProjRadius = projectile.getCollisionRadius();
        double visualFighterRadius = fighter.getCollisionRadius();


        double adjustedProjRadius = visualProjRadius * 0.7;
        double adjustedFighterRadius = visualFighterRadius * fighterAdjustment;

        double collisionDistance = adjustedFighterRadius + adjustedProjRadius;

        return distance <= collisionDistance;
    }

    public static boolean checkRectangleCollision(Projectile projectile, Fighter fighter) {

        return checkAdjustedRectangleCollision(projectile, fighter, 0.9);
    }

    public static boolean checkCircularCollision(Projectile projectile, Fighter fighter) {

        return checkAdjustedCircularCollision(projectile, fighter, 0.85);
    }

    public static boolean checkHybridCollision(Projectile projectile, Fighter fighter) {
        return checkAdjustedCircularCollision(projectile, fighter, 0.85);
    }

    public static boolean checkBoundaryCollision(double x, double y) {
        double padding = 25;
        return x < padding || x > config.GameConfig.CURRENT_ARENA_WIDTH - padding ||
                y < padding || y > config.GameConfig.CURRENT_ARENA_HEIGHT - padding;
    }

    public static boolean checkBoundaryCollision(double x, double y, boolean isPlayer1) {
        return checkBoundaryCollision(x, y);
    }

    public static boolean checkFighterCollision(Fighter fighter1, Fighter fighter2) {
        if (fighter1 == null || fighter2 == null || !fighter1.isAlive() || !fighter2.isAlive()) {
            return false;
        }

        if (fighter1 instanceof Warrior || fighter2 instanceof Warrior) {
            double f1Adjustment = getFighterAdjustment(fighter1);
            double f2Adjustment = getFighterAdjustment(fighter2);

            double f1HitboxWidth = fighter1.getWidth() * f1Adjustment;
            double f1HitboxHeight = fighter1.getHeight() * f1Adjustment;
            double f2HitboxWidth = fighter2.getWidth() * f2Adjustment;
            double f2HitboxHeight = fighter2.getHeight() * f2Adjustment;

            double f1Left = fighter1.getPosition().getX() - (f1HitboxWidth / 2);
            double f1Right = fighter1.getPosition().getX() + (f1HitboxWidth / 2);
            double f1Top = fighter1.getPosition().getY() - (f1HitboxHeight / 2);
            double f1Bottom = fighter1.getPosition().getY() + (f1HitboxHeight / 2);

            double f2Left = fighter2.getPosition().getX() - (f2HitboxWidth / 2);
            double f2Right = fighter2.getPosition().getX() + (f2HitboxWidth / 2);
            double f2Top = fighter2.getPosition().getY() - (f2HitboxHeight / 2);
            double f2Bottom = fighter2.getPosition().getY() + (f2HitboxHeight / 2);

            return f1Left <= f2Right && f1Right >= f2Left &&
                    f1Top <= f2Bottom && f1Bottom >= f2Top;
        } else {
            Vector2D pos1 = fighter1.getPosition();
            Vector2D pos2 = fighter2.getPosition();

            double distance = pos1.distanceTo(pos2);
            double f1Adjustment = getFighterAdjustment(fighter1);
            double f2Adjustment = getFighterAdjustment(fighter2);

            double collisionDistance = (fighter1.getCollisionRadius() * f1Adjustment) +
                    (fighter2.getCollisionRadius() * f2Adjustment);

            return distance <= collisionDistance;
        }
    }

    public static boolean isPointInFighter(double x, double y, Fighter fighter) {
        if (fighter instanceof Warrior) {
            double hitboxWidth = fighter.getWidth() * 0.9;
            double hitboxHeight = fighter.getHeight() * 0.9;

            double left = fighter.getPosition().getX() - (hitboxWidth / 2);
            double right = fighter.getPosition().getX() + (hitboxWidth / 2);
            double top = fighter.getPosition().getY() - (hitboxHeight / 2);
            double bottom = fighter.getPosition().getY() + (hitboxHeight / 2);

            return x >= left && x <= right && y >= top && y <= bottom;
        } else {
            Vector2D fighterPos = fighter.getPosition();
            double dx = x - fighterPos.getX();
            double dy = y - fighterPos.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            double adjustment = getFighterAdjustment(fighter);
            double adjustedRadius = fighter.getCollisionRadius() * adjustment;
            return distance <= adjustedRadius;
        }
    }

    public static boolean checkShapeCollision(Shape shape1, Shape shape2) {
        if (shape1 == null || shape2 == null) return false;

        Shape intersection = Shape.intersect(shape1, shape2);
        return intersection.getBoundsInLocal().getWidth() != -1;
    }

    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double calculateDistance(Vector2D v1, Vector2D v2) {
        return v1.distanceTo(v2);
    }

    private static double getFighterAdjustment(Fighter fighter) {
        if (fighter instanceof Warrior) return 0.9;
        if (fighter instanceof Archer) return 0.8;
        if (fighter instanceof Assassin) return 0.75;
        if (fighter instanceof Mage) return 0.85;
        return 0.85;
    }
}