package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import main.java.fr.univlille.iut.sae302.madmaze.view.Observer;

/**
 * Représente une partie de jeu complète comprenant un labyrinthe et un mode de jeu.
 * Gère la position du joueur, le compteur de déplacements et fournit des méthodes
 * pour initialiser et réinitialiser la partie.
 * 
 * @author G4
 */
public class Game implements Observable {
    private List<Observer> observers;
    protected Mode mode;
    protected Cell playerPosition;
    private int moveCount = 0;
    private Direction joueurDirection;
    private Set<Cell> exploredCells; // Pour l'étape 6 : cellules explorées
    private int exploredCellsVisionRadius;
    private Timer madMaxRegenerationTimer;

    /**
     * Construit une nouvelle partie avec le mode de jeu spécifié.
     * Lance automatiquement la génération du labyrinthe et place le joueur à l'entrée.
     * 
     * @param mode le mode de jeu à utiliser pour cette partie
     */
    public Game(Mode mode) {
        this.mode = mode;
        this.exploredCells = new HashSet<>();

        if (this.mode != null) {
            this.mode.prepareMaze();
            if (this.getMode().getMaze() != null) {
                this.playerPosition = this.getMode().getMaze().getStart();
            }
        }
        setExploredCellsVision();
        exploreVision(getExploredCellsVisionRadius());
        this.joueurDirection = Direction.D;
        this.moveCount = 0;
        this.observers = new ArrayList<>();
        
        // Démarrer la régénération pour MadMaxMode
        if (this.mode instanceof MadMaxMode) {
            startMadMaxRegeneration();
        }
    }

    public Maze getMaze() {
        return this.getMode().getMaze();
    }

    public Mode getMode() {
        return this.mode;
    }

    public int getExploredCellsVisionRadius() {
        return this.exploredCellsVisionRadius;
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public Profil getPlayer() {
        return this.getMode().getPlayer();
    }

    public Cell getPlayerPosition() {
        return this.playerPosition;
    }

    /**
     * Incrémente le compteur de déplacements de 1.
     * Cette méthode est appelée à chaque mouvement du joueur.
     */
    public void incrementMoveCount() {
        this.moveCount++;
    }

    public Cell getPositionJoueur() {
        return this.playerPosition;
    }

    /**
     * Déplace le joueur dans la direction spécifiée.
     * Met à jour la position du joueur, marque l'ancienne cellule comme chemin
     * et incrémente le compteur de déplacements.
     *
     * @param direction la direction dans laquelle déplacer le joueur
     */
    public boolean movePlayer(Direction direction) {
        Maze maze = getMaze();

        // Vérifier les règles spécifiques du mode fuel avant de permettre le déplacement
        if (mode instanceof FuelMode fuelMode) {
            if (!fuelMode.canMove()) {
                return false;
            }
        }

        // Calculer les coordonnées de la prochaine cellule
        int newRow = playerPosition.getRow() + direction.getDy();
        int newCol = playerPosition.getCol() + direction.getDx();
        
        // Vérifier que la position est valide dans les limites du labyrinthe
        if (newRow >= 0 && newRow < maze.getHeight() && newCol >= 0 && newCol < maze.getWidth()) {
            Cell nextCell = maze.getCell(newRow, newCol);
            
            if (nextCell != null && maze.isDirectlyReachable(playerPosition, nextCell)){
                playerPosition = nextCell;
                incrementMoveCount();
                joueurDirection = direction;

                if (exploredCellsVisionRadius > 0) {
                    exploreVision(exploredCellsVisionRadius);
                } else if (!exploredCells.contains(nextCell)) {
                     exploredCells.add(nextCell); 
                }
                
                if (mode instanceof FuelMode) {
                    FuelMode fuelMode = (FuelMode) mode;
                    fuelMode.onMove(this.playerPosition);
                }

                // Démarrer la tempête au premier mouvement
                if (mode instanceof StormMode stormMode && !stormMode.isStormStarted()) {
                    stormMode.startStorm();
                    startStormPropagation();
                }

                notifyObservers();
                return true;
            }
        }
        return false;
    }

    /**
     * Marque les cellules dans le rayon de vision du joueur comme explorées.
     * Pour l'étape 6 : explore les chemins accessibles dans le rayon de vision.
     * 
     * @param radius le rayon de vision autour du joueur
     */
    public void exploreVision(int radius) {
        int px = playerPosition.getCol();
        int py = playerPosition.getRow();
        int w = getMaze().getWidth();
        int h = getMaze().getHeight();
        
        // Marquer toutes les cellules dans le rayon de vision comme explorées
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int x = px + dx;
                int y = py + dy;
                if (x >= 0 && y >= 0 && x < w && y < h) {
                    Cell cell = getMaze().getGrid()[y][x];
                    if (!exploredCells.contains(cell)) {
                        exploredCells.add(cell);
                    }
                }
            }
        }
    }

    public void setExploredCellsVision(){
        if (mode instanceof ProgressionMode) {
            ProgressionMode pm = (ProgressionMode) mode;
            if (pm.getCurrentLevelIndex() == 5) {
                if (pm.getCurrentChallengeIndex() == 0) {
                    this.exploredCellsVisionRadius = 2;
                }
                else if (pm.getCurrentChallengeIndex() == 1) {
                    this.exploredCellsVisionRadius = 1;
                } 
            }
        } else {
            this.exploredCellsVisionRadius = 0;
        }
    }

    /**
     * Vérifie si une cellule a été explorée par le joueur.
     * 
     * @param cell la cellule à vérifier
     * @return true si la cellule a été visitée, false sinon
     */
    public boolean isExplored(Cell cell) {
        return exploredCells.contains(cell);
    }

    /**
     * Retourne l'ensemble des cellules explorées par le joueur.
     * 
     * @return un Set contenant toutes les cellules visitées
     */
    public Set<Cell> getExploredCells() {
        return exploredCells;
    }

    /**
     * Vérifie si la partie est terminée.
     * En RaceMode, la partie est terminée si le joueur a perdu (dépassé le nombre de coups du bot)
     * ou s'il a atteint la sortie avant le bot.
     * Pour les autres modes, la partie est terminée seulement si le joueur atteint la sortie.
     * 
     * @return true si la partie est terminée, false sinon
     */
    public boolean isFinished() {
        if (mode instanceof RaceMode) {
            // En RaceMode, la partie s'arrête si le joueur a perdu (bot a atteint l'arrivée avant lui)
            return isLost() || mode.getMaze().isEnd(playerPosition);
        } else if (mode instanceof FuelMode) {
            // En FuelMode, la partie s'arrête si le joueur a perdu (pas assez de carburant) ou atteint la sortie
            return isLost() || mode.getMaze().isEnd(playerPosition);
        } else if (mode instanceof StormMode) {
            // En StormMode, la partie s'arrête si le joueur est rattrapé par la tempête ou atteint la sortie
            return isLost() || mode.getMaze().isEnd(playerPosition);
        } else {
            // Pour les autres modes, seulement vérifier si on atteint la sortie
            return mode.getMaze().isEnd(playerPosition);
        }
    }

    public boolean isLost() {
        if (mode instanceof FuelMode fuelMode) {
            return fuelMode.isLost();
        } else if (mode instanceof RaceMode raceMode) {
            return raceMode.isLost(this.getMoveCount());
        } else if (mode instanceof StormMode stormMode) {
            return stormMode.isLost(this.getPlayerPosition());
        }
        return false;
    }

    public void endGame() {
        if (this.mode instanceof ProgressionMode) {
            ProgressionMode pm = (ProgressionMode) mode;
            Profil player = pm.getPlayer();
            player.finishChallenge(pm.getCurrentLevelIndex(), pm.getCurrentChallengeIndex());
            Saves.updateScore(player.getName(), player.getScore() + 1);
            // Persist player progress (challenge completion) to CSV
            Saves.savePlayer(player);
        }
    }

    /**
     * Réinitialise la partie à son état initial.
     * Replace le joueur à l'entrée du labyrinthe et remet le compteur de déplacements à zéro.
     */
    public void reset() {
        this.moveCount = 0;
        this.exploredCells.clear();
        this.exploredCells.add(this.playerPosition); // Marquer la position de départ comme explorée
        this.joueurDirection = Direction.D;
        if (mode instanceof FuelMode) {
            ((FuelMode) mode).reset();
        }
        this.playerPosition = mode.getMaze().getStart();
        notifyObservers();
    }

    /**
     * Démarre la régénération automatique du labyrinthe pour MadMaxMode.
     * Le labyrinthe sera régénéré toutes les 15 secondes.
     */
    private void startMadMaxRegeneration() {
        if (madMaxRegenerationTimer != null) {
            madMaxRegenerationTimer.cancel();
        }
        madMaxRegenerationTimer = new Timer(true);
        madMaxRegenerationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mode instanceof MadMaxMode) {
                    ((MadMaxMode) mode).changeWalls();
                    notifyObservers();
                }
            }
        }, 5000, 5000); // 5 secondes
    }
    
    /**
     * Arrête la régénération automatique du labyrinthe MadMax.
     */
    public void stopMadMaxRegeneration() {
        if (madMaxRegenerationTimer != null) {
            madMaxRegenerationTimer.cancel();
            madMaxRegenerationTimer = null;
        }
    }

    /**
     * Démarre la propagation de la tempête dans un thread séparé.
     * Cette méthode ne bloque pas le thread principal.
     */
    private void startStormPropagation() {
        Thread stormThread = new Thread(() -> {
            while (!isFinished()) {
                try {
                    Thread.sleep(((StormMode) this.mode).getPropagationInterval());
                    ((StormMode) this.mode).propagateStorm();
                    notifyObservers();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        stormThread.setDaemon(true);
        stormThread.start();
    }

    @Override
    public void addObserver(Observer observer) {
        if (observer != null && !this.observers.contains(observer)) {
            this.observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        if (observer != null && this.observers.contains(observer)) {
            this.observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        for (Observer obs : this.observers) {
            obs.update(this);
        }
    }

    public Direction getJoueurDirection() {
        return this.joueurDirection;
    }
}
