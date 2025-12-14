package weapons;

import projectiles.Bullet;
import projectiles.Projectile;

public class Pistol extends Weapon {
    public Pistol() {
        super("Pistol",
                config.WeaponConfig.Pistol.DAMAGE,
                config.WeaponConfig.Pistol.SPEED,
                config.WeaponConfig.Pistol.COOLDOWN,
                config.WeaponConfig.Pistol.AMMO);
    }

    @Override
    protected Projectile createProjectile(double x, double y, double direction, boolean isPlayer1) {
        return new Bullet(x, y, direction, damage, projectileSpeed, isPlayer1);
    }
}