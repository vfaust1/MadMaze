package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.Random;

/**
 * Représente le mode Sorties Multiples (Multiple Exit Mode) du jeu MadMaze.
 * 
 * Dans ce mode, le labyrinthe contient plusieurs sorties : une vraie sortie et plusieurs fausses sorties.
 * Le joueur doit identifier et atteindre la vraie sortie en évitant les fausses sorties qui le ramènent
 * au menu ou terminent le jeu.
 * 
 * Les fausses sorties sont générées aléatoirement sur les bords du labyrinthe et sont affichées en doré
 * pour les distinguer visuellement de la vraie sortie (affichée normalement).
 * 
 * Le mode supporte deux types de labyrinthes :
 * - RANDOM : labyrinthe aléatoire avec un pourcentage de murs configurable
 * - PERFECT : labyrinthe parfait avec un seul chemin de l'entrée à la sortie
 * 
 * @author G4
 * @see Mode\n * @see RandomMaze\n * @see PerfectMaze\n */
public class MultipleExitMode implements Mode {

    protected Maze maze;
    protected MazeType mazeType;
    protected int width;
    protected int height;
    protected int parameter3;
    /** Le tableau contenant les fausses sorties générées */
    private Cell[] falseExit;

    /**
     * Constructeur par défaut du mode Sorties Multiples.
     * Initialise tous les attributs à leur valeur par défaut.
     */
    public MultipleExitMode() {}

    /**
     * Retourne le labyrinthe associé à ce mode de jeu.
     * 
     * @return l'instance du labyrinthe généré pour le mode Sorties Multiples
     */
    @Override
    public Maze getMaze() {
        return maze;
    }

    /**
     * Prépare et génère le labyrinthe pour le mode Sorties Multiples.
     * Crée le labyrinthe selon le type spécifié (RANDOM ou PERFECT) et génère
     * un ensemble de fausses sorties sur les bords du labyrinthe.
     * 
     * Cette méthode doit être appelée après setMaze() pour initialiser le labyrinthe.
     */
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
        generateFalseExits(3);
    }

    /**
     * Retourne le nom du mode de jeu.
     * 
     * @return le nom du mode : "Mode Sorties Multiples"
     */
    @Override
    public String getModeName() {
        return "Mode Sorties Multiples";
    }

    /**
     * Définit les paramètres du labyrinthe pour le mode Sorties Multiples.
     * Cette méthode doit être appelée avant prepareMaze().
     * 
     * @param mazeType le type de labyrinthe (RANDOM ou PERFECT)
     * @param width la largeur du labyrinthe en nombre de cellules
     * @param height la hauteur du labyrinthe en nombre de cellules
     * @param parameter3 pour RANDOM : pourcentage de murs (0-100),
     *                   pour PERFECT : longueur minimale du chemin
     */
    @Override
    public void setMaze(MazeType mazeType, int width, int height, int parameter3) {
        this.mazeType = mazeType;
        this.width = width;
        this.height = height;
        this.parameter3 = parameter3;
    }

    /**
     * Génère aléatoirement un ensemble de fausses sorties dans le labyrinthe.
     * Les fausses sorties sont placées sur les bords du labyrinthe (haut, bas, gauche, droite)
     * de manière aléatoire et doivent être distinctes les unes des autres.
     * 
     * Chaque fausse sortie est positionnée sur un bord aléatoire à une coordonnée aléatoire
     * sur ce bord. Les doublons sont évités.
     * 
     * @param numberOfFalseExits le nombre de fausses sorties à générer
     */
    public void generateFalseExits(int numberOfFalseExits) {
        Random random = new Random();
        falseExit = new Cell[numberOfFalseExits];

        int count = 0;

        while (count < numberOfFalseExits) {

            // Choisir un côté aléatoire : 0 = haut, 1 = bas, 2 = gauche, 3 = droite
            int side = random.nextInt(4);
            int x = 0, y = 0;

            switch (side) {
                case 0: // haut
                    x = random.nextInt(width);
                    y = 0;
                    break;

                case 1: // bas
                    x = random.nextInt(width);
                    y = height - 1;
                    break;

                case 2: // gauche
                    x = 0;
                    y = random.nextInt(height);
                    break;

                case 3: // droite
                    x = width - 1;
                    y = random.nextInt(height);
                    break;
            }

            // getCell attend (row, col) donc on passe (y, x)
            Cell cell = maze.getCell(y, x);

            // Éviter les doublons
            boolean alreadyUsed = false;
            for (int i = 0; i < count; i++) {
                if (falseExit[i] == cell) {
                    alreadyUsed = true;
                    break;
                }
            }

            if (!alreadyUsed) {
                falseExit[count] = cell;

                count++;
            }
        }
    }

    /**
     * Retourne le tableau des fausses sorties générées.
     * 
     * @return un tableau de cellules représentant les fausses sorties,
     *         ou null si aucune fausse sortie n'a été générée
     */
    public Cell[] getFalseExit() {
        return this.falseExit;
    }
    
}
