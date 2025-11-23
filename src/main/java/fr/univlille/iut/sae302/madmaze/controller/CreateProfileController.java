package main.java.fr.univlille.iut.sae302.madmaze.controller;

import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;
import main.java.fr.univlille.iut.sae302.madmaze.model.Saves;
import main.java.fr.univlille.iut.sae302.madmaze.view.CreateProfileView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ProgressionModeView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CreateProfileController {

    private final Stage stage;
    private CreateProfileView view;

    public CreateProfileController(Stage stage, CreateProfileView view) {
        this.stage = stage;
        this.view = view;
    }

    public void setupListeners() {
        view.getBackButton().setOnAction(e -> back());
        view.getValidateButton().setOnAction(e -> onValidate());
        Scene scene = view.getScene();
        if (scene != null) attachKeyHandlers(scene);
    }

    /**
     * Validate fields and append a new profile into ressources/csv/players.
     * Returns null on success or an error message on failure.
     */
    public String createProfile(String name, String pwd, String pwd2) {
        try{
            Profil player = new Profil(name,pwd,pwd2);
            Saves.createPlayer(player);
        }catch(Exception e){
            if(!pwd.equals(pwd2)){
                return "Mots de passes incorrect";
            }
            return "Profil déjà éxistant";
        }
        return null;
    }


    public void back() {
        // navigate back to progression view
        if (stage != null) {
            ViewsUtils.saveStageSize(stage);
            new ProgressionModeView(stage);
            ViewsUtils.restoreStageSize(stage);
        }
    }

    public void onValidate() {
        /*String name = view.getNameValue() == null ? "" : view.getNameValue().trim();
        String pwd = view.getPasswordValue() == null ? "" : view.getPasswordValue();
        String pwd2 = view.getConfirmPasswordValue() == null ? "" : view.getConfirmPasswordValue();*/
        String name = view.getNameValue().getText();
        String pwd = view.getPasswordValue().getText();
        String pwd2 = view.getConfirmPasswordValue().getText();

        // 1. Appeler le contrôleur
        String errorMsg = createProfile(name, pwd, pwd2);

        // 2. Vérifier le résultat
        if (errorMsg != null) {
            // ERREUR : Afficher le message et RESTER sur la page
            view.getErrorLabel().setText(errorMsg);
        } else {
            // SUCCÈS : Naviguer en arrière
            back();
        }
    }

    private void attachKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER: onValidate(); break;
                case ESCAPE: back(); break;
                default: break;
            }
        });
    }

}
