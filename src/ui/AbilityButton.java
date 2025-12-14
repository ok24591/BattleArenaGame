package ui;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AbilityButton {
    private VBox container;
    private Button abilityButton;
    private ProgressBar cooldownBar;
    private Label cooldownLabel;
    private AnimationTimer cooldownTimer;
    private long lastUsedTime;
    private long cooldownDuration;
    private Runnable onAbilityUsed;

    public AbilityButton(double x, double y, String abilityName, long cooldownDuration, Runnable onAbilityUsed) {
        this.cooldownDuration = cooldownDuration;
        this.onAbilityUsed = onAbilityUsed;
        this.lastUsedTime = 0;
        createAbilityButton(x, y, abilityName);
        startCooldownTimer();
    }

    private void createAbilityButton(double x, double y, String abilityName) {
        container = new VBox(5);
        container.setLayoutX(x);
        container.setLayoutY(y);

        abilityButton = new Button(abilityName);
        abilityButton.setStyle("-fx-background-color: linear-gradient(to bottom, #9b59b6, #8e44ad); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; " +
                "-fx-background-radius: 15;");
        abilityButton.setOnAction(e -> useAbility());

        cooldownBar = new ProgressBar(1.0);
        cooldownBar.setPrefWidth(100);
        cooldownBar.setPrefHeight(6);
        cooldownBar.setStyle("-fx-accent: #3498db;");
        cooldownBar.setVisible(false);

        cooldownLabel = new Label("READY");
        cooldownLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 10; -fx-font-weight: bold;");

        container.getChildren().addAll(abilityButton, cooldownBar, cooldownLabel);

        // Hover effects
        abilityButton.setOnMouseEntered(e -> {
            if (isReady()) {
                abilityButton.setStyle("-fx-background-color: linear-gradient(to bottom, #a569bd, #9b59b6); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; " +
                        "-fx-background-radius: 15;");
            }
        });

        abilityButton.setOnMouseExited(e -> {
            if (isReady()) {
                abilityButton.setStyle("-fx-background-color: linear-gradient(to bottom, #9b59b6, #8e44ad); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; " +
                        "-fx-background-radius: 15;");
            }
        });
    }

    private void startCooldownTimer() {
        cooldownTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateCooldown();
            }
        };
        cooldownTimer.start();
    }

    private void updateCooldown() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastUse = currentTime - lastUsedTime;
        double cooldownProgress = Math.min(1.0, (double) timeSinceLastUse / cooldownDuration);

        if (cooldownProgress < 1.0) {
            cooldownBar.setVisible(true);
            cooldownBar.setProgress(cooldownProgress);
            double remaining = (cooldownDuration - timeSinceLastUse) / 1000.0;
            cooldownLabel.setText(String.format("%.1fs", remaining));
            cooldownLabel.setStyle("-fx-text-fill: #e74c3c;");
            abilityButton.setDisable(true);
            abilityButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: #bdc3c7; " +
                    "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 15;");
        } else {
            cooldownBar.setVisible(false);
            cooldownLabel.setText("READY");
            cooldownLabel.setStyle("-fx-text-fill: #2ecc71;");
            abilityButton.setDisable(false);
            abilityButton.setStyle("-fx-background-color: linear-gradient(to bottom, #9b59b6, #8e44ad); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; " +
                    "-fx-background-radius: 15;");
        }
    }

    private void useAbility() {
        if (isReady()) {
            lastUsedTime = System.currentTimeMillis();
            if (onAbilityUsed != null) {
                onAbilityUsed.run();
            }
        }
    }

    private boolean isReady() {
        return System.currentTimeMillis() - lastUsedTime >= cooldownDuration;
    }

    public void stop() {
        if (cooldownTimer != null) {
            cooldownTimer.stop();
        }
    }

    public VBox getContainer() {
        return container;
    }
}