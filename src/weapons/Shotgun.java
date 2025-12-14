package weapons;

import projectiles.ShotgunPellet;
import projectiles.Projectile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Shotgun extends Weapon {
    private Random random;
    private static final double SPREAD_ANGLE = 0.5;
    private static final int PELLET_COUNT = 8;

    public Shotgun() {
        super("Shotgun",
                config.WeaponConfig.Shotgun.DAMAGE,
                config.WeaponConfig.Shotgun.SPEED,
                config.WeaponConfig.Shotgun.COOLDOWN,
                config.WeaponConfig.Shotgun.AMMO);
        this.random = new Random();
    }

    @Override
    protected Projectile createProjectile(double x, double y, double direction, boolean isPlayer1) {
        double spread = (random.nextDouble() - 0.5) * SPREAD_ANGLE;
        double pelletDirection = direction + spread;
        double pelletDamage = damage / PELLET_COUNT * (0.8 + random.nextDouble() * 0.4);

        double offsetX = (random.nextDouble() - 0.5) * 15;
        double offsetY = (random.nextDouble() - 0.5) * 15;

        return new ShotgunPellet(x + offsetX, y + offsetY, pelletDirection, pelletDamage, projectileSpeed, isPlayer1);
    }

    @Override
    public List<Projectile> createProjectiles(double x, double y, double direction, boolean isPlayer1) {
        List<Projectile> pellets = new ArrayList<>();

        for (int i = 0; i < PELLET_COUNT; i++) {
            Projectile pellet = createProjectile(x, y, direction, isPlayer1);
            pellets.add(pellet);
        }

        return pellets;
    }
}