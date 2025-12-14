package characters;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Archer extends Fighter {
    public Archer(boolean isPlayer1) {
        super("Archer",
                config.CharacterConfig.Archer.HEALTH,
                config.CharacterConfig.Archer.SPEED,
                isPlayer1,
                config.CharacterConfig.Archer.DEFAULT_WEAPON,
                35, 40, 17.5);
    }

    @Override
    public Polygon getVisualRepresentation() {
        Polygon triangle = new Polygon();

        triangle.getPoints().addAll(
                0.0, -20.0,
                -17.0, 17.0,
                17.0, 17.0
        );

        triangle.setFill(isPlayer1() ? Color.GREEN : Color.ORANGE);
        triangle.setStroke(Color.WHITE);
        triangle.setStrokeWidth(2);
        return triangle;
    }
}