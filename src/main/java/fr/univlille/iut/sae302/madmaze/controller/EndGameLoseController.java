package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;
import main.java.fr.univlille.iut.sae302.madmaze.model.ProgressionMode;
import main.java.fr.univlille.iut.sae302.madmaze.view.EndGameLoseView;
import main.java.fr.univlille.iut.sae302.madmaze.view.LevelChoiceView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;


/**
 * Controller for the end-game (félicitations) view.
 * Responsible for wiring the "Menu principal" button back to the EtapeChoice view.
 */
public class EndGameLoseController {
    private final EndGameLoseView view;
    private final Stage stage;
    
    public EndGameLoseController(EndGameLoseView view) {
        this.view = view;
        this.stage = view.getStage();
    }

    public void setupListeners() {
        view.getMenuBtn().setOnAction(event -> onReturnToMenu());
    }


    public void onReturnToMenu() {
        ViewsUtils.saveStageSize(stage);
        if (view.getGame().getMode() instanceof ProgressionMode) {
            Profil player = ((ProgressionMode) view.getGame().getMode()).getPlayer();
            new LevelChoiceView(stage, player);
            ViewsUtils.restoreStageSize(stage);
        } else {
            new MainMenuView().start(stage);
            ViewsUtils.restoreStageSize(stage);
        }
    }
}
