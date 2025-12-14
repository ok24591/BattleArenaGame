package config;

public class WeaponConfig {
    public static class Pistol {
        public static final double DAMAGE = 15;
        public static final double SPEED = 8;
        public static final long COOLDOWN = 400;
        public static final int AMMO = 12;
    }

    public static class Shotgun {
        public static final double DAMAGE = 60;
        public static final double SPEED = 6;
        public static final long COOLDOWN = 800;
        public static final int AMMO = 6;
    }

    public static class SniperRifle {
        public static final double DAMAGE = 40;
        public static final double SPEED = 15;
        public static final long COOLDOWN = 1200;
        public static final int AMMO = 4;
    }

    public static class FlameThrower {
        public static final double DAMAGE = 5;
        public static final double SPEED = 4;
        public static final long COOLDOWN = 100;
        public static final int AMMO = 50;
    }

    public static class LaserRifle {
        public static final double DAMAGE = 25;
        public static final double SPEED = 12;
        public static final long COOLDOWN = 300;
        public static final int AMMO = 20;
    }

    public static class RecurveBow {
        public static final double DAMAGE = 28;
        public static final double SPEED = 10;
        public static final long COOLDOWN = 600;
        public static final int AMMO = 15;
    }
}