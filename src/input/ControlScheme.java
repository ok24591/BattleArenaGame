/*package input;

import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.Map;

public class ControlScheme {
    private Map<Action, KeyCode> controls;

    public enum Action {
        MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT,
        SHOOT, SWITCH_WEAPON, SPECIAL_ABILITY, RELOAD
    }

    public ControlScheme(boolean isPlayer1) {
        controls = new HashMap<>();
        if (isPlayer1) {
            controls.put(Action.MOVE_UP, KeyCode.W);
            controls.put(Action.MOVE_DOWN, KeyCode.S);
            controls.put(Action.MOVE_LEFT, KeyCode.A);
            controls.put(Action.MOVE_RIGHT, KeyCode.D);
            controls.put(Action.SHOOT, KeyCode.F);
            controls.put(Action.SWITCH_WEAPON, KeyCode.Q);
            controls.put(Action.SPECIAL_ABILITY, KeyCode.E);
            controls.put(Action.RELOAD, KeyCode.DIGIT1);
        } else {
            controls.put(Action.MOVE_UP, KeyCode.UP);
            controls.put(Action.MOVE_DOWN, KeyCode.DOWN);
            controls.put(Action.MOVE_LEFT, KeyCode.LEFT);
            controls.put(Action.MOVE_RIGHT, KeyCode.RIGHT);
            controls.put(Action.SHOOT, KeyCode.L);
            controls.put(Action.SWITCH_WEAPON, KeyCode.P);
            controls.put(Action.SPECIAL_ABILITY, KeyCode.O);
            controls.put(Action.RELOAD, KeyCode.DIGIT2);
        }
    }

    public KeyCode getKey(Action action) {
        return controls.get(action);
    }

    public Map<Action, KeyCode> getAllControls() {
        return new HashMap<>(controls);
    }
}*/