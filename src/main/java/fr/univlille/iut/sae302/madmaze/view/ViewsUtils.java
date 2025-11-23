package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.stage.Stage;

public class ViewsUtils {
    private static double lastWidth = 800;
    private static double lastHeight = 600;
    private static double lastX = -1;
    private static double lastY = -1;

    // À appeler avant de changer de page (optionnel mais conseillé)
    public static void saveStageSize(Stage stage) {
        lastWidth = stage.getWidth();
        lastHeight = stage.getHeight();
        lastX = stage.getX();
        lastY = stage.getY();
    }

    // À appeler après le changement de page pour réappliquer la taille
    public static void restoreStageSize(Stage stage) {
        stage.setWidth(lastWidth);
        stage.setHeight(lastHeight);
        // Ces deux lignes sont optionnelles (pour éviter que la fenêtre bouge) :
        if (lastX >= 0 && lastY >= 0) {
            stage.setX(lastX);
            stage.setY(lastY);
        }
    }   
}
    

