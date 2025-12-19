package ui;

import javafx.scene.control.ToggleButton;
import audio.AudioManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import audio.SoundEffect;
import audio.BackgroundMusic;
import config.GameConfig;

public class PauseMenuUI {
    private StackPane container;
    private VBox menuContainer;
    private boolean isVisible = false;
    private boolean isPaused = false;
    private Runnable onResume;
    private Runnable onRestart;
    private Runnable onMainMenu;
    private Runnable onExit;
    private ToggleButton musicToggle;
    private ToggleButton soundToggle;
    private ToggleButton restrictionToggle;
    private Label restrictionLabel;
    private BackgroundMusic backgroundMusic;

    private final Color BACKGROUND_COLOR = Color.rgb(0, 0, 0, 0.92);
    private final Color CARD_BASE = Color.web("#0A0A0F");
    private final Color CARD_ACCENT = Color.web("#1A1A24");
    private final Color PRIMARY_GLOW = Color.web("#00D4FF");
    private final Color SECONDARY_GLOW = Color.web("#FF2A6D");
    private final Color PRIMARY_TEXT = Color.web("#F5F5F5");
    private final Color SECONDARY_TEXT = Color.web("#AAAAAA");
    private final Color ACCENT_COLOR = Color.web("#00D4FF");
    private final Color BUTTON_PRIMARY = Color.web("#00D4FF");
    private final Color BUTTON_PRIMARY_HOVER = Color.web("#00B8E6");
    private final Color BUTTON_WARNING = Color.web("#FFA726");
    private final Color BUTTON_INFO = Color.web("#4A90E2");
    private final Color BUTTON_DANGER = Color.web("#FF4757");
    private final Color BUTTON_SUCCESS = Color.web("#36B37E");
    private final Color TOGGLE_ON = Color.web("#4A90E2");
    private final Color TOGGLE_OFF = Color.web("#2A2F40");

    private final Font TITLE_FONT = Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 46);
    private final Font BUTTON_FONT = Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16);
    private final Font CONTROL_FONT = Font.font("Segoe UI", FontWeight.BOLD, 12);
    private final Font HINT_FONT = Font.font("Segoe UI", FontWeight.NORMAL, 12);

    private static final double MENU_WIDTH = 620;
    private static final double MENU_HEIGHT = 720;
    private static final double BUTTON_WIDTH = 300;
    private static final double BUTTON_HEIGHT = 52;
    private static final double TOGGLE_WIDTH = 85;
    private static final double TOGGLE_HEIGHT = 42;
    private static final double BORDER_THICKNESS = 5;

    public PauseMenuUI(BackgroundMusic backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
        createPauseMenu();
    }

    private void createPauseMenu() {
        container = new StackPane();
        container.setAlignment(Pos.CENTER);
        container.setVisible(false);
        container.setMouseTransparent(true);

        Rectangle overlay = new Rectangle(1920, 1080);
        LinearGradient overlayGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 0, 0, 0.85)),
                new Stop(0.3, Color.rgb(0, 0, 0, 0.90)),
                new Stop(0.7, Color.rgb(0, 0, 0, 0.90)),
                new Stop(1, Color.rgb(0, 0, 0, 0.85))
        );
        overlay.setFill(overlayGradient);

        Pane decorativeBackground = createDecorativeBackground();

        VBox menuContent = createMenuContent();

        StackPane borderWrapper = new StackPane();
        borderWrapper.setPadding(new Insets(BORDER_THICKNESS));
        borderWrapper.setStyle(
                "-fx-background-color: linear-gradient(135deg, " + toHexString(PRIMARY_GLOW) + ", " + toHexString(SECONDARY_GLOW) + "); " +
                        "-fx-background-radius: 30; " +
                        "-fx-border-radius: 30; " +
                        "-fx-effect: dropshadow(gaussian, " + toHexString(PRIMARY_GLOW) + "40, 40, 0, 0, 0), " +
                        "dropshadow(gaussian, " + toHexString(SECONDARY_GLOW) + "40, 40, 0, 0, 0);"
        );
        borderWrapper.getChildren().add(menuContent);

        menuContainer = new VBox();
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.getChildren().add(borderWrapper);

        StackPane centeredMenu = new StackPane(menuContainer);
        centeredMenu.setPadding(new Insets(40));

        container.getChildren().addAll(overlay, decorativeBackground, centeredMenu);
    }

    private VBox createMenuContent() {
        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(70, 60, 50, 60));
        content.setMinWidth(MENU_WIDTH);
        content.setMinHeight(MENU_HEIGHT);
        content.setMaxWidth(MENU_WIDTH);
        content.setMaxHeight(MENU_HEIGHT);

        content.setStyle(
                "-fx-background-color: linear-gradient(145deg, " + toHexString(CARD_BASE) + ", " + toHexString(CARD_ACCENT) + "); " +
                        "-fx-background-radius: 24; " +
                        "-fx-border-color: linear-gradient(to right, " + toHexString(PRIMARY_GLOW) + "66, " + toHexString(SECONDARY_GLOW) + "66); " +
                        "-fx-border-radius: 24; " +
                        "-fx-border-width: 2; " +
                        "-fx-effect: dropshadow(gaussian, " + toHexString(PRIMARY_GLOW) + "25, 30, 0, 0, 0), " +
                        "dropshadow(gaussian, rgba(255,255,255,0.15), 25, 0, 0, 0);"
        );

        VBox titleSection = createTitleSection();
        VBox audioSection = createAudioControlsSection();
        VBox gameplaySection = createGameplaySection();
        VBox buttonsSection = createButtonsSection();
        HBox hintSection = createHintSection();

        content.getChildren().addAll(titleSection, audioSection, gameplaySection, buttonsSection, hintSection);
        return content;
    }

    private Pane createDecorativeBackground() {
        Pane background = new Pane();

        for (int i = 0; i < 1920; i += 80) {
            Rectangle line = new Rectangle(1, 1080);
            line.setFill(Color.web("#FFFFFF", 0.015));
            line.setTranslateX(i);
            background.getChildren().add(line);
        }

        double cornerSize = 250;
        for (int i = 0; i < 4; i++) {
            Rectangle corner = new Rectangle(cornerSize, cornerSize);
            corner.setFill(Color.TRANSPARENT);

            corner.setStyle(
                    "-fx-stroke: linear-gradient(45deg, " + toHexString(PRIMARY_GLOW) + "30, " + toHexString(SECONDARY_GLOW) + "30); " +
                            "-fx-stroke-width: 3; " +
                            "-fx-stroke-line-cap: round;"
            );

            switch (i) {
                case 0:
                    corner.setTranslateX(40);
                    corner.setTranslateY(40);
                    corner.setRotate(0);
                    break;
                case 1:
                    corner.setTranslateX(1920 - cornerSize - 40);
                    corner.setTranslateY(40);
                    corner.setRotate(90);
                    break;
                case 2:
                    corner.setTranslateX(40);
                    corner.setTranslateY(1080 - cornerSize - 40);
                    corner.setRotate(-90);
                    break;
                case 3:
                    corner.setTranslateX(1920 - cornerSize - 40);
                    corner.setTranslateY(1080 - cornerSize - 40);
                    corner.setRotate(180);
                    break;
            }

            background.getChildren().add(corner);
        }

        return background;
    }

    private VBox createTitleSection() {
        VBox titleSection = new VBox(8);
        titleSection.setAlignment(Pos.CENTER);
        titleSection.setPadding(new Insets(30, 0, 0, 0));

        Text title = new Text("GAME PAUSED");
        title.setFont(TITLE_FONT);
        title.setFill(Color.TRANSPARENT);

        LinearGradient titleGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PRIMARY_GLOW),
                new Stop(0.3, Color.WHITE),
                new Stop(0.7, Color.WHITE),
                new Stop(1, SECONDARY_GLOW)
        );
        title.setFill(titleGradient);

        DropShadow titleShadow = new DropShadow(25, PRIMARY_GLOW.brighter());
        titleShadow.setSpread(0.2);
        title.setEffect(titleShadow);

        Label subtitle = new Label("Take a moment. The adventure awaits.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(SECONDARY_TEXT);
        subtitle.setPadding(new Insets(5, 0, 0, 0));

        Rectangle divider = new Rectangle(320, 3);
        LinearGradient dividerGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.3, PRIMARY_GLOW),
                new Stop(0.7, SECONDARY_GLOW),
                new Stop(1, Color.TRANSPARENT)
        );
        divider.setFill(dividerGradient);
        divider.setArcWidth(3);
        divider.setArcHeight(3);

        DropShadow dividerGlow = new DropShadow(10, PRIMARY_GLOW);
        divider.setEffect(dividerGlow);

        titleSection.getChildren().addAll(title, divider, subtitle);
        return titleSection;
    }

    private VBox createAudioControlsSection() {
        VBox audioSection = new VBox(15);
        audioSection.setAlignment(Pos.CENTER);
        audioSection.setPadding(new Insets(15, 0, 20, 0));

        Label sectionTitle = new Label("AUDIO SETTINGS");
        sectionTitle.setFont(CONTROL_FONT);
        sectionTitle.setTextFill(SECONDARY_TEXT);

        Rectangle sectionDivider = new Rectangle(200, 2);
        sectionDivider.setFill(Color.web("#FFFFFF", 0.15));

        HBox controlsContainer = new HBox(30);
        controlsContainer.setAlignment(Pos.CENTER);

        VBox musicControl = createAudioControl("BACKGROUND MUSIC", true, TOGGLE_ON);
        VBox soundControl = createAudioControl("SOUND EFFECTS", false, BUTTON_SUCCESS);

        controlsContainer.getChildren().addAll(musicControl, soundControl);
        audioSection.getChildren().addAll(sectionTitle, sectionDivider, controlsContainer);

        return audioSection;
    }

    private VBox createAudioControl(String title, boolean isMusic, Color color) {
        VBox control = new VBox(8);
        control.setAlignment(Pos.CENTER);
        control.setMinWidth(140);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 11));
        titleLabel.setTextFill(SECONDARY_TEXT);
        titleLabel.setAlignment(Pos.CENTER);

        ToggleButton toggle = new ToggleButton();
        toggle.setPrefSize(TOGGLE_WIDTH, TOGGLE_HEIGHT);
        toggle.setMinSize(TOGGLE_WIDTH, TOGGLE_HEIGHT);

        String hexColor = toHexString(color);
        String darkerColor = toHexString(color.darker());
        String brighterColor = toHexString(color.brighter());

        String onStyle = String.format(
                "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: %f; " +
                        "-fx-border-radius: %f; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 2; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, %s55, 10, 0, 0, 2);",
                hexColor, darkerColor, TOGGLE_HEIGHT / 2, TOGGLE_HEIGHT / 2, brighterColor, hexColor
        );

        String offStyle = String.format(
                "-fx-background-color: linear-gradient(to bottom, #2A2F40, #1E2230); " +
                        "-fx-text-fill: #666666; " +
                        "-fx-font-size: 14; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: %f; " +
                        "-fx-border-radius: %f; " +
                        "-fx-border-color: #3A3F50; " +
                        "-fx-border-width: 2; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 1);",
                TOGGLE_HEIGHT / 2, TOGGLE_HEIGHT / 2
        );

        toggle.setStyle(onStyle);
        toggle.setText("ON");
        toggle.setSelected(true);

        toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                toggle.setStyle(onStyle);
                toggle.setText("ON");
                if (isMusic) {
                    if (backgroundMusic != null) {
                        backgroundMusic.setMusicEnabled(true);
                    }
                } else {
                    AudioManager.getInstance().setSoundEffectVolume(0.7);
                }
            } else {
                toggle.setStyle(offStyle);
                toggle.setText("OFF");
                if (isMusic) {
                    if (backgroundMusic != null) {
                        backgroundMusic.setMusicEnabled(false);
                    }
                } else {
                    AudioManager.getInstance().setSoundEffectVolume(0);
                }
            }
            SoundEffect.MENU_SELECT.play();
        });

        if (isMusic) {
            musicToggle = toggle;
        } else {
            soundToggle = toggle;
        }

        control.getChildren().addAll(titleLabel, toggle);
        return control;
    }

    private VBox createGameplaySection() {
        VBox gameplaySection = new VBox(15);
        gameplaySection.setAlignment(Pos.CENTER);
        gameplaySection.setPadding(new Insets(15, 0, 20, 0));

        Label sectionTitle = new Label("GAMEPLAY SETTINGS");
        sectionTitle.setFont(CONTROL_FONT);
        sectionTitle.setTextFill(SECONDARY_TEXT);

        Rectangle sectionDivider = new Rectangle(200, 2);
        sectionDivider.setFill(Color.web("#FFFFFF", 0.15));

        HBox restrictionContainer = new HBox(15);
        restrictionContainer.setAlignment(Pos.CENTER);

        restrictionLabel = new Label("Half-Screen Restriction");
        restrictionLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        restrictionLabel.setTextFill(PRIMARY_TEXT);

        restrictionToggle = new ToggleButton();
        restrictionToggle.setPrefSize(TOGGLE_WIDTH, TOGGLE_HEIGHT);
        restrictionToggle.setSelected(GameConfig.HALF_SCREEN_RESTRICTION);
        updateRestrictionToggle();

        restrictionToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            GameConfig.setHalfScreenRestriction(newVal);
            updateRestrictionToggle();
            SoundEffect.MENU_SELECT.play();
        });

        restrictionContainer.getChildren().addAll(restrictionLabel, restrictionToggle);
        gameplaySection.getChildren().addAll(sectionTitle, sectionDivider, restrictionContainer);

        return gameplaySection;
    }

    private void updateRestrictionToggle() {
        boolean enabled = GameConfig.HALF_SCREEN_RESTRICTION;
        String onStyle = String.format("-fx-background-color: linear-gradient(to bottom, %s, %s); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-background-radius: %f; -fx-border-radius: %f; -fx-border-color: %s; -fx-border-width: 2; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, %s55, 10, 0, 0, 2);", toHexString(BUTTON_DANGER), toHexString(BUTTON_DANGER.darker()), TOGGLE_HEIGHT / 2, TOGGLE_HEIGHT / 2, toHexString(BUTTON_DANGER.brighter()), toHexString(BUTTON_DANGER));
        String offStyle = String.format("-fx-background-color: linear-gradient(to bottom, %s, %s); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-background-radius: %f; -fx-border-radius: %f; -fx-border-color: %s; -fx-border-width: 2; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, %s55, 10, 0, 0, 2);", toHexString(BUTTON_SUCCESS), toHexString(BUTTON_SUCCESS.darker()), TOGGLE_HEIGHT / 2, TOGGLE_HEIGHT / 2, toHexString(BUTTON_SUCCESS.brighter()), toHexString(BUTTON_SUCCESS));

        if (enabled) {
            restrictionToggle.setStyle(onStyle);
            restrictionToggle.setText("ON");
            restrictionLabel.setText("Half-Screen Restriction");
        } else {
            restrictionToggle.setStyle(offStyle);
            restrictionToggle.setText("OFF");
            restrictionLabel.setText("Full Movement");
        }
    }

    private VBox createButtonsSection() {
        VBox buttonsSection = new VBox(10);
        buttonsSection.setAlignment(Pos.CENTER);
        buttonsSection.setPadding(new Insets(5, 0, 15, 0));
        buttonsSection.setMinWidth(BUTTON_WIDTH + 40);

        Button resumeButton = createStyledButton("RESUME GAME", BUTTON_PRIMARY, true);
        resumeButton.setOnAction(e -> {
            SoundEffect.MENU_CONFIRM.play();
            hide();
            if (onResume != null) onResume.run();
        });

        Button restartButton = createStyledButton("RESTART LEVEL", BUTTON_WARNING, false);
        restartButton.setOnAction(e -> {
            SoundEffect.MENU_CONFIRM.play();
            hide();
            if (onRestart != null) onRestart.run();
        });

        Button mainMenuButton = createStyledButton("MAIN MENU", BUTTON_INFO, false);
        mainMenuButton.setOnAction(e -> {
            SoundEffect.MENU_SELECT.play();
            hide();
            if (onMainMenu != null) onMainMenu.run();
        });

        Button exitButton = createStyledButton("EXIT TO DESKTOP", BUTTON_DANGER, false);
        exitButton.setOnAction(e -> {
            SoundEffect.MENU_CONFIRM.play();
            hide();
            if (onExit != null) onExit.run();
        });

        buttonsSection.getChildren().addAll(resumeButton, restartButton, mainMenuButton, exitButton);
        return buttonsSection;
    }

    private Button createStyledButton(String text, Color color, boolean isPrimary) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        String hexColor = toHexString(color);
        String darkerColor = toHexString(color.darker());
        String brighterColor = toHexString(color.brighter());

        String baseStyle = String.format(
                "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-weight: %s; " +
                        "-fx-background-radius: %f; " +
                        "-fx-border-radius: %f; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 2; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, %s44, 12, 0, 0, 3); " +
                        "-fx-alignment: center; " +
                        "-fx-padding: 0;",
                hexColor, darkerColor,
                isPrimary ? "#0A0A0F" : "white",
                "600",
                BUTTON_HEIGHT / 2, BUTTON_HEIGHT / 2,
                brighterColor,
                hexColor
        );

        String hoverStyle = String.format(
                "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2.5; " +
                        "-fx-effect: dropshadow(gaussian, %s77, 18, 0, 0, 4); " +
                        "-fx-translate-y: -2;",
                brighterColor, hexColor,
                hexColor
        );

        String pressedStyle = String.format(
                "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                        "-fx-effect: dropshadow(gaussian, %s33, 8, 0, 0, 1); " +
                        "-fx-translate-y: 0;",
                darkerColor, hexColor,
                hexColor
        );

        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> {
            button.setStyle(baseStyle + hoverStyle);
            SoundEffect.MENU_SELECT.play();
        });

        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        button.setOnMousePressed(e -> button.setStyle(baseStyle + pressedStyle));

        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                button.setStyle(baseStyle + hoverStyle);
            } else {
                button.setStyle(baseStyle);
            }
        });

        return button;
    }

    private HBox createHintSection() {
        HBox hintSection = new HBox(8);
        hintSection.setAlignment(Pos.CENTER);
        hintSection.setPadding(new Insets(15, 0, 0, 0));

        Text escIcon = new Text("ESC");
        escIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        escIcon.setFill(ACCENT_COLOR);

        Rectangle keyCap = new Rectangle(36, 24);
        keyCap.setArcWidth(6);
        keyCap.setArcHeight(6);
        keyCap.setFill(Color.web("#2A2A3A"));
        keyCap.setStroke(Color.web("#00D4FF", 0.4));
        keyCap.setStrokeWidth(1.5);

        StackPane keyContainer = new StackPane();
        keyContainer.getChildren().addAll(keyCap, escIcon);
        StackPane.setAlignment(escIcon, Pos.CENTER);

        Label hint = new Label("Press to continue");
        hint.setFont(HINT_FONT);
        hint.setTextFill(SECONDARY_TEXT);

        hintSection.getChildren().addAll(keyContainer, hint);
        return hintSection;
    }

    public void show() {
        if (!isVisible) {
            isVisible = true;
            isPaused = true;
            container.setVisible(true);
            container.setMouseTransparent(false);
            container.setOpacity(0);

            if (backgroundMusic != null) {
                musicToggle.setSelected(backgroundMusic.isMusicEnabled());
            }
            soundToggle.setSelected(AudioManager.getInstance().getSoundEffectVolume() > 0);
            restrictionToggle.setSelected(GameConfig.HALF_SCREEN_RESTRICTION);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), container);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), menuContainer);
            scaleIn.setFromX(0.92);
            scaleIn.setFromY(0.92);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            scaleIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            fadeIn.play();
            scaleIn.play();

            SoundEffect.MENU_SELECT.play();
        }
    }

    public void hide() {
        if (isVisible) {
            isVisible = false;
            isPaused = false;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(250), container);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setInterpolator(javafx.animation.Interpolator.EASE_IN);
            fadeOut.setOnFinished(e -> {
                container.setVisible(false);
                container.setMouseTransparent(true);
            });

            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), menuContainer);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.96);
            scaleOut.setToY(0.96);
            scaleOut.setInterpolator(javafx.animation.Interpolator.EASE_IN);

            scaleOut.play();
            fadeOut.play();

            SoundEffect.MENU_SELECT.play();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void togglePause() {
        if (isPaused) {
            hide();
        } else {
            show();
        }
    }

    public StackPane getContainer() {
        return container;
    }

    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    public void setOnMainMenu(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu;
    }

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    public void toggle() {
        if (isVisible) {
            hide();
        } else {
            show();
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}