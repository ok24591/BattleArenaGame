package weapons;

import java.util.ArrayList;
import java.util.List;

public class WeaponManager {
    private List<Weapon> availableWeapons;
    private int currentWeaponIndex;

    public WeaponManager() {
        availableWeapons = new ArrayList<>();
        initializeWeapons();
        currentWeaponIndex = 0;
    }

    private void initializeWeapons() {
        availableWeapons.add(new Pistol());
        availableWeapons.add(new Shotgun());
        availableWeapons.add(new SniperRifle());
        availableWeapons.add(new FlameThrower());
        availableWeapons.add(new LaserRifle());
        availableWeapons.add(new RecurveBow());
    }

    public Weapon getCurrentWeapon() {
        return availableWeapons.get(currentWeaponIndex);
    }

    public void switchToNextWeapon() {
        currentWeaponIndex = (currentWeaponIndex + 1) % availableWeapons.size();
    }

    public void switchToPreviousWeapon() {
        currentWeaponIndex = (currentWeaponIndex - 1 + availableWeapons.size()) % availableWeapons.size();
    }

    public void switchToWeapon(int index) {
        if (index >= 0 && index < availableWeapons.size()) {
            currentWeaponIndex = index;
        }
    }

    public List<Weapon> getAvailableWeapons() {
        return new ArrayList<>(availableWeapons);
    }

    public String getCurrentWeaponName() {
        return getCurrentWeapon().getName();
    }

    public void switchToWeapon(String weaponName) {
        for (int i = 0; i < availableWeapons.size(); i++) {
            if (availableWeapons.get(i).getName().equalsIgnoreCase(weaponName)) {
                currentWeaponIndex = i;
                break;
            }
        }
    }
}