package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.view.CreditsView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView;

public class CreditsController {

    private final Stage stage;

    public CreditsController(Stage stage) {
        this.stage = stage;
    }

    public void setupListeners(CreditsView view) {
        view.getBackButton().setOnAction(e -> onBack());
    }

    public void onBack() {
        System.out.println("[CreditsController] Retour requested");
        // go back to main menu
        new MainMenuView().start(stage);
    }

    
    
}
