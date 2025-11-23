package main.java.fr.univlille.iut.sae302.madmaze.view;

import java.io.InputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.MadMaxParameterController;
import main.java.fr.univlille.iut.sae302.madmaze.model.MazeType;
import main.java.fr.univlille.iut.sae302.madmaze.model.Mode;

public class MadMaxParameterView {

    private final Stage stage;
    private Button backBtn;
    private Button confirmBtn;
    private Button perfectLabBtn;
    private Button randomLabBtn;

    private Spinner<Integer> heightSpinner;
    private Spinner<Integer> widthSpinner;
    private Spinner<Integer> parameterSpinner;
    private Label parameterLabel;

    private MazeType selectedMazeType = MazeType.PERFECT;
    private Mode mode;

    private final MadMaxParameterController controller;

    public MadMaxParameterView(Stage stage, Mode mode) {
        this.stage = stage;
        this.mode = mode;

        heightSpinner = new Spinner<>(5, 80, 15);
        widthSpinner = new Spinner<>(5, 80, 15);
        parameterSpinner = new Spinner<>(5, 300, 20);

        heightSpinner.setEditable(true);
        widthSpinner.setEditable(true);
        parameterSpinner.setEditable(true);

        backBtn = new Button("Retour");
        confirmBtn = new Button("Valider");
        perfectLabBtn = new Button("Parfait");
        randomLabBtn = new Button("Aléatoire");

        controller = new MadMaxParameterController(this);

        show();

        controller.setupListeners();
    }

    public void show() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Medium.ttf"), 10);

        BorderPane root = new BorderPane();
        StackPane stackRoot = new StackPane();

        Label title = new Label("Paramètres");
        title.getStyleClass().add("parameter-title");
        StackPane titlePane = new StackPane(title);
        titlePane.setPadding(new Insets(10, 0, -20, 0));
        BorderPane.setAlignment(titlePane, Pos.CENTER);
        root.setTop(titlePane);

        VBox centerContent = new VBox(5);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setMaxWidth(VBox.USE_PREF_SIZE);


        StackPane rounded = new StackPane();
        rounded.getStyleClass().add("parameter-panel");

        VBox rows = new VBox(18);
        rows.setAlignment(Pos.CENTER);

        parameterLabel = new Label("Distance minimale");
        parameterLabel.getStyleClass().add("parameter-label");

        rows.getChildren().addAll(
            rowWithSpinner("Hauteur du labyrinthe", heightSpinner),
            rowWithSpinner("Largeur du labyrinthe", widthSpinner),
            rowWithSpinner(parameterLabel, parameterSpinner)
        );

        InputStream bgStream = getClass().getResourceAsStream("/images/ground.png");
        if (bgStream != null) {
            ImageView background = new ImageView(new Image(bgStream));
            background.setPreserveRatio(false);
            background.fitWidthProperty().bind(stage.widthProperty());
            background.fitHeightProperty().bind(stage.heightProperty());
            stackRoot.getChildren().add(background);
        }

        rounded.getChildren().add(rows);
        centerContent.getChildren().addAll(rounded);

        root.setCenter(centerContent);

        HBox bottom = new HBox(40);
        bottom.setPadding(new Insets(20));
        bottom.setAlignment(Pos.CENTER);
        backBtn.getStyleClass().addAll("parameter-button", "parameter-button-full");
        confirmBtn.getStyleClass().addAll("parameter-button", "parameter-button-full");
        bottom.getChildren().addAll(backBtn, confirmBtn);
        root.setBottom(bottom);

        stackRoot.getChildren().add(root);

        Scene scene = new Scene(stackRoot, 500, 350);
        scene.getStylesheets().add(getClass().getResource("/styles/parameter.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        setSelectedMazeType(selectedMazeType);
    }

    private HBox rowWithSpinner(String labelText, Spinner<Integer> spinner) {
        Label label = new Label(labelText);
        label.getStyleClass().add("parameter-label");
        return rowWithSpinner(label, spinner);
    }

    private HBox rowWithSpinner(Label label, Spinner<Integer> spinner) {
        HBox h = new HBox(20);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(10, 40, 10, 40));
        spinner.getStyleClass().add("spinner");
        h.getChildren().addAll(label, spinner);
        return h;
    }

    public Button getBackButton() { return backBtn; }
    public Button getConfirmButton() { return confirmBtn; }
    public Button getPerfectLabButton() { return perfectLabBtn; }
    public Button getRandomLabButton() { return randomLabBtn; }
    public MazeType getSelectedMazeType() { return selectedMazeType; }
    public void setSelectedMazeType(MazeType t) { this.selectedMazeType = t; }
    public int getMazeParamValue() { return parameterSpinner.getValue(); }
    public int getWidthValue() { return widthSpinner.getValue(); }
    public int getHeightValue() { return heightSpinner.getValue(); }
    public Mode getMode() { return mode; }
    public Stage getStage() { return stage; }
    public Spinner<Integer> getHeightSpinner() { return heightSpinner; }
    public Spinner<Integer> getWidthSpinner() { return widthSpinner; }
    public Spinner<Integer> getParameterSpinner() { return parameterSpinner; }
    public Label getParameterLabel() { return parameterLabel; }
}
