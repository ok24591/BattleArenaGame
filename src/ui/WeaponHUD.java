package ui;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.animation.*;
import javafx.util.Duration;

public class WeaponHUD extends VBox {
    private Label weaponName;
    private Label weaponType;
    private HBox ammoContainer;
    private Label ammoCount;
    private Rectangle ammoBar;
    private Rectangle ammoBackground;
    private CooldownIndicator cooldownIndicator;
    private boolean isPlayer1;
    private Timeline ammoPulse;
    private Timeline reloadAnimation;

    public WeaponHUD(boolean isPlayer1) {
        this.isPlayer1 = isPlayer1;

        setSpacing(8);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(12, 16, 12, 16));
        setStyle("-fx-background-color: rgba(26, 29, 40, 0.9); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK.deriveColor(0, 1, 1, 0.3));
        shadow.setRadius(10);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        setEffect(shadow);

        createWeaponName();
        createAmmoDisplay();
        createCooldownIndicator();

        setupAnimations();

        getChildren().addAll(weaponName, weaponType, ammoContainer, cooldownIndicator);
    }

    private void createWeaponName() {
        weaponName = new Label();
        weaponName.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-letter-spacing: 0.5px;");

        Color textColor = isPlayer1 ? Color.web("#00D4FF") : Color.web("#FF2A6D");
        weaponName.setTextFill(textColor);

        weaponType = new Label();
        weaponType.setStyle("-fx-font-size: 10; -fx-font-weight: normal; -fx-text-fill: #8A8A8A;");
    }

    private void createAmmoDisplay() {
        ammoContainer = new HBox(10);
        ammoContainer.setAlignment(Pos.CENTER_LEFT);
        ammoContainer.setPadding(new Insets(5, 0, 0, 0));

        ammoBackground = new Rectangle(120, 6);
        ammoBackground.setArcWidth(3);
        ammoBackground.setArcHeight(3);
        ammoBackground.setFill(createAmmoBackgroundGradient());

        ammoBar = new Rectangle(120, 6);
        ammoBar.setArcWidth(3);
        ammoBar.setArcHeight(3);
        ammoBar.setFill(createAmmoGradient());

        ammoCount = new Label();
        ammoCount.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");
        ammoCount.setMinWidth(60);

        VBox ammoBarContainer = new VBox(2);
        ammoBarContainer.setAlignment(Pos.CENTER_LEFT);

        Label ammoPercentage = new Label("AMMO");
        ammoPercentage.setStyle("-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: #8A8A8A;");

        StackPane barStack = new StackPane();
        barStack.getChildren().addAll(ammoBackground, ammoBar);

        ammoBarContainer.getChildren().addAll(ammoPercentage, barStack);
        ammoContainer.getChildren().addAll(ammoBarContainer, ammoCount);
    }

    private LinearGradient createAmmoBackgroundGradient() {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2A2F40")),
                new Stop(1, Color.web("#363B4A"))
        );
    }

    private LinearGradient createAmmoGradient() {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#00D4FF")),
                new Stop(0.5, Color.web("#00B8E6")),
                new Stop(1, Color.web("#0099CC"))
        );
    }

    private LinearGradient createCriticalAmmoGradient() {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FF2A6D")),
                new Stop(0.5, Color.web("#FF1A5C")),
                new Stop(1, Color.web("#E0245E"))
        );
    }

    private LinearGradient createWarningAmmoGradient() {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FFA726")),
                new Stop(0.5, Color.web("#FF9800")),
                new Stop(1, Color.web("#F57C00"))
        );
    }

    private void createCooldownIndicator() {
        cooldownIndicator = new CooldownIndicator(140, 4);
    }

    private void setupAnimations() {
        ammoPulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(ammoBar.scaleXProperty(), 1.0),
                        new KeyValue(ammoBar.scaleYProperty(), 1.0),
                        new KeyValue(ammoCount.scaleXProperty(), 1.0),
                        new KeyValue(ammoCount.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(ammoBar.scaleXProperty(), 1.05),
                        new KeyValue(ammoBar.scaleYProperty(), 1.05),
                        new KeyValue(ammoCount.scaleXProperty(), 1.05),
                        new KeyValue(ammoCount.scaleYProperty(), 1.05)
                ),
                new KeyFrame(Duration.millis(800),
                        new KeyValue(ammoBar.scaleXProperty(), 1.0),
                        new KeyValue(ammoBar.scaleYProperty(), 1.0),
                        new KeyValue(ammoCount.scaleXProperty(), 1.0),
                        new KeyValue(ammoCount.scaleYProperty(), 1.0)
                )
        );
        ammoPulse.setCycleCount(Timeline.INDEFINITE);
        ammoPulse.setAutoReverse(true);

        reloadAnimation = new Timeline();
    }

    public void updateWeaponInfo(weapons.Weapon weapon) {
        String weaponNameText = weapon.getName().toUpperCase();
        weaponName.setText(weaponNameText);

        String type = getWeaponType(weapon);
        weaponType.setText(type);

        int currentAmmo = weapon.getAmmo();
        int maxAmmo = weapon.getMaxAmmo();
        double ammoPercentage = (double) currentAmmo / maxAmmo;

        double targetWidth = 120 * ammoPercentage;
        animateAmmoBar(targetWidth);

        updateAmmoCount(currentAmmo, maxAmmo, ammoPercentage);

        cooldownIndicator.updateCooldown(weapon.getLastFireTime(), weapon.getCooldown());

        handleLowAmmoEffects(ammoPercentage);
    }

    private String getWeaponType(weapons.Weapon weapon) {
        String name = weapon.getName().toLowerCase();
        if (name.contains("rifle") || name.contains("assault")) {
            return "ASSAULT RIFLE";
        } else if (name.contains("pistol") || name.contains("handgun")) {
            return "SIDEARM";
        } else if (name.contains("shotgun")) {
            return "SHOTGUN";
        } else if (name.contains("sniper")) {
            return "SNIPER";
        } else if (name.contains("rocket") || name.contains("launcher")) {
            return "EXPLOSIVE";
        } else if (name.contains("melee") || name.contains("knife")) {
            return "MELEE";
        } else {
            return "STANDARD";
        }
    }

    public void updateWeaponInfo(String name, int ammo, int maxAmmo, long lastFireTime, long cooldown) {
        String weaponNameText = name.toUpperCase();
        weaponName.setText(weaponNameText);

        String type = getWeaponType(name);
        weaponType.setText(type);

        double ammoPercentage = (double) ammo / maxAmmo;

        double targetWidth = 120 * ammoPercentage;
        animateAmmoBar(targetWidth);

        updateAmmoCount(ammo, maxAmmo, ammoPercentage);

        cooldownIndicator.updateCooldown(lastFireTime, cooldown);

        handleLowAmmoEffects(ammoPercentage);
    }

    private String getWeaponType(String name) {
        String nameLower = name.toLowerCase();
        if (nameLower.contains("rifle") || nameLower.contains("assault")) {
            return "ASSAULT RIFLE";
        } else if (nameLower.contains("pistol") || nameLower.contains("handgun")) {
            return "SIDEARM";
        } else if (nameLower.contains("shotgun")) {
            return "SHOTGUN";
        } else if (nameLower.contains("sniper")) {
            return "SNIPER";
        } else if (nameLower.contains("rocket") || nameLower.contains("launcher")) {
            return "EXPLOSIVE";
        } else if (nameLower.contains("melee") || nameLower.contains("knife")) {
            return "MELEE";
        } else {
            return "STANDARD";
        }
    }

    private void animateAmmoBar(double targetWidth) {
        Timeline widthAnimation = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(ammoBar.widthProperty(), targetWidth)
                )
        );
        widthAnimation.play();
    }

    private void updateAmmoCount(int currentAmmo, int maxAmmo, double percentage) {
        String ammoText = String.format("%02d/%02d", currentAmmo, maxAmmo);
        ammoCount.setText(ammoText);

        if (percentage > 0.5) {
            ammoCount.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");
            ammoBar.setFill(createAmmoGradient());
        } else if (percentage > 0.2) {
            ammoCount.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #FFA726;");
            ammoBar.setFill(createWarningAmmoGradient());
        } else {
            ammoCount.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #FF2A6D;");
            ammoBar.setFill(createCriticalAmmoGradient());
        }
    }

    private void handleLowAmmoEffects(double percentage) {
        if (percentage <= 0.2) {
            if (ammoPulse.getStatus() != Animation.Status.RUNNING) {
                ammoPulse.play();
            }

            if (percentage <= 0.1) {
                playLowAmmoShake();
            }
        } else {
            if (ammoPulse.getStatus() == Animation.Status.RUNNING) {
                ammoPulse.stop();
                ammoBar.setScaleX(1.0);
                ammoBar.setScaleY(1.0);
                ammoCount.setScaleX(1.0);
                ammoCount.setScaleY(1.0);
            }
        }
    }

    private void playLowAmmoShake() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(100), ammoContainer);
        shake.setFromX(0);
        shake.setToX(3);
        shake.setCycleCount(2);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> ammoContainer.setTranslateX(0));
        shake.play();
    }

    public void showReloadAnimation() {
        if (reloadAnimation.getStatus() == Animation.Status.RUNNING) {
            reloadAnimation.stop();
        }

        reloadAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(ammoBar.fillProperty(), Color.web("#FFFFFF"))
                ),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(ammoBar.fillProperty(), ammoBar.getFill())
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(ammoBar.fillProperty(), Color.web("#FFFFFF"))
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(ammoBar.fillProperty(), ammoBar.getFill())
                )
        );

        reloadAnimation.play();
    }

    public void setPosition(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);
    }

    public void reset() {
        if (ammoPulse.getStatus() == Animation.Status.RUNNING) {
            ammoPulse.stop();
        }
        if (reloadAnimation.getStatus() == Animation.Status.RUNNING) {
            reloadAnimation.stop();
        }

        ammoBar.setWidth(120);
        ammoBar.setFill(createAmmoGradient());
        ammoBar.setScaleX(1.0);
        ammoBar.setScaleY(1.0);
        ammoCount.setScaleX(1.0);
        ammoCount.setScaleY(1.0);
        ammoContainer.setTranslateX(0);
        ammoCount.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");
    }
}