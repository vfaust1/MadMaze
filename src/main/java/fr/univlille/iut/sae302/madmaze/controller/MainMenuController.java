package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.view.BonusModeView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ParameterView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ProgressionModeView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;
import main.java.fr.univlille.iut.sae302.madmaze.model.FreeMode;
//import main.java.fr.univlille.iut.sae302.madmaze.view.CreditsView;
import main.java.fr.univlille.iut.sae302.madmaze.model.Saves;
import static main.java.fr.univlille.iut.sae302.madmaze.model.Profil.listePlayers;


/**
 * Controller for the main menu view.
 * Contains action handlers that the view can call when buttons are pressed.
 * Keep UI logic here (navigation, dialogs, application state changes).
 */
public class MainMenuController {

    private final Stage stage;

    public MainMenuController(Stage stage) {
        this.stage = stage;
    }

    public void setupListeners(MainMenuView view) {
        view.getFreeModeButton().setOnAction(this::onFreeMode);
        view.getProgressionButton().setOnAction(this::onProgression);
        view.getBonusButton().setOnAction(this::onBonusMode);
        //view.getCreditsButton().setOnAction(this::onCredits);
        view.getQuitButton().setOnAction(this::onQuit);
    }

    public void onFreeMode(ActionEvent event) {
        System.out.println("[Controller] Mode libre requested");
        // navigate to free mode configuration scene
        ViewsUtils.saveStageSize(stage);
        new ParameterView(stage, new FreeMode());
        ViewsUtils.restoreStageSize(stage);
    }

    public void onProgression(ActionEvent event) {
        System.out.println("[Controller] Mode progression requested");
        if(listePlayers.isEmpty()) {
            Saves.loadAllPlayers();
        }
        new ProgressionModeView(stage);
    }

    public void onBonusMode(ActionEvent event) {
        System.out.println("[Controller] Mode bonus requested");
        new BonusModeView(stage);
    }

    /*public void onCredits(ActionEvent event) {
        System.out.println("[Controller] Credits requested");
        new CreditsView(stage);
    }*/

    public void onQuit(ActionEvent event) {
        System.out.println("[Controller] Quit requested");
        stage.close();
    }
}
