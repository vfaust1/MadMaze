package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.EndGameWinController;
import main.java.fr.univlille.iut.sae302.madmaze.model.Game;
import main.java.fr.univlille.iut.sae302.madmaze.model.ProgressionMode;

public class EndGameWinView {
    private final Stage stage;
    private final int moves;
    private Button menuBtn;
    private final EndGameWinController controller;
    private final Game game;
    private ImageView background;

    public EndGameWinView(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        this.moves = game.getMoveCount();
        this.controller = new EndGameWinController(this);
        setBackground(game);

        show();
        controller.setupListeners();
    }

    public void show() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-SemiBold.ttf"), 10);
        
        Label lbl = new Label("Félicitations ! Vous avez trouvé la sortie.");
        lbl.getStyleClass().add("end-game-win-title");
        
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(100, 0, 0, 0));
        centerBox.getChildren().add(lbl);
        if (game.getMode() instanceof ProgressionMode) {
            ProgressionMode pm = (ProgressionMode) game.getMode();
            Label ProgressionLabel = new Label("Étape : " + (pm.getCurrentLevelIndex() + 1) + "   Défi : " + (pm.getCurrentChallengeIndex() + 1));
            ProgressionLabel.getStyleClass().add("end-game-win-info");
            centerBox.getChildren().add(ProgressionLabel);
        }

        Label moveCount = new Label("Mouvements : " + moves);
        moveCount.getStyleClass().add("end-game-win-info");
        
        Label moveMin = new Label("Mouvements minimum : " + game.getMode().getMaze().optimalPathLength());
        moveMin.getStyleClass().add("end-game-win-info");
        
        centerBox.getChildren().addAll(moveCount, moveMin);
        
        menuBtn = new Button("Retour");
        menuBtn.getStyleClass().add("end-game-win-button");
        
        BorderPane layout = new BorderPane();
        layout.setCenter(centerBox);
        HBox bottomBox = new HBox(menuBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(0, 0, 30, 0));
        layout.setBottom(bottomBox);

        StackPane root = new StackPane();
        root.getChildren().addAll(background, layout);
        Scene scene = new Scene(root, 600, 300);
        scene.getStylesheets().add(getClass().getResource("/styles/end_game_win.css").toExternalForm());

        double curW = stage.getWidth();
        double curH = stage.getHeight();
        boolean hadSize = curW > 0 && curH > 0;
        stage.setScene(scene);
        if (hadSize) {
            stage.setWidth(curW);
            stage.setHeight(curH);
        }

        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());
        background.setPreserveRatio(false);
        
        stage.setTitle("Félicitations");
        stage.show();
    }

    private void setBackground(Game game) {
        Image backgroundImage = null;
        java.io.InputStream is = null;
        try {
            is = getClass().getResourceAsStream("/images/back.png");
            if (is != null) backgroundImage = new Image(is);
        } catch (Exception ex) {
            backgroundImage = null;
        } finally {
            try { if (is != null) is.close(); } catch (Exception e) { /* ignore */ }
        }

        this.background = new ImageView();
        if (backgroundImage != null) {
            this.background.setImage(backgroundImage);
        }
    }

    public String setLbl() { return "Félicitations ! Vous avez trouvé la sortie."; }
    public Button getMenuBtn() { return menuBtn; }
    public Stage getStage() { return stage; }
    public Game getGame() { return game; }
    public boolean isLost() { return game.isLost(); }
}
