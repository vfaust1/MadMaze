package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.model.Game;
import main.java.fr.univlille.iut.sae302.madmaze.model.MazeType;
import main.java.fr.univlille.iut.sae302.madmaze.model.Mode;
import main.java.fr.univlille.iut.sae302.madmaze.view.GameView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MadMaxParameterView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;

public class MadMaxParameterController {
    private final MadMaxParameterView view;
    private final Stage stage;

    public MadMaxParameterController(MadMaxParameterView view) {
        this.view = view;
        this.stage = view.getStage();
        setupListeners();
    }

    public void setupListeners() {
        view.getBackButton().setOnAction(this::onBack);

        view.getConfirmButton().setOnAction(e -> onConfirm(
                MazeType.PERFECT,
                view.getWidthValue(),
                view.getHeightValue(),
                view.getMazeParamValue()    // pourcentage ou distance mini selon le type
        ));
        
        // Configuration des spinners avec validation et support clavier/molette
        setupSpinnerControls(view.getHeightSpinner(), 5, 80);
        setupSpinnerControls(view.getWidthSpinner(), 5, 80);
        setupSpinnerControls(view.getParameterSpinner(), 10, 300);
    }
    
    private void setupSpinnerControls(javafx.scene.control.Spinner<Integer> spinner, int min, int max) {
        // Validation de l'input clavier
        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinner.getEditor().setText(oldValue);
            }
        });
        
        // Commit de la valeur quand on quitte le champ
        spinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                try {
                    String text = spinner.getEditor().getText();
                    if (!text.isEmpty()) {
                        int value = Integer.parseInt(text);
                        if (value < min) {
                            spinner.getValueFactory().setValue(min);
                        } else if (value > max) {
                            spinner.getValueFactory().setValue(max);
                        } else {
                            spinner.getValueFactory().setValue(value);
                        }
                    } else {
                        spinner.getValueFactory().setValue(spinner.getValue());
                    }
                } catch (NumberFormatException e) {
                    spinner.getValueFactory().setValue(spinner.getValue());
                }
            }
        });
        
        // Support de la molette
        spinner.setOnScroll(event -> {
            if (event.getDeltaY() > 0) {
                spinner.increment();
            } else if (event.getDeltaY() < 0) {
                spinner.decrement();
            }
            event.consume();
        });
    }

    private void onBack(ActionEvent event) {
        ViewsUtils.saveStageSize(stage);
        new MainMenuView().start(stage);
        ViewsUtils.restoreStageSize(stage);
    }

    private void onConfirm(MazeType mazeType, int width, int height, int value) {
        Mode mode = view.getMode();
        mode.setMaze(mazeType, width, height, value);

        Game game = new Game(mode);

        ViewsUtils.saveStageSize(stage);
        new GameView(stage, game);
        ViewsUtils.restoreStageSize(stage);
    }
}
