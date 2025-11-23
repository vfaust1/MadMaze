package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.*;

/**
 * Mode de jeu avec système de carburant.
 * Le joueur doit ramasser des bidons d'essence pour continuer à se déplacer.
 * Les bidons sont placés de manière à garantir qu'ils sont atteignables avec le carburant disponible.
 *
 * @author G4
 */
public class FuelMode implements Mode {
    private Maze maze;
    private int fuelLeft;
    private int maxFuel;
    private Set<Cell> fuelCells;
    private MazeType mazeType;
    private int width;
    private int height;
    private int thirdParameter;
    
    /**
     * Constructeur par défaut.
     */
    public FuelMode() {
        this.maxFuel = 10;
        this.fuelLeft = maxFuel;
        this.fuelCells = new HashSet<>();
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public String getModeName() {
        return "Fuel Mode";
    }

    @Override
    public void setMaze(MazeType mazeType, int width, int height, int thirdParameter) {
        this.mazeType = mazeType;
        this.width = width;
        this.height = height;
        this.thirdParameter = thirdParameter;
    }

    @Override
    public void prepareMaze() {
        if (mazeType == MazeType.PERFECT) {
            maze = new PerfectMaze(width, height, thirdParameter);
        } else {
            maze = new RandomMaze(width, height, thirdParameter);
        }
        this.fuelLeft = maxFuel;
        this.fuelCells = new HashSet<>();
        placeFuelCans();
    }
    
    /**
     * Place les bidons d'essence dans le maze en garantissant qu'ils sont atteignables.
     * Calcule le nombre de bidons nécessaires basé sur le chemin optimal + 20%.
     */
    private void placeFuelCans() {
        List<Cell> optimalPath = maze.findOptimalPath(maze.getStart(), maze.getEnd());
        if (optimalPath == null || optimalPath.isEmpty()) {
            return;
        }
        
        int minPathLength = maze.optimalPathLength();
        
        // Calcule le nombre de bidons nécessaires pour couvrir le chemin
        int numberOfCans = (int) Math.ceil((double) minPathLength / maxFuel);
        
        if (numberOfCans <= 0) {
            numberOfCans = 1;
        }
        
        // Limite raisonnable : pas plus de bidons que la moitié de la taille du chemin
        numberOfCans = Math.min(numberOfCans, optimalPath.size() / 2);
        
        List<Cell> canPositions = calculateOptimalFuelPositions(numberOfCans, optimalPath);
        
        // Vérifie que le dernier bidon permet d'atteindre la sortie
        ensureReachableEnd(canPositions, optimalPath);
        
        for (Cell canPosition : canPositions) {
            fuelCells.add(canPosition);
            Cell cell = maze.getGrid()[canPosition.getRow()][canPosition.getCol()];
            if (cell != null && !cell.equals(maze.getStart()) && !cell.equals(maze.getEnd())) {
                if (cell instanceof RandomCell) {
                    ((RandomCell) cell).setFuel();
                } else if (cell instanceof PerfectCell) {
                    ((PerfectCell) cell).setFuel();
                }
            }
        }
        if (maze instanceof RandomMaze) {
            placeRandomFuelCans(((RandomMaze) maze).countFreeCells() / 25); // 5% de bidons aléatoires supplémentaires
        } else if (maze instanceof PerfectMaze) {
            // Pour les labyrinthes parfaits, ajoute aussi des bidons aléatoires (environ 4% des cellules)
            int totalCells = maze.getWidth() * maze.getHeight();
            placeRandomFuelCans(totalCells / 25);
        }
    }
    
    /**
     * Calcule les positions optimales pour placer les bidons d'essence.
     * Utilise le chemin optimal et place les bidons à intervalles réguliers.
     *
     * @param numberOfCans le nombre de bidons à placer
     * @param optimalPath le chemin optimal du départ à l'arrivée
     * @return la liste des positions où placer les bidons
     */
    private List<Cell> calculateOptimalFuelPositions(int numberOfCans, List<Cell> optimalPath) {
        List<Cell> canPositions = new ArrayList<>();
        
        if (optimalPath == null || optimalPath.isEmpty() || numberOfCans <= 0) {
            return canPositions;
        }
        
        // Limite numberOfCans pour éviter les débordements et les calculs inutiles
        int maxReasonableCans = Math.min(numberOfCans, optimalPath.size() - 2);
        if (maxReasonableCans <= 0) {
            return canPositions;
        }
        
        int interval = Math.max(1, optimalPath.size() / (maxReasonableCans + 1));
        
        for (int i = 1; i <= maxReasonableCans; i++) {
            // Protection contre le débordement : utilise long pour le calcul puis clamp
            long indexLong = (long) i * interval;
            int index = (int) Math.min(indexLong, (long) optimalPath.size() - 1);
            
            // Vérification de sécurité supplémentaire
            if (index < 0 || index >= optimalPath.size()) {
                continue;
            }
            
            Cell position = optimalPath.get(index);
            
            if (!position.equals(maze.getStart()) && !position.equals(maze.getEnd())) {
                canPositions.add(position);
            }
        }
        
        if (canPositions.size() < numberOfCans) {
            addAdditionalFuelCans(canPositions, numberOfCans - canPositions.size(), optimalPath);
        }
        
        return canPositions;
    }
    
    /**
     * Vérifie et garantit que la sortie est atteignable depuis le dernier bidon.
     * Ajoute un bidon supplémentaire si nécessaire.
     *
     * @param canPositions la liste des positions de bidons déjà calculées
     * @param optimalPath le chemin optimal du départ à l'arrivée
     */
    private void ensureReachableEnd(List<Cell> canPositions, List<Cell> optimalPath) {
        if (canPositions.isEmpty() || optimalPath.isEmpty()) {
            return;
        }
        
        // Trouve la position du dernier bidon sur le chemin optimal
        Cell lastFuel = canPositions.get(canPositions.size() - 1);
        int lastFuelIndex = optimalPath.indexOf(lastFuel);
        
        if (lastFuelIndex == -1) {
            // Le dernier bidon n'est pas sur le chemin optimal, on cherche le plus proche de la fin
            lastFuelIndex = -1;
            for (Cell can : canPositions) {
                int idx = optimalPath.indexOf(can);
                if (idx != -1 && idx > lastFuelIndex) {
                    lastFuelIndex = idx;
                    lastFuel = can;
                }
            }
            // Si aucun bidon n'est sur le chemin optimal, on ne peut rien faire
            if (lastFuelIndex == -1) {
                return;
            }
        }
        
        // Calcule la distance entre le dernier bidon et la sortie
        int distanceToEnd = optimalPath.size() - 1 - lastFuelIndex;
        
        // Si la distance est supérieure au fuel max, ajoute un bidon supplémentaire
        if (distanceToEnd > maxFuel) {
            // Place un bidon à maxFuel-1 cases avant la sortie sur le chemin optimal
            int newFuelIndex = Math.max(lastFuelIndex + 1, optimalPath.size() - maxFuel);
            // Clamp pour éviter dépassement : index max = size - 1
            newFuelIndex = Math.min(newFuelIndex, optimalPath.size() - 1);
            
            // Vérification de sécurité supplémentaire
            if (newFuelIndex >= 0 && newFuelIndex < optimalPath.size()) {
                Cell newFuelPosition = optimalPath.get(newFuelIndex);
                
                if (!newFuelPosition.equals(maze.getEnd()) && !canPositions.contains(newFuelPosition)) {
                    canPositions.add(newFuelPosition);
                }
            }
        }
    }
    
    /**
     * Ajoute des bidons supplémentaires autour du chemin optimal si nécessaire.
     *
     * @param currentCans les bidons déjà placés
     * @param needed le nombre de bidons supplémentaires nécessaires
     * @param optimalPath le chemin optimal du maze
     */
    private void addAdditionalFuelCans(List<Cell> currentCans, int needed, List<Cell> optimalPath) {
        Set<Cell> usedPositions = new HashSet<>(currentCans);
        usedPositions.add(maze.getStart());
        usedPositions.add(maze.getEnd());
        
        Cell[][] grid = maze.getGrid();
        
        for (Cell pathCell : optimalPath) {
            if (needed <= 0) break;
            
            for (Direction dir : Direction.allDirections()) {
                int newX = pathCell.getCol() + dir.getDx();
                int newY = pathCell.getRow() + dir.getDy();
                
                if (isValidPosition(newX, newY)) {
                    Cell neighbor = grid[newY][newX];
                    if (neighbor != null && !usedPositions.contains(neighbor)) {
                        if (maze instanceof RandomMaze) {
                            if (!neighbor.isWall()) {
                                currentCans.add(neighbor);
                                usedPositions.add(neighbor);
                                needed--;
                                if (needed <= 0) break;
                            }
                        } else {
                            currentCans.add(neighbor);
                            usedPositions.add(neighbor);
                            needed--;
                            if (needed <= 0) break;
                        }
                    }
                }
            }
        }

        
    }
    
    /**
     * Vérifie si une position est valide dans le maze.
     *
     * @param x la coordonnée x
     * @param y la coordonnée y
     * @return true si la position est dans les limites du maze
     */
    private boolean isValidPosition(int x, int y) {
        return maze.isValidPosition(y, x);
    }
    
    /**
     * Place des bidons de fuel au hasard sur des cases PATH du maze.
     *
     * @param numberOfRandomFuel le nombre de bidons aléatoires à placer
     */
    public void placeRandomFuelCans(int numberOfRandomFuel) {
        Cell[][] grid = maze.getGrid();
        Random random = new Random();
        
        // Collecte toutes les cellules PATH (non murs, non start, non end, sans fuel déjà)
        List<Cell> pathCells = new ArrayList<>();
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                Cell cell = grid[y][x];
                if (cell != null && !cell.equals(maze.getStart()) 
                    && !cell.equals(maze.getEnd())
                    && !fuelCells.contains(cell)) {
                    if (maze instanceof RandomMaze) {
                        if (!cell.isWall()) {
                            pathCells.add(cell);
                        }
                    } else {
                pathCells.add(cell);
                    }
                }
            }
        }
        
        // Place les bidons aléatoirement
        int placed = 0;
        while (placed < numberOfRandomFuel && !pathCells.isEmpty()) {
            int randomIndex = random.nextInt(pathCells.size());
            Cell randomCell = pathCells.remove(randomIndex);
            
            fuelCells.add(randomCell);
            if (randomCell instanceof RandomCell) {
                ((RandomCell) randomCell).setFuel();
            } else if (randomCell instanceof PerfectCell) {
                ((PerfectCell) randomCell).setFuel();
            }
            placed++;
        }
    }
    
    /**
     * Vérifie si le joueur peut effectuer un déplacement selon les règles du mode fuel.
     *
     * @return true si le joueur a encore du carburant, false sinon
     */
    public boolean canMove() {
        return fuelLeft > 0;
    }
    
    /**
     * Gère les effets d'un déplacement du joueur selon les règles du mode fuel.
     * Décrémente le carburant et vérifie si un bidon a été ramassé.
     *
     * @param newPosition la nouvelle position du joueur après le déplacement
     */
    public void onMove(Cell newPosition) {
        fuelLeft--;
        
        if (fuelCells.contains(newPosition)) {
            fuelLeft = maxFuel;
            fuelCells.remove(newPosition);
        }
    }
    
    /**
     * Vérifie si la partie est perdue selon les règles du mode fuel.
     *
     * @return true si le joueur n'a plus de carburant, false sinon
     */
    public boolean isLost() {
        return fuelLeft <= 0;
    }
    
    /**
     * Retourne le carburant restant.
     *
     * @return le nombre de mouvements restants avant de devoir ramasser un bidon
     */
    public int getFuelLeft() {
        return fuelLeft;
    }
    
    /**
     * Retourne le carburant maximum.
     *
     * @return le nombre maximum de mouvements entre deux bidons
     */
    public int getMaxFuel() {
        return maxFuel;
    }
    
    /**
     * Retourne les positions des bidons restants.
     *
     * @return un ensemble des cellules contenant encore des bidons
     */
    public Set<Cell> getFuelCells() {
        return new HashSet<>(fuelCells);
    }
    
    /**
     * Vérifie si un bidon de carburant est présent sur la cellule donnée.
     *
     * @param cell la cellule à vérifier
     * @return true si un bidon non ramassé est présent sur cette cellule
     */
    public boolean hasFuelAt(Cell cell) {
        return fuelCells.contains(cell);
    }
    
    /**
     * Réinitialise le mode fuel en régénérant le maze et les bidons.
     */
    public void reset() {
        prepareMaze();
    }
}