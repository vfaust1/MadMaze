package main.java.fr.univlille.iut.sae302.madmaze.model;
/**
 * Représente les différents types de cellule possibles dans le labyrinthe.
 * Chaque type définit un état spécifique qu'une cellule peut avoir pendant
 * le déroulement du jeu.
 * 
 * @author G4
 */
public enum CellType {
    /**
     * Représente un mur infranchissable qui bloque le passage du joueur.
     */
    WALL,
    /**
     * Représente un chemin praticable où le joueur peut se déplacer librement.
     */
    PATH,
    /**
     * Représente la cellule contenant un bidon d'essence que le joueur doit collecter pour avancer.
     */
    FUEL, 
    /**
     * Représente une cellule recouverte par du sable pour le mode bonus tempête.
     */
    SAND;
}
