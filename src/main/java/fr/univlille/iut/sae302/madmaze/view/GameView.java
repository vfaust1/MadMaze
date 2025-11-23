package main.java.fr.univlille.iut.sae302.madmaze.view;

import java.util.Arrays;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.GameController;
import main.java.fr.univlille.iut.sae302.madmaze.model.Difficulty;
import main.java.fr.univlille.iut.sae302.madmaze.model.FreeMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.FuelMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.Game;
import main.java.fr.univlille.iut.sae302.madmaze.model.MadMaxMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.MultipleExitMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.NightMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.Observable;
import main.java.fr.univlille.iut.sae302.madmaze.model.ProgressionMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.RaceMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.StormMode;

/**
 * Vue de jeu affichant le labyrinthe et les contrôles de navigation.
 * 
 * GameView est responsable du rendu graphique pendant le jeu et de la mise à jour dynamique
 * de l'interface en fonction du mode de jeu actif (Progression, Libre, Essence, Nuit, etc.).
 * Elle gère :
 * - Le rendu du labyrinthe sur le canvas principal avec vue adaptée au mode
 * - L'affichage d'un deuxième canvas pour les vues secondaires (zoom local)
 * - Les boutons de contrôle directionnels (↑↓←→) et les boutons d'action
 * - La mise à jour des labels HUD (mouvements, étapes, carburant)
 * - Le chargement des images de fond spécifiques à chaque mode
 * 
 * La vue observe le modèle Game et se met à jour automatiquement quand l'état change.
 * 
 * @author G4
 * @see GameController
 * @see MazeRenderer
 * @see Game
 */
public class GameView implements Observer{

    /** Label affichant le nombre de mouvements ou les informations de progression */
    private Label movesLabel;
    /** Label affichant la taille du labyrinthe */
    private Label sizeLabel;

    /** Bouton directionnel vers le haut */
    private Button upBtn;
    /** Bouton directionnel vers le bas */
    private Button downBtn;
    /** Bouton directionnel vers la gauche */
    private Button leftBtn;
    /** Bouton directionnel vers la droite */
    private Button rightBtn;
    /** Bouton pour quitter la partie */
    private Button abandonBtn;
    /** Bouton pour augmenter le zoom */
    private Button zoomIn;
    /** Bouton pour diminuer le zoom */
    private Button zoomOut;
    /** Bouton pour recommencer la partie */
    private Button resetBtn;

    /** La fenêtre principale de l'application */
    private final Stage stage;
    /** La scène affichée */
    private Scene scene;
    /** Le panneau racine de la disposition */
    private BorderPane borderPane;
    /** Le canvas principal pour afficher le labyrinthe */
    private Canvas mainCanvas;
    /** Le canvas secondaire pour les vues additionnelles (zoom local) */
    private Canvas secondCanvas;
    /** L'image de fond spécifique au mode de jeu */
    private ImageView background;
    /** Le modèle du jeu */
    private final Game model;
    /** Le contrôleur gérant les interactions utilisateur */
    private final GameController controller;
    /** La taille des cellules en pixels */
    private int cellSize;


    /**
     * Constructeur de GameView.
     * Initialise tous les éléments visuels (canvases, boutons, labels) et configure le contrôleur.
     * 
     * @param stage la fenêtre principale de l'application
     * @param game le modèle du jeu
     */
    public GameView (Stage stage, Game game) {
        this.stage = stage;
        this.model = game;
        model.addObserver(this);
        this.mainCanvas = new Canvas(800, 800);
        int nbColonnes = model.getMaze().getWidth();
        int nbLignes = model.getMaze().getHeight();
        cellSize = (int) Math.min(mainCanvas.getWidth() / nbColonnes, mainCanvas.getHeight() / nbLignes);

        this.secondCanvas = new Canvas(200, 200);

        upBtn = new Button("↑");
        downBtn = new Button("↓");
        leftBtn = new Button("←");
        rightBtn = new Button("→");
        abandonBtn = new Button("Quitter");
        zoomIn = new Button("+");
        zoomOut = new Button("-");
        resetBtn = new Button("Recommencer");

        List<Button> btns = Arrays.asList(upBtn, downBtn, leftBtn, rightBtn, abandonBtn, zoomIn, zoomOut, resetBtn);
        btns.forEach(btn -> btn.setFocusTraversable(false));
        btns.forEach(btn -> btn.setMinSize(50, 30));
        btns.forEach(btn -> btn.setFont(Font.font(18)));

        finalMazeView();
        setBackgroundImage();

        show();

        this.controller = new GameController(game, this);
        controller.setupListeners();
        
    }

    /**
     * Construit et affiche l'interface de jeu sur le stage.
     * Crée la disposition avec le canvas principal, les boutons de contrôle,
     * et le canvas secondaire pour les vues additionnelles.
     */
    public void show() {

        movesLabel = new Label("Mouvements : 0");
        sizeLabel = new Label("Taille : " + this.model.getMaze().getWidth() + "x" + this.model.getMaze().getHeight());
        movesLabel.setFont(Font.font(16));
        sizeLabel.setFont(Font.font(16));
        movesLabel.setStyle("-fx-text-fill: white;");
        sizeLabel.setStyle("-fx-text-fill: white;");

        // VBox du haut : empile les labels verticalement
        VBox topBox = new VBox(4);

        topBox.getChildren().addAll(movesLabel, sizeLabel);

        topBox.setAlignment(Pos.TOP_CENTER);
        
        // Vbox de droite
        VBox rightBox = new VBox(10);

        // Canvas secondaire (vue locale)
        rightBox.getChildren().add(topBox);
        topBox.setAlignment(Pos.TOP_CENTER);
        // Petit écart entre le haut du rightBox et le topBox
        VBox.setMargin(topBox, new Insets(20, 0, 0, 0));

        // Add flexible spacers above and below the canvas to vertically center it
        Region spacerTop = new Region();
        Region spacerBottom = new Region();
        VBox.setVgrow(spacerTop, Priority.ALWAYS);
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);

        // Use a StackPane that fills the available width so the child canvas is centered
        StackPane secondCanvasWrapper = new StackPane(secondCanvas);
        secondCanvasWrapper.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(secondCanvas, Pos.CENTER);
        // ensure children can fill the width of the VBox
        rightBox.setFillWidth(true);
        rightBox.getChildren().addAll(spacerTop, secondCanvasWrapper, spacerBottom);

        HBox upBtnBox = new HBox(10, upBtn);
        HBox leftRightBtnBox = new HBox(40, leftBtn, rightBtn);
        HBox downBtnBox = new HBox(10, downBtn);
        HBox zoomBox = new HBox(10, zoomIn, zoomOut);
        HBox btnBox = new HBox(10, abandonBtn, resetBtn);

        upBtnBox.setAlignment(Pos.CENTER);
        leftRightBtnBox.setAlignment(Pos.CENTER);
        downBtnBox.setAlignment(Pos.CENTER);
        zoomBox.setAlignment(Pos.CENTER);
        btnBox.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(10, upBtnBox, leftRightBtnBox, downBtnBox, zoomBox, btnBox);

        StackPane centerPane = new StackPane(mainCanvas);

        mainCanvas.widthProperty().bind(centerPane.widthProperty());
        mainCanvas.heightProperty().bind(centerPane.heightProperty());

        mainCanvas.widthProperty().addListener((obs, oldVal, newVal) -> finalMazeView());
        mainCanvas.heightProperty().addListener((obs, oldVal, newVal) -> finalMazeView());

        centerPane.setAlignment(Pos.CENTER);

        rightBox.getChildren().add(buttonBox);
        rightBox.setAlignment(Pos.TOP_CENTER);
        rightBox.setPadding(new Insets(40,15,30,0));

        borderPane = new BorderPane();
        borderPane.setCenter(centerPane);
        borderPane.setRight(rightBox);
        rightBox.setPrefWidth(300);

        
        background.setPreserveRatio(false);
        background.setFitWidth(stage.getWidth());
        background.setFitHeight(stage.getHeight());
        background.setOpacity(0.75);

        StackPane root = new StackPane();
        root.getChildren().addAll(background, borderPane);

        scene = new Scene(root, 1280, 800);

        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        stage.setScene(scene);
        stage.setTitle("MadMaze");
        stage.show();

    }

    /*public void flashCell(GraphicsContext gc, int x, int y, int cellSize, Game game, MazeRenderer renderer) {
        gc.setFill(Color.CRIMSON);
        gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

        PauseTransition pt = new PauseTransition(Duration.millis(250));

        pt.setOnFinished(ev -> {
            gc.setFill(renderer.cellColor(game, y, x, true, true, true));
            gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            gc.setStroke(Color.LIGHTGRAY);
            gc.strokeRect(x * cellSize, y * cellSize, cellSize, cellSize);
        });
        pt.play();
    }*/

    /**
     * Rend le labyrinthe selon le mode de jeu actif.
     * Sélectionne automatiquement la vue appropriée (Simple, Global, Local, Race, Nuit, etc.)
     * en fonction du type de mode de jeu et appelle la méthode de rendu correspondante.
     */
    public void finalMazeView(){
        int nbColonnes = model.getMaze().getWidth();
        int nbLignes = model.getMaze().getHeight();
        cellSize = (int) Math.min(mainCanvas.getWidth() / nbColonnes, mainCanvas.getHeight() / nbLignes);
        if (model.getMode() instanceof ProgressionMode) {
            viewByLevel(this.getLevel(), this.getChallengeDifficulty());
        } else if (model.getMode() instanceof FreeMode) {
            MazeRenderer.drawSimpleGame(mainCanvas.getGraphicsContext2D(), model, cellSize);
            secondCanvas.setVisible(false);
        } else if (model.getMode() instanceof FuelMode) {
            MazeRenderer.drawFuelView(mainCanvas.getGraphicsContext2D(), model, cellSize);
            secondCanvas.setVisible(false);
        }  else if (model.getMode() instanceof NightMode) {
            MazeRenderer.drawNightView(mainCanvas.getGraphicsContext2D(), model, cellSize);
            secondCanvas.setVisible(false);
        }  else if (model.getMode() instanceof MultipleExitMode) {
            MazeRenderer.drawMultipleExitView(mainCanvas.getGraphicsContext2D(), model, cellSize);
            secondCanvas.setVisible(false);
        } else if (model.getMode() instanceof RaceMode) {
            MazeRenderer.drawRaceView(mainCanvas.getGraphicsContext2D(), model, cellSize);
            secondCanvas.setVisible(false);
        } else if (model.getMode() instanceof StormMode) {
            MazeRenderer.drawStormView(mainCanvas.getGraphicsContext2D(), model, cellSize);
            secondCanvas.setVisible(false);
        } else if (model.getMode() instanceof MadMaxMode) {
            MazeRenderer.drawSimpleGame(mainCanvas.getGraphicsContext2D(), model, cellSize);
            secondCanvas.setVisible(false);
        }
    }

    /**
     * Rend la vue spécifique au niveau de progression actuel.
     * Adapte l'affichage selon le niveau (0-5) et la difficulté (facile, moyen, difficile).
     * Utilise le canvas secondaire pour afficher une vue locale/zoom à partir du niveau 2.
     * 
     * @param levelNumber le niveau de progression (0 à 5)
     * @param challengeDifficulty la difficulté du défi (EASY, MEDIUM, HARD)
     */
    public void viewByLevel(int levelNumber, Difficulty challengeDifficulty) {
        if (levelNumber == 0 || levelNumber == 1 || levelNumber == 3) {
            //if (levelNumber)
            MazeRenderer.drawSimpleGame(mainCanvas.getGraphicsContext2D(), model, cellSize);
            secondCanvas.setVisible(false);
        } 
        
        else if (levelNumber == 2) {
            MazeRenderer.drawGlobalView(mainCanvas.getGraphicsContext2D(), model, cellSize);
            if (challengeDifficulty == Difficulty.EASY) {
                MazeRenderer.drawLocalView(secondCanvas.getGraphicsContext2D(), model, 40, 2);
            } else if (challengeDifficulty == Difficulty.MEDIUM) {
                MazeRenderer.drawLocalView(secondCanvas.getGraphicsContext2D(), model, 40, 1);
            } else if (challengeDifficulty == Difficulty.HARD) {
                MazeRenderer.drawLocalView(secondCanvas.getGraphicsContext2D(), model, 40, 1);
            }
        }
        
        else if (levelNumber == 4) {
            if (challengeDifficulty == Difficulty.EASY) {
                MazeRenderer.drawHybridView(mainCanvas.getGraphicsContext2D(), model, cellSize, 3);
            } else if (challengeDifficulty == Difficulty.MEDIUM) {
                MazeRenderer.drawHybridView(mainCanvas.getGraphicsContext2D(), model, cellSize, 2);
            } else if (challengeDifficulty == Difficulty.HARD) {
                MazeRenderer.drawHybridView(mainCanvas.getGraphicsContext2D(), model, cellSize, 1);
            }
            secondCanvas.setVisible(false);
        } 
    
        else if (levelNumber == 5) {
            MazeRenderer.drawProgressiveView(mainCanvas.getGraphicsContext2D(), model, cellSize);

            if (challengeDifficulty == Difficulty.EASY) {
                MazeRenderer.drawLocalView(secondCanvas.getGraphicsContext2D(), model, 40, 3);
            } else if (challengeDifficulty == Difficulty.MEDIUM) {
                MazeRenderer.drawLocalView(secondCanvas.getGraphicsContext2D(), model, 40, 2);
            } else if (challengeDifficulty == Difficulty.HARD) {
                MazeRenderer.drawLocalView(secondCanvas.getGraphicsContext2D(), model, 40, 1);
            }
        }
    }


    /**
     * Mise à jour de la vue lors d'un changement d'état du modèle.
     * Re-rend le labyrinthe et met à jour les labels d'information (mouvements, carburant, progression).
     * 
     * @param model le modèle Observable qui a changé
     */
    @Override
    public void update(Observable model) {
        finalMazeView();
        if (model.getMode() instanceof ProgressionMode) {
            ProgressionMode pm = (ProgressionMode) model.getMode();
            movesLabel.setText("Étape : " + (pm.getCurrentLevelIndex() + 1) + " \nDéfi : " + (pm.getCurrentChallengeIndex() + 1) + " \nMouvements : " + model.getMoveCount());
        } else if (model.getMode() instanceof FuelMode) {
            movesLabel.setText("Mouvements : " + model.getMoveCount() + " \nCarburant restant : " + ((FuelMode) this.model.getMode()).getFuelLeft());
        } else {
            movesLabel.setText("Mouvements : " + model.getMoveCount());
        }
    }

    /**
     * Retourne le niveau actuel en mode Progression.
     * 
     * @return l'indice du niveau (0-5) ou 0 si le mode n'est pas Progression
     */
    public int getLevel() {
        if (model.getMode() instanceof ProgressionMode) {
            return ((ProgressionMode) model.getMode()).getCurrentLevelIndex();
        }
        return 0;
    }

    /**
     * Retourne la difficulté du défi en mode Progression.
     * 
     * @return la difficulté (EASY, MEDIUM, ou HARD) ou MEDIUM par défaut
     */
    public Difficulty getChallengeDifficulty() {
        if (model.getMode() instanceof ProgressionMode) {
            return ((ProgressionMode) model.getMode()).getCurrentChallengeDifficulty();
        }
        return Difficulty.MEDIUM;
    }

    /**
     * Charge l'image de fond adaptée au mode de jeu actuel.
     * Sélectionne automatiquement le fichier image approprié selon le mode
     * (Progression, Libre, Essence, Nuit, Sortie Multiple, Course, Tempête).
     * En cas d'erreur de chargement, la vue reste avec un fond transparent.
     */
    private void setBackgroundImage() {
        Image backgroundImage = null;
        java.io.InputStream is = null;
        
        try {
            if (model.getMode() instanceof ProgressionMode) {
                is = getClass().getResourceAsStream("/images/ProgressionModeBackground.png");
            } else if (model.getMode() instanceof FreeMode) {
                is = getClass().getResourceAsStream("/images/FuelModeBackground.png");
            } else if (model.getMode() instanceof FuelMode) {
                is = getClass().getResourceAsStream("/images/FuelModeBackground.png");
            } else if (model.getMode() instanceof NightMode) {
                is = getClass().getResourceAsStream("/images/NightModeBackground.jpg");
            } else if (model.getMode() instanceof MultipleExitMode) {
                is = getClass().getResourceAsStream("/images/MultipleExitModeBackground.jpg");
            } else if (model.getMode() instanceof RaceMode) {
                is = getClass().getResourceAsStream("/images/RaceModeBackground.jpg");
            } else if (model.getMode() instanceof StormMode) {
                is = getClass().getResourceAsStream("/images/Tempete.png");
            } else if (model.getMode() instanceof MadMaxMode) {
                is = getClass().getResourceAsStream("/images/madmaxground.png");
            }
            
            // Create Image from the input stream
            if (is != null) {
                backgroundImage = new Image(is);
            }
        } catch (Exception ex) {
            // keep backgroundImage null on error
            System.err.println("Error loading background image: " + ex.getMessage());
            ex.printStackTrace();
            backgroundImage = null;
        } finally {
            try { if (is != null) is.close(); } catch (Exception e) { /* ignore */ }
        }

        // create ImageView even if image failed to load to avoid NPEs later
        this.background = new ImageView();
        if (backgroundImage != null && !backgroundImage.isError()) {
            this.background.setImage(backgroundImage);
        } else {
            // make background transparent if no image available
            this.background.setStyle("-fx-background-color: transparent;");
        }
    }

    /**
     * Retourne le bouton directionnel vers le haut.
     * 
     * @return le bouton ↑
     */
    public Button getUpBtn() {
        return upBtn;
    }

    /**
     * Retourne le bouton directionnel vers le bas.
     * 
     * @return le bouton ↓
     */
    public Button getDownBtn() {
        return downBtn;
    }

    /**
     * Retourne le bouton directionnel vers la gauche.
     * 
     * @return le bouton ←
     */
    public Button getLeftBtn() {
        return leftBtn;
    }

    /**
     * Retourne le bouton directionnel vers la droite.
     * 
     * @return le bouton →
     */
    public Button getRightBtn() {
        return rightBtn;
    }

    /**
     * Retourne le bouton pour augmenter le zoom.
     * 
     * @return le bouton de zoom avant
     */
    public Button getZoomInBtn() {
        return zoomIn;
    }

    /**
     * Retourne le bouton pour diminuer le zoom.
     * 
     * @return le bouton de zoom arrière
     */
    public Button getZoomOutBtn() {
        return zoomOut;
    }

    /**
     * Retourne le bouton pour quitter/abandonner la partie.
     * 
     * @return le bouton Quitter
     */
    public Button getAbandonBtn() {
        return abandonBtn;
    }

    /**
     * Retourne le bouton pour recommencer la partie.
     * 
     * @return le bouton Recommencer
     */
    public Button getResetBtn() {
        return resetBtn;
    }

    /**
     * Retourne la fenêtre principale de l'application.
     * 
     * @return le Stage principal
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Retourne le canvas principal affichant le labyrinthe.
     * 
     * @return le nœud du canvas principal
     */
    public Node getMainCanvas() {
        return mainCanvas;
    }
}
