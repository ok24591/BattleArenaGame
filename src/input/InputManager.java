package input;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import java.util.HashMap;
import java.util.Map;

public class InputManager {
    private Pane root;
    private Map<KeyCode, Boolean> pressedKeys;
    private Runnable player1Up, player1Down, player1Left, player1Right, player1Shoot, player1Special, player1SwitchWeapon;
    private Runnable player2Up, player2Down, player2Left, player2Right, player2Shoot, player2Special, player2SwitchWeapon;

    public InputManager(Pane root) {
        this.root = root;
        this.pressedKeys = new HashMap<>();
        setupKeyHandling();
    }

    private void setupKeyHandling() {
        root.setOnKeyPressed(e -> {
            pressedKeys.put(e.getCode(), true);
            handleKeyPress(e.getCode());
        });

        root.setOnKeyReleased(e -> {
            pressedKeys.put(e.getCode(), false);
        });

        root.setFocusTraversable(true);
        root.requestFocus();
    }

    private void handleKeyPress(KeyCode code) {
        switch (code) {
            case W -> { if (player1Up != null) player1Up.run(); }
            case S -> { if (player1Down != null) player1Down.run(); }
            case A -> { if (player1Left != null) player1Left.run(); }
            case D -> { if (player1Right != null) player1Right.run(); }
            case F -> { if (player1Shoot != null) player1Shoot.run(); }
            case R -> { if (player1Special != null) player1Special.run(); }
            case Q -> { if (player1SwitchWeapon != null) player1SwitchWeapon.run(); }

            case UP -> { if (player2Up != null) player2Up.run(); }
            case DOWN -> { if (player2Down != null) player2Down.run(); }
            case LEFT -> { if (player2Left != null) player2Left.run(); }
            case RIGHT -> { if (player2Right != null) player2Right.run(); }
            case L -> { if (player2Shoot != null) player2Shoot.run(); }
            case P -> { if (player2Special != null) player2Special.run(); }
            case O -> { if (player2SwitchWeapon != null) player2SwitchWeapon.run(); }
        }
    }

    public void setPlayer1Controls(Runnable up, Runnable down, Runnable left, Runnable right,
                                   Runnable shoot, Runnable special, Runnable switchWeapon) {
        this.player1Up = up;
        this.player1Down = down;
        this.player1Left = left;
        this.player1Right = right;
        this.player1Shoot = shoot;
        this.player1Special = special;
        this.player1SwitchWeapon = switchWeapon;
    }

    public void setPlayer2Controls(Runnable up, Runnable down, Runnable left, Runnable right,
                                   Runnable shoot, Runnable special, Runnable switchWeapon) {
        this.player2Up = up;
        this.player2Down = down;
        this.player2Left = left;
        this.player2Right = right;
        this.player2Shoot = shoot;
        this.player2Special = special;
        this.player2SwitchWeapon = switchWeapon;
    }

    public boolean isKeyPressed(KeyCode code) {
        return pressedKeys.getOrDefault(code, false);
    }

    public void clear() {
        pressedKeys.clear();
    }
}