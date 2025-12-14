package characters;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Mage extends Fighter {
    public Mage(boolean isPlayer1) {
        super("Mage",
                config.CharacterConfig.Mage.HEALTH,
                config.CharacterConfig.Mage.SPEED,
                isPlayer1,
                config.CharacterConfig.Mage.DEFAULT_WEAPON,
                60, 60, 30);
    }

    @Override
    public Circle getVisualRepresentation() {
        Circle circle = new Circle(30);
        circle.setFill(isPlayer1() ? Color.DARKBLUE : Color.DARKRED);
        circle.setStroke(Color.PURPLE);
        circle.setStrokeWidth(2);
        return circle;
    }
}