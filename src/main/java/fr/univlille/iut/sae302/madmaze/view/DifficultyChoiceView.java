package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.DifficultyChoiceController;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;

public class DifficultyChoiceView {

    private final Stage stage;
    private int numStep;
    private final Profil player;
    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private Button back;
    private Button confirm;
    private DifficultyChoiceController controller;
    private Label diff;
    private Label text;

    public DifficultyChoiceView(Stage stage, Profil player, int numStep) {
        this.stage = stage;
        this.player = player;
        this.numStep = numStep;

        text = new Label("DIFFICULTÉ :");
        diff = new Label("FACILE");

        easyButton = new Button("FACILE");
        mediumButton = new Button("MOYEN");
        hardButton = new Button("DIFFICILE");
        back = new Button("Retour");
        confirm = new Button("Confirmer");

        controller = new DifficultyChoiceController(stage, player, numStep);

        show();

        controller.setupListeners(this);
    }

    public void show() {
        javafx.scene.text.Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 10);

        BorderPane root = new BorderPane();

        java.net.URL imageUrl = getClass().getResource("/images/levelChoice.jpg");
        if (imageUrl != null) {
            root.setStyle("-fx-background-image: url('" + imageUrl.toExternalForm() + "'); -fx-background-size: cover;");
        } else {
            System.err.println("Erreur: L'image de fond /images/levelChoice.jpg n'a pas \u00e9t\u00e9 trouv\u00e9e.");
            root.getStyleClass().add("difficulty-root");
        }

        BorderPane topContainer = new BorderPane();
        Label title = new Label("Choix Difficultés");
        title.getStyleClass().add("difficulty-title");
        Label etape = new Label("Etape " + (numStep + 1));
        etape.getStyleClass().add("step-label");
        topContainer.setCenter(title);
        topContainer.setTop(etape);
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(topContainer);

        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setMaxWidth(VBox.USE_PREF_SIZE);

        StackPane rounded = new StackPane();
        rounded.getStyleClass().add("difficulty-panel");

        VBox rows = new VBox(18);
        rows.setAlignment(Pos.CENTER);

        easyButton.getStyleClass().addAll("difficulty-button", "difficulty-button-selected");
        mediumButton.getStyleClass().add("difficulty-button");
        hardButton.getStyleClass().add("difficulty-button");

        rows.getChildren().addAll(easyButton, mediumButton, hardButton);

        text.getStyleClass().add("info-label");
        diff.getStyleClass().add("selected-difficulty-label");

        rounded.getChildren().add(rows);
        centerContent.getChildren().addAll(rounded, text, diff);
        root.setCenter(centerContent);

        HBox bottom = new HBox(40);
        bottom.setPadding(new Insets(20));
        bottom.setAlignment(Pos.CENTER);

        back.getStyleClass().add("bottom-button");
        confirm.getStyleClass().add("bottom-button");

        bottom.getChildren().addAll(back, confirm);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/difficulty_choice.css").toExternalForm());
        
        double curW = stage.getWidth();
        double curH = stage.getHeight();
        boolean hadSize = curW > 0 && curH > 0;
        stage.setScene(scene);
        stage.setTitle("Paramètre - Mode Progression");
        stage.show();
        if (hadSize) {
            stage.setWidth(curW);
            stage.setHeight(curH);
        }
    }

    public Button getEasyButton() { return easyButton; }
    public Button getMediumButton() { return mediumButton; }
    public Button getHardButton() { return hardButton; }
    public Button getBackButton() { return back; }
    public Button getConfirmButton() { return confirm; }
    public Label getLabel() { return diff; }
}
