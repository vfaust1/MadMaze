package main.java.fr.univlille.iut.sae302.madmaze.controller;

import main.java.fr.univlille.iut.sae302.madmaze.view.ProfilConnectionView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ProgressionModeView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;
import main.java.fr.univlille.iut.sae302.madmaze.view.LevelChoiceView;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfilConnectionController {

    private final Stage stage;
    private final Profil player;
    private final ProfilConnectionView view;

    public ProfilConnectionController(Profil player, ProfilConnectionView view) {
        this.stage = view.getStage();
        this.player = player;
        this.view = view;
    }

    public void setupListeners(ProfilConnectionView view) {
        view.getBackButton().setOnAction(e -> back());
        view.getValidateButton().setOnAction(e -> connectProfile());
        Scene scene = view.getScene();
        if (scene != null) attachKeyHandlers(scene);
    }

    public void connectProfile() {
        String enteredPassword = view.getPasswordField().getText();

        if (enteredPassword.equals(player.getPwd())) {

            try {
                ViewsUtils.saveStageSize(stage);
                new LevelChoiceView(stage, player);
                ViewsUtils.restoreStageSize(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            view.getErrorLabel().setText("Mot de passe incorrect");
        }
    }
    
    public void back() {
        ViewsUtils.saveStageSize(stage);
        // navigate back to progression view
        if (stage != null) {
            new ProgressionModeView(stage);
        }
        ViewsUtils.restoreStageSize(stage);
    }

    private void attachKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER: connectProfile(); break;
                case ESCAPE: back(); break;
                default:
                    break;
            }
        });
    }

}
