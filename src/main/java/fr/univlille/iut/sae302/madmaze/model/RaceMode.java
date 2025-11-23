package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.List;
import java.util.Random;

/**
 * Représente le mode Course (Race Mode) du jeu MadMaze.
 * 
 * Dans ce mode, le joueur doit atteindre la sortie du labyrinthe avant un bot virtuel.
 * Le bot suit un chemin optimal ou plus long (selon le type de labyrinthe) et le joueur
 * perd si le bot atteint la sortie en premier (c'est-à-dire si le nombre de coups du joueur
 * atteint la longueur du chemin du bot).
 * 
 * Le mode supporte deux types de labyrinthes :
 * - RANDOM : labyrinthe aléatoire où le bot part d'une position aléatoire différente de celle du joueur
 * - PERFECT : labyrinthe parfait (un seul chemin) où le bot emprunte le chemin optimal
 * 
 * @author G4
 * @see Mode
 * @see RandomMaze
 * @see PerfectMaze
 */
public class RaceMode implements Mode {
    private Maze maze;
    private MazeType mazeType;
    private int width;
    private int height;
    private int parameter3;
    /** Le chemin que le bot doit suivre pour atteindre la sortie */
    private List<Cell> botPerfectPath;
   
    /**
     * Constructeur par défaut du mode Course.
     * Initialise tous les attributs à leur valeur par défaut.
     */
    public RaceMode(){}

    /**
     * Prépare et génère le labyrinthe pour le mode Course.
     * Crée le labyrinthe selon le type spécifié (RANDOM ou PERFECT) et calcule
     * le chemin que le bot doit emprunter.
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
        prepareBotPerfectPath();
    }

    /**
     * Définit les paramètres du labyrinthe pour le mode Course.
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
     * Retourne le labyrinthe associé à ce mode de jeu.
     *
     * @return l'instance du labyrinthe générée pour le mode Course
     */
    @Override
    public Maze getMaze() {
        return this.maze;
    }

    /**
     * Retourne le nom du mode de jeu.
     * 
     * @return le nom du mode : "Mode Course"
     */
    @Override
    public String getModeName() {
        return "Mode Course";
    }

    /**
     * Retourne le type de labyrinthe utilisé dans ce mode.
     * 
     * @return le type de labyrinthe (RANDOM ou PERFECT)
     */
    public MazeType getMazeType() {
        return this.mazeType;
    }

    /**
     * Retourne le chemin que le bot doit emprunter pour atteindre la sortie.
     * Si le chemin n'a pas pu être calculé, retourne une liste vide.
     * 
     * @return la liste des cellules formant le chemin du bot, ou une liste vide si aucun chemin valide
     */
    public List<Cell> getBotPerfectPath() {
        if (botPerfectPath == null) return new java.util.ArrayList<>();
        return botPerfectPath;
    }

    /**
     * Calcule et prépare le chemin que le bot doit suivre.
     * 
     * Pour les labyrinthes PERFECT : utilise le chemin optimal de l'entrée à la sortie.
     * Pour les labyrinthes RANDOM : cherche un chemin plus long en partant d'une position
     *                               aléatoire différente de l'entrée, ce qui crée un défi
     *                               équitable où le bot n'a pas l'avantage de la position.
     * 
     * Si aucun chemin convenable ne peut être trouvé pour les labyrinthes aléatoires,
     * on utilise le chemin optimal du départ à la sortie comme fallback.
     */
    private void prepareBotPerfectPath() {
        if (maze instanceof PerfectMaze) {
            PerfectMaze perfectMaze = (PerfectMaze) maze;
            botPerfectPath = perfectMaze.findOptimalPath(maze.getStart(), maze.getEnd());
            botPerfectPath.add(0, maze.getStart()); // Ajouter la cellule de départ en double au début du chemin pour le mode Perfect
        } else if(maze instanceof RandomMaze) {
            RandomMaze randomMaze = (RandomMaze) maze;
            Random rand = new Random();
            int newHeight, newWidth;
            CellType cellType = null;
            RandomCell newStart = null;
            int attempts = 0;
            do {
                newHeight = rand.nextInt(height);
                newWidth = rand.nextInt(width);
                RandomCell gridCell = randomMaze.getCell(newHeight, newWidth);
                if (gridCell == null) {
                    // defensive: skip invalid cell
                    continue;
                }
                cellType = gridCell.getCellType();
                newStart = new RandomCell(newHeight, newWidth, cellType);
                try {
                    botPerfectPath = randomMaze.findOptimalPath(newStart, maze.getEnd());
                } catch (Exception ex) {
                    botPerfectPath = new java.util.ArrayList<>();
                }
                attempts++;
            } while ((cellType == null || botPerfectPath == null || botPerfectPath.isEmpty() || botPerfectPath.size()-1 <= maze.optimalPathLength() || randomMaze.isWall(newStart)) && attempts < 2000);

            // Fallback: use the default start->end optimal path if no suitable random start found
            if (botPerfectPath == null || botPerfectPath.isEmpty()) {
                try {
                    botPerfectPath = randomMaze.findOptimalPath(randomMaze.getStart(), randomMaze.getEnd());
                } catch (Exception ex) {
                    botPerfectPath = new java.util.ArrayList<>();
                }
            }
        }
    }

    /**
     * Détermine si le joueur a perdu dans le mode Course.
     * Le joueur perd si le nombre de coups qu'il a effectués atteint ou dépasse
     * la longueur du chemin du bot (ce qui signifie que le bot aurait atteint la sortie).
     * 
     * @param moveCount le nombre de coups effectués par le joueur
     * @return true si le joueur a perdu (bot l'a rattrapé/devancé), false sinon
     */
    public boolean isLost(int moveCount) {
        if (botPerfectPath == null || botPerfectPath.isEmpty()) return false;
        return moveCount >= Math.max(0, botPerfectPath.size()-1);
    }
}
