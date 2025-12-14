package config;

public class CharacterConfig {
    public static class Warrior {
        public static final int HEALTH = 150;
        public static final double SPEED = 4.0;
        public static final String DEFAULT_WEAPON = "Pistol";
    }

    public static class Mage {
        public static final int HEALTH = 100;
        public static final double SPEED = 3.5;
        public static final String DEFAULT_WEAPON = "FlameThrower";
    }

    public static class Archer {
        public static final int HEALTH = 120;
        public static final double SPEED = 5.0;
        public static final String DEFAULT_WEAPON = "RecurveBow";
    }

    public static class Assassin {
        public static final int HEALTH = 90;
        public static final double SPEED = 6.0;
        public static final String DEFAULT_WEAPON = "Shotgun";
    }
}