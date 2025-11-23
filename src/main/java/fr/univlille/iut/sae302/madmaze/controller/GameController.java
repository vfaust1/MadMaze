package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.model.Direction;
import main.java.fr.univlille.iut.sae302.madmaze.model.FuelMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.Game;
import main.java.fr.univlille.iut.sae302.madmaze.model.ProgressionMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.RaceMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.StormMode;
import main.java.fr.univlille.iut.sae302.madmaze.view.EndGameLoseView;
import main.java.fr.univlille.iut.sae302.madmaze.view.EndGameWinView;
import main.java.fr.univlille.iut.sae302.madmaze.view.LevelChoiceView;
import main.java.fr.univlille.iut.sae302.madmaze.view.GameView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;

public class GameController {
    private final Game model;
    private final GameView view;
    private double currentScale = 1.0;
    private static final double MIN_SCALE = 0.3;
    private static final double MAX_SCALE = 4.0;

    public GameController(Game model, GameView view) {
        this.model = model;
        this.view = view;
    }

    public void setupListeners() {
        Scene scene = view.getStage().getScene();
        if (scene != null) {
            attachKeyHandler(scene);
        }

        view.getUpBtn().setOnAction(e -> moveUp());
        view.getDownBtn().setOnAction(e -> moveDown());
        view.getLeftBtn().setOnAction(e -> moveLeft());
        view.getRightBtn().setOnAction(e -> moveRight());

        view.getZoomInBtn().setOnAction(e -> zoomIn());
        view.getZoomOutBtn().setOnAction(e -> zoomOut());
        view.getAbandonBtn().setOnAction(e -> onAbandon(view.getStage()));
        view.getResetBtn().setOnAction(e -> onReset());
    }

    private void attachKeyHandler(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP: case Z: moveUp(); break;
                case DOWN: case S: moveDown(); break;
                case LEFT: case Q: moveLeft(); break;
                case RIGHT: case D: moveRight(); break;
                case PLUS: case ADD: zoomIn(); break;
                case MINUS: case SUBTRACT: zoomOut(); break;
                default: break;
            }
        });
    }

    private void move(Direction dir) {
        model.movePlayer(dir);

        if (model.isFinished()) {
            ViewsUtils.saveStageSize(view.getStage());
            model.endGame();
            if ((model.getMode() instanceof FuelMode || model.getMode() instanceof RaceMode || model.getMode() instanceof StormMode) && model.isLost()) {
                new EndGameLoseView(view.getStage(), model);
            } else {
                new EndGameWinView(view.getStage(), model);
            }
            model.removeObserver(view);
            ViewsUtils.restoreStageSize(view.getStage());

        }
    }

    public void moveUp() { move(Direction.Z); }
    public void moveDown() { move(Direction.S); }
    public void moveLeft() { move(Direction.Q); }
    public void moveRight() { move(Direction.D); }

    private void zoomIn()  { setZoom(currentScale * 1.2); }
    private void zoomOut() { setZoom(currentScale / 1.2); }

    private void setZoom(double newScale) {
        currentScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScale));
        view.getMainCanvas().setScaleX(currentScale);
        view.getMainCanvas().setScaleY(currentScale);
        view.getStage().getScene().getRoot().requestFocus();
    }

    public void onAbandon(Stage stage) {
        ViewsUtils.saveStageSize(stage);   
        if (model.getMode() instanceof ProgressionMode) {
            new LevelChoiceView(stage, ((ProgressionMode) model.getMode()).getPlayer());
        } else {
            new MainMenuView().start(stage);
        }
        ViewsUtils.restoreStageSize(stage);
        model.removeObserver(view);
    }

    public void onReset() {
        model.reset();
    }

}
