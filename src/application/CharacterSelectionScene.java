package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import audio.BackgroundMusic;
import audio.SoundEffect;
import audio.AudioManager;

public class CharacterSelectionScene {
    private Scene scene;
    private SceneManager sceneManager;
    private ComboBox<String> player1Selector;
    private ComboBox<String> player2Selector;
    private StringProperty player1Selection = new SimpleStringProperty("Warrior");
    private StringProperty player2Selection = new SimpleStringProperty("Warrior");
    private VBox player1StatsDisplay;
    private VBox player2StatsDisplay;
    private StackPane player1IconContainer;
    private StackPane player2IconContainer;
    private BackgroundMusic backgroundMusic;
    private Button musicToggleButton;
    private Button soundToggleButton;
    private boolean musicOn = true;
    private boolean soundOn = true;

    private final Color BACKGROUND_COLOR = Color.web("#0A0A0F");
    private final Color CARD_COLOR = Color.web("#151820");
    private final Color ACCENT_COLOR = Color.web("#00D4FF");
    private final Color PLAYER1_COLOR = Color.web("#00D4FF");
    private final Color PLAYER2_COLOR = Color.web("#FF2A6D");
    private final Color PRIMARY_TEXT = Color.web("#E0E0E0");
    private final Color SECONDARY_TEXT = Color.web("#8A8A8A");
    private final String[] CHARACTER_TYPES = {"Warrior", "Mage", "Archer", "Assassin"};

    private final String WARRIOR_ICON = "/icon/warrior.jpg";
    private final String MAGE_ICON = "/icon/mage.jpg";
    private final String ARCHER_ICON = "/icon/archer.jpg";
    private final String ASSASSIN_ICON = "/icon/assassin.jpg";

    public CharacterSelectionScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.backgroundMusic = sceneManager.getBackgroundMusic();
        buildScene();
    }

    private void buildScene() {
        StackPane root = new StackPane();
        createBackground(root);

        VBox mainContainer = new VBox(15);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(20, 20, 20, 20));
        mainContainer.setMaxWidth(1200);
        mainContainer.setMaxHeight(800);

        VBox header = createHeader();
        HBox characterSelectionArea = createCharacterSelectionArea();
        HBox actionButtons = createActionButtons();
        VBox controlsInfo = createControlsInfo();

        mainContainer.getChildren().addAll(header, characterSelectionArea, actionButtons, controlsInfo);
        root.getChildren().add(mainContainer);

        scene = new Scene(root, 1200, 800);
        scene.setFill(BACKGROUND_COLOR);

        playEnterAnimation(mainContainer);
        setupSelectionListeners();
    }

    private VBox createHeader() {
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 10, 0));

        Text title = new Text("BATTLE ARENA");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setFill(PRIMARY_TEXT);

        Glow glow = new Glow();
        glow.setLevel(0.2);
        title.setEffect(glow);

        Text subtitle = new Text("SELECT YOUR FIGHTER");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitle.setFill(ACCENT_COLOR);

        Rectangle line = new Rectangle(200, 2);
        line.setFill(ACCENT_COLOR);
        line.setOpacity(0.6);

        header.getChildren().addAll(title, subtitle, line);
        return header;
    }

    private HBox createCharacterSelectionArea() {
        HBox container = new HBox(30);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10, 0, 10, 0));

        VBox player1Card = createCharacterCard("PLAYER 1", true);
        VBox player2Card = createCharacterCard("PLAYER 2", false);

        container.getChildren().addAll(player1Card, player2Card);
        return container;
    }

    private VBox createCharacterCard(String title, boolean isPlayer1) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(400, 450);
        card.setMaxSize(400, 450);

        String borderColor = isPlayer1 ? "#00D4FF" : "#FF2A6D";
        card.setStyle("-fx-background-color: #151820; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + borderColor + "; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 0);");

        Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        cardTitle.setTextFill(isPlayer1 ? PLAYER1_COLOR : PLAYER2_COLOR);

        VBox selector = createCharacterSelector(isPlayer1);

        StackPane iconContainer = createCharacterIcon(isPlayer1 ? player1Selection.get() : player2Selection.get(), isPlayer1);

        VBox stats = createCharacterStats(isPlayer1 ? player1Selection.get() : player2Selection.get());

        if (isPlayer1) {
            player1StatsDisplay = stats;
            player1IconContainer = iconContainer;
        } else {
            player2StatsDisplay = stats;
            player2IconContainer = iconContainer;
        }

        card.getChildren().addAll(cardTitle, selector, iconContainer, stats);
        return card;
    }

    private StackPane createCharacterIcon(String character, boolean isPlayer1) {
        StackPane iconContainer = new StackPane();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefSize(200, 200);

        Rectangle outerFrame = new Rectangle(200, 200);
        outerFrame.setArcWidth(15);
        outerFrame.setArcHeight(15);
        LinearGradient outerGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, isPlayer1 ? PLAYER1_COLOR : PLAYER2_COLOR),
                new Stop(1, isPlayer1 ? Color.web("#0088CC") : Color.web("#CC0044"))
        );
        outerFrame.setFill(outerGradient);

        DropShadow outerShadow = new DropShadow();
        outerShadow.setColor(Color.BLACK);
        outerShadow.setRadius(10);
        outerShadow.setOffsetX(0);
        outerShadow.setOffsetY(5);
        outerFrame.setEffect(outerShadow);

        Rectangle middleFrame = new Rectangle(185, 185);
        middleFrame.setArcWidth(12);
        middleFrame.setArcHeight(12);
        middleFrame.setFill(Color.web("#0A0A0F"));

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.BLACK);
        innerShadow.setRadius(8);
        innerShadow.setOffsetX(0);
        innerShadow.setOffsetY(2);
        middleFrame.setEffect(innerShadow);

        Rectangle innerFrame = new Rectangle(170, 170);
        innerFrame.setArcWidth(8);
        innerFrame.setArcHeight(8);
        innerFrame.setFill(Color.web("#151820"));

        Rectangle borderGlow = new Rectangle(175, 175);
        borderGlow.setArcWidth(10);
        borderGlow.setArcHeight(10);
        borderGlow.setFill(Color.TRANSPARENT);
        borderGlow.setStroke(isPlayer1 ? Color.web("#00D4FF") : Color.web("#FF2A6D"));
        borderGlow.setStrokeWidth(2);
        borderGlow.setOpacity(0.7);

        Glow glowEffect = new Glow();
        glowEffect.setLevel(0.3);
        borderGlow.setEffect(glowEffect);

        ImageView iconView = new ImageView();
        iconView.setFitWidth(160);
        iconView.setFitHeight(160);
        iconView.setPreserveRatio(true);

        updateIconImage(iconView, character);

        iconContainer.getChildren().addAll(outerFrame, middleFrame, innerFrame, borderGlow, iconView);
        return iconContainer;
    }

    private void updateIconImage(ImageView iconView, String character) {
        try {
            String iconPath = getCharacterIconPath(character);
            Image iconImage = new Image(CharacterSelectionScene.class.getResourceAsStream(iconPath));
            iconView.setImage(iconImage);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), iconView);
            scale.setFromX(0.8);
            scale.setFromY(0.8);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        } catch (Exception e) {
            System.out.println("Failed to load icon for: " + character);
        }
    }

    private String getCharacterIconPath(String character) {
        switch (character.toLowerCase()) {
            case "warrior":
                return WARRIOR_ICON;
            case "mage":
                return MAGE_ICON;
            case "archer":
                return ARCHER_ICON;
            case "assassin":
                return ASSASSIN_ICON;
            default:
                return WARRIOR_ICON;
        }
    }

    private void updateCharacterIcon(String character, boolean isPlayer1) {
        if (isPlayer1 && player1IconContainer != null) {
            updateIconContainer(player1IconContainer, character, isPlayer1);
        } else if (player2IconContainer != null) {
            updateIconContainer(player2IconContainer, character, isPlayer1);
        }
    }

    private void updateIconContainer(StackPane iconContainer, String character, boolean isPlayer1) {
        if (iconContainer.getChildren().size() >= 5) {
            Rectangle outerFrame = (Rectangle) iconContainer.getChildren().get(0);
            Rectangle borderGlow = (Rectangle) iconContainer.getChildren().get(3);
            ImageView iconView = (ImageView) iconContainer.getChildren().get(4);

            LinearGradient outerGradient = new LinearGradient(
                    0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, isPlayer1 ? PLAYER1_COLOR : PLAYER2_COLOR),
                    new Stop(1, isPlayer1 ? Color.web("#0088CC") : Color.web("#CC0044"))
            );
            outerFrame.setFill(outerGradient);

            borderGlow.setStroke(isPlayer1 ? Color.web("#00D4FF") : Color.web("#FF2A6D"));

            updateIconImage(iconView, character);
        }
    }

    private VBox createCharacterSelector(boolean isPlayer1) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(CHARACTER_TYPES);
        combo.setValue("Warrior");
        combo.setPrefWidth(180);

        combo.setStyle("-fx-background-color: #1E2230; " +
                "-fx-text-fill: #E0E0E0; " +
                "-fx-font-size: 13; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 12; " +
                "-fx-background-radius: 6; " +
                "-fx-border-color: #2A2F40; " +
                "-fx-border-radius: 6; " +
                "-fx-border-width: 1;");

        combo.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1E2230;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #1E2230; " +
                            "-fx-text-fill: #E0E0E0; " +
                            "-fx-font-size: 12; " +
                            "-fx-padding: 6 10;");
                }
            }
        });

        combo.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #E0E0E0; " +
                            "-fx-font-size: 13; " +
                            "-fx-font-weight: bold;");
                }
            }
        });

        if (isPlayer1) {
            player1Selector = combo;
        } else {
            player2Selector = combo;
        }

        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);

        Label label = new Label("SELECT CHARACTER");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        label.setTextFill(SECONDARY_TEXT);

        container.getChildren().addAll(label, combo);
        return container;
    }

    private VBox createCharacterStats(String character) {
        VBox stats = new VBox(8);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.setPadding(new Insets(12));
        stats.setPrefWidth(240);
        stats.setStyle("-fx-background-color: #1A1D28; -fx-background-radius: 6;");

        Label title = new Label("CHARACTER STATS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        title.setTextFill(ACCENT_COLOR);
        stats.getChildren().add(title);

        String[] statLines = getCharacterStats(character);
        for (String line : statLines) {
            HBox statRow = new HBox(6);
            statRow.setAlignment(Pos.CENTER_LEFT);

            Rectangle dot = new Rectangle(4, 4);
            dot.setFill(ACCENT_COLOR);
            dot.setArcWidth(4);
            dot.setArcHeight(4);

            Label stat = new Label(line);
            stat.setFont(Font.font("Arial", 11));
            stat.setTextFill(PRIMARY_TEXT);

            statRow.getChildren().addAll(dot, stat);
            stats.getChildren().add(statRow);
        }

        return stats;
    }

    private String[] getCharacterStats(String character) {
        switch (character) {
            case "Warrior":
                return new String[]{"Health: 150", "Speed: Medium", "Defense: High", "Damage: Balanced"};
            case "Mage":
                return new String[]{"Health: 100", "Speed: Low", "Defense: Low", "Damage: Area"};
            case "Archer":
                return new String[]{"Health: 120", "Speed: High", "Defense: Medium", "Damage: Ranged"};
            case "Assassin":
                return new String[]{"Health: 90", "Speed: Very High", "Defense: Low", "Damage: High"};
            default:
                return new String[]{"Health: 100", "Speed: Medium", "Defense: Medium", "Damage: Medium"};
        }
    }

    private HBox createActionButtons() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10, 0, 10, 0));

        musicToggleButton = createMusicToggleButton();

        soundToggleButton = createSoundToggleButton();

        Button startButton = createStartButton();
        Button exitButton = createExitButton();

        container.getChildren().addAll(musicToggleButton, soundToggleButton, startButton, exitButton);
        return container;
    }

    private Button createMusicToggleButton() {
        Button button = new Button("🎵");
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setPrefSize(40, 40);

        button.setStyle("-fx-background-color: #2A2F40; " +
                "-fx-text-fill: #E0E0E0; " +
                "-fx-background-radius: 20; " +
                "-fx-border-radius: 20; " +
                "-fx-border-color: #00D4FF; " +
                "-fx-border-width: 2; " +
                "-fx-cursor: hand;");

        button.setOnAction(e -> {
            musicOn = !musicOn;
            if (backgroundMusic != null) {
                if (musicOn) {
                    backgroundMusic.play();
                    button.setText("🎵");
                    button.setStyle("-fx-background-color: #2A2F40; " +
                            "-fx-text-fill: #E0E0E0; " +
                            "-fx-background-radius: 20; " +
                            "-fx-border-radius: 20; " +
                            "-fx-border-color: #00D4FF; " +
                            "-fx-border-width: 2; " +
                            "-fx-cursor: hand;");
                } else {
                    backgroundMusic.pause();
                    button.setText("🔇");
                    button.setStyle("-fx-background-color: #2A2F40; " +
                            "-fx-text-fill: #E0E0E0; " +
                            "-fx-background-radius: 20; " +
                            "-fx-border-radius: 20; " +
                            "-fx-border-color: #FF2A6D; " +
                            "-fx-border-width: 2; " +
                            "-fx-cursor: hand;");
                }
            }
            SoundEffect.MENU_SELECT.play();
        });

        return button;
    }

    private Button createSoundToggleButton() {
        Button button = new Button("🔊");
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setPrefSize(40, 40);

        button.setStyle("-fx-background-color: #2A2F40; " +
                "-fx-text-fill: #E0E0E0; " +
                "-fx-background-radius: 20; " +
                "-fx-border-radius: 20; " +
                "-fx-border-color: #00D4FF; " +
                "-fx-border-width: 2; " +
                "-fx-cursor: hand;");

        button.setOnAction(e -> {
            soundOn = !soundOn;
            if (soundOn) {
                AudioManager.getInstance().setSoundEffectVolume(0.7);
                button.setText("🔊");
                button.setStyle("-fx-background-color: #2A2F40; " +
                        "-fx-text-fill: #E0E0E0; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-color: #00D4FF; " +
                        "-fx-border-width: 2; " +
                        "-fx-cursor: hand;");
            } else {
                AudioManager.getInstance().setSoundEffectVolume(0);
                button.setText("🔇");
                button.setStyle("-fx-background-color: #2A2F40; " +
                        "-fx-text-fill: #E0E0E0; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-color: #FF2A6D; " +
                        "-fx-border-width: 2; " +
                        "-fx-cursor: hand;");
            }
            SoundEffect.MENU_SELECT.play();
        });

        return button;
    }

    private Button createStartButton() {
        Button button = new Button("START BATTLE");
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setPrefSize(160, 45);

        String baseStyle = "-fx-background-color: #00D4FF; " +
                "-fx-text-fill: #0A0A0F; " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,212,255,0.3), 12, 0, 0, 4);";

        String hoverStyle = "-fx-background-color: #00B8E6; " +
                "-fx-scale-x: 1.05; " +
                "-fx-scale-y: 1.05; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,212,255,0.5), 15, 0, 0, 6);";

        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> {
            button.setStyle(baseStyle + hoverStyle);
            SoundEffect.MENU_SELECT.play();
        });
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setOnMousePressed(e -> button.setStyle(baseStyle + "-fx-scale-x: 0.95; -fx-scale-y: 0.95;"));
        button.setOnMouseReleased(e -> button.setStyle(baseStyle + (button.isHover() ? hoverStyle : "")));
        button.setOnAction(e -> {
            SoundEffect.MENU_CONFIRM.play();
            startGame();
        });

        return button;
    }

    private Button createExitButton() {
        Button button = new Button("EXIT GAME");
        button.setFont(Font.font("System", FontWeight.BOLD, 16));
        button.setPrefSize(160, 45);

        String baseStyle = "-fx-background-color: #FF2A6D; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(255,42,109,0.3), 12, 0, 0, 4);";

        String hoverStyle = "-fx-background-color: #E0245E; " +
                "-fx-scale-x: 1.05; " +
                "-fx-scale-y: 1.05; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(255,42,109,0.5), 15, 0, 0, 6);";

        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> {
            button.setStyle(baseStyle + hoverStyle);
            SoundEffect.MENU_SELECT.play();
        });
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setOnMousePressed(e -> button.setStyle(baseStyle + "-fx-scale-x: 0.95; -fx-scale-y: 0.95;"));
        button.setOnMouseReleased(e -> button.setStyle(baseStyle + (button.isHover() ? hoverStyle : "")));
        button.setOnAction(e -> {
            SoundEffect.MENU_CONFIRM.play();
            sceneManager.exitGame();
        });

        return button;
    }

    private VBox createControlsInfo() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(800);
        container.setPadding(new Insets(5, 0, 0, 0));

        Label title = new Label("CONTROLS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(ACCENT_COLOR);

        HBox grid = new HBox(20);
        grid.setAlignment(Pos.CENTER);

        VBox player1Controls = createControlCard("PLAYER 1", PLAYER1_COLOR,
                "W A S D  -  Move\nF  -  Shoot\nQ  -  Switch Weapon\n1  -  Reload");

        VBox player2Controls = createControlCard("PLAYER 2", PLAYER2_COLOR,
                "ARROWS  -  Move\nL  -  Shoot\nP  -  Switch Weapon\n0  -  Reload");

        grid.getChildren().addAll(player1Controls, player2Controls);
        container.getChildren().addAll(title, grid);

        return container;
    }

    private VBox createControlCard(String player, Color color, String text) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);

        card.setStyle("-fx-background-color: #151820; " +
                "-fx-background-radius: 6; " +
                "-fx-border-color: " + toHexString(color) + "; " +
                "-fx-border-radius: 6; " +
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);");

        Label playerLabel = new Label(player);
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        playerLabel.setTextFill(color);

        Text controlsText = new Text(text);
        controlsText.setFont(Font.font("Arial", 11));
        controlsText.setFill(PRIMARY_TEXT);

        card.getChildren().addAll(playerLabel, controlsText);
        return card;
    }

    private void createBackground(StackPane root) {
        Rectangle background = new Rectangle(2000, 2000);
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0A0A0F")),
                new Stop(0.5, Color.web("#0D0F18")),
                new Stop(1, Color.web("#0A0A0F"))
        );
        background.setFill(gradient);
        root.getChildren().add(background);
        createBackgroundPattern(root);
    }

    private void createBackgroundPattern(StackPane root) {
        for (int i = 0; i < 15; i++) {
            Rectangle shape = new Rectangle(50, 50);
            shape.setFill(Color.TRANSPARENT);
            shape.setStroke(Color.web("#1A1A2E"));
            shape.setStrokeWidth(1);
            shape.setOpacity(0.2);
            shape.setRotate(45);
            shape.setTranslateX(Math.random() * 1200 - 600);
            shape.setTranslateY(Math.random() * 800 - 400);
            root.getChildren().add(shape);
        }
    }

    private void setupSelectionListeners() {
        player1Selector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                player1Selection.set(newValue);
                updateCharacterCard(true, newValue);
                SoundEffect.MENU_SELECT.play();
            }
        });

        player2Selector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                player2Selection.set(newValue);
                updateCharacterCard(false, newValue);
                SoundEffect.MENU_SELECT.play();
            }
        });
    }

    private void updateCharacterCard(boolean isPlayer1, String character) {
        if (isPlayer1) {
            updatePlayerCard(player1StatsDisplay, character);
        } else {
            updatePlayerCard(player2StatsDisplay, character);
        }
        updateCharacterIcon(character, isPlayer1);
    }

    private void updatePlayerCard(VBox statsDisplay, String character) {
        if (statsDisplay != null) {
            statsDisplay.getChildren().clear();
            VBox newStats = createCharacterStats(character);
            if (newStats != null) {
                statsDisplay.getChildren().addAll(newStats.getChildren());
            }
        }
    }

    private void playEnterAnimation(VBox mainContainer) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), mainContainer);
        scaleIn.setFromX(0.95);
        scaleIn.setFromY(0.95);
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        fadeIn.play();
        scaleIn.play();
    }

    private void startGame() {
        if (sceneManager != null) {
            String player1Char = player1Selection.get();
            String player2Char = player2Selection.get();
            sceneManager.startGame(player1Char, player2Char);
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public Scene getScene() {
        return scene;
    }

    public void cleanup() {
        player1Selector = null;
        player2Selector = null;
        player1StatsDisplay = null;
        player2StatsDisplay = null;
        player1IconContainer = null;
        player2IconContainer = null;
        musicToggleButton = null;
        soundToggleButton = null;
    }
}