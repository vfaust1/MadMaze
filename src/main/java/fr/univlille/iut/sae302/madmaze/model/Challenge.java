package main.java.fr.univlille.iut.sae302.madmaze.model;

/**
 * Représente un défi dans le jeu avec un niveau de difficulté et un état de validation.
 * Un défi peut être validé par le joueur lorsqu'il le complète avec succès.
 * 
 * @author G4
 */
public class Challenge {
    private final Difficulty difficulty;
    private boolean isValid = false;
    private final int width;
    private final int height;
    private final int mazeParam;    // % de murs ou longueur de chemin selon le type

    /**
     * Construit un nouveau défi avec une difficulté spécifiée.
     * Le défi est initialement non validé.
     * 
     * @param difficulty le niveau de difficulté du défi
     */
    public Challenge(Difficulty difficulty, int width, int height, int mazeParam) {
        this.difficulty = difficulty;
        this.width = width;
        this.height = height;
        this.mazeParam = mazeParam;
    }


    public boolean isValid() {
        return this.isValid;
    }

    public void validateChallenge(){
        this.isValid = true;
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMazeParam() {
        return mazeParam;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getDifficulty()).append(" -  ")
          .append(this.getHeight()).append("x").append(this.getWidth())
          .append(", param: ").append(this.getMazeParam());
        if (isValid) sb.append(" [VALIDÉ]");
        return sb.toString();
    }
}
