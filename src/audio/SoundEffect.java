package audio;

public enum SoundEffect {
    SHOOT("shoot"),
    HIT("hit"),
    EXPLOSION("explosion"),
    WEAPON_SWITCH("weapon_switch"),
    ABILITY("ability"),
    VICTORY("victory"),
    DEFEAT("defeat"),
    MENU_SELECT("menu_select"),
    MENU_CONFIRM("menu_confirm");

    private final String soundName;

    SoundEffect(String soundName) {
        this.soundName = soundName;
    }

    public String getSoundName() {
        return soundName;
    }

    public void play() {
        AudioManager.getInstance().playSound(soundName);
    }
}