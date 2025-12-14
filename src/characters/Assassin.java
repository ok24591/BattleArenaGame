package characters;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Assassin extends Fighter {
    public Assassin(boolean isPlayer1) {
        super("Assassin",
                config.CharacterConfig.Assassin.HEALTH,
                config.CharacterConfig.Assassin.SPEED,
                isPlayer1,
                config.CharacterConfig.Assassin.DEFAULT_WEAPON,
                40, 40, 20);
    }

    @Override
    public Polygon getVisualRepresentation() {
        Polygon diamond = new Polygon();

        diamond.getPoints().addAll(
                0.0, -20.0,
                20.0, 0.0,
                0.0, 20.0,
                -20.0, 0.0
        );

        diamond.setFill(isPlayer1() ? Color.DARKSLATEBLUE : Color.DARKRED);
        diamond.setStroke(Color.BLACK);
        diamond.setStrokeWidth(2);

        return diamond;
    }
}