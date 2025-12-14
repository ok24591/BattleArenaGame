package weapons;

import projectiles.Projectile;
import java.util.List;
import java.util.ArrayList;

public abstract class Weapon {
    protected String name;
    protected double damage;
    protected double projectileSpeed;
    protected long cooldown;
    protected int ammo;
    protected int maxAmmo;
    protected long lastFireTime;

    public Weapon(String name, double damage, double projectileSpeed, long cooldown, int maxAmmo) {
        this.name = name;
        this.damage = damage;
        this.projectileSpeed = projectileSpeed;
        this.cooldown = cooldown;
        this.maxAmmo = maxAmmo;
        this.ammo = maxAmmo;
        this.lastFireTime = 0;
    }

    public boolean canFire() {
        long currentTime = System.currentTimeMillis();
        return ammo > 0 && (currentTime - lastFireTime >= cooldown);
    }

    public Projectile fire(double x, double y, double direction, boolean isPlayer1) {
        if (canFire()) {
            lastFireTime = System.currentTimeMillis();
            ammo--;
            return createProjectile(x, y, direction, isPlayer1);
        }
        return null;
    }

    public List<Projectile> createProjectiles(double x, double y, double direction, boolean isPlayer1) {
        List<Projectile> projectiles = new ArrayList<>();
        Projectile projectile = createProjectile(x, y, direction, isPlayer1);
        if (projectile != null) {
            projectiles.add(projectile);
        }
        return projectiles;
    }

    protected abstract Projectile createProjectile(double x, double y, double direction, boolean isPlayer1);

    public void reload() {
        ammo = maxAmmo;
    }

    public String getName() { return name; }
    public double getDamage() { return damage; }
    public double getProjectileSpeed() { return projectileSpeed; }
    public long getCooldown() { return cooldown; }
    public int getAmmo() { return ammo; }
    public int getMaxAmmo() { return maxAmmo; }
    public long getLastFireTime() { return lastFireTime; }

    public void setAmmo(int ammo) { this.ammo = ammo; }
}