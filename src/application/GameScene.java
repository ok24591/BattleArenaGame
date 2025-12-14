package application;

import javafx.scene.control.Button;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.control.Label;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import characters.Fighter;
import characters.CharacterFactory;
import projectiles.Projectile;
import physics.Vector2D;
import physics.CollisionDetector;
import animations.GameAnimations;
import animations.ParticleEffects;
import audio.SoundEffect;
import audio.BackgroundMusic;
import ui.HealthBar;
import ui.WeaponHUD;
import ui.PauseMenuUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GameScene {
    private Scene scene;
    private SceneManager sceneManager;
    private Pane gamePane;
    private Fighter player1;
    private Fighter player2;
    private List<Projectile> activeProjectiles;
    private AnimationTimer gameLoop;
    private HealthBar healthBar1, healthBar2;
    private WeaponHUD weaponHUD1, weaponHUD2;
    private boolean gameRunning;

    private Rectangle background;
    private Line centerLine;
    private Rectangle ground;
    private Pane effectsLayer;
    private Text roundText;
    private Text timerText;

    private Set<KeyCode> pressedKeys = new HashSet<>();

    private long lastWeaponSwitch1 = 0;
    private long lastWeaponSwitch2 = 0;
    private long lastReload1 = 0;
    private long lastReload2 = 0;
    private long lastShot1 = 0;
    private long lastShot2 = 0;

    private long gameStartTime;
    private int roundNumber;

    private final Color PLAYER1_COLOR = Color.web("#4A90E2");
    private final Color PLAYER2_COLOR = Color.web("#E24A4A");
    private final Color ACCENT_COLOR = Color.web("#36B37E");

    private double screenWidth;
    private double screenHeight;
    private double arenaWidth;
    private double arenaHeight;
    private double groundLevel;
    private double controlsBoxHeight = 150;

    private boolean sceneHasFocus = true;
    private Rectangle physicalBoundary;
    private PauseMenuUI pauseMenu;
    private boolean isPaused = false;
    private StackPane rootContainer;

    public GameScene(SceneManager sceneManager, String player1Character, String player2Character, int roundNumber) {
        this.sceneManager = sceneManager;
        this.roundNumber = roundNumber;

        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        this.screenWidth = screenBounds.getWidth();
        this.screenHeight = screenBounds.getHeight();

        this.arenaWidth = screenWidth;
        this.arenaHeight = screenHeight - controlsBoxHeight;
        this.groundLevel = arenaHeight - 50;

        config.GameConfig.CURRENT_ARENA_WIDTH = arenaWidth;
        config.GameConfig.CURRENT_ARENA_HEIGHT = arenaHeight;

        this.player1 = CharacterFactory.createCharacter(player1Character, true);
        this.player2 = CharacterFactory.createCharacter(player2Character, false);
        this.activeProjectiles = new ArrayList<>();
        this.gameRunning = true;
        this.gameStartTime = System.currentTimeMillis();
        this.isPaused = false;

        if (sceneManager.getBackgroundMusic() != null) {
            sceneManager.getBackgroundMusic().setVolume(0.15);
        }

        buildScene();
        startGameLoop();
        setupWindowListeners();
    }

    private void setupInputHandling() {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                togglePause();
                e.consume();
                return;
            }

            if (sceneHasFocus && !isPaused) {
                pressedKeys.add(e.getCode());
                showKeyPressEffect(e.getCode());

                if (e.getCode() == KeyCode.Q || e.getCode() == KeyCode.P ||
                        e.getCode() == KeyCode.DIGIT1 || e.getCode() == KeyCode.DIGIT0) {
                    SoundEffect.MENU_SELECT.play();
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (sceneHasFocus && !isPaused) {
                pressedKeys.remove(e.getCode());
            }
        });

        scene.setOnMouseClicked(e -> {
            requestFocus();
            SoundEffect.MENU_SELECT.play();
        });

        requestFocus();
    }

    private void buildScene() {
        gamePane = new Pane();
        gamePane.setPrefSize(screenWidth, screenHeight);

        createBackground();
        createArena();
        createUserInterface();
        createPlayerVisuals();

        addPauseButton();

        Pane gameRoot = createRootLayout();

        pauseMenu = new PauseMenuUI(sceneManager.getBackgroundMusic());

        StackPane mainContainer = new StackPane();
        mainContainer.setPrefSize(screenWidth, screenHeight);

        mainContainer.getChildren().addAll(gameRoot, pauseMenu.getContainer());

        scene = new Scene(mainContainer, screenWidth, screenHeight);
        setupInputHandling();

        setupPauseMenuCallbacks();

        showRoundStartAnimation();
    }

    private void setupPauseMenuCallbacks() {
        pauseMenu.setOnResume(() -> {
            resumeGame();
        });

        pauseMenu.setOnRestart(() -> {
            sceneManager.startGame(
                    player1.getName().equals("Warrior") ? "Warrior" : player1.getName(),
                    player2.getName().equals("Warrior") ? "Warrior" : player2.getName()
            );
        });

        pauseMenu.setOnMainMenu(() -> {
            sceneManager.showCharacterSelection();
        });

        pauseMenu.setOnExit(() -> {
            sceneManager.exitGame();
        });
    }

    private void togglePause() {
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        isPaused = true;
        sceneHasFocus = false;
        pauseMenu.show();

        if (gameLoop != null) {
            gameLoop.stop();
        }

        if (sceneManager.getBackgroundMusic() != null) {
            sceneManager.getBackgroundMusic().pause();
        }
    }

    private void resumeGame() {
        isPaused = false;
        sceneHasFocus = true;
        pauseMenu.hide();

        if (gameLoop != null) {
            gameLoop.start();
        }

        if (sceneManager.getBackgroundMusic() != null) {
            sceneManager.getBackgroundMusic().play();
            sceneManager.getBackgroundMusic().setVolume(0.15);
        }

        requestFocus();
    }

    private void createBackground() {
        background = new Rectangle(screenWidth, screenHeight);
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0F1419")),
                new Stop(0.7, Color.web("#1A1F26")),
                new Stop(1, Color.web("#0F1419"))
        );
        background.setFill(gradient);

        int gridSpacing = 50;
        for (int x = 0; x < arenaWidth; x += gridSpacing) {
            for (int y = 0; y < arenaHeight; y += gridSpacing) {
                Rectangle gridDot = new Rectangle(1, 1, Color.web("#2A2F36"));
                gridDot.setTranslateX(x);
                gridDot.setTranslateY(y);
                gamePane.getChildren().add(gridDot);
            }
        }

        gamePane.getChildren().add(background);
    }

    private void createArena() {
        ground = new Rectangle(0, arenaHeight - 50, arenaWidth, 50);

        LinearGradient groundGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1E2328")),
                new Stop(1, Color.web("#15191E"))
        );
        ground.setFill(groundGradient);

        DropShadow groundShadow = new DropShadow();
        groundShadow.setColor(Color.BLACK);
        groundShadow.setRadius(20);
        groundShadow.setOffsetY(-5);
        ground.setEffect(groundShadow);

        gamePane.getChildren().add(ground);

        physicalBoundary = new Rectangle(0, arenaHeight, arenaWidth, 10);
        physicalBoundary.setFill(Color.TRANSPARENT);
        gamePane.getChildren().add(physicalBoundary);

        Line boundaryLine = new Line(0, arenaHeight, arenaWidth, arenaHeight);
        boundaryLine.setStroke(ACCENT_COLOR);
        boundaryLine.setStrokeWidth(3);
        boundaryLine.getStrokeDashArray().addAll(15.0, 10.0);

        Glow boundaryGlow = new Glow(0.6);
        boundaryLine.setEffect(boundaryGlow);

        gamePane.getChildren().add(boundaryLine);

        centerLine = new Line(arenaWidth / 2, 0, arenaWidth / 2, arenaHeight);
        centerLine.setStroke(ACCENT_COLOR);
        centerLine.setStrokeWidth(3);
        centerLine.getStrokeDashArray().addAll(25.0, 15.0);

        Glow centerGlow = new Glow(0.3);
        centerLine.setEffect(centerGlow);

        gamePane.getChildren().add(centerLine);

        Rectangle boundaryLeft = new Rectangle(5, arenaHeight);
        boundaryLeft.setFill(ACCENT_COLOR);
        boundaryLeft.setTranslateX(0);
        boundaryLeft.setEffect(new Glow(0.5));
        gamePane.getChildren().add(boundaryLeft);

        Rectangle boundaryRight = new Rectangle(5, arenaHeight);
        boundaryRight.setFill(ACCENT_COLOR);
        boundaryRight.setTranslateX(arenaWidth - 5);
        boundaryRight.setEffect(new Glow(0.5));
        gamePane.getChildren().add(boundaryRight);

        effectsLayer = new Pane();
        gamePane.getChildren().add(effectsLayer);
    }

    private void createUserInterface() {
        healthBar1 = new HealthBar(280, 20, player1.getMaxHealth(), true);
        healthBar2 = new HealthBar(280, 20, player2.getMaxHealth(), false);

        healthBar1.setPosition(20, 40);
        healthBar2.setPosition(arenaWidth - 300, 40);

        weaponHUD1 = new WeaponHUD(true);
        weaponHUD2 = new WeaponHUD(false);

        weaponHUD1.setPosition(20, 70);
        weaponHUD2.setPosition(arenaWidth - 300, 70);

        timerText = new Text();
        timerText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        timerText.setFill(Color.WHITE);

        StackPane timerContainer = new StackPane();
        timerContainer.setLayoutX(arenaWidth / 2 + 90);
        timerContainer.setLayoutY(22);

        Rectangle timerBg = new Rectangle(110, 40);
        timerBg.setArcWidth(20);
        timerBg.setArcHeight(20);
        timerBg.setFill(Color.web("#1E2328"));
        timerBg.setStroke(Color.web("#36B37E"));
        timerBg.setStrokeWidth(2);

        DropShadow timerGlow = new DropShadow();
        timerGlow.setColor(Color.web("#36B37E"));
        timerGlow.setRadius(10);
        timerBg.setEffect(timerGlow);

        timerText.setX(-40);
        timerText.setY(8);

        timerContainer.getChildren().addAll(timerBg, timerText);
        gamePane.getChildren().add(timerContainer);

        StackPane roundContainer = new StackPane();
        roundContainer.setLayoutX(arenaWidth / 2 - 60);
        roundContainer.setLayoutY(85);

        Rectangle roundBg = new Rectangle(120, 30);
        roundBg.setFill(Color.web("#1E2328"));
        roundBg.setArcWidth(15);
        roundBg.setArcHeight(15);
        roundBg.setStroke(ACCENT_COLOR);
        roundBg.setStrokeWidth(2);

        DropShadow roundGlow = new DropShadow();
        roundGlow.setColor(ACCENT_COLOR);
        roundGlow.setRadius(10);
        roundGlow.setSpread(0.2);
        roundBg.setEffect(roundGlow);

        roundText = new Text("ROUND " + roundNumber);
        roundText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        roundText.setFill(Color.WHITE);

        roundContainer.getChildren().addAll(roundBg, roundText);
        gamePane.getChildren().add(roundContainer);

        Text player1Label = createPlayerLabel("PLAYER 1", PLAYER1_COLOR, 20, 25);
        Text player2Label = createPlayerLabel("PLAYER 2", PLAYER2_COLOR, arenaWidth - 90, 25);

        gamePane.getChildren().addAll(healthBar1, healthBar2, weaponHUD1, weaponHUD2,
                player1Label, player2Label);
    }

    private Text createPlayerLabel(String text, Color color, double x, double y) {
        Text label = new Text(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setFill(color);
        label.setX(x);
        label.setY(y);

        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(8);
        label.setEffect(glow);

        return label;
    }

    private void createPlayerVisuals() {
        player1.getVisual().setEffect(createPlayerGlow(PLAYER1_COLOR));
        player2.getVisual().setEffect(createPlayerGlow(PLAYER2_COLOR));

        player1.setPosition(new Vector2D(arenaWidth * 0.2, arenaHeight / 2));
        player2.setPosition(new Vector2D(arenaWidth * 0.8, arenaHeight / 2));

        gamePane.getChildren().addAll(player1.getVisual(), player2.getVisual());
    }

    private Glow createPlayerGlow(Color color) {
        Glow glow = new Glow();
        glow.setLevel(0.3);
        return glow;
    }

    private Pane createRootLayout() {
        Pane rootPane = new Pane();
        rootPane.setPrefSize(screenWidth, screenHeight);

        rootPane.getChildren().add(gamePane);

        VBox controlsBox = createControlsDisplay();
        controlsBox.setLayoutY(arenaHeight);
        rootPane.getChildren().add(controlsBox);

        rootPane.setStyle("-fx-background-color: #0A0A0F;");
        rootPane.setFocusTraversable(true);

        return rootPane;
    }

    private VBox createControlsDisplay() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.setPrefWidth(screenWidth);
        container.setPrefHeight(controlsBoxHeight);
        container.setStyle("-fx-background-color: linear-gradient(to bottom, #151820, #0A0A0F); " +
                "-fx-border-color: #36B37E; " +
                "-fx-border-width: 2 0 0 0; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 15, 0, 0, -5);");

        Text controlsTitle = new Text("GAME CONTROLS");
        controlsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        controlsTitle.setFill(ACCENT_COLOR);

        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(ACCENT_COLOR);
        titleGlow.setRadius(10);
        controlsTitle.setEffect(titleGlow);

        HBox controlsGrid = new HBox(60);
        controlsGrid.setAlignment(Pos.CENTER);
        controlsGrid.setPrefWidth(screenWidth);

        VBox player1Controls = createControlPanel("PLAYER 1", PLAYER1_COLOR,
                "W A S D   -   MOVE\n" +
                        "F           -   SHOOT\n" +
                        "Q           -   SWITCH WEAPON\n" +
                        "1           -   RELOAD");

        VBox player2Controls = createControlPanel("PLAYER 2", PLAYER2_COLOR,
                "ARROWS   -   MOVE\n" +
                        "L           -   SHOOT\n" +
                        "P           -   SWITCH WEAPON\n" +
                        "0           -   RELOAD");

        controlsGrid.getChildren().addAll(player1Controls, player2Controls);
        container.getChildren().addAll(controlsTitle, controlsGrid);

        return container;
    }

    private VBox createControlPanel(String player, Color color, String text) {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(20, 25, 20, 25));
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setStyle("-fx-background-color: rgba(30, 35, 40, 0.9); " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: " + toHexString(color) + "; " +
                "-fx-border-radius: 12; " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 2, 2);");

        Label playerLabel = new Label(player);
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerLabel.setTextFill(color);

        Text controlsText = new Text(text);
        controlsText.setFont(Font.font("Consolas", FontWeight.NORMAL, 13));
        controlsText.setFill(Color.web("#E8EDF2"));
        controlsText.setLineSpacing(3);

        panel.getChildren().addAll(playerLabel, controlsText);
        return panel;
    }

    private void showRoundStartAnimation() {
        sceneHasFocus = false;

        Iterator<javafx.scene.Node> cleanupIterator = gamePane.getChildren().iterator();
        while (cleanupIterator.hasNext()) {
            javafx.scene.Node node = cleanupIterator.next();
            if (node instanceof Text) {
                Text textNode = (Text) node;
                if (textNode.getText() != null) {
                    String text = textNode.getText().toUpperCase();
                    if (text.contains("FIGHT") || text.contains("FIGHT!")) {
                        cleanupIterator.remove();
                    }
                }
            }
        }

        Text roundStartText = new Text("ROUND " + roundNumber);
        roundStartText.setFont(Font.font("Arial Black", FontWeight.BOLD, 60));

        LinearGradient roundGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PLAYER1_COLOR),
                new Stop(0.5, Color.WHITE),
                new Stop(1, PLAYER2_COLOR)
        );
        roundStartText.setFill(roundGradient);

        Glow glow = new Glow();
        glow.setLevel(0.8);
        roundStartText.setEffect(glow);

        roundStartText.setOpacity(0);
        roundStartText.setScaleX(0);
        roundStartText.setScaleY(0);

        roundStartText.setX((arenaWidth - roundStartText.getLayoutBounds().getWidth()) / 2);
        roundStartText.setY(arenaHeight / 2 - 40);

        gamePane.getChildren().add(roundStartText);

        FadeTransition roundFadeIn = new FadeTransition(Duration.millis(500), roundStartText);
        roundFadeIn.setFromValue(0);
        roundFadeIn.setToValue(1);

        ScaleTransition roundScaleIn = new ScaleTransition(Duration.millis(600), roundStartText);
        roundScaleIn.setFromX(0);
        roundScaleIn.setFromY(0);
        roundScaleIn.setToX(1.2);
        roundScaleIn.setToY(1.2);

        ParallelTransition roundEntrance = new ParallelTransition(roundFadeIn, roundScaleIn);

        ScaleTransition roundPulse = new ScaleTransition(Duration.millis(500), roundStartText);
        roundPulse.setFromX(1.2);
        roundPulse.setFromY(1.2);
        roundPulse.setToX(1.1);
        roundPulse.setToY(1.1);
        roundPulse.setCycleCount(2);
        roundPulse.setAutoReverse(true);

        FadeTransition roundFadeOut = new FadeTransition(Duration.millis(400), roundStartText);
        roundFadeOut.setFromValue(1);
        roundFadeOut.setToValue(0);

        SequentialTransition roundAnimation = new SequentialTransition(
                roundEntrance,
                roundPulse,
                roundFadeOut
        );

        roundAnimation.setOnFinished(e -> {
            gamePane.getChildren().remove(roundStartText);

            PauseTransition delay = new PauseTransition(Duration.millis(100));
            delay.setOnFinished(event -> showFightAnimation());
            delay.play();
        });

        roundAnimation.play();
    }

    private void showFightAnimation() {
        for (int pass = 0; pass < 3; pass++) {
            Iterator<javafx.scene.Node> iterator = gamePane.getChildren().iterator();
            while (iterator.hasNext()) {
                javafx.scene.Node node = iterator.next();
                if (node instanceof Text) {
                    Text textNode = (Text) node;
                    if (textNode.getText() != null) {
                        String text = textNode.getText().toUpperCase();
                        if (text.contains("FIGHT")) {
                            iterator.remove();
                            continue;
                        }
                    }
                }
            }

            if (effectsLayer != null) {
                Iterator<javafx.scene.Node> effectsIterator = effectsLayer.getChildren().iterator();
                while (effectsIterator.hasNext()) {
                    javafx.scene.Node node = effectsIterator.next();
                    if (node instanceof Text) {
                        Text textNode = (Text) node;
                        if (textNode.getText() != null) {
                            String text = textNode.getText().toUpperCase();
                            if (text.contains("FIGHT")) {
                                effectsIterator.remove();
                            }
                        }
                    }
                }
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {}
        }

        Text fightText = new Text("FIGHT!");
        fightText.setFont(Font.font("Arial Black", FontWeight.BLACK, 84));

        LinearGradient fightGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PLAYER1_COLOR),
                new Stop(0.5, Color.WHITE),
                new Stop(1, PLAYER2_COLOR)
        );
        fightText.setFill(fightGradient);

        Glow glow = new Glow();
        glow.setLevel(0.7);
        fightText.setEffect(glow);

        fightText.setOpacity(0);
        fightText.setScaleX(0);
        fightText.setScaleY(0);

        gamePane.getChildren().add(fightText);

        Platform.runLater(() -> {
            fightText.setX((arenaWidth - fightText.getLayoutBounds().getWidth()) / 2);
            fightText.setY(arenaHeight / 2);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), fightText);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), fightText);
            scaleIn.setFromX(0);
            scaleIn.setFromY(0);
            scaleIn.setToX(1.3);
            scaleIn.setToY(1.3);

            ParallelTransition entrance = new ParallelTransition(fadeIn, scaleIn);

            ScaleTransition pulse = new ScaleTransition(Duration.millis(900), fightText);
            pulse.setFromX(1.3);
            pulse.setFromY(1.3);
            pulse.setToX(1.2);
            pulse.setToY(1.2);
            pulse.setCycleCount(3);
            pulse.setAutoReverse(true);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(700), fightText);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            SequentialTransition fightAnimation = new SequentialTransition(
                    entrance,
                    pulse,
                    fadeOut
            );

            fightAnimation.setOnFinished(e -> {
                gamePane.getChildren().remove(fightText);
                sceneHasFocus = true;
                requestFocus();
                SoundEffect.SHOOT.play();
                SoundEffect.ABILITY.play();
            });

            fightAnimation.play();
        });
    }

    private void showKeyPressEffect(KeyCode keyCode) {
        if (!sceneHasFocus || isPaused) return;

        if (keyCode == KeyCode.F || keyCode == KeyCode.L) {
            createMuzzleFlash(keyCode == KeyCode.F ? player1 : player2);
        }

        if (keyCode == KeyCode.Q) {
            GameAnimations.scalePop(weaponHUD1, 1.2, 200);
        }
        if (keyCode == KeyCode.P) {
            GameAnimations.scalePop(weaponHUD2, 1.2, 200);
        }
    }

    private void createMuzzleFlash(Fighter shooter) {
        Circle flash = new Circle(8, Color.YELLOW);

        double direction = Math.toRadians(shooter.getRotation());
        double centerX = shooter.getPosition().getX();
        double centerY = shooter.getPosition().getY();

        double offsetDistance = 35;
        double offsetX = Math.cos(direction) * offsetDistance;
        double offsetY = Math.sin(direction) * offsetDistance;

        flash.setTranslateX(centerX + offsetX);
        flash.setTranslateY(centerY + offsetY);
        flash.setEffect(new Glow(0.8));

        effectsLayer.getChildren().add(flash);
        GameAnimations.fadeOut(flash, 100);

        new Thread(() -> {
            try { Thread.sleep(150); } catch (InterruptedException e) {}
            javafx.application.Platform.runLater(() ->
                    effectsLayer.getChildren().remove(flash));
        }).start();
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                if (gameRunning && !isPaused) {
                    handleInput();
                    updateGame(deltaTime);
                    checkCollisions();
                    updateUI();
                    checkGameEnd();
                    constrainPlayersToBounds();
                    checkLowHealthEffects();
                    updateSoundEffects();
                }
            }
        };
        gameLoop.start();
    }

    private void handleInput() {
        if (!sceneHasFocus || isPaused) return;

        double p1dx = 0, p1dy = 0;
        if (pressedKeys.contains(KeyCode.W)) p1dy -= 1;
        if (pressedKeys.contains(KeyCode.S)) p1dy += 1;
        if (pressedKeys.contains(KeyCode.A)) p1dx -= 1;
        if (pressedKeys.contains(KeyCode.D)) p1dx += 1;

        if (p1dx != 0 || p1dy != 0) player1.move(p1dx, p1dy);

        double p2dx = 0, p2dy = 0;
        if (pressedKeys.contains(KeyCode.UP)) p2dy -= 1;
        if (pressedKeys.contains(KeyCode.DOWN)) p2dy += 1;
        if (pressedKeys.contains(KeyCode.LEFT)) p2dx -= 1;
        if (pressedKeys.contains(KeyCode.RIGHT)) p2dx += 1;

        if (p2dx != 0 || p2dy != 0) player2.move(p2dx, p2dy);

        if (sceneHasFocus && !isPaused) {
            handleShooting();
            handleWeaponSwitching();
            handleReloading();
        }
    }

    private void handleShooting() {
        if (!sceneHasFocus || isPaused) return;

        long currentTime = System.currentTimeMillis();

        if (pressedKeys.contains(KeyCode.F) && currentTime - lastShot1 > player1.getCurrentWeapon().getCooldown()) {
            weapons.Weapon weapon = player1.getCurrentWeapon();
            List<projectiles.Projectile> projectiles = weapon.createProjectiles(
                    player1.getPosition().getX(),
                    player1.getPosition().getY(),
                    Math.toRadians(player1.getRotation()),
                    true
            );

            if (weapon instanceof weapons.Shotgun) {
                for (projectiles.Projectile projectile : projectiles) {
                    activeProjectiles.add(projectile);
                    gamePane.getChildren().add(projectile.getVisual());
                }
                if (weapon.canFire()) {
                    weapon.fire(0, 0, 0, true);
                }
            } else {
                projectiles.Projectile projectile = player1.shoot();
                if (projectile != null) {
                    activeProjectiles.add(projectile);
                    gamePane.getChildren().add(projectile.getVisual());
                }
            }

            lastShot1 = currentTime;
            SoundEffect.SHOOT.play();
        }

        if (pressedKeys.contains(KeyCode.L) && currentTime - lastShot2 > player2.getCurrentWeapon().getCooldown()) {
            weapons.Weapon weapon = player2.getCurrentWeapon();
            List<projectiles.Projectile> projectiles = weapon.createProjectiles(
                    player2.getPosition().getX(),
                    player2.getPosition().getY(),
                    Math.toRadians(player2.getRotation()),
                    false
            );

            if (weapon instanceof weapons.Shotgun) {
                for (projectiles.Projectile projectile : projectiles) {
                    activeProjectiles.add(projectile);
                    gamePane.getChildren().add(projectile.getVisual());
                }
                if (weapon.canFire()) {
                    weapon.fire(0, 0, 0, false);
                }
            } else {
                projectiles.Projectile projectile = player2.shoot();
                if (projectile != null) {
                    activeProjectiles.add(projectile);
                    gamePane.getChildren().add(projectile.getVisual());
                }
            }

            lastShot2 = currentTime;
            SoundEffect.SHOOT.play();
        }
    }

    private void handleWeaponSwitching() {
        if (!sceneHasFocus || isPaused) return;

        long currentTime = System.currentTimeMillis();

        if (pressedKeys.contains(KeyCode.Q) && currentTime - lastWeaponSwitch1 > 300) {
            player1.switchWeapon();
            SoundEffect.WEAPON_SWITCH.play();
            lastWeaponSwitch1 = currentTime;
        }

        if (pressedKeys.contains(KeyCode.P) && currentTime - lastWeaponSwitch2 > 300) {
            player2.switchWeapon();
            SoundEffect.WEAPON_SWITCH.play();
            lastWeaponSwitch2 = currentTime;
        }
    }

    private void handleReloading() {
        if (!sceneHasFocus || isPaused) return;

        long currentTime = System.currentTimeMillis();

        if (pressedKeys.contains(KeyCode.DIGIT1) && currentTime - lastReload1 > 500) {
            player1.getCurrentWeapon().reload();
            SoundEffect.WEAPON_SWITCH.play();
            lastReload1 = currentTime;
        }

        if (pressedKeys.contains(KeyCode.DIGIT0) && currentTime - lastReload2 > 500) {
            player2.getCurrentWeapon().reload();
            SoundEffect.WEAPON_SWITCH.play();
            lastReload2 = currentTime;
        }
    }

    private void updateGame(double deltaTime) {
        Iterator<Projectile> iterator = activeProjectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update();

            if (projectile.isOutOfBounds(arenaWidth, arenaHeight) || !projectile.isActive()) {
                if (projectile.isActive()) {
                    SoundEffect.EXPLOSION.play();
                }
                gamePane.getChildren().remove(projectile.getVisual());
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        Iterator<Projectile> iterator = activeProjectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            if (projectile.isActive()) {
                boolean hit = false;

                if (!projectile.isPlayer1Projectile() && CollisionDetector.checkProjectileHit(projectile, player1)) {
                    handlePlayerHit(player1, projectile);
                    hit = true;
                }

                if (!hit && projectile.isPlayer1Projectile() && CollisionDetector.checkProjectileHit(projectile, player2)) {
                    handlePlayerHit(player2, projectile);
                    hit = true;
                }

                if (hit) {
                    projectile.setActive(false);
                    gamePane.getChildren().remove(projectile.getVisual());
                    iterator.remove();
                    SoundEffect.HIT.play();

                    if (projectile.getDamage() > 25) {
                        SoundEffect.EXPLOSION.play();
                    }
                }
            }
        }
    }

    private void handlePlayerHit(Fighter player, Projectile projectile) {
        double damage = projectile.getDamage();
        player.takeDamage(damage);

        double hitX = projectile.getPosition().getX();
        double hitY = projectile.getPosition().getY();

        showHitEffects(player, damage, hitX, hitY);

        if (damage > 20) {
            GameAnimations.shakeScreen(gamePane, 5, 300);
        }
    }

    private void showHitEffects(Fighter fighter, double damage, double hitX, double hitY) {

        fighter.flashHit(Color.RED, 300);

        GameAnimations.floatDamageText(gamePane,
                String.format("%.0f", damage),
                hitX - 20, hitY - 20,
                damage > 25 ? Color.GOLD : Color.WHITE,
                damage > 25 ? 20 : 16,
                800
        );

        if (damage > 15) {
            ParticleEffects.createBloodSplatter(effectsLayer, hitX, hitY);
        }

        ParticleEffects.createImpactParticles(effectsLayer, hitX, hitY, 5);
    }

    private void updateUI() {
        healthBar1.updateHealth(player1.getHealth());
        healthBar2.updateHealth(player2.getHealth());
        weaponHUD1.updateWeaponInfo(player1.getCurrentWeapon());
        weaponHUD2.updateWeaponInfo(player2.getCurrentWeapon());

        long elapsedTime = (System.currentTimeMillis() - gameStartTime) / 1000;
        long minutes = elapsedTime / 60;
        long seconds = elapsedTime % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));

        if (elapsedTime >= 170) {
            double pulse = Math.sin(System.currentTimeMillis() * 0.01) * 0.3 + 0.7;
            timerText.setFill(Color.rgb(255, (int)(100 * pulse), (int)(100 * pulse)));
            GameAnimations.pulseEffect(timerText, 1000, 1);

            if (elapsedTime % 10 == 0 && elapsedTime >= 180) {
                SoundEffect.ABILITY.play();
            }
        } else {
            timerText.setFill(Color.WHITE);
        }
    }

    private void checkGameEnd() {
        if (!player1.isAlive() || !player2.isAlive()) {
            gameRunning = false;
            showGameEndEffects();
            endGame();
        }
    }

    private void showGameEndEffects() {
        String winnerName = player1.isAlive() ? "PLAYER 1" : "PLAYER 2";
        Color winnerColor = player1.isAlive() ? PLAYER1_COLOR : PLAYER2_COLOR;

        ParticleEffects.createExplosion(effectsLayer, arenaWidth / 2, arenaHeight / 2, winnerColor);
        GameAnimations.shakeScreen(gamePane, 15, 1000);

        SoundEffect.EXPLOSION.play();

        if (player1.isAlive()) {
            SoundEffect.VICTORY.play();
        } else {
            SoundEffect.DEFEAT.play();
        }
    }

    private void endGame() {
        String winner = player1.isAlive() ? "Player 1" : "Player 2";
        int player1Score = player1.isAlive() ? 1 : 0;
        int player2Score = player2.isAlive() ? 1 : 0;

        gameLoop.stop();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    sceneManager.showGameOver(winner, player1Score, player2Score);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void constrainPlayersToBounds() {
        constrainPlayerToBounds(player1);
        constrainPlayerToBounds(player2);
    }

    private void constrainPlayerToBounds(Fighter player) {
        double x = player.getPosition().getX();
        double y = player.getPosition().getY();

        double collisionRadius = player.getCollisionRadius();
        double minX = collisionRadius;
        double maxX = arenaWidth - collisionRadius;
        double minY = collisionRadius;
        double maxY = arenaHeight - collisionRadius;

        double newX = Math.max(minX, Math.min(maxX, x));
        double newY = Math.max(minY, Math.min(maxY, y));

        if (newX != x || newY != y) {
            player.setPosition(new Vector2D(newX, newY));
        }
    }

    private void checkLowHealthEffects() {
        if (player1.getHealth() < 30) {
            GameAnimations.pulseEffect(healthBar1, 500, 1);
            if (player1.getHealth() < 20 && System.currentTimeMillis() % 2000 < 50) {
                SoundEffect.ABILITY.play();
            }
        }
        if (player2.getHealth() < 30) {
            GameAnimations.pulseEffect(healthBar2, 500, 1);
            if (player2.getHealth() < 20 && System.currentTimeMillis() % 2000 < 50) {
                SoundEffect.ABILITY.play();
            }
        }
    }

    private void updateSoundEffects() {
        long currentTime = System.currentTimeMillis();
        if (currentTime % 10000 < 50) {
            SoundEffect.MENU_SELECT.play();
        }
    }

    private void setupWindowListeners() {
        Stage stage = sceneManager.getStage();
        if (stage != null) {
            stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                sceneHasFocus = newValue;
                if (newValue && !isPaused) {
                    pressedKeys.clear();
                    requestFocus();
                } else {
                    pressedKeys.clear();
                }
            });
        }
    }

    private void requestFocus() {
        if (scene != null && scene.getRoot() != null && !isPaused) {
            scene.getRoot().requestFocus();
            sceneHasFocus = true;
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
        if (gameLoop != null) gameLoop.stop();
        activeProjectiles.clear();
        if (effectsLayer != null) effectsLayer.getChildren().clear();
        pauseMenu = null;
        isPaused = false;
    }

    private void addPauseButton() {
        Button pauseButton = new Button("⏸ PAUSE");

        double buttonWidth = 140;
        double buttonHeight = 40;
        double buttonX = arenaWidth / 2 - buttonWidth - 40;
        double buttonY = 22;

        pauseButton.setLayoutX(buttonX);
        pauseButton.setLayoutY(buttonY);
        pauseButton.setPrefSize(buttonWidth, buttonHeight);

        String baseStyle = "-fx-background-color: linear-gradient(to bottom, #FF2A6D, #E0245E); " +
                "-fx-text-fill: #0A0A0F; " +
                "-fx-font-family: 'Arial Black'; " +
                "-fx-font-size: 15px; " +
                "-fx-font-weight: 900; " +
                "-fx-background-radius: 20; " +
                "-fx-border-radius: 20; " +
                "-fx-border-color: #FF4A8D; " +
                "-fx-border-width: 2; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(255,42,109,0.5), 15, 0, 0, 4); " +
                "-fx-padding: 0 10 0 10;";

        String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #FF4A8D, #FF2A6D); " +
                "-fx-scale-x: 1.04; " +
                "-fx-scale-y: 1.04; " +
                "-fx-border-color: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(255,42,109,0.7), 20, 0, 0, 6);";

        String pressedStyle = "-fx-scale-x: 0.97; " +
                "-fx-scale-y: 0.97; " +
                "-fx-effect: dropshadow(gaussian, rgba(255,42,109,0.3), 10, 0, 0, 2);";

        pauseButton.setStyle(baseStyle);

        pauseButton.setOnMouseEntered(e -> {
            pauseButton.setStyle(baseStyle + hoverStyle);
            SoundEffect.MENU_SELECT.play();
        });

        pauseButton.setOnMouseExited(e -> pauseButton.setStyle(baseStyle));

        pauseButton.setOnMousePressed(e -> {
            pauseButton.setStyle(baseStyle + hoverStyle + pressedStyle);
            animations.GameAnimations.scalePop(pauseButton, 0.95, 100);
        });

        pauseButton.setOnMouseReleased(e -> {
            if (pauseButton.isHover()) {
                pauseButton.setStyle(baseStyle + hoverStyle);
            } else {
                pauseButton.setStyle(baseStyle);
            }
        });

        pauseButton.setOnAction(e -> {
            SoundEffect.MENU_CONFIRM.play();
            animations.GameAnimations.scalePop(pauseButton, 1.1, 200);
            togglePause();
        });

        DropShadow redGlow = new DropShadow();
        redGlow.setColor(Color.web("#FF2A6D"));
        redGlow.setRadius(12);
        redGlow.setSpread(0.3);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(6);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);

        javafx.scene.effect.Blend buttonEffect = new javafx.scene.effect.Blend();
        buttonEffect.setMode(javafx.scene.effect.BlendMode.ADD);
        buttonEffect.setTopInput(redGlow);
        buttonEffect.setBottomInput(shadow);

        pauseButton.setEffect(buttonEffect);

        StackPane buttonContainer = new StackPane();
        buttonContainer.setLayoutX(buttonX - 6);
        buttonContainer.setLayoutY(buttonY - 6);

        Rectangle buttonGlowBg = new Rectangle(buttonWidth + 12, buttonHeight + 12);
        buttonGlowBg.setArcWidth(24);
        buttonGlowBg.setArcHeight(24);
        buttonGlowBg.setFill(Color.TRANSPARENT);
        buttonGlowBg.setStroke(Color.web("#FF2A6D"));
        buttonGlowBg.setStrokeWidth(1);
        buttonGlowBg.setOpacity(0.4);

        Glow outerGlow = new Glow();
        outerGlow.setLevel(0.5);
        buttonGlowBg.setEffect(outerGlow);

        buttonContainer.getChildren().addAll(buttonGlowBg, pauseButton);

        gamePane.getChildren().add(buttonContainer);
    }
}