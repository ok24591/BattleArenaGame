package projectiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectileManager {
    private static ProjectileManager instance;
    private List<Projectile> activeProjectiles;

    private ProjectileManager() {
        activeProjectiles = new ArrayList<>();
    }

    public static ProjectileManager getInstance() {
        if (instance == null) {
            instance = new ProjectileManager();
        }
        return instance;
    }

    public void addProjectile(Projectile projectile) {
        activeProjectiles.add(projectile);
    }

    public void updateProjectiles() {
        Iterator<Projectile> iterator = activeProjectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            if (projectile.isActive()) {
                projectile.update();

                if (projectile.isOutOfBounds(config.GameConfig.ARENA_WIDTH, config.GameConfig.ARENA_HEIGHT)) {
                    projectile.setActive(false);
                }
            }

            if (!projectile.isActive()) {
                iterator.remove();
            }
        }
    }

    public List<Projectile> getActiveProjectiles() {
        return new ArrayList<>(activeProjectiles);
    }

    public void clearProjectiles() {
        activeProjectiles.clear();
    }
}