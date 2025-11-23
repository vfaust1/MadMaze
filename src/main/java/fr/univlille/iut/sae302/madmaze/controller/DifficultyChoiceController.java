package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javafx.event.ActionEvent;

import javafx.stage.Stage;

import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;
import main.java.fr.univlille.iut.sae302.madmaze.model.Game;
import main.java.fr.univlille.iut.sae302.madmaze.model.ProgressionMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.Difficulty;


import main.java.fr.univlille.iut.sae302.madmaze.view.DifficultyChoiceView;
import main.java.fr.univlille.iut.sae302.madmaze.view.LevelChoiceView;
import main.java.fr.univlille.iut.sae302.madmaze.view.GameView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;

/**
 * Controller for the difficulty choice view. Owns button wiring and navigation.
 */
public class DifficultyChoiceController {

    private final Stage stage;
    private final Profil player;
    private final int stepSelected;
    private int difficulty;

    public DifficultyChoiceController(Stage stage, Profil player, int stepSelected) {
        this.stage = stage;
        this.player = player;
        this.stepSelected = stepSelected;
    }

    public void setupListeners(DifficultyChoiceView view) {
        view.getBackButton().setOnAction(e -> onBack());
        view.getConfirmButton().setOnAction(e -> onConfirm(e));
        view.getEasyButton().setOnAction(e -> {
            view.getLabel().setText("FACILE");
            view.getEasyButton().getStyleClass().add("difficulty-button-selected");
            view.getMediumButton().getStyleClass().remove("difficulty-button-selected");
            view.getHardButton().getStyleClass().remove("difficulty-button-selected");
            this.difficulty = 0;
        });
        view.getMediumButton().setOnAction(e -> {
            view.getLabel().setText("MOYEN");
            view.getEasyButton().getStyleClass().remove("difficulty-button-selected");
            view.getMediumButton().getStyleClass().add("difficulty-button-selected");
            view.getHardButton().getStyleClass().remove("difficulty-button-selected");
            this.difficulty = 1;
        });
        view.getHardButton().setOnAction(e -> {
            view.getLabel().setText("DIFFICILE");
            view.getEasyButton().getStyleClass().remove("difficulty-button-selected");
            view.getMediumButton().getStyleClass().remove("difficulty-button-selected");
            view.getHardButton().getStyleClass().add("difficulty-button-selected");
            this.difficulty = 2;
        });
    }

    public void onBack() {
        // go back to the previous view (for now main menu)
        new LevelChoiceView(stage, player);
    }

    public void onConfirm(ActionEvent event) {
        try {
            ProgressionMode mode = new ProgressionMode(player);
            mode.selectLevelAndChallenge(stepSelected, difficulty);
            mode.prepareMaze();

            Game game = new Game(mode);

            ViewsUtils.saveStageSize(stage);
            new GameView(stage, game);
            ViewsUtils.restoreStageSize(stage);
        } catch (Exception e) {

        }
    }
}
