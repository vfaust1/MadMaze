package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.LevelChoiceController;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;

public class LevelChoiceView {

    private final Stage stage;
    private final Profil player;
    private Button stepOneBtn;
    private Button stepTwoBtn;
    private Button stepThreeBtn;
    private Button stepFourBtn;
    private Button stepFiveBtn;
    private Button stepSixBtn;
    private Button backBtn;
    private Button confirmBtn;
    private LevelChoiceController controller;
    private Label levelStep;

    public LevelChoiceView(Stage stage, Profil player) {
        this.stage = stage;
        this.player = player;
        
        show();
        controller = new LevelChoiceController(this);
        controller.setupListeners();
    }

    public void show() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 10);

        BorderPane root = new BorderPane();

        java.net.URL imageUrl = getClass().getResource("/images/levelChoice.jpg");
        if (imageUrl != null) {
            root.setStyle("-fx-background-image: url('" + imageUrl.toExternalForm() + "'); -fx-background-size: cover;");
        } else {
            System.err.println("Erreur: L'image de fond /images/levelChoice.jpg n'a pas été trouvée.");
            root.getStyleClass().add("level-root");
        }

        Label title = new Label("Choix ÉTAPES");
        title.getStyleClass().add("level-title");
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(new StackPane(title));

        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setMaxWidth(Region.USE_PREF_SIZE);

        StackPane rounded = new StackPane();
        rounded.getStyleClass().add("level-panel");

        VBox rows = new VBox(18);
        rows.setAlignment(Pos.CENTER);

        HBox columns1 = new HBox(18);
        columns1.setAlignment(Pos.CENTER);

        HBox columns2 = new HBox(18);
        columns2.setAlignment(Pos.CENTER);

        Label texteEtape = new Label("ÉTAPE :");
        texteEtape.getStyleClass().add("info-label");
        levelStep = new Label("1");
        levelStep.getStyleClass().add("selected-level-label");

        stepOneBtn = new Button("ÉTAPE 1");
        stepTwoBtn = new Button("ÉTAPE 2");
        stepThreeBtn = new Button("ÉTAPE 3");
        stepFourBtn = new Button("ÉTAPE 4");
        stepFiveBtn = new Button("ÉTAPE 5");
        stepSixBtn = new Button("ÉTAPE 6");

        stepOneBtn.getStyleClass().add("level-button");
        stepTwoBtn.getStyleClass().add("level-button");
        stepThreeBtn.getStyleClass().add("level-button");
        stepFourBtn.getStyleClass().add("level-button");
        stepFiveBtn.getStyleClass().add("level-button");
        stepSixBtn.getStyleClass().add("level-button");

        columns1.getChildren().addAll(stepOneBtn, stepTwoBtn, stepThreeBtn);
        columns2.getChildren().addAll(stepFourBtn, stepFiveBtn, stepSixBtn);
        rows.getChildren().addAll(columns1, columns2);

        rounded.getChildren().add(rows);
        centerContent.getChildren().addAll(rounded, texteEtape, levelStep);

        root.setCenter(centerContent);

        HBox bottom = new HBox(40);
        bottom.setPadding(new Insets(20));
        bottom.setAlignment(Pos.CENTER);
        backBtn = new Button("Retour");
        confirmBtn = new Button("Confirmer");
        backBtn.getStyleClass().add("bottom-button");
        confirmBtn.getStyleClass().add("bottom-button");
        bottom.getChildren().addAll(backBtn, confirmBtn);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/level_choice.css").toExternalForm());
        
        double curW = stage.getWidth();
        double curH = stage.getHeight();
        boolean hadSize = curW > 0 && curH > 0;
        stage.setScene(scene);
        stage.setTitle("Choix de l'étape");
        stage.show();
        if (hadSize) {
            stage.setWidth(curW);
            stage.setHeight(curH);
        }
    }

    public Button getStepOneButton() { return stepOneBtn; }
    public Button getStepTwoButton() { return stepTwoBtn; }
    public Button getStepThreeButton() { return stepThreeBtn; }
    public Button getStepFourButton() { return stepFourBtn; }
    public Button getStepFiveButton() { return stepFiveBtn; }
    public Button getStepSixButton() { return stepSixBtn; }
    public Button getBackButton() { return backBtn; }
    public Button getConfirmButton() { return confirmBtn; }
    public Label getLabel() { return levelStep; }
    public Stage getStage() { return stage; }
    public Profil getPlayer() { return player; }
}
