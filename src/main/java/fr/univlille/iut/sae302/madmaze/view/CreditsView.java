package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.CreditsController;

public class CreditsView {

    private final Stage stage;
    private Button backBtn;
    private CreditsController controller;

    public CreditsView(Stage stage) {
        this.stage = stage;
        this.controller = new CreditsController(stage);
        show();
        controller.setupListeners(this);
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("credits-root");

        Label title = new Label("Crédits");
        title.getStyleClass().add("credits-title");
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        String descText = "Dans le cadre de notre formation en BUT2 Informatique à l'IUT de Lille, nous avons développé un jeu de labyrinthe. " +
                "Ce projet a été réalisé en collaboration par : Valentin Faust, Aurelien Dochy, Corentin Chocraux et Martin Lecoester.\n\n" +
                "Notre jeu propose une expérience immersive où le joueur doit naviguer à travers différents niveaux de complexité croissante et trouver la sortie en évitant les murs.\n\n" +
                "Ce projet nous a permis de mettre en pratique la programmation orientée objet, la conception d'algorithmes et le travail collaboratif, ainsi que la gestion de projet et la résolution de problèmes techniques.";

        Label descLabel = new Label(descText);
        descLabel.getStyleClass().add("credits-description");

        VBox centerBox = new VBox(descLabel);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        backBtn = new Button("Retour");
        // On pourrait ajouter un style au bouton si nécessaire, par ex: backBtn.getStyleClass().add("back-button");

        BorderPane.setAlignment(backBtn, Pos.CENTER);
        root.setBottom(backBtn);

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/credits.css").toExternalForm());
        setScenePreserveSize(scene);
        stage.setTitle("Crédits");
        stage.show();
    }

    private void setScenePreserveSize(Scene newScene) {
        double curW = stage.getWidth();
        double curH = stage.getHeight();
        boolean hadSize = curW > 0 && curH > 0;
        stage.setScene(newScene);
        if (hadSize) {
            stage.setWidth(curW);
            stage.setHeight(curH);
        }
    }

    public Button getBackButton() {
        return backBtn;
    }
}
