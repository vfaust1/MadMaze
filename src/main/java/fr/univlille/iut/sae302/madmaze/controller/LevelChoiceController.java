package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javax.swing.text.View;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.view.DifficultyChoiceView;
import main.java.fr.univlille.iut.sae302.madmaze.view.LevelChoiceView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ProgressionModeView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;

/**
 * Controller for EtapeChoiceView: owns the etape buttons, back and confirm actions.
 */
public class LevelChoiceController {

    private final Stage stage;
    private int selectedEtape;
    private final Profil player;
    private final LevelChoiceView view;

    public LevelChoiceController(LevelChoiceView view) {
        this.stage = view.getStage();
        this.player = view.getPlayer();
        this.view = view;
    }

    public void onBack() {
        if (stage != null) {
            ViewsUtils.saveStageSize(stage);
            new ProgressionModeView(stage);
            ViewsUtils.restoreStageSize(stage);
        }
    }

    public void onConfirm() {
        ViewsUtils.saveStageSize(stage);
        if (stage != null) new DifficultyChoiceView(stage, player, selectedEtape);
        ViewsUtils.restoreStageSize(stage);
    }

    public void setupListeners() {
        view.getBackButton().setOnAction(e -> onBack());
        view.getConfirmButton().setOnAction(e -> onConfirm());

        view.getStepOneButton().setOnAction(e -> {
            this.selectedEtape = 0;
            view.getLabel().setText("1");
        });

        view.getStepTwoButton().setOnAction(e -> {
            if(player.hasPermission(2)){
                this.selectedEtape = 1;
                view.getLabel().setText("2");
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Accès refusé");
                alert.setHeaderText("Étape 2 verrouillée");
                alert.setContentText("Vous devez d'abord terminer un défi de l'Étape 1 !");
                alert.showAndWait();
            }
        });

        view.getStepThreeButton().setOnAction(e -> {
            if(player.hasPermission(3)){
                this.selectedEtape = 2;
                view.getLabel().setText("3");
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Accès refusé");
                alert.setHeaderText("Étape 3 verrouillée");
                alert.setContentText("Vous devez d'abord terminer un défi de l'Étape 2 !");
                alert.showAndWait();
            }
        });

        view.getStepFourButton().setOnAction(e -> {
            if(player.hasPermission(4)){
                this.selectedEtape = 3;
                view.getLabel().setText("4");
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Accès refusé");
                alert.setHeaderText("Étape 4 verrouillée");
                alert.setContentText("Vous devez d'abord terminer un défi de l'Étape 3 !");
                alert.showAndWait();
            }
        });

        view.getStepFiveButton().setOnAction(e -> {
            if(player.hasPermission(5)){
                this.selectedEtape = 4;
                view.getLabel().setText("5");
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Accès refusé");
                alert.setHeaderText("Étape 5 verrouillée");
                alert.setContentText("Vous devez d'abord terminer un défi de l'Étape 4 !");
                alert.showAndWait();
            }
        });

        view.getStepSixButton().setOnAction(e -> {
            if(player.hasPermission(6)){
                this.selectedEtape = 5;
                view.getLabel().setText("6");
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Accès refusé");
                alert.setHeaderText("Étape 6 verrouillée");
                alert.setContentText("Vous devez d'abord terminer un défi de l'Étape 5 !");
                alert.showAndWait();
            }
        });

    }

}
