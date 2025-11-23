package main.java.fr.univlille.iut.sae302.madmaze.model;

/**
 * Interface définissant les comportements communs à tous les modes de jeu.
 * Chaque mode de jeu doit fournir un labyrinthe et une méthode pour le générer.
 *
 * @author G4
 */
public interface Mode {

    // Fournit le labyrinthe en cours (quelle que soit la logique du mode)
    public Maze getMaze();

    // Gère la génération, la configuration ou la réinitialisation du labyrinthe selon le mode choisi
    void prepareMaze();

    // Fournit un nom/textuel pour affichage ou debug
    String getModeName();

    // Optionnel, utilisé uniquement pour les modes qui en ont besoin
    default Profil getPlayer() { return null; }
    default int getScore() { return 0; }
    default void setMaze(MazeType mazeType, int width, int height, int value) {}
}
