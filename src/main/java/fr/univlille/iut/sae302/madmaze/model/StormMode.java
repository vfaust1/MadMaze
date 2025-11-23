package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.*;

/**
 * Mode de jeu avec tempête de sable qui se propage progressivement.
 * La tempête commence au point de départ et se propage en arc de cercle,
 * recouvrant les cellules de sable. Le joueur doit atteindre la sortie
 * avant d'être rattrapé par la tempête.
 *
 * @author G4
 */
public class StormMode implements Mode {
    private Maze maze;
    private Set<Cell> sandCells;
    private Set<Cell> stormFront; // Front actuel de la tempête
    private MazeType mazeType;
    private int width;
    private int height;
    private int thirdParameter;
    private int propagationInterval; // En millisecondes
    private boolean stormStarted; // Indique si la tempête a commencé
    
    /**
     * Construit un mode tempête avec un intervalle de propagation fixe de 2 secondes.
     */
    public StormMode() {       
        this.propagationInterval = 1000; // 2 secondes
        this.sandCells = new HashSet<>();
        this.stormFront = new HashSet<>();
        this.stormStarted = false;
    }
    
    @Override
    public Maze getMaze() {
        return maze;
    }

    public void setMaze(MazeType mazeType, int width, int height, int thirdParameter) {
        this.mazeType = mazeType;
        this.width = width;
        this.height = height;
        this.thirdParameter = thirdParameter;
    }

    @Override
    public void prepareMaze() {
        switch(mazeType) {
            case RANDOM:
                this.maze = new RandomMaze(width, height, thirdParameter);
                break;
            case PERFECT:
                this.maze = new PerfectMaze(width, height, thirdParameter);
                break;
        }
        initializeStorm();
    }
    
    /**
     * Initialise la tempête au point de départ du labyrinthe.
     * Le front commence à la position de départ mais le sable n'apparaît qu'après le premier mouvement.
     */
    private void initializeStorm() {
        Cell start = maze.getStart();
        stormFront.add(start);
    }
    
    /**
     * Propage la tempête d'une génération.
     * Toutes les cellules adjacentes au front actuel deviennent du sable.
     * Utilise un algorithme BFS pour propager en arc de cercle.
     */
    public void propagateStorm() {
        Set<Cell> newFront = new HashSet<>();
        
        // Pour chaque cellule du front actuel de la tempête
        for (Cell frontCell : stormFront) {
            // Propager aux 4 voisins (haut, bas, gauche, droite)
            for (Direction dir : Direction.allDirections()) {
                int newX = frontCell.getCol() + dir.getDx();
                int newY = frontCell.getRow() + dir.getDy();
                
                if (isValidPosition(newX, newY)) {
                    Cell neighbor = maze.getGrid()[newY][newX];
                    
                    // Transformer toutes les cellules en sable (chemins ET murs pour RandomMaze)
                    // Pour PerfectMaze, les murs n'existent pas dans la grille donc pas de problème
                    if (neighbor != null && !sandCells.contains(neighbor)) {
                        newFront.add(neighbor);
                        sandCells.add(neighbor);
                        
                        setSandOnCell(neighbor);
                    }
                }
            }
        }
        
        // Le nouveau front devient le front actuel
        stormFront = newFront;
    }
    
    /**
     * Définit une cellule comme recouverte de sable.
     * Fonctionne avec RandomCell et PerfectCell.
     *
     * @param cell la cellule à transformer en sable
     */
    private void setSandOnCell(Cell cell) {
        if (cell instanceof RandomCell) {
            ((RandomCell) cell).setSand();
        } else if (cell instanceof PerfectCell) {
            ((PerfectCell) cell).setSand();
        }
    }
    
    /**
     * Vérifie si une position est valide dans le labyrinthe.
     *
     * @param x la coordonnée x
     * @param y la coordonnée y
     * @return true si la position est dans les limites du labyrinthe
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < maze.getWidth() && y >= 0 && y < maze.getHeight();
    }
    
    /**
     * Vérifie si le joueur est rattrapé par la tempête.
     *
     * @param playerPosition la position actuelle du joueur
     * @return true si le joueur est sur une cellule de sable, false sinon
     */
    public boolean isLost(Cell playerPosition) {
        if (playerPosition == null) {
            return false;
        }
        
        // Vérifier directement si la cellule du labyrinthe est de type SAND
        Cell cell = maze.getGrid()[playerPosition.getRow()][playerPosition.getCol()];
        
        if (cell instanceof RandomCell) {
            return ((RandomCell) cell).isSand();
        } else if (cell instanceof PerfectCell) {
            return ((PerfectCell) cell).isSand();
        }
        
        return false;
    }
    
    /**
     * Retourne l'intervalle de propagation en millisecondes.
     *
     * @return l'intervalle de propagation
     */
    public int getPropagationInterval() {
        return propagationInterval;
    }

    /**
     * Démarre la tempête (appelé au premier mouvement du joueur).
     * Met la case de départ en sable immédiatement.
     */
    public void startStorm() {
        this.stormStarted = true;
        
        // Mettre la case de départ en sable au démarrage de la tempête
        Cell start = maze.getStart();
        sandCells.add(start);
        Cell startCell = maze.getGrid()[start.getRow()][start.getCol()];
        if (startCell != null) {
            setSandOnCell(startCell);
        }
    }

    /**
     * Vérifie si la tempête a démarré.
     *
     * @return true si la tempête a commencé, false sinon
     */
    public boolean isStormStarted() {
        return stormStarted;
    }

    @Override
    public String getModeName() {
        return "Storm Mode";
    }
    
    /**
     * Retourne toutes les cellules recouvertes de sable.
     *
     * @return un ensemble des cellules de sable
     */
    public Set<Cell> getSandCells() {
        return new HashSet<>(sandCells);
    }
    
    /**
     * Retourne le front actuel de la tempête.
     *
     * @return un ensemble des cellules du front de la tempête
     */
    public Set<Cell> getStormFront() {
        return new HashSet<>(stormFront);
    }
    
    /**
     * Réinitialise le mode tempête en régénérant le labyrinthe.
     */
    public void reset() {
        sandCells.clear();
        stormFront.clear();
        prepareMaze();
    }
}