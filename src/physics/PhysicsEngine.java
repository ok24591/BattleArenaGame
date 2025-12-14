package physics;

import characters.Fighter;
import projectiles.Projectile;
import java.util.List;

public class PhysicsEngine {

    public static void updateFighterPosition(Fighter fighter, double dx, double dy) {
        Vector2D currentPosition = fighter.getPosition();
        double newX = currentPosition.getX() + dx;
        double newY = currentPosition.getY() + dy;

        if (!CollisionDetector.checkBoundaryCollision(newX, newY, fighter.isPlayer1())) {
            fighter.setPosition(new Vector2D(newX, newY));

            if (dx != 0 || dy != 0) {
                double angle = Math.toDegrees(Math.atan2(dy, dx));
                fighter.setRotation(angle);
            }
        }
    }

    public static void updateProjectiles(List<Projectile> projectiles) {
        for (Projectile projectile : projectiles) {
            if (projectile.isActive()) {
                projectile.update();

                if (projectile.getPosition().getX() < -50 ||
                        projectile.getPosition().getX() > config.GameConfig.CURRENT_ARENA_WIDTH + 50 ||
                        projectile.getPosition().getY() < -50 ||
                        projectile.getPosition().getY() > config.GameConfig.CURRENT_ARENA_HEIGHT + 50) {
                    projectile.setActive(false);
                }
            }
        }
    }
}