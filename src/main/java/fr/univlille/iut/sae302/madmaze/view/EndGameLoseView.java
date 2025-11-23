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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.EndGameLoseController;
import main.java.fr.univlille.iut.sae302.madmaze.model.FuelMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.Game;
import main.java.fr.univlille.iut.sae302.madmaze.model.RaceMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.StormMode;

public class EndGameLoseView {
    private final Stage stage;
    private final int moves;
    private Button menuBtn;
    private final EndGameLoseController controller;
    private final Game game;
    private ImageView background;

    public EndGameLoseView(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        this.moves = game.getMoveCount();
        this.controller = new EndGameLoseController(this);
        setBackground(game);

        show();
        controller.setupListeners();
    }

    public void show() {
        Font tekoFont48 = Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 48);
        Font tekoSemiBold32 = Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-SemiBold.ttf"), 32);
        
        Label lbl = new Label(setLbl());
        lbl.setFont(tekoFont48);
        lbl.setTextFill(Color.web("#96501e"));
        
        Label moveCount = new Label("Mouvements : " + moves);
        moveCount.setFont(tekoSemiBold32);
        
        Label moveMin = new Label("Mouvements minimum : " + game.getMode().getMaze().optimalPathLength());
        moveMin.setFont(tekoSemiBold32);
        
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(100, 0, 0, 0));
        centerBox.getChildren().addAll(lbl, moveCount, moveMin);
        
        menuBtn = createStyledButton("Retour");
        BorderPane layout = new BorderPane();
        layout.setCenter(centerBox);
        HBox bottomBox = new HBox(menuBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(0, 0, 30, 0));
        layout.setBottom(bottomBox);

        StackPane root = new StackPane();
        root.getChildren().addAll(background, layout);
        Scene scene = new Scene(root, 600, 300);

        // Preserve current stage size when possible
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
            is = getClass().getResourceAsStream("/images/furiosa.jpg");
            if (is != null) backgroundImage = new Image(is);
        } catch (Exception ex) {
            // keep backgroundImage null on error
            backgroundImage = null;
        } finally {
            try { if (is != null) is.close(); } catch (Exception e) { /* ignore */ }
        }

        // create ImageView even if image failed to load to avoid NPEs later
        this.background = new ImageView();
        if (backgroundImage != null) {
            this.background.setImage(backgroundImage);
        } else {
            // make background transparent if no image available
            this.background.setStyle("-fx-background-color: transparent;");
        }
    }

    public String setLbl() {
        if (game.getMode() instanceof FuelMode){
            return "Perdu ! Vous avez manqué de carburant.";
        } else if (game.getMode() instanceof RaceMode){
            return "Perdu ! Il est arrivé avant vous.";
        } else if (game.getMode() instanceof StormMode){
            return "Perdu ! La tempête vous a rattrapé.";
        } else {
            return "Perdu ! Vous n'avez pas réussi à terminer le jeu.";
        }
    }

    public Button getMenuBtn() {
        // Return the "Menu principal" button for controller access
        return menuBtn;
    }

    public Stage getStage() {
        return stage;
    }

    public Game getGame() {
        return game;
    }

    public boolean isLost() {
        return game.isLost();
    }
    
    private Button createStyledButton(String text) {
        Font tekoFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 24);
        Button btn = new Button(text);
        btn.setFont(tekoFont);
        btn.setPrefWidth(400);
        btn.setPrefHeight(75);
        btn.setStyle(
                "-fx-background-color: rgba(150, 80, 30);" +
                "-fx-text-fill: #050e0e;" +
                "-fx-background-radius: 40;"
        );
        Rectangle clip = new Rectangle(400, 75);
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        btn.setClip(clip);
        return btn;
    }
}
