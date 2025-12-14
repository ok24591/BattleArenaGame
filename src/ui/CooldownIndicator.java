package ui;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class CooldownIndicator extends StackPane {
    private Rectangle background;
    private Rectangle cooldownFill;
    private double width;
    private double height;

    public CooldownIndicator(double width, double height) {
        this.width = width;
        this.height = height;

        background = new Rectangle(width, height);
        background.setFill(Color.web("#2A2F36"));
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);
        background.setArcWidth(5);
        background.setArcHeight(5);

        cooldownFill = new Rectangle(width, height);
        cooldownFill.setFill(Color.LIME);
        cooldownFill.setArcWidth(5);
        cooldownFill.setArcHeight(5);

        getChildren().addAll(background, cooldownFill);
    }

    public void updateCooldown(long lastFireTime, long cooldown) {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastFire = currentTime - lastFireTime;

        if (timeSinceLastFire < cooldown) {
            double percentage = (double) timeSinceLastFire / cooldown;
            cooldownFill.setWidth(width * percentage);

            if (percentage < 0.33) {
                cooldownFill.setFill(Color.RED);
            } else if (percentage < 0.66) {
                cooldownFill.setFill(Color.ORANGE);
            } else {
                cooldownFill.setFill(Color.YELLOW);
            }
        } else {
            cooldownFill.setWidth(width);
            cooldownFill.setFill(Color.LIME);
        }
    }
}