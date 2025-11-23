package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.view.ProgressionModeView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;
import main.java.fr.univlille.iut.sae302.madmaze.view.CreateProfileView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ProfilConnectionView;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;
public class ProgressionModeController {

    private Profil selectedPlayer;
    private final ProgressionModeView view;
    private final Stage stage;

        public ProgressionModeController(ProgressionModeView view) {
            this.view = view;
            this.stage = view.getStage();
        }

    public void setupListeners() {
        view.getPlayButton().setOnAction(e -> onPlay());
        view.getCreateButton().setOnAction(e -> onCreateProfile());
        view.getBackButton().setOnAction(e -> onBack());
        Scene scene = view.getScene();
        if (scene != null) attachKeyHandlers(scene);
    }

    public void onPlay() {
        System.out.println("[ProgressionModeController] Jouer pressed");
        if (selectedPlayer != null) {
            ViewsUtils.saveStageSize(stage);
            new ProfilConnectionView(view.getStage(), selectedPlayer);
            ViewsUtils.restoreStageSize(stage);
        } else {
            System.out.println("Aucun profil sélectionné !");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText("Aucun profil n'a été sélectionné.");
            alert.setContentText("Veuillez cliquer sur un profil dans la liste avant de cliquer sur 'Jouer'.");
            alert.showAndWait();
        }

    }

    public void setSelectedPlayer(Profil player) {
        this.selectedPlayer = player;
        if (player != null) {
            System.out.println("[ProgressionModeController] Selected player: " + player.getName());
        }
    }


    public void onCreateProfile() {
        System.out.println("[ProgressionModeController] Create Profile pressed");
        ViewsUtils.saveStageSize(stage);
        new CreateProfileView(stage);
        ViewsUtils.restoreStageSize(stage);
    }

    public void onBack() {
        System.out.println("[ProgressionModeController] Back pressed");
        ViewsUtils.saveStageSize(stage);
        new MainMenuView().start(view.getStage());
        ViewsUtils.restoreStageSize(stage);
    }

    private void attachKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER: onPlay(); break;
                case ESCAPE: onBack(); break;
                default:
                    break;
            }
        });
    }
}
