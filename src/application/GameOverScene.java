package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class GameOverScene {
    private Scene scene;
    private SceneManager sceneManager;
    private String winner;
    private int player1Score;
    private int player2Score;
    private int nextRound;

    private final Color BACKGROUND_COLOR = Color.web("#0F1419");
    private final Color CARD_COLOR = Color.web("#1E2328");
    private final Color PRIMARY_TEXT = Color.web("#FFFFFF");
    private final Color SECONDARY_TEXT = Color.web("#B8C2CC");
    private final Color PLAYER1_COLOR = Color.web("#4A90E2");
    private final Color PLAYER2_COLOR = Color.web("#E24A4A");
    private final Color ACCENT_COLOR = Color.web("#36B37E");
    private final Color PRIMARY_BUTTON = Color.web("#36B37E");
    private final Color SECONDARY_BUTTON = Color.web("#4A90E2");
    private final Color DANGER_BUTTON = Color.web("#E24A4A");

    public GameOverScene(SceneManager sceneManager, String winner, int player1Score, int player2Score, int nextRound) {
        this.sceneManager = sceneManager;
        this.winner = winner;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.nextRound = nextRound;
        buildScene();
    }

    private void buildScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0F1419;");
        createBackgroundEffects(root);

        VBox mainCard = createMainCard();
        root.getChildren().add(mainCard);
        scene = new Scene(root, config.GameConfig.WINDOW_WIDTH, config.GameConfig.WINDOW_HEIGHT);
        playEnterAnimation(mainCard);
    }

    private void createBackgroundEffects(StackPane root) {
        for (int i = 0; i < 20; i++) {
            Rectangle particle = new Rectangle(2, 2);
            particle.setFill(winner.contains("1") ? PLAYER1_COLOR : PLAYER2_COLOR);
            particle.setOpacity(0.3);
            particle.setTranslateX(Math.random() * config.GameConfig.WINDOW_WIDTH);
            particle.setTranslateY(Math.random() * config.GameConfig.WINDOW_HEIGHT);

            FadeTransition fade = new FadeTransition(Duration.seconds(2 + Math.random() * 3), particle);
            fade.setFromValue(0.1);
            fade.setToValue(0.5);
            fade.setCycleCount(FadeTransition.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();
            root.getChildren().add(particle);
        }
    }

    private VBox createMainCard() {
        VBox mainCard = new VBox(25);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(40, 50, 40, 50));
        mainCard.setMaxWidth(500);
        mainCard.setMaxHeight(600);

        mainCard.setStyle("-fx-background-color: #1E2328; " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: " + (winner.contains("1") ? "#4A90E2" : "#E24A4A") + "; " +
                "-fx-border-radius: 20; " +
                "-fx-border-width: 3; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 30, 0, 0, 0);");

        VBox roundSection = createRoundSection();
        VBox winnerSection = createWinnerSection();
        VBox scoreSection = createScoreSection();
        HBox buttonSection = createButtonSection();

        mainCard.getChildren().addAll(roundSection, winnerSection, scoreSection, buttonSection);
        return mainCard;
    }

    private VBox createRoundSection() {
        VBox roundSection = new VBox(5);
        roundSection.setAlignment(Pos.CENTER);

        Text roundText = new Text("NEXT: ROUND " + nextRound);
        roundText.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        LinearGradient roundGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PLAYER1_COLOR),
                new Stop(0.5, Color.WHITE),
                new Stop(1, PLAYER2_COLOR)
        );
        roundText.setFill(roundGradient);

        Rectangle underline = new Rectangle(120, 2);
        underline.setFill(ACCENT_COLOR);
        underline.setArcWidth(5);
        underline.setArcHeight(5);

        roundSection.getChildren().addAll(roundText, underline);
        return roundSection;
    }

    private VBox createWinnerSection() {
        VBox winnerSection = new VBox(10);
        winnerSection.setAlignment(Pos.CENTER);

        Text winnerText = new Text(winner.toUpperCase() + " VICTORY!");
        winnerText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        winnerText.setFill(winner.contains("1") ? PLAYER1_COLOR : PLAYER2_COLOR);

        Glow glow = new Glow();
        glow.setLevel(0.8);
        winnerText.setEffect(glow);

        Text subtitle = new Text("Battle Concluded");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setFill(SECONDARY_TEXT);

        winnerSection.getChildren().addAll(winnerText, subtitle);
        return winnerSection;
    }

    private VBox createScoreSection() {
        VBox scoreSection = new VBox(20);
        scoreSection.setAlignment(Pos.CENTER);
        scoreSection.setPadding(new Insets(20));
        scoreSection.setStyle("-fx-background-color: #2A2F36; -fx-background-radius: 15;");

        HBox modeIndicator = new HBox(10);
        modeIndicator.setAlignment(Pos.CENTER);

        Text modeText = new Text("Game Mode: ");
        modeText.setFont(Font.font("Arial", 12));
        modeText.setFill(SECONDARY_TEXT);

        Text modeValue = new Text(config.GameConfig.HALF_SCREEN_RESTRICTION ? "Half-Screen Restriction" : "Full Movement");
        modeValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        modeValue.setFill(config.GameConfig.HALF_SCREEN_RESTRICTION ? PLAYER2_COLOR : PLAYER1_COLOR);

        modeIndicator.getChildren().addAll(modeText, modeValue);

        HBox mainScore = new HBox(30);
        mainScore.setAlignment(Pos.CENTER);

        VBox player1ScoreBox = createPlayerScoreBox("PLAYER 1", player1Score, PLAYER1_COLOR, winner.contains("1"));
        Text vsText = new Text("VS");
        vsText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        vsText.setFill(ACCENT_COLOR);
        vsText.setEffect(new DropShadow(10, ACCENT_COLOR));
        VBox player2ScoreBox = createPlayerScoreBox("PLAYER 2", player2Score, PLAYER2_COLOR, winner.contains("2"));

        mainScore.getChildren().addAll(player1ScoreBox, vsText, player2ScoreBox);

        HBox scoreBreakdown = createScoreBreakdown();
        scoreSection.getChildren().addAll(modeIndicator, mainScore, scoreBreakdown);
        return scoreSection;
    }

    private VBox createPlayerScoreBox(String playerName, int score, Color color, boolean isWinner) {
        VBox scoreBox = new VBox(8);
        scoreBox.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(playerName);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(color);

        StackPane scoreCircle = new StackPane();
        scoreCircle.setPrefSize(80, 80);
        scoreCircle.setStyle("-fx-background-color: " + (isWinner ? toHexString(color) : "#2A2F36") + "; " +
                "-fx-background-radius: 40; " +
                "-fx-border-color: " + toHexString(color) + "; " +
                "-fx-border-radius: 40; " +
                "-fx-border-width: " + (isWinner ? "3" : "2") + ";");

        Text scoreText = new Text(String.valueOf(score));
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreText.setFill(isWinner ? Color.WHITE : color);

        scoreCircle.getChildren().add(scoreText);

        if (isWinner) {
            Text crown = new Text("👑");
            crown.setFont(Font.font(24));
            crown.setFill(Color.GOLD);
            crown.setTranslateY(-45);
            scoreCircle.getChildren().add(crown);
        }

        scoreBox.getChildren().addAll(nameLabel, scoreCircle);
        return scoreBox;
    }

    private HBox createScoreBreakdown() {
        HBox breakdown = new HBox(30);
        breakdown.setAlignment(Pos.CENTER);

        VBox player1Stats = createPlayerStats("Player 1", player1Score, PLAYER1_COLOR);
        VBox player2Stats = createPlayerStats("Player 2", player2Score, PLAYER2_COLOR);

        breakdown.getChildren().addAll(player1Stats, player2Stats);
        return breakdown;
    }

    private VBox createPlayerStats(String player, int score, Color color) {
        VBox stats = new VBox(5);
        stats.setAlignment(Pos.CENTER_LEFT);

        Label playerLabel = new Label(player);
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        playerLabel.setTextFill(color);

        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(Font.font("Arial", 11));
        scoreLabel.setTextFill(SECONDARY_TEXT);

        String result = (player.contains("1") && winner.contains("1")) ||
                (player.contains("2") && winner.contains("2")) ? "WINNER" : "DEFEAT";
        Label resultLabel = new Label(result);
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        resultLabel.setTextFill(result.equals("WINNER") ? ACCENT_COLOR : Color.web("#E24A4A"));

        stats.getChildren().addAll(playerLabel, scoreLabel, resultLabel);
        return stats;
    }

    private HBox createButtonSection() {
        HBox buttonSection = new HBox(20);
        buttonSection.setAlignment(Pos.CENTER);

        Button playAgainButton = createStyledButton("PLAY AGAIN", PRIMARY_BUTTON, true);
        playAgainButton.setOnAction(e -> {
            playButtonSound();
            sceneManager.playAgain();
        });

        Button menuButton = createStyledButton("MAIN MENU", SECONDARY_BUTTON, false);
        menuButton.setOnAction(e -> {
            playButtonSound();
            sceneManager.showCharacterSelection();
        });

        Button exitButton = createStyledButton("EXIT GAME", DANGER_BUTTON, false);
        exitButton.setOnAction(e -> {
            playButtonSound();
            System.exit(0);
        });

        buttonSection.getChildren().addAll(playAgainButton, menuButton, exitButton);
        return buttonSection;
    }

    private Button createStyledButton(String text, Color color, boolean isPrimary) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setPrefSize(140, 50);

        String hexColor = toHexString(color);
        String darkerColor = toHexString(color.darker());

        String baseStyle = "-fx-background-color: " + hexColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 25; " +
                "-fx-border-radius: 25; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);";

        String hoverStyle = "-fx-background-color: " + darkerColor + "; " +
                "-fx-scale-x: 1.05; " +
                "-fx-scale-y: 1.05; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 5);";

        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(baseStyle + hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setOnMousePressed(e -> button.setStyle(baseStyle + "-fx-scale-x: 0.95; -fx-scale-y: 0.95;"));
        button.setOnMouseReleased(e -> button.setStyle(baseStyle + (button.isHover() ? hoverStyle : "")));

        return button;
    }

    private void playEnterAnimation(VBox mainCard) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainCard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(800), mainCard);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        fadeIn.play();
        scaleIn.play();
        audio.SoundEffect.VICTORY.play();
    }

    private void playButtonSound() {
        audio.SoundEffect.WEAPON_SWITCH.play();
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
}