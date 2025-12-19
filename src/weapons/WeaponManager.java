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

}