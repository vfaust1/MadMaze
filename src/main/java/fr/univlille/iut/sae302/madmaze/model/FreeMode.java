package main.java.fr.univlille.iut.sae302.madmaze.model;

/**
 * Représente le mode de jeu libre où le joueur peut explorer un labyrinthe généré aléatoirement.
 * Ce mode implémente l'interface Mode et permet de créer un labyrinthe personnalisé
 * avec des dimensions et un pourcentage de murs définis par l'utilisateur.
 *
 * @author G4
 */
public class FreeMode implements Mode {

    private Maze maze;
    private MazeType mazeType;
    private int width;
    private int height;
    private int parameter3;
   
    public FreeMode(){}

    @Override
    public void prepareMaze() {
        switch(mazeType) {
            case RANDOM:
                // Le paramètre wallPercentage peut être ajusté par l'utilisateur
                this.maze = new RandomMaze(width, height, parameter3);
                break;
            case PERFECT:
                // Parfait = chemin unique. minLength = longueur min du chemin solution (pour chalenge supplémentaire)
                this.maze = new PerfectMaze(width, height, parameter3);
                break;
        }
    }

    @Override
    public void setMaze(MazeType mazeType, int width, int height, int parameter3) {
        this.mazeType = mazeType;
        this.width = width;
        this.height = height;
        this.parameter3 = parameter3;
    }

    /**
     * Retourne le labyrinthe associé à ce mode de jeu.
     *
     * @return l'instance du labyrinthe utilisée dans ce mode libre
     */
    @Override
    public Maze getMaze() {
        return this.maze;
    }

    public String getModeName() {
        return "Mode Libre";
    }

    public MazeType getMazeType() {
        return this.mazeType;
    }

}
