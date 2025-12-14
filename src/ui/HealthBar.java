package ui;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Glow;

public class HealthBar extends StackPane {
    private Rectangle background;
    private Rectangle healthFill;
    private Rectangle damageOverlay;
    private Text healthText;
    private Text healthPercentage;
    private double maxHealth;
    private double width;
    private double height;
    private Color primaryColor;
    private Color secondaryColor;
    private boolean isPlayer1;
    private Timeline damageAnimation;
    private Timeline glowAnimation;
    private boolean isCritical;

    public HealthBar(double width, double height, double maxHealth, boolean isPlayer1) {
        this.width = width;
        this.height = height;
        this.maxHealth = maxHealth;
        this.isPlayer1 = isPlayer1;
        this.isCritical = false;

        this.primaryColor = isPlayer1 ? Color.web("#00D4FF") : Color.web("#FF2A6D");
        this.secondaryColor = isPlayer1 ? Color.web("#00B8E6") : Color.web("#FF1A5C");

        createHealthBar();
        setupAnimations();
    }

    private void createHealthBar() {
        background = new Rectangle(width, height);
        background.setFill(createBackgroundGradient());
        background.setStroke(Color.web("#1A1D28"));
        background.setStrokeWidth(1.5);
        background.setArcWidth(8);
        background.setArcHeight(8);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK.deriveColor(0, 1, 1, 0.3));
        shadow.setRadius(5);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        background.setEffect(shadow);

        healthFill = new Rectangle(width, height);
        healthFill.setFill(createHealthGradient());
        healthFill.setArcWidth(8);
        healthFill.setArcHeight(8);

        damageOverlay = new Rectangle(width, height);
        damageOverlay.setFill(Color.TRANSPARENT);
        damageOverlay.setArcWidth(8);
        damageOverlay.setArcHeight(8);
        damageOverlay.setOpacity(0);

        healthText = new Text(String.valueOf((int)maxHealth));
        healthText.setFill(Color.WHITE);
        healthText.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        healthPercentage = new Text("100%");
        healthPercentage.setFill(Color.WHITE.deriveColor(0, 1, 1, 0.7));
        healthPercentage.setStyle("-fx-font-weight: normal; -fx-font-size: 10;");
        healthPercentage.setTranslateY(12);

        StackPane textContainer = new StackPane(healthText, healthPercentage);
        textContainer.setAlignment(Pos.CENTER);

        getChildren().addAll(background, healthFill, damageOverlay, textContainer);
        setAlignment(Pos.CENTER_LEFT);
    }

    private LinearGradient createBackgroundGradient() {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1E2230")),
                new Stop(1, Color.web("#2A2F40"))
        );
    }

    private LinearGradient createHealthGradient() {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, primaryColor),
                new Stop(1, secondaryColor)
        );
    }

    private void setupAnimations() {
        damageAnimation = new Timeline();

        glowAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(healthFill.opacityProperty(), 1.0),
                        new KeyValue(healthText.fillProperty(), Color.WHITE)
                ),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(healthFill.opacityProperty(), 0.7),
                        new KeyValue(healthText.fillProperty(), Color.web("#FF4444"))
                ),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(healthFill.opacityProperty(), 1.0),
                        new KeyValue(healthText.fillProperty(), Color.WHITE)
                )
        );
        glowAnimation.setCycleCount(Timeline.INDEFINITE);
        glowAnimation.setAutoReverse(true);
    }

    public void updateHealth(double currentHealth) {
        double percentage = Math.max(0, currentHealth) / maxHealth;
        double newWidth = width * percentage;

        animateWidthChange(newWidth);

        healthText.setText(String.valueOf((int)currentHealth));
        healthPercentage.setText(String.format("%.0f%%", percentage * 100));

        updateVisualState(percentage);

        if (currentHealth < maxHealth) {
            showDamageEffect();
        }
    }

    private void animateWidthChange(double targetWidth) {
        if (damageAnimation.getStatus() == Animation.Status.RUNNING) {
            damageAnimation.stop();
        }

        KeyValue widthValue = new KeyValue(healthFill.widthProperty(), targetWidth);
        KeyFrame widthFrame = new KeyFrame(Duration.millis(300), widthValue);

        damageAnimation.getKeyFrames().clear();
        damageAnimation.getKeyFrames().add(widthFrame);
        damageAnimation.play();
    }

    private void updateVisualState(double percentage) {
        boolean wasCritical = isCritical;
        isCritical = percentage <= 0.3;

        if (percentage > 0.6) {
            healthText.setFill(Color.WHITE);
            healthPercentage.setFill(Color.WHITE.deriveColor(0, 1, 1, 0.7));
            healthFill.setFill(createHealthGradient());

            if (wasCritical && !isCritical) {
                glowAnimation.stop();
                healthFill.setOpacity(1.0);
            }
        } else if (percentage > 0.3) {
            healthText.setFill(Color.web("#FFD700"));
            healthPercentage.setFill(Color.web("#FFD700").deriveColor(0, 1, 1, 0.7));
            healthFill.setFill(createWarningGradient());

            if (glowAnimation.getStatus() == Animation.Status.RUNNING) {
                glowAnimation.stop();
                healthFill.setOpacity(1.0);
            }
        } else {
            healthText.setFill(Color.web("#FF4444"));
            healthPercentage.setFill(Color.web("#FF4444").deriveColor(0, 1, 1, 0.7));
            healthFill.setFill(createCriticalGradient());

            if (!glowAnimation.getStatus().equals(Animation.Status.RUNNING)) {
                glowAnimation.play();
            }

            if (percentage > 0) {
                playCriticalShake();
            }
        }
    }

    private LinearGradient createWarningGradient() {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FFA726")),
                new Stop(1, Color.web("#FF7043"))
        );
    }

    private LinearGradient createCriticalGradient() {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FF4444")),
                new Stop(1, Color.web("#D32F2F"))
        );
    }

    private void showDamageEffect() {
        Timeline flashTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(damageOverlay.fillProperty(), Color.WHITE),
                        new KeyValue(damageOverlay.opacityProperty(), 0.6)
                ),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(damageOverlay.fillProperty(), Color.WHITE),
                        new KeyValue(damageOverlay.opacityProperty(), 0.3)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(damageOverlay.fillProperty(), Color.TRANSPARENT),
                        new KeyValue(damageOverlay.opacityProperty(), 0)
                )
        );

        ScaleTransition bounce = new ScaleTransition(Duration.millis(150), this);
        bounce.setFromX(1.0);
        bounce.setFromY(1.0);
        bounce.setToX(0.98);
        bounce.setToY(0.98);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(2);

        flashTimeline.play();
        bounce.play();
    }

    private void playCriticalShake() {
        if (!isCritical) return;

        TranslateTransition shake = new TranslateTransition(Duration.millis(50), this);
        shake.setFromX(0);
        shake.setToX(2);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> setTranslateX(0));

        shake.setToX(Math.random() * 3 + 1);

        shake.play();
    }

    public void setPosition(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);
    }

    public void reset() {
        if (damageAnimation.getStatus() == Animation.Status.RUNNING) {
            damageAnimation.stop();
        }
        if (glowAnimation.getStatus() == Animation.Status.RUNNING) {
            glowAnimation.stop();
        }

        healthFill.setWidth(width);
        healthFill.setFill(createHealthGradient());
        healthFill.setOpacity(1.0);
        healthText.setText(String.valueOf((int)maxHealth));
        healthPercentage.setText("100%");
        healthText.setFill(Color.WHITE);
        setTranslateX(0);
        setScaleX(1.0);
        setScaleY(1.0);
        isCritical = false;
    }
}