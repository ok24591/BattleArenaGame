package characters;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Warrior extends Fighter {
    public Warrior(boolean isPlayer1) {
        super("Warrior",
                config.CharacterConfig.Warrior.HEALTH,
                config.CharacterConfig.Warrior.SPEED,
                isPlayer1,
                config.CharacterConfig.Warrior.DEFAULT_WEAPON,
                50, 50, 25);
    }

    @Override
    public Rectangle getVisualRepresentation() {
        Rectangle rect = new Rectangle(50, 50);

        rect.setX(-25);
        rect.setY(-25);

        rect.setFill(isPlayer1() ? Color.BLUE : Color.RED);
        rect.setStroke(Color.WHITE);
        rect.setStrokeWidth(2);

        return rect;
    }
}