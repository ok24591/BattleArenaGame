package weapons;

import projectiles.LaserBeam;
import projectiles.Projectile;

public class LaserRifle extends Weapon {
    public LaserRifle() {
        super("Laser Rifle",
                25,
                12,
                300,
                20
        );
    }

    @Override
    protected Projectile createProjectile(double x, double y, double direction, boolean isPlayer1) {
        return new LaserBeam(x, y, direction, damage, projectileSpeed, isPlayer1);
    }
}