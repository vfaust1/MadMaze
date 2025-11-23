package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Représente un labyrinthe composé d'une grille de cellules avec une entrée et une sortie.
 * Le labyrinthe peut être généré aléatoirement avec un pourcentage de murs configurable
 * et garantit l'existence d'un chemin entre l'entrée et la sortie.
 * @author G4
 */
public class RandomMaze implements Maze {
    protected RandomCell start;
    protected RandomCell end;
    protected int width;
    protected int height;
    protected RandomCell[][] grid;
    protected int wallPercentage;

    /**
     * Construit un labyrinthe avec des dimensions et un pourcentage de murs spécifiés.
     * Les positions d'entrée et de sortie seront définies lors de la génération.
     * 
     * @param width la largeur du labyrinthe en nombre de cellules
     * @param height la hauteur du labyrinthe en nombre de cellules
     * @param wallPercentage le pourcentage de murs dans le labyrinthe (entre 0 et 100)
     */
    public RandomMaze(int width, int height, int wallPercentage) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Largeur et hauteur doivent être > 0");
        if (wallPercentage < 0 || wallPercentage > 100)
            throw new IllegalArgumentException("Le pourcentage de murs doit être entre 0 et 100");
        this.width = width;
        this.height = height;
        this.wallPercentage = wallPercentage;
        this.grid = new RandomCell[height][width];

        generateMaze();
    }

    /**
     * Trouve le chemin optimal entre deux cellules en utilisant l'algorithme BFS (parcours en largeur).
     * Cette méthode garantit de trouver le chemin le plus court s'il existe.
     * 
     * @param startCell la cellule de départ
     * @param endCell la cellule d'arrivée
     * @return une liste ordonnée de cellules représentant le chemin optimal, ou une liste vide si aucun chemin n'existe
     */
    public List<Cell> findOptimalPath(Cell startCell, Cell endCell) {
        // Cast des cellules aux types spécifiques
        RandomCell start = (RandomCell) startCell;
        RandomCell end = (RandomCell) endCell;

        // Cas spécial où le départ et l'arrivée sont identiques
        if (start.equals(end)) {
            List<Cell> path = new ArrayList<>();
            path.add(start);
            return path;
        }

        // Initialisation de la structure de données pour BFS
        Queue<RandomCell> queue = new LinkedList<>();
        Map<RandomCell, RandomCell> parentMap = new HashMap<>();
        boolean[][] visited = new boolean[height][width];

        queue.offer(start);
        visited[start.getRow()][start.getCol()] = true;
        parentMap.put(start, null);

        // Parcours en largeur
        while (!queue.isEmpty()) {
            RandomCell current = queue.poll();

            if (current.equals(end)) {
                return reconstructPath(parentMap, end);
            }
            
            // 
            for (RandomCell neighbor : neighbors(current.getRow(), current.getCol())) {
                int nRow = neighbor.getRow();
                int nCol = neighbor.getCol();

                if (nRow < 0 || nRow >= height || nCol < 0 || nCol >= width) {
                    System.err.println("ERREUR: Voisin invalide détecté row=" + nRow + " col=" + nCol);
                    continue;
                }

                if (!visited[nRow][nCol]) {
                    queue.offer(neighbor);
                    parentMap.put(neighbor, current);
                    visited[nRow][nCol] = true;
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Calcule la longueur minimale du chemin entre l'entrée et la sortie du labyrinthe.
     * 
     * @return le nombre de déplacements minimum nécessaires pour atteindre la sortie depuis l'entrée
     */
    @Override
    public int optimalPathLength() {
        List<Cell> chemin = findOptimalPath(this.start, this.end);
        if (chemin.isEmpty()) return -1; // Aucun chemin possible
        return chemin.size() - 1; // nb d'arêtes, pas nb de sommets
    }

    /**
     * Trouve un chemin plus long que le chemin optimal en explorant des chemins alternatifs.
     * La stratégie consiste à faire intentionnellement des détours en priorité aux voisins
     * qui ne mènent pas directement au chemin optimal.
     * 
     * @param startCell la cellule de départ
     * @param endCell la cellule d'arrivée
     * @return une liste de cellules représentant un chemin plus long, ou null si aucun chemin plus long n'existe
     */
    public List<Cell> findLongerPath(Cell startCell, Cell endCell) {
        // Récupère d'abord le chemin optimal pour connaître sa longueur
        if (startCell == null || endCell == null) return null;

        RandomCell start = (RandomCell) startCell;
        RandomCell end = (RandomCell) endCell;

        // Validate positions are inside this maze
        if (!isValidPosition(start.getRow(), start.getCol()) || !isValidPosition(end.getRow(), end.getCol())) {
            return null;
        }

        List<Cell> optimalPath = findOptimalPath(startCell, endCell);
        if (optimalPath == null || optimalPath.isEmpty()) {
            return null; // Aucun chemin ne peut exister
        }

        int optimalLength = optimalPath.size();

        // Parcours en profondeur avec limite pour trouver un chemin plus long
        List<RandomCell> longerPath = new ArrayList<>();
        boolean[][] visited = new boolean[height][width];
        
        // Cherche un chemin plus long par DFS avec une limite raisonnable
        if (depthFirstSearchForLonger(start, end, longerPath, visited, optimalLength)) {
            List<Cell> result = new ArrayList<>(longerPath);
            return result;
        }

        return null; // Aucun chemin plus long trouvé
    }

    /**
     * Parcours en profondeur pour trouver un chemin plus long que le chemin optimal.
     * Utilise une limite d'appels récursifs pour éviter les boucles infinies.
     * 
     * @param current cellule courante
     * @param end cellule de sortie cible
     * @param path chemin en construction
     * @param visited matrice de visite
     * @param minRequiredLength longueur minimale que le chemin doit dépasser
     * @return true si un chemin plus long a été trouvé, false sinon
     */
    private boolean depthFirstSearchForLonger(RandomCell current, RandomCell end, 
                                               List<RandomCell> path, boolean[][] visited, 
                                               int minRequiredLength) {
        // Limite de profondeur pour éviter trop de détours
        int maxDepth = (height * width) / 2 + minRequiredLength;
        if (path.size() > maxDepth) {
            return false;
        }

        path.add(current);
        visited[current.getRow()][current.getCol()] = true;

        // Vérifie si nous avons atteint la fin avec un chemin suffisamment long
        if (current.equals(end) && path.size() > minRequiredLength) {
            return true;
        }

        // Explore les voisins
        List<RandomCell> neighbors = neighbors(current.getRow(), current.getCol());
        
        // Trie les voisins pour prioritiser les chemins moins directs
        for (RandomCell neighbor : neighbors) {
            int nRow = neighbor.getRow();
            int nCol = neighbor.getCol();

            if (!visited[nRow][nCol]) {
                if (depthFirstSearchForLonger(neighbor, end, path, visited, minRequiredLength)) {
                    return true;
                }
            }
        }

        // Backtrack
        path.remove(path.size() - 1);
        visited[current.getRow()][current.getCol()] = false;
        return false;
    }

    /**
     * Reconstruit le chemin complet à partir de la carte des parents générée par l'algorithme BFS.
     * 
     * @param parentMap la map associant chaque cellule à son parent dans le chemin
     * @param end la cellule de destination
     * @return la liste ordonnée des cellules formant le chemin de l'entrée à la sortie
     */
    private List<Cell> reconstructPath(Map<RandomCell, RandomCell> parentMap, RandomCell end) {
        List<Cell> path = new ArrayList<>();
        for (RandomCell curr = end; curr != null; curr = parentMap.get(curr)) {
            path.add(0, curr);
        }
        return path;
    }

    /**
     * Vérifie si une cellule est un mur.
     * Les positions invalides (hors limites) sont considérées comme des murs.
     * 
     * @param cell la cellule à vérifier
     * @return true si la cellule est un mur ou hors limites, false sinon
     */
    public boolean isWall(Cell cell) {
        if (cell instanceof RandomCell) {
            RandomCell rc = (RandomCell) cell;
            return rc.isWall();
        }
        return false;
    }

    /**
     * Vérifie si la cellule à la position spécifiée est un mur.
     * @param row la ligne de la cellule à vérifier
     * @param col la colonne de la cellule à vérifier
     * @return true si la cellule est un mur ou hors limites, false sinon
     */
    public boolean isWall(int row, int col) {
        return isValidPosition(row, col) && grid[row][col] != null && grid[row][col].isWall();
    }
        
    /**
     * Génère aléatoirement la structure du labyrinthe avec le pourcentage de murs spécifié.
     * Place l'entrée et la sortie sur des bords opposés et distribue les murs aléatoirement.
     * Ouvre au minimum une case adjacente à l'entrée et à la sortie.
     */
    public void generate() {
        Random rand = new Random();
    
        // Initialisation de la grille avec seulement des chemins
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = new RandomCell(i, j);
            }
        }
    
        // Placement de l'entrée et de la sortie
        //ensureStartEndOpposite();
        ensureStartEndOpposite(rand);
    
        // Placement aléatoire des murs, en évitant l'entrée et la sortie
        int nbMurs = Math.round(width * height * wallPercentage / 100f);
        int mursPlaces = 0;
    
        while (mursPlaces < nbMurs) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
        
            if (!(x == start.getCol() && y == start.getRow()) && 
                !(x == end.getCol() && y == end.getRow()) && 
                !grid[y][x].isWall()) {
            
                grid[y][x].setWall();
                mursPlaces++;
            }
        }

        // Ouvre au moins une case adjacente à l'entrée et à la sortie
        openNeighborPath(start, end);

        // Ouvre une case autour de la sortie
        openNeighborPath(end, start);
    }

    /**
     * Retourne la liste des cellules voisines accessibles d'une cellule donnée.
     * @param row la ligne de la cellule
     * @param col la colonne de la cellule
     * @return la liste des cellules voisines accessibles
     */
    public List<RandomCell> neighbors(int row, int col) {
        List<RandomCell> voisins = new ArrayList<>();
        int[][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
        for (int[] d : directions) {
            int nRow = row + d[0], nCol = col + d[1];
            if (nRow >= 0 && nRow < height && nCol >= 0 && nCol < width) {
                RandomCell neighbor = grid[nRow][nCol];
                if (neighbor != null && !neighbor.isWall()) {
                    voisins.add(neighbor);
                }
            }
        }
        return voisins;
    }
    

    /**
     * Ouvre aléatoirement une case voisine (hors cellule exclue) autour d’une cellule donnée.
     * Si la seule case disponible est l’exclusion (typiquement l’autre extrémité), ne fait rien.
     *
     * @param cell la cellule centrale autour de laquelle ouvrir un passage
     * @param exclude la cellule à ne pas ouvrir (exemple : côté entrée lors du traitement de la sortie)
     */
    public void openNeighborPath(RandomCell cell, RandomCell exclude) {
        // Récupérer TOUS les voisins adjacents (même les murs)
        List<RandomCell> allNeighbors = new ArrayList<>();
        int[][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
        for (int[] d : directions) {
            int nRow = cell.getRow() + d[0];
            int nCol = cell.getCol() + d[1];
            if (nRow >= 0 && nRow < height && nCol >= 0 && nCol < width) {
                RandomCell neighbor = grid[nRow][nCol];
                if (neighbor != null && !neighbor.equals(exclude)) {
                    allNeighbors.add(neighbor);
                }
            }
        }
        
        // Ouvrir (forcer à PATH) un voisin aléatoire parmi ceux disponibles
        if (!allNeighbors.isEmpty()) {
            RandomCell open = allNeighbors.get(new Random().nextInt(allNeighbors.size()));
            open.setPath();  // Force l'ouverture même si c'était un mur
        }
    }

    /**
     * Positionne l'entrée et la sortie à deux cases différentes, n'importe où dans la grille.
     */
    public void placeRandomStartEnd() {
        Random rand = new Random();
        int startRow = rand.nextInt(height);
        int startCol = rand.nextInt(width);

        int endRow, endCol;
        do {
            endRow = rand.nextInt(height);
            endCol = rand.nextInt(width);
        } while (endRow == startRow && endCol == startCol);

        start = grid[startRow][startCol];
        end = grid[endRow][endCol];
    }

    /**
     * Assure que l'entrée et la sortie du labyrinthe sont placées sur des bords opposés.
     * @param random
     */
    private void ensureStartEndOpposite(Random random) {
        int startSide;
        int sx, sy;
        // Si start déjà défini, calcule son bord :
        if (this.start == null) {
            int[] sp = generateBorderPosition(random);
            sx = sp[0];
            sy = sp[1];
            startSide = sp[2];
        } else {
            sx = this.start.getCol();
            sy = this.start.getRow();
            if (sy == 0) startSide = 0; // haut
            else if (sx == width - 1) startSide = 1; // droite
            else if (sy == height - 1) startSide = 2; // bas
            else if (sx == 0) startSide = 3; // gauche
            else startSide = random.nextInt(4);
        }
        // Bord opposé :
        int endSide = (startSide + 2) % 4;
        int[] ep = generateBorderPosition(random, endSide);
        int ex = ep[0];
        int ey = ep[1];
        this.start = new RandomCell(sy, sx, CellType.PATH);
        this.end = new RandomCell(ey, ex, CellType.PATH);
        grid[sy][sx] = this.start;
        grid[ey][ex] = this.end;
    }

    private int[] generateBorderPosition(Random random) {
        int side = random.nextInt(4);
        return generateBorderPosition(random, side);
    }

    /**
     * Génère une position aléatoire sur un bord spécifique du labyrinthe.
     * 
     * @param random le générateur de nombres aléatoires
     * @param forcedSide le bord sur lequel générer la position (0=haut, 1=droite, 2=bas, 3=gauche)
     * @return un tableau contenant [x, y, side] représentant la position générée
     */
    private int[] generateBorderPosition(Random random, int forcedSide) {
        int x, y;

        switch (forcedSide) {
            case 0:
                x = random.nextInt(width);
                y = 0;
                break;
            case 1:
                x = width - 1;
                y = random.nextInt(height);
                break;
            case 2:
                x = random.nextInt(width);
                y = height - 1;
                break;
            case 3:
                x = 0;
                y = random.nextInt(height);
                break;
            default:
                throw new IllegalArgumentException("forcedSide must be between 0 and 3");
        }

        return new int[]{x, y, forcedSide};
    }

    /**
     * Génère un labyrinthe valide avec un chemin garanti entre l'entrée et la sortie.
     * Effectue jusqu'à 500 tentatives par niveau de pourcentage de murs.
     * Si aucun chemin n'est trouvé après 500 tentatives, réduit le pourcentage de murs de 1%.
     */
    public void generateMaze() {
        int maxAttemptsPerLevel = 500; // Nombre maximum de tentatives avant de réduire le pourcentage de murs
        int totalAttempts = 1; // Compteur total de tentatives
        int initialWallPercentage = this.wallPercentage; // Sauvegarde du pourcentage initial de murs

        this.generate(); // Première génération
        
        // Boucle jusqu'à ce qu'un chemin soit trouvé
        while (this.findOptimalPath(this.start, this.end).isEmpty()) {
            this.generate();
            totalAttempts++;
            
            // Après un certain nombre de tentatives, réduire le pourcentage de murs de 1%
            if (totalAttempts % maxAttemptsPerLevel == 0) {
                this.wallPercentage -= 1;
            }
        }

        // Affichage des informations de génération dans la console
        if (this.wallPercentage < initialWallPercentage) {
            System.out.println("OK: Labyrinthe genere avec " + this.wallPercentage + "% de murs (initialement " + initialWallPercentage + "%)");
            System.out.println("   Nombre total de tentatives : " + totalAttempts);
        } else {
            System.out.println("OK: Labyrinthe genere en " + totalAttempts + " tentative(s)");
        }
        System.out.println("Distance chemin optimal : " + optimalPathLength() + "\nNombre de murs : " + countWalls() + "\nNombres de chemins : " + countFreeCells() + "\nPourcentage de murs exact : " + ((countWalls() * 100f) / (width * height)) + "%");
    }

    /**
     * Vérifie si la cellule donnée existe dans la grille et en est une instance reconnue.
     * @param cell la cellule à tester (de type Cell)
     * @return true si la cellule appartient à la grille, false sinon
     */
    public boolean isValidPosition(Cell cell) {
        if (cell instanceof RandomCell) {
            RandomCell rc = (RandomCell) cell;
            return isValidPosition(rc.getRow(), rc.getCol());
        }
        return false;
    }

    /**
     * Vérifie si la position (row, col) correspond à une cellule valide de la grille.
     * @param row ligne à tester
     * @param col colonne à tester
     * @return true si les coordonnées sont dans les bornes de la grille, false sinon
     */
    @Override
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width && grid[row][col] != null;
    }


    /**
     * Vérifie si une cellule correspond à la sortie du labyrinthe.
     * 
     * @param cell la cellule à vérifier
     * @return true si la cellule est la sortie, false sinon
     */
    public boolean isEnd(Cell cell) {
        return cell != null && isEnd(cell.getRow(), cell.getCol());
    }

    /**
     * Vérifie si une position (row, col) correspond à la sortie du labyrinthe.
     * @param row la ligne de la cellule
     * @param col la colonne de la cellule
     * @return true si la cellule est la sortie, false sinon
     */
    public boolean isEnd(int row, int col) {
        return end != null && end.getRow() == row && end.getCol() == col;
    }

    /**
     * Vérifie si une cellule est accessible depuis une autre cellule adjacente.
     * Les deux cellules doivent être valides, ne pas être des murs et être adjacentes (une case de distance).
     * 
     * @param from la cellule de départ
     * @param to la cellule de destination
     * @return true si le déplacement est possible, false sinon
     */
    public boolean isDirectlyReachable(Cell from, Cell to) {
        return adjacent(from, to) && !isWall(from) && !isWall(to);
    }

    /**
     * Vérifie si deux cellules sont orthogonalement adjacentes dans la grille.
     * @param a première cellule
     * @param b seconde cellule
     * @return true si les deux cellules sont voisines (haut, bas, gauche, droite)
     */
    public boolean adjacent(Cell a, Cell b) {
        if (a == null || b == null) return false;
        int dr = Math.abs(a.getRow() - b.getRow());
        int dc = Math.abs(a.getCol() - b.getCol());
        return (dr + dc) == 1;
    }

    /**
    * Définit la cellule de sortie du labyrinthe.
    * Vérifie que la cellule appartient bien à la grille.
    * 
    * @param cell la cellule à définir comme sortie
    */
    public void setEnd(Cell cell) {
        // On vérifie que cell est bien dans la grille et non null
        if (cell != null && isValidPosition(cell.getRow(), cell.getCol())) {
            this.end = grid[cell.getRow()][cell.getCol()];
        }
    }

    /**
     * Définit la sortie à la position (row, col) si elle existe.
     * 
     * @param row ligne cible
     * @param col colonne cible
     */
    public void setEnd(int row, int col) {
        if (isValidPosition(row, col)) {
            this.end = grid[row][col];
        }
    }

    /**
     * Définit la position de l'entrée du labyrinthe.
     * La cellule doit être une position valide dans la grille.
     * 
     * @param start la cellule désignée comme entrée
     */
    public void setStart(Cell start) {
        this.start = (RandomCell) start;
        
    }

   /**
     * Définit la cellule donnée comme mur (WALL) si c’est possible.
     * @param cell la cellule à transformer en mur
     */
    public void setWall(Cell cell) {
        if (cell != null && isValidPosition(cell.getRow(), cell.getCol())) {
            grid[cell.getRow()][cell.getCol()].setWall();
        }
    }

    /**
     * Définit la cellule à la position (row, col) comme mur,
     * si la position est valide.
     * @param row ligne cible
     * @param col colonne cible
     */
    public void setWall(int row, int col) {
        if (isValidPosition(row, col)) {
            grid[row][col].setWall();
        }
    }

    /**
     * Définit la largeur du labyrinthe et réinitialise la grille si besoin.
     * @param width nouvelle largeur à affecter (>0)
     */
    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
            this.grid = new RandomCell[height][width];
        } else {
            throw new IllegalArgumentException("La largeur doit être un entier positif.");
        }
    }


    /**
     * Définit la largeur du labyrinthe et réinitialise la grille si besoin.
     * @param height nouvelle hauteur à affecter (>0)
     */
    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
            this.grid = new RandomCell[height][width];
        } else {
            throw new IllegalArgumentException("La hauteur doit être un entier positif.");
        }
    }

    /**
     * Modifie le pourcentage de murs dans le labyrinthe.
     * 
     * @param newWallPercentage le nouveau pourcentage de murs (entre 0 et 100)
     */
    public void setWallPercentage(int newWallPercentage) {
        this.wallPercentage = newWallPercentage;
    }

    /**
     * Retourne la cellule d'entrée du labyrinthe.
     * 
     * @return la cellule de départ
     */
    public RandomCell getStart() {
        return this.start;
    }

    /**
     * Retourne la cellule de sortie du labyrinthe.
     * 
     * @return la cellule d'arrivée
     */
    public RandomCell getEnd() {
        return this.end;
    }

    /**
     * Retourne la largeur du labyrinthe.
     * 
     * @return le nombre de cellules en largeur
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Retourne la hauteur du labyrinthe.
     * 
     * @return le nombre de cellules en hauteur
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Retourne le pourcentage de murs du labyrinthe.
     * 
     * @return le pourcentage de murs (entre 0 et 100)
     */
    public int getWallPercentage(){
        return this.wallPercentage;
    }

    /**
     * Retourne la grille complète du labyrinthe.
     * 
     * @return la matrice bidimensionnelle de cellules
     */
    public Cell[][] getGrid() {
        return this.grid;
    }

    public RandomCell getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return grid[row][col];
        }
        return null;
    }


    /**
     * Retourne une représentation textuelle du labyrinthe.
     * Utilise les symboles : S (entrée), E (sortie), # (mur), et espaces (chemins).
     * 
     * @return une chaîne représentant le labyrinthe sous forme de grille
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (start != null && i == start.getRow() && j == start.getCol()) {
                    sb.append("S ");
                } else if (end != null && i == end.getRow() && j == end.getCol()) {
                    sb.append("E ");
                } else if (grid[i][j] != null && grid[i][j].isWall()) {
                    sb.append("# ");
                } else {
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Compte le nombre de cellules libres (chemins) dans le labyrinthe.
     * 
     * @return le nombre de cellules qui ne sont pas des murs
     */
    public int countFreeCells() {
        int count = 0;
        if (this.grid == null) return 0;
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (!this.grid[i][j].isWall()) count++;
            }
        }
        return count;
    }

    /**
     * Compte le nombre de murs dans le labyrinthe.
     * 
     * @return le nombre total de cellules qui sont des murs
     */
    public int countWalls(){
        int count = 0;
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (this.grid[i][j].isWall()) count++;
            }
        }
        return count;
    }
}
