package main.java.fr.univlille.iut.sae302.madmaze;

import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView;

/**
 * Application entry point. Delegates to JavaFX MainMenuView.
 */
public class App {

    public static void main(String[] args) {
        Stage stage = new Stage();
        MainMenuView mainMenuView = new MainMenuView();
        mainMenuView.start(stage);
    }

}
