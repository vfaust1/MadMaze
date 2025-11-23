package main.java.fr.univlille.iut.sae302.madmaze.model;
/**
 * Représente les niveaux de difficulté disponibles pour les défis du jeu.
 * Permet de catégoriser les défis selon leur complexité.
 * 
 * @author G4
 */
public enum Difficulty {
    /**
     * Niveau de difficulté facile, adapté aux joueurs débutants.
     */
    EASY("Facile", 1),   
    /**
     * Niveau de difficulté moyen, offrant un défi équilibré.
     */
    MEDIUM("Moyen", 2),    
    /**
     * Niveau de difficulté élevé, destiné aux joueurs expérimentés.
     */
    HARD("Difficile", 3);


    private final String label;
    private final int level;

    Difficulty(String label, int level) {
        this.label = label;
        this.level = level;
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return level;
    }

    public static Difficulty fromLevel(int lvl) {
        for (Difficulty d : values()) {
            if (d.level == lvl) return d;
        }
        throw new IllegalArgumentException("Niveau inconnu");
    }

    @Override
    public String toString() { return label; }
}