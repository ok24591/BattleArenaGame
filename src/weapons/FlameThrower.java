package weapons;

import projectiles.Fireball;
import projectiles.Projectile;

public class FlameThrower extends Weapon {
    public FlameThrower() {
        super("Flame Thrower",
                config.WeaponConfig.FlameThrower.DAMAGE,
                config.WeaponConfig.FlameThrower.SPEED,
                config.WeaponConfig.FlameThrower.COOLDOWN,
                config.WeaponConfig.FlameThrower.AMMO);
    }

    @Override
    protected Projectile createProjectile(double x, double y, double direction, boolean isPlayer1) {
        return new Fireball(x, y, direction, damage, projectileSpeed, isPlayer1);
    }
}