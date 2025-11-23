package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Représente un labyrinthe parfait généré aléatoirement.
 * Un labyrinthe parfait est un labyrinthe sans boucles où il existe un chemin unique entre
 * deux points quelconques.
 * @author G4
 */
public class PerfectMaze implements Maze {
    private int width, height;
    private boolean[][] verticalWalls, horizontalWalls;
    private PerfectCell start, end;
    private int minLength;
    private PerfectCell[][] grid;

    public PerfectMaze(int width, int height, int minLength) {
        if (width < 2 || height < 2) 
            throw new IllegalArgumentException("width et height doivent être ≥ 2");
        if (minLength < 1)
            throw new IllegalArgumentException("minLength doit être ≥ 1");
        this.width = width;
        this.height = height;
        verticalWalls = new boolean[height][width - 1];
        horizontalWalls = new boolean[height - 1][width];
        this.minLength = minLength;
        
        // Initialiser la grille de cellules
        this.grid = new PerfectCell[height][width];

        // Generate maze and select start/end positions
        generateMaze();
    }

    /**
     * algorithme de génération (Growing Tree)
     */
    @Override
    public void generateMaze() {

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = new PerfectCell(row, col);
            }
        }

        // Initialisation du tableau de visites
        boolean[][] visited = new boolean[height][width];
        List<Cell> list = new ArrayList<>();
        Random rand = new Random();

        // Sélection aléatoire de la cellule de départ
        int row0 = rand.nextInt(height);
        int col0 = rand.nextInt(width);
        PerfectCell startCell = new PerfectCell(row0, col0);
        list.add(startCell);
        visited[row0][col0] = true;

        // Initialisation des murs à true (tous les murs sont présents)
        for (int i = 0; i < verticalWalls.length; i++){
            for (int j = 0; j < verticalWalls[0].length; j++){
                verticalWalls[i][j] = true;
            }
        }
        for (int i = 0; i < horizontalWalls.length; i++){
            for (int j = 0; j < horizontalWalls[0].length; j++){
                horizontalWalls[i][j] = true;
            }
        }

        // Algorithme "Growing Tree" (DFS/Prim aléatoire à chaque étape)
        while (!list.isEmpty()) {
            int index;
            if (rand.nextDouble() < 0.5) {
                index = list.size() - 1; // DFS/backtracking (dernier visité)
            } else {
                index = rand.nextInt(list.size()); // Prim (RandomCell)
            }
            PerfectCell current = (PerfectCell) list.get(index);

            List<PerfectCell> unvisitedNeighbors = new ArrayList<>();
            for (Cell n : neighbors(current.getRow(), current.getCol())) {
                if (!visited[n.getRow()][n.getCol()])
                    unvisitedNeighbors.add((PerfectCell) n);
            }

            if (!unvisitedNeighbors.isEmpty()) {
                PerfectCell chosen = unvisitedNeighbors.get(rand.nextInt(unvisitedNeighbors.size()));
                removeWall(current.getRow(), current.getCol(), chosen.getRow(), chosen.getCol());
                visited[chosen.getRow()][chosen.getCol()] = true;
                list.add(chosen);
            } else {
                list.remove(index);
            }
        }
        
        selectStartAndEnd();

        System.out.println("Distance minimale demandé : " + minLength + "\nDistance chemin optimal : " + optimalPathLength());
    }
    
    /**
     * Selection de l'entrée et de la sortie du labyrinthe au hasard en respectant la distance minimale.
     */
    private void selectStartAndEnd() {
        Random rand = new Random();
        
        start = new PerfectCell(rand.nextInt(height), rand.nextInt(width));
        
        // Calculate distances from start
        int[][] distances = shortestDistancesFrom((PerfectCell) start);
        
        // Trouver les candidats qui respectent minLength
        List<PerfectCell> candidates = new ArrayList<>();
        int maxDistanceFound = 0;
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int dist = distances[row][col];
                if (dist >= minLength) {
                    candidates.add(new PerfectCell(row, col));
                }
                if (dist > maxDistanceFound) {
                    maxDistanceFound = dist;
                }
            }
        }
        
        // Si aucun candidat ne respecte minLength, prendre les cellules les plus éloignées possibles
        if (candidates.isEmpty()) {
            System.out.println("WARNING: minLength=" + minLength + " trop grand pour ce labyrinthe. Distance max trouvée: " + maxDistanceFound);
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (distances[row][col] == maxDistanceFound) {
                        candidates.add(new PerfectCell(row, col));
                    }
                }
            }
        }
        
        this.end = candidates.get(rand.nextInt(candidates.size()));
    }


    /**
     * @param c1 la cellule de départ
     * @param c2 la cellule d'arrivée
     * @return true si c2 est accessible directement depuis c1, false sinon
     */
    @Override
    public boolean isDirectlyReachable(Cell c1, Cell c2) {
        // Défense sur null et position valide
        if (c1 == null || c2 == null) return false;
        
        // Vérifie que les deux cellules sont valides
        if (!isValidPosition(c1.getRow(), c1.getCol()) || !isValidPosition(c2.getRow(), c2.getCol())) return false;
        
        // Vérifie que les cellules sont adjacentes
        if (!adjacent(c1.getRow(), c1.getCol(), c2.getRow(), c2.getCol())) {
            return false;
        }
        // Vérifie qu'il n'y a pas de mur entre elles
        return !isWall(c1.getRow(), c1.getCol(), c2.getRow(), c2.getCol());
    }

    /**
     * Retourne le chemin optimal (le plus court) du point de départ à d'arrivée dans le labyrinthe.
     * @param startCell Cellule de départ
     * @param endCell Cellule d'arrivée
     * @return La liste de Cell du chemin, ou liste vide si pas de chemin (devrait être impossible en perfect maze)
     */
    @Override
    public List<Cell> findOptimalPath(Cell startCell, Cell endCell) {
        if (!(startCell instanceof PerfectCell) || !(endCell instanceof PerfectCell)) {
            throw new IllegalArgumentException("Les cellules doivent être des PerfectCell");
        }

        // Cast des cellules aux types spécifiques  
        PerfectCell start = (PerfectCell) startCell;
        PerfectCell end = (PerfectCell) endCell;

        int[][] dist = shortestDistancesFrom(start);
        if (dist[end.getRow()][end.getCol()] == -1) return Collections.emptyList(); // pas de chemin

        List<Cell> path = new ArrayList<>();
        Cell cur = end;
        path.add(cur);

        while (!cur.equals(start)) {
            int d = dist[cur.getRow()][cur.getCol()];
            boolean moved = false;
            for (Cell nb : neighbors(cur.getRow(), cur.getCol())) {
                if (!isWall(cur.getRow(), cur.getCol(), nb.getRow(), nb.getCol()) && dist[nb.getRow()][nb.getCol()] == d - 1) {
                    cur = nb;
                    path.add(cur);
                    moved = true;
                    break;
                }
            }
            if (!moved) {
                // Défensive: ne devrait pas arriver si dist est correct
                return Collections.emptyList();
            }
        }

        Collections.reverse(path);
        return path;
    }

    /** 
     * Vérifie s'il y a un mur entre les deux cellules adjacentes spécifiées.
     * @param row la ligne de la première cellule
     * @param col la colonne de la première cellule
     * @param row2 la ligne de la deuxième cellule
     * @param col2 la colonne de la deuxième cellule
     * @return true s'il y a un mur entre les deux cellules, false sinon
     */
    public boolean isWall(int row, int col, int row2, int col2) {
        // Vérifie que (y1, x1) et (y2, x2) sont bien adjacents
        if (!adjacent(row, col, row2, col2)) {
            throw new IllegalArgumentException("Les cellules ne sont pas adjacentes");
        }

        if (!isValidPosition(row, col) || !isValidPosition(row2, col2)) return true;

        // Si adjacence horizontale
        if (row == row2) {
            int cMin = Math.min(col, col2);
            return verticalWalls[row][cMin];
        }

        // Si adjacence verticale
        if (col == col2) {
            int rMin = Math.min(row, row2);
            return horizontalWalls[rMin][col];
        }

        // Cas impossible si la méthode adjacent est correcte
        throw new IllegalArgumentException("Les cellules ne sont pas adjacentes");

    }

    /**
     * Vérifie si les coordonnées spécifiées sont valides dans le labyrinthe.
     * @param row
     * @param col
     * @return true si les coordonnées sont valides, false sinon
     */
    @Override
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    /**
     * Vérifie si deux cellules sont adjacentes
     * @param row
     * @param col
     * @param row2
     * @param col2
     * @return true si les cellules sont adjacentes, false sinon
     */
    private boolean adjacent(int row, int col, int row2, int col2) {
        int dx = Math.abs(col - col2);
        int dy = Math.abs(row - row2);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    /**
     * Retire le mur entre les deux cellules adjacentes spécifiées.
     * @param row1
     * @param col1
     * @param row2
     * @param col2
     */
    private void removeWall(int row1, int col1, int row2, int col2) {

        if (!adjacent(row1, col1, row2, col2))
            throw new IllegalArgumentException("Les cellules ne sont pas adjacentes");

        if (!isValidPosition(row1, col1) || !isValidPosition(row2, col2))
            throw new IllegalArgumentException("Position invalide");

        if (row1 == row2) {
            // Supression d'un mur vertical
            int minCol = Math.min(col1, col2);
            verticalWalls[row1][minCol] = false;
        } else if (col1 == col2) {
            // Suppression d'un mur horizontal
            int minRow = Math.min(row1, row2);
            horizontalWalls[minRow][col1] = false;
        } 
    }

    /**
     * Retourne la liste des cellules voisines (haut, bas, gauche, droite) de la cellule spécifiée.
     * @param row la ligne de la cellule
     * @param col la colonne de la cellule
     * @return la liste des cellules voisines
     */
    public List<Cell> neighbors(int row, int col) {
        List<Cell> neighbors = new ArrayList<>();
        if (row > 0) neighbors.add(new PerfectCell(row - 1, col));
        if (row < height - 1) neighbors.add(new PerfectCell(row + 1, col));
        if (col > 0) neighbors.add(new PerfectCell(row, col - 1));
        if (col < width - 1) neighbors.add(new PerfectCell(row, col + 1));
        return neighbors;
    }

    /**
     * Calcule les distances les plus courtes depuis la cellule de départ vers toutes les autres cellules.
     * @param start
     * @return un tableau 2D , -1 si inaccessible
     */
    private int[][] shortestDistancesFrom(Cell start) {

        int[][] dist = new int[height][width];
        for (int[] line : dist) Arrays.fill(line, -1);
        Queue<Cell> queue = new LinkedList<>();
        queue.offer(start);
        dist[start.getRow()][start.getCol()] = 0;

        while (!queue.isEmpty()) {
            Cell c = queue.poll();
            for (Cell n : neighbors(c.getRow(), c.getCol())) {
                if (!isWall(c.getRow(), c.getCol(), n.getRow(), n.getCol())
                    && dist[n.getRow()][n.getCol()] == -1) {
                    dist[n.getRow()][n.getCol()] = dist[c.getRow()][c.getCol()] + 1;
                    queue.offer(n);
                }
            }
        }
        return dist;
    }

    /**
     * @return la longueur minimale du chemin entre le point de départ et le point d'arrivée
     */
    @Override
    public int optimalPathLength() {
        return findOptimalPath(getStart(), getEnd()).size() - 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Bordure supérieure
        sb.append("+");
        for (int col = 0; col < width; col++) {
            sb.append("---+");
        }
        sb.append("\n");

        for (int row = 0; row < height; row++) {
            // Mur de gauche au début de ligne
            sb.append("|");
            for (int col = 0; col < width; col++) {
                String caseCourante = "   ";
                if (start != null && start.getRow() == row && start.getCol() == col)
                    caseCourante = " E ";
                else if (end != null && end.getRow() == row && end.getCol() == col)
                    caseCourante = " X ";

                sb.append(caseCourante);

                // Mur vertical de droite
                if (col < width - 1 && verticalWalls[row][col])
                    sb.append("|");
                else
                    sb.append(" ");
            }
            sb.append("|\n");

            // Murs horizontaux
            sb.append("+");
            for (int col = 0; col < width; col++) {
                if (row < height - 1) {
                    // Murs internes horizontaux
                    if (horizontalWalls[row][col]) {
                        sb.append("---");
                    } else {
                        sb.append("   ");
                    }
                } else {
                    // Bordure inférieure
                    boolean open = false;
                    if (start != null && start.getRow() == row && start.getCol() == col || end != null && end.getRow() == row && end.getCol() == col) {
                        open = true;
                    } if (open) {
                        sb.append("   ");
                    } else {
                        sb.append("---");
                    }
                }
                sb.append("+");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public void setWidth(int newWidth) {
        this.width = newWidth;
        generateMaze();
    }

    @Override
    public void setHeight(int newHeight) {
        this.height = newHeight;
        generateMaze();
    }

    public void setMinLength(int newMinLength) {
        this.minLength = newMinLength;
        generateMaze();
    }

    @Override
    public void setStart(Cell start) {
        if (!(start instanceof PerfectCell)) {
            throw new IllegalArgumentException("Start must be a PerfectCell");
        }
        this.start = (PerfectCell) start;
    }

    @Override
    public void setEnd(Cell end) {
        if (!(end instanceof PerfectCell)) {
            throw new IllegalArgumentException("End must be a PerfectCell");
        }
        this.end = (PerfectCell) end; 
    }

    /**
     * @return une grille virtuelle de Cell pour compatibilité avec les vues
     */
    @Override
    public Cell[][] getGrid() {
        return grid;
    }

    @Override
    public PerfectCell getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return grid[row][col];
        }
        return null;
    }

    @Override
    public int getWidth() { return this.width; }

    @Override
    public int getHeight() { return this.height; }

    @Override
    public Cell getStart() { return this.start; }

    @Override
    public Cell getEnd() { return this.end; }

    public int getMinLength() { return this.minLength; }

    /**
     * @param cell la cellule à vérifier
     * @return true si la cellule est la cellule de fin, false sinon
     */
    @Override
    public boolean isEnd(Cell cell) { 
        return cell != null && this.end != null && cell.getCol() == this.end.getCol() && cell.getRow() == this.end.getRow(); 
    }

}
