package weapons;

import projectiles.SniperBullet;
import projectiles.Projectile;

public class SniperRifle extends Weapon {
    public SniperRifle() {
        super("Sniper Rifle",
                config.WeaponConfig.SniperRifle.DAMAGE,
                config.WeaponConfig.SniperRifle.SPEED,
                config.WeaponConfig.SniperRifle.COOLDOWN,
                config.WeaponConfig.SniperRifle.AMMO);
    }

    @Override
    protected Projectile createProjectile(double x, double y, double direction, boolean isPlayer1) {
        return new SniperBullet(x, y, direction, damage, projectileSpeed, isPlayer1);
    }
}