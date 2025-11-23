package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.BonusModeController;

public class BonusModeView {

    private final Stage stage;
    private Button btnNightMode;
    private Button btnBack;
    private Button btnFuelMode;
    private Button btnMultipleExitMode;
    private Button btnRaceMode;
    private Button btnStormMode;
    private Button btnMadMaxMode;
    private BonusModeController controller;

    public BonusModeView(Stage stage) {
        this.stage = stage;
        this.controller = new BonusModeController(this);
        show();
        controller.setupListeners();
    }

    public void show() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 10);

        StackPane stackRoot = new StackPane();

        ImageView backgroundView = new ImageView(new Image(getClass().getResourceAsStream("/images/bg.png")));
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(stackRoot.widthProperty());
        backgroundView.fitHeightProperty().bind(stackRoot.heightProperty());

        BorderPane contentRoot = new BorderPane();
        contentRoot.setBackground(null);

        Label title = new Label("Modes Bonus");
        title.getStyleClass().add("bonus-title");
        StackPane top = new StackPane(title);
        top.setPadding(new Insets(20, 0, 10, 0));
        contentRoot.setTop(top);

        VBox center = new VBox(20);
        center.setPadding(new Insets(40));
        center.setAlignment(Pos.CENTER);

        btnNightMode = new Button("Mode Nuit");
        btnFuelMode = new Button("Mode Essence");
        btnMultipleExitMode = new Button("Mode Sortie Multiple");
        btnRaceMode = new Button("Mode Course");
        btnStormMode = new Button("Mode Tempête");
        btnMadMaxMode = new Button("Mode Mad Max");
        btnBack = new Button("Retour");

        styleSideButton(btnNightMode);
        styleSideButton(btnFuelMode);
        styleSideButton(btnMultipleExitMode);
        styleSideButton(btnRaceMode);
        styleSideButton(btnStormMode);
        styleSideButton(btnMadMaxMode);
        styleSideButton(btnBack);

        center.getChildren().addAll(btnNightMode, btnFuelMode, btnMultipleExitMode, btnRaceMode, btnStormMode, btnMadMaxMode);
        contentRoot.setCenter(center);

        VBox bottom = new VBox();
        bottom.setPadding(new Insets(20));
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().add(btnBack);
        contentRoot.setBottom(bottom);

        stackRoot.getChildren().addAll(backgroundView, contentRoot);

        Scene scene = new Scene(stackRoot, 1200, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/bonus.css").toExternalForm());
        setScenePreserveSize(stage, scene);
        stage.setTitle("Modes Bonus");
        stage.show();
    }

    private void styleSideButton(Button btn) {
        btn.getStyleClass().add("styled-button");
        btn.setPrefWidth(300);
        btn.setPrefHeight(60);
    }

    private void setScenePreserveSize(Stage stage, Scene newScene) {
        double curW = stage.getWidth();
        double curH = stage.getHeight();
        boolean hadSize = curW > 0 && curH > 0;
        stage.setScene(newScene);
        if (hadSize) {
            stage.setWidth(curW);
            stage.setHeight(curH);
        }
    }

    public Button getBackButton() { return btnBack; }
    public Button getNightModeButton() { return btnNightMode; }
    public Button getFuelModeButton() { return btnFuelMode; }
    public Button getMultipleExitModeButton() { return btnMultipleExitMode; }
    public Button getRaceModeButton() { return btnRaceMode; }
    public Button getStormModeButton() { return btnStormMode; }
    public Button getMadMaxModeButton() { return btnMadMaxMode; }
    public Stage getStage() { return stage; }
}
