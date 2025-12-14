package characters;

public class CharacterFactory {
    public static Fighter createCharacter(String characterType, boolean isPlayer1) {
        switch (characterType.toLowerCase()) {
            case "warrior":
                return new Warrior(isPlayer1);
            case "mage":
                return new Mage(isPlayer1);
            case "archer":
                return new Archer(isPlayer1);
            case "assassin":
                return new Assassin(isPlayer1);
            default:
                return new Warrior(isPlayer1);
        }
    }
}