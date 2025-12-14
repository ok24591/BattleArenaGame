package weapons;

import projectiles.Arrow;
import projectiles.Projectile;
import java.util.ArrayList;
import java.util.List;

public class RecurveBow extends Weapon {
    public RecurveBow() {
        super("Recurve Bow",
                config.WeaponConfig.RecurveBow.DAMAGE,
                config.WeaponConfig.RecurveBow.SPEED,
                config.WeaponConfig.RecurveBow.COOLDOWN,
                config.WeaponConfig.RecurveBow.AMMO);
    }

    @Override
    protected Projectile createProjectile(double x, double y, double direction, boolean isPlayer1) {
        return new Arrow(x, y, direction, damage, projectileSpeed, isPlayer1);
    }

    @Override
    public List<Projectile> createProjectiles(double x, double y, double direction, boolean isPlayer1) {
        List<Projectile> arrows = new ArrayList<>();
        arrows.add(new Arrow(x, y, direction, damage, projectileSpeed, isPlayer1));
        return arrows;
    }
}