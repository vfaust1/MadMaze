package main.java.fr.univlille.iut.sae302.madmaze.view;

import java.io.InputStream;
import java.util.Set;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.java.fr.univlille.iut.sae302.madmaze.model.Cell;
import main.java.fr.univlille.iut.sae302.madmaze.model.Direction;
import main.java.fr.univlille.iut.sae302.madmaze.model.FuelMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.Game;
import main.java.fr.univlille.iut.sae302.madmaze.model.Maze;
import main.java.fr.univlille.iut.sae302.madmaze.model.MultipleExitMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.RaceMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.StormMode;

/**
 * Classe utilitaire responsable du rendu graphique des labyrinthes et des éléments du jeu.
 * 
 * MazeRenderer offre une collection de méthodes statiques pour dessiner les différentes vues
 * du jeu sur un canvas JavaFX (GraphicsContext). Elle gère le rendu de :
 * - La structure du labyrinthe (murs, chemins, entrée, sortie)
 * - Les joueurs (joueur humain et bot en mode Course)
 * - Les éléments spécifiques (carburant en mode Carburant, vision en mode Nuit, fausses sorties, etc.)
 * - Les différentes vues (vue complète, vue locale/centrée, vue Race Mode, etc.)
 * 
 * Les images sont chargées une seule fois via un bloc statique et mises en cache pour optimiser
 * les performances. Les coordonnées sont automatiquement centrées sur le canvas.
 * 
 * @author G4
 * @see GraphicsContext
 * @see Game
 * @see Maze
 */
public class MazeRenderer {

    /** Image de la voiture du joueur */
    private static Image playerImage;
    /** Image de la voiture du bot (utilisée en mode Course) */
    private static Image botImage;
    /** Image du bidon de carburant */
    private static Image fuel;
    /** Image du brouillard/fog (utilisée en mode Nuit) */
    private static Image fog;

    /**
     * Charge une image depuis les ressources.
     * Ferme automatiquement le flux d'entrée après le chargement.
     * 
     * @param path le chemin de l'image dans les ressources (ex: "/images/Voiture.png")
     * @return l'image chargée, ou null si le chargement a échoué
     */
    private static Image loadImage(String path) {
        InputStream is = MazeRenderer.class.getResourceAsStream(path);
        if (is == null) {
            System.err.println("Warning: Could not load image: " + path);
            return null;
        }
        try {
            return new Image(is);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                // Ignore close errors
            }
        }
    }

    static {
        playerImage = loadImage("/images/Voiture.png");
        botImage = loadImage("/images/VoitureBot.PNG");
        fuel = loadImage("/images/bidon.png");
        fog = loadImage("/images/NightModecase.JPG");
    }


    /**
     * Dessine la structure complète du labyrinthe sur le canvas (sans le joueur).
     * 
     * @param gc le contexte graphique JavaFX pour dessiner
     * @param game l'instance du jeu contenant le modèle du labyrinthe
     * @param cellSize la taille en pixels de chaque cellule du labyrinthe
     * @param showWalls true pour afficher les murs, false sinon
     * @param showStart true pour afficher l'entrée, false sinon
     * @param showEnd true pour afficher la sortie, false sinon
     */
    public static void drawMaze(GraphicsContext gc, Game game, int cellSize, boolean showWalls, boolean showStart, boolean showEnd) {
        Maze maze = game.getMaze();
        int h = maze.getHeight();
        int w = maze.getWidth();

        int mazeWidthPixels = w * cellSize; 
        int mazeHeightPixels = h * cellSize;     

        double offsetX = (gc.getCanvas().getWidth() - mazeWidthPixels) / 2.0;
        double offsetY = (gc.getCanvas().getHeight() - mazeHeightPixels) / 2.0;

        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                //Cell cell = maze.getCell(y, x);
                gc.setFill(cellColor(game, y, x, showWalls, showStart, showEnd));
                gc.fillRect(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);

                // Affiche le bidon seulement s'il n'a pas été ramassé
                if (game.getMode() instanceof FuelMode) {
                    FuelMode fuelMode = (FuelMode) game.getMode();
                    Cell currentCell = game.getMode().getMaze().getCell(y, x);
                    if (fuelMode.hasFuelAt(currentCell)) {
                        gc.drawImage(fuel, offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                    }
                }

                if (game.getMode().getMaze() instanceof RandomMaze) {
                    gc.setStroke(Color.LIGHTGRAY);
                    gc.strokeRect(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                }
            }
        }
    }
    /**
     * Surcharge de drawMaze avec les paramètres par défaut (affiche tous les éléments).
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawMaze(GraphicsContext gc, Game game, int cellSize) {
        drawMaze(gc, game, cellSize, true, true, true);
    }

    /**
     * Retourne la couleur d'une cellule selon son type.
     * 
     * @param game l'instance du jeu
     * @param y la coordonnée row de la cellule
     * @param x la coordonnée col de la cellule
     * @return la couleur appropriée pour cette cellule
     */
    private static Color cellColor(Game game, int y, int x) {
        Maze maze = game.getMaze();
        Cell cell = maze.getCell(y, x);

        if (cell.isWall()) return Color.rgb(186, 102, 85);
        else if (maze.getStart().equals(cell)) return Color.LIGHTGREEN;
        else if (maze.getEnd().equals(cell)) return Color.GOLD;
        
        return Color.web("#FDFCEE");
    }


    /**
     * Variante de cellColor avec filtres booléens pour contrôler l'affichage.
     * 
     * @param game l'instance du jeu
     * @param y la coordonnée row
     * @param x la coordonnée col
     * @param isWall true pour afficher les murs
     * @param isStart true pour afficher l'entrée
     * @param isEnd true pour afficher la sortie
     * @return la couleur appropriée pour cette cellule
     */
    private static Color cellColor(Game game, int y, int x, boolean isWall, boolean isStart, boolean isEnd) {
        Maze maze = game.getMaze();
        Cell cell = maze.getCell(y, x);

        if (isWall && cell.isWall()) return Color.rgb(186, 102, 85);
        else if (isStart && maze.getStart().equals(cell)) return Color.LIGHTGREEN;
        else if (isEnd && maze.getEnd().equals(cell)) return Color.GOLD;   

        return Color.web("#FDFCEE");
    }

    /**
     * Dessine la vue classique du jeu : labyrinthe entier avec le joueur au centre.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawSimpleGame(GraphicsContext gc, Game game, int cellSize) {

        Maze maze = game.getMaze();

        int w = maze.getWidth();
        int h = maze.getHeight();   

        int mazeWidthPixels = w * cellSize;
        int mazeHeightPixels = h * cellSize;
        double offsetX = (gc.getCanvas().getWidth() - mazeWidthPixels) / 2.0;
        double offsetY = (gc.getCanvas().getHeight() - mazeHeightPixels) / 2.0;

        drawMaze(gc, game, cellSize);
        
        int px = game.getPositionJoueur().getCol();
        int py = game.getPositionJoueur().getRow();

        Direction dir = game.getJoueurDirection();
        drawPlayerRotated(gc, playerImage, offsetX + px * cellSize, offsetY + py * cellSize, cellSize * 0.9, cellSize * 0.9, dir);
        
        // Spécificité pour PerfectMaze : dessiner les murs internes
        if (maze instanceof PerfectMaze) {
            drawPerfectMazeWalls(gc, (PerfectMaze) maze, cellSize, offsetX, offsetY);
        }
    }

    /**
     * Dessine la vue du mode course : labyrinthe avec le joueur et le bot concurrent.
     * Affiche le chemin du bot en pointillés et la direction du joueur.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawRaceModeGame(GraphicsContext gc, Game game, int cellSize) {

        Maze maze = game.getMaze();

        int w = maze.getWidth();
        int h = maze.getHeight();   

        int mazeWidthPixels = w * cellSize;
        int mazeHeightPixels = h * cellSize;
        double offsetX = (gc.getCanvas().getWidth() - mazeWidthPixels) / 2.0;
        double offsetY = (gc.getCanvas().getHeight() - mazeHeightPixels) / 2.0;

        drawMaze(gc, game, cellSize);
        
        // Spécificité pour PerfectMaze : dessiner les murs internes
        if (maze instanceof PerfectMaze) {
            drawPerfectMazeWalls(gc, (PerfectMaze) maze, cellSize, offsetX, offsetY);
        }

        /* */

        java.util.List<Cell> botPath = null;
        if (game.getMode() instanceof RaceMode) {
            botPath = ((RaceMode) game.getMode()).getBotPerfectPath();
        }

        if (botPath != null && game.getMoveCount() < botPath.size()) {
            Cell botCell = botPath.get(game.getMoveCount());
            int fx = botCell.getCol();
            int fy = botCell.getRow();
            Direction dir = game.getJoueurDirection();
            if(game.getMoveCount()!=0) {
                Cell previousCell = botPath.get(game.getMoveCount()-1);
                if(previousCell.getCol()==botCell.getCol()-1) {
                    dir = Direction.D;
                } else if(previousCell.getCol()==botCell.getCol()+1) {
                    dir = Direction.Q;
                } else if(previousCell.getRow()==botCell.getRow()-1) {
                    dir = Direction.S;
                } else if(previousCell.getRow()==botCell.getRow()+1) {
                    dir = Direction.Z;
                }
            }
            drawPlayerRotated(gc, botImage, offsetX + fx * cellSize, offsetY + fy * cellSize, cellSize * 0.9, cellSize * 0.9, dir);
        }

        int px = game.getPositionJoueur().getCol();
        int py = game.getPositionJoueur().getRow();

        Direction dir = game.getJoueurDirection();
        drawPlayerRotated(gc, playerImage, offsetX + px * cellSize, offsetY + py * cellSize, cellSize * 0.9, cellSize * 0.9, dir);
    }

    /**
     * Dessine les murs internes d'un labyrinthe parfait en tant que lignes.
     * Utilise la structure du labyrinthe pour identifier et afficher les murs internes.
     * 
     * @param gc le contexte graphique JavaFX
     * @param maze le labyrinthe parfait contenant les données des murs
     * @param cellSize la taille en pixels de chaque cellule
     * @param offsetX le décalage horizontal du labyrinthe sur le canvas
     * @param offsetY le décalage vertical du labyrinthe sur le canvas
     */
    private static void drawPerfectMazeWalls(GraphicsContext gc, PerfectMaze maze, int cellSize, double offsetX, double offsetY) {
        int h = maze.getHeight();
        int w = maze.getWidth();

        // Murs internes horizontaux et verticaux
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                // Mur du bas si ce n'est pas la dernière ligne
                if (row < h - 1 && maze.isWall(row, col, row + 1, col)) {
                    drawWall(gc, col, row + 1, col + 1, row + 1, cellSize, offsetX, offsetY);
                }
                // Mur de droite si ce n'est pas la dernière
                if (col < w - 1 && maze.isWall(row, col, row, col + 1)) {
                    drawWall(gc, col + 1, row, col + 1, row + 1, cellSize, offsetX, offsetY);
                }
            }
        }
        // Bordures externes :
        drawWall(gc, 0, 0, w, 0, cellSize, offsetX, offsetY);          // Haut
        drawWall(gc, 0, h, w, h, cellSize, offsetX, offsetY);          // Bas
        drawWall(gc, 0, 0, 0, h, cellSize, offsetX, offsetY);          // Gauche
        drawWall(gc, w, 0, w, h, cellSize, offsetX, offsetY);          // Droite
    }

    /**
     * Trace une ligne pour dessiner un mur entre deux points du labyrinthe.
     * Utilise une couleur marron et une épaisseur de 2 pixels.
     * 
     * @param gc le contexte graphique JavaFX
     * @param col1 la colonne du premier point
     * @param row1 la ligne du premier point
     * @param col2 la colonne du deuxième point
     * @param row2 la ligne du deuxième point
     * @param cellSize la taille en pixels de chaque cellule
     * @param offsetX le décalage horizontal du labyrinthe sur le canvas
     * @param offsetY le décalage vertical du labyrinthe sur le canvas
     */
    private static void drawWall(GraphicsContext gc, int col1, int row1, int col2, int row2, int cellSize, double offsetX, double offsetY) {
        gc.setStroke(Color.rgb(186, 102, 85));
        gc.setLineWidth(2);
        gc.strokeLine(
            offsetX + col1 * cellSize, offsetY + row1 * cellSize,
            offsetX + col2 * cellSize, offsetY + row2 * cellSize
        );
    }

    /**
     * Dessine la vue globale du labyrinthe sans l'indicateur de départ.
     * Affiche le labyrinthe complet centré avec la sortie visible.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawGlobalView(GraphicsContext gc, Game game, int cellSize) {
        drawMaze(gc, game, cellSize, true, false, true);
    }

    /**
     * Dessine la vue locale centrée sur le joueur avec un rayon de vision limité.
     * Affiche une portion du labyrinthe autour du joueur, créant un effet de brouillard de guerre.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     * @param visionRadius le rayon de vision en nombre de cellules autour du joueur
     */
    public static void drawLocalView(GraphicsContext gc, Game game, int cellSize, int visionRadius) {
        Maze maze = game.getMaze();

        int px = game.getPlayerPosition().getCol();
        int py = game.getPlayerPosition().getRow();

        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        int offset = visionRadius;

        double canvasW = gc.getCanvas().getWidth();
        double canvasH = gc.getCanvas().getHeight();
        int gridCells = 2 * offset + 1;
        double gridW = gridCells * cellSize;
        double gridH = gridCells * cellSize;

        double baseX = (canvasW - gridW) / 2.0;
        double baseY = (canvasH - gridH) / 2.0;
        for (int dx = -offset; dx <= offset; dx++) {
            for (int dy = -offset; dy <= offset; dy++) {
                int x = px + dx;
                int y = py + dy;
                double pxDraw = baseX + (dx + offset) * cellSize;
                double pyDraw = baseY + (dy + offset) * cellSize;
                if (x >= 0 && x < maze.getWidth() && y >= 0 && y < maze.getHeight()) {
                    gc.setFill(cellColor(game, y, x));
                    
                    gc.fillRect(pxDraw, pyDraw, cellSize, cellSize);

                    if (game.getMode().getMaze() instanceof RandomMaze) {
                        gc.setStroke(Color.LIGHTGRAY);
                        gc.strokeRect(pxDraw, pyDraw, cellSize, cellSize);
                    }

                    if (dx == 0 && dy == 0) {
                        Direction dir = game.getJoueurDirection();
                        drawPlayerRotated(gc, playerImage, pxDraw, pyDraw, cellSize * 0.9, cellSize * 0.9, dir);
                    }
                } else {
                    gc.setFill(Color.rgb(186, 102, 85));
                    gc.fillRect(pxDraw, pyDraw, cellSize, cellSize);
                }
            }
        }
        if (maze instanceof PerfectMaze) {
        drawLocalPerfectMazeWalls(
            gc, (PerfectMaze) maze, cellSize, px, py, visionRadius, baseX, baseY
        );
}

    }

    private static void drawLocalPerfectMazeWalls(
        GraphicsContext gc, PerfectMaze maze, int cellSize, int px, int py, int visionRadius, double baseX, double baseY
    ) {
        int w = maze.getWidth();
        int h = maze.getHeight();

        gc.setStroke(Color.rgb(186, 102, 85));
        gc.setLineWidth(3); // adapte la largeur à ton style

        int offset = visionRadius;

        // Pour chaque cellule affichée dans la vue locale
        for (int dx = -offset; dx <= offset; dx++) {
            for (int dy = -offset; dy <= offset; dy++) {
                int x = px + dx;
                int y = py + dy;

                // Cellule dans le labyrinthe
                if (x >= 0 && x < w && y >= 0 && y < h) {
                    double pxDraw = baseX + (dx + offset) * cellSize;
                    double pyDraw = baseY + (dy + offset) * cellSize;

                    // MUR BAS (vers la cellule en dessous)
                    if (y < h - 1 && maze.isWall(y, x, y + 1, x) && Math.abs(dy + 1) <= offset) {
                        double yStart = pyDraw + cellSize;
                        gc.strokeLine(pxDraw, yStart, pxDraw + cellSize, yStart);
                    }
                    // MUR DROITE (vers la cellule à droite)
                    if (x < w - 1 && maze.isWall(y, x, y, x + 1) && Math.abs(dx + 1) <= offset) {
                        double xStart = pxDraw + cellSize;
                        gc.strokeLine(xStart, pyDraw, xStart, pyDraw + cellSize);
                    }
                }
            }
        }
    }


    /**
     * Dessine une vue hybride combinant la vue globale et une zone locale explorée.
     * Affiche le labyrinthe complet avec une fenêtre de vision centrée sur le joueur.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     * @param rayon le rayon de la zone de vision locale en nombre de cellules
     */
    public static void drawHybridView(GraphicsContext gc, Game game, int cellSize, int rayon) {
        Maze maze = game.getMaze();

        int w = maze.getWidth();
        int h = maze.getHeight();

        int mazeWidthPixels = w * cellSize;
        int mazeHeightPixels = h * cellSize;
        double offsetX = (gc.getCanvas().getWidth() - mazeWidthPixels) / 2.0;
        double offsetY = (gc.getCanvas().getHeight() - mazeHeightPixels) / 2.0;
        
        drawLabyrinthBaseHybrid(gc, game, cellSize, rayon, offsetX, offsetY);

        int px = game.getPlayerPosition().getCol();
        int py = game.getPlayerPosition().getRow();

        if (maze instanceof PerfectMaze) {
            drawPerfectMazeWallsRadius(gc, (PerfectMaze) maze, cellSize, px, py, rayon, offsetX, offsetY);
        }

        double psize = Math.max(6, Math.round(cellSize * 0.8));
        double offsetP = (cellSize - psize) / 2.0;
        Direction dir = game.getJoueurDirection();
        drawPlayerRotated(gc, playerImage, offsetX + px * cellSize + offsetP, offsetY + py * cellSize + offsetP, psize, psize, dir);
    }

    private static void drawLabyrinthBaseHybrid(GraphicsContext gc, Game game, int cellSize, int rayon, double offsetX, double offsetY) {
        Maze maze = game.getMaze();
        int w = maze.getWidth();
        int h = maze.getHeight();
        int px = game.getPlayerPosition().getCol();
        int py = game.getPlayerPosition().getRow();

        // Effacer le canvas
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Remplir uniquement la zone du labyrinthe avec la couleur de mur
        gc.setFill(Color.rgb(186, 102, 85));
        gc.fillRect(offsetX, offsetY, w * cellSize, h * cellSize);

        // Dessiner uniquement les cellules valides dans le rayon de vision
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // Vérifier que la cellule est dans le rayon ET dans les limites du labyrinthe
                if (Math.abs(x - px) <= rayon && Math.abs(y - py) <= rayon) {
                    gc.setFill(cellColor(game, y, x));
                    gc.fillRect(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                    
                    // Ajouter les bordures pour RandomMaze
                    if (game.getMode().getMaze() instanceof RandomMaze) {
                        gc.setStroke(Color.LIGHTGRAY);
                        gc.strokeRect(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                    }
                }
            }
        }
    }

    private static void drawPerfectMazeWallsRadius(GraphicsContext gc, PerfectMaze maze, int cellSize, int px, int py, int rayon, double offsetX, double offsetY) {
        int w = maze.getWidth();
        int h = maze.getHeight();

        gc.setStroke(Color.rgb(186, 102, 85));
        gc.setLineWidth(2);

        // Calculer les limites de la zone visible
        int minRow = Math.max(0, py - rayon);
        int maxRow = Math.min(h - 1, py + rayon);
        int minCol = Math.max(0, px - rayon);
        int maxCol = Math.min(w - 1, px + rayon);

        // Murs horizontaux (entre row et row+1)
        for (int row = minRow; row < maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                if (maze.isWall(row, col, row + 1, col)) {
                    gc.strokeLine(
                        offsetX + col * cellSize, 
                        offsetY + (row + 1) * cellSize,
                        offsetX + (col + 1) * cellSize, 
                        offsetY + (row + 1) * cellSize);
                }
            }
        }
        
        // Murs verticaux (entre col et col+1)
        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col < maxCol; col++) {
                if (maze.isWall(row, col, row, col + 1)) {
                    gc.strokeLine(
                        offsetX + (col + 1) * cellSize, 
                        offsetY + row * cellSize,
                        offsetX + (col + 1) * cellSize, 
                        offsetY + (row + 1) * cellSize);
                }
            }
        }
    }

    /**
     * Dessine l'image du joueur/bot avec rotation basée sur la direction.
     * Effectue une rotation autour du centre de la cellule. En cas d'image manquante,
     * affiche un cercle bleu comme symbole de secours.
     * 
     * @param gc le contexte graphique JavaFX
     * @param img l'image du joueur ou du bot (peut être null)
     * @param x la coordonnée x du coin supérieur gauche
     * @param y la coordonnée y du coin supérieur gauche
     * @param w la largeur de l'image en pixels
     * @param h la hauteur de l'image en pixels
     * @param dir la direction du joueur (Z=0°, D=90°, S=180°, Q=270°)
     */
    public static void drawPlayerRotated(GraphicsContext gc, Image img, double x, double y, double w, double h, Direction dir) {
        if (img == null) {
            // Fallback: dessiner un cercle bleu si l'image n'est pas chargée
            gc.setFill(Color.BLUE);
            gc.fillOval(x, y, w, h);
            return;
        }
        gc.save();
        // Centre de rotation = centre de la cellule
        double cx = x + w/2;
        double cy = y + h/2;
        gc.translate(cx, cy);
        double angle = 0;
        switch (dir) {
            case Z: angle = 0; break;
            case D: angle = 90; break;
            case S: angle = 180; break;
            case Q: angle = 270; break;
        }
        gc.rotate(angle);
        gc.drawImage(img, -w/2, -h/2, w, h);
        gc.restore();
    }

    /**
     * Dessine la vue progressive du mode progression : affiche uniquement les cellules explorées.
     * Les cellules non visitées restent invisibles, créant un brouillard de guerre progressif.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawProgressiveView(GraphicsContext gc, Game game, int cellSize) {
        Maze maze = game.getMaze();
        int w = maze.getWidth(), h = maze.getHeight();

        // Fond gris partout
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        int mazeWidthPixels = w * cellSize;
        int mazeHeightPixels = h * cellSize;
        double offsetX = (gc.getCanvas().getWidth() - mazeWidthPixels) / 2.0;
        double offsetY = (gc.getCanvas().getHeight() - mazeHeightPixels) / 2.0;

        // Affiche cellules explorées avec ta cellColor généralisée
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Cell cell = maze.getCell(y, x);
                if (game.isExplored(cell)) {
                    // Dessine types explorés, hors joueur (image à part)
                    gc.setFill(cellColor(game, y, x));
                    gc.fillRect(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                }
            }
        }

        // Murs internes pour PerfectMaze (affichés pour cases explorées)
        if (maze instanceof PerfectMaze) {
            PerfectMaze pm = (PerfectMaze) maze;
            gc.setStroke(Color.rgb(186, 102, 85));
            gc.setLineWidth(2);
            for (int row = 0; row < h - 1; row++) {
                for (int col = 0; col < w; col++) {
                    Cell c1 = pm.getGrid()[row][col];
                    Cell c2 = pm.getGrid()[row+1][col];
                    if ((game.isExplored(c1) || game.isExplored(c2)) && pm.isWall(row, col, row+1, col)) {
                        gc.strokeLine(
                            offsetX + col * cellSize + 0.5, 
                            offsetY + (row+1) * cellSize + 0.5,
                            offsetX + (col+1) * cellSize - 0.5, 
                            offsetY + (row+1) * cellSize + 0.5);
                    }
                }
            }
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w-1; col++) {
                    Cell c1 = pm.getGrid()[row][col];
                    Cell c2 = pm.getGrid()[row][col+1];
                    if ((game.isExplored(c1) || game.isExplored(c2)) && pm.isWall(row, col, row, col+1)) {
                        gc.strokeLine(
                            offsetX + (col+1) * cellSize + 0.5, 
                            offsetY + row * cellSize + 0.5,
                            offsetX + (col+1) * cellSize + 0.5, 
                            offsetY + (row+1) * cellSize - 0.5);
                    }
                }
            }
        }

        // Dessin du joueur, seulement si la cellule est explorée
        int px = game.getPlayerPosition().getCol();
        int py = game.getPlayerPosition().getRow();
        if (game.isExplored(maze.getCell(py, px))) {
            Direction dir = game.getJoueurDirection(); 
            drawPlayerRotated(gc, playerImage, offsetX + px * cellSize, offsetY + py * cellSize, cellSize * 0.9, cellSize * 0.9, dir);
        }
    }

    /**
     * Dessine la vue du mode nuit avec vision limitée autour du joueur.
     * Crée un effet de lampe torche avec une zone visible et le reste en noir.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawNightView(GraphicsContext gc, Game game, int cellSize) {
        Maze maze = game.getMaze();
        int w = maze.getWidth();
        int h = maze.getHeight();
        int px = game.getPositionJoueur().getCol();
        int py = game.getPositionJoueur().getRow();

        int mazeWidthPixels = w * cellSize;
        int mazeHeightPixels = h * cellSize;    
        double offsetX = (gc.getCanvas().getWidth() - mazeWidthPixels) / 2.0;
        double offsetY = (gc.getCanvas().getHeight() - mazeHeightPixels) / 2.0; 

        // Fill background with fog texture per cell instead of a solid color
        if (fog != null) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    gc.drawImage(fog, offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                }
            }
        } else {
            gc.setFill(Color.rgb(28, 28, 41));
            gc.fillRect(offsetX, offsetY, w * cellSize, h * cellSize);
        }

        if (game.getPositionJoueur().equals(maze.getStart())) {
            // Joueur sur la case de départ : affichage classique
            drawSimpleGame(gc, game, cellSize);
        } else {
            // Joueur a bougé : afficher case entrée, case sortie et joueur
            int startX = maze.getStart().getCol();
            int startY = maze.getStart().getRow();
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(offsetX + startX * cellSize, offsetY + startY * cellSize, cellSize, cellSize);
            
            int endX = maze.getEnd().getCol();
            int endY = maze.getEnd().getRow();
            gc.setFill(Color.GOLD);
            gc.fillRect(offsetX + endX * cellSize, offsetY + endY * cellSize, cellSize, cellSize);
                        
            Direction dir = game.getJoueurDirection(); 
            drawPlayerRotated(gc, playerImage, offsetX + px * cellSize, offsetY + py * cellSize, cellSize * 0.9, cellSize * 0.9, dir);
        }
    }

    /**
     * Dessine la vue du mode carburant : affiche le labyrinthe complet avec le joueur.
     * Utilise la vue simple classique pour ce mode.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawFuelView(GraphicsContext gc, Game game, int cellSize) {
        drawSimpleGame(gc, game, cellSize);
    }

    /**
     * Dessine la vue du mode sorties multiples avec affichage des fausses sorties.
     * Affiche le labyrinthe et colore les sorties factices en différence de la vraie sortie.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawMultipleExitView(GraphicsContext gc, Game game, int cellSize) {
        Maze maze = game.getMaze();
        int w = maze.getWidth();
        int h = maze.getHeight();
        int mazeWidthPixels = w * cellSize;
        int mazeHeightPixels = h * cellSize;   
        double offsetX = (gc.getCanvas().getWidth() - mazeWidthPixels) / 2.0;
        double offsetY = (gc.getCanvas().getHeight() - mazeHeightPixels) / 2.0;
        drawSimpleGame(gc, game, cellSize);
        Cell[] falseExit = ((MultipleExitMode) game.getMode()).getFalseExit();
        for(Cell cell : falseExit) {
            if(!cell.equals(game.getPositionJoueur())) {
                int fx = cell.getCol();
                int fy = cell.getRow();
                gc.setFill(Color.GOLD);
                gc.fillRect(offsetX + fx * cellSize, offsetY + fy * cellSize, cellSize, cellSize);
            }
        }
    }

    /**
     * Dessine la vue du mode course en appelant la méthode spécialisée pour le mode course.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawRaceView(GraphicsContext gc, Game game, int cellSize) {
        drawRaceModeGame(gc, game, cellSize);
    }

    /**
     * Dessine la vue du mode tempête avec affichage des obstacles dynamiques.
     * Utilise la vue simple classique pour ce mode.
     * 
     * @param gc le contexte graphique JavaFX
     * @param game l'instance du jeu
     * @param cellSize la taille en pixels de chaque cellule
     */
    public static void drawStormView(GraphicsContext gc, Game game, int cellSize) {
        drawSimpleGame(gc, game, cellSize);

        Maze maze = game.getMaze();
        int w = maze.getWidth();
        int h = maze.getHeight();
        int mazeWidthPixels = w * cellSize;
        int mazeHeightPixels = h * cellSize;   
        double offsetX = (gc.getCanvas().getWidth() - mazeWidthPixels) / 2.0;
        double offsetY = (gc.getCanvas().getHeight() - mazeHeightPixels) / 2.0;

        // Afficher les cellules SAND en rouge
        if (game.getMode() instanceof StormMode) {
            StormMode stormMode = (StormMode) game.getMode();
            Set<Cell> sandCells = stormMode.getSandCells();
            
            gc.setFill(Color.RED);
            for (Cell sandCell : sandCells) {
                int fx = sandCell.getCol();
                int fy = sandCell.getRow();
                gc.fillRect(offsetX + fx * cellSize, offsetY + fy * cellSize, cellSize, cellSize);
            }
        }
    }

}