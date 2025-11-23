package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font; // Assurez-vous que l'import est présent
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.ProgressionModeController;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;

public class ProgressionModeView {

    private final Stage stage;
    private Profil player;
    private Button btnPlay;
    private Button btnCreate;
    private Button btnBack;
    private ProgressionModeController controller;

    public ProgressionModeView(Stage stage) {
        this.stage = stage;
        this.player = null;
        this.controller = new ProgressionModeController(this);
        show();
        controller.setupListeners();

    }

    public void show() {
        // Charger la police personnalisée pour qu'elle soit disponible dans le CSS
        Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 10);

        // Racine principale en StackPane pour superposer l'image et le contenu
        StackPane stackRoot = new StackPane();

        // 1. Image de fond
        ImageView backgroundView = new ImageView();
        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/back_Progression.jpg"));
        backgroundView.setImage(backgroundImage);
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(stackRoot.widthProperty());
        backgroundView.fitHeightProperty().bind(stackRoot.heightProperty());

        // 2. Contenu principal en BorderPane
        BorderPane contentRoot = new BorderPane();
        contentRoot.setBackground(null);

        Label title = new Label("Mode progression");
        title.getStyleClass().add("progression-title");
        StackPane top = new StackPane(title);
        top.setPadding(new Insets(20, 0, 10, 0));
        contentRoot.setTop(top);

        // Left: buttons
        VBox left = new VBox(20);
        left.setPadding(new Insets(40));
        left.setAlignment(Pos.TOP_CENTER);

        btnPlay = new Button("Jouer");
        btnCreate = new Button("Créer un nouveau profil");
        btnBack = new Button("Retour");

        styleSideButton(btnPlay);
        styleSideButton(btnCreate);
        styleSideButton(btnBack);

        left.getChildren().addAll(btnPlay, btnCreate, btnBack);
        contentRoot.setLeft(left);

        // Right / center: TableView
        TableView<ProfileRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ProfileRow, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProfileRow, Integer> stepCol = new TableColumn<>("N°Etape");
        stepCol.setCellValueFactory(new PropertyValueFactory<>("step"));

        TableColumn<ProfileRow, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        table.getColumns().addAll(nameCol, stepCol, scoreCol);

        ObservableList<ProfileRow> rows = FXCollections.observableArrayList();

        try{
            for(Profil player:Profil.listePlayers){
                rows.add(new ProfileRow(player.getName(), player.getLastValidate(),player.getScore()));
            }
        } catch (Exception ex) {
            System.err.println("Failed to list players directory: " + ex.getMessage());
        }
        table.setItems(rows);

        table.setFixedCellSize(35);
        table.prefHeightProperty().bind(
            Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(40)
        );
        table.setMaxHeight(Region.USE_PREF_SIZE);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Profil selectedPlayer = Profil.listePlayers.stream()
                        .filter(p -> p.getName().equals(newSelection.getName()))
                        .findFirst()
                        .orElse(null);
                controller.setSelectedPlayer(selectedPlayer);
            }
        });

        StackPane centerWrapper = new StackPane(table);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(20));
        contentRoot.setCenter(centerWrapper);    

        // Superposer l'image et le contenu
        stackRoot.getChildren().addAll(backgroundView, contentRoot);

        Scene scene = new Scene(stackRoot, 1200, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/progression.css").toExternalForm());
        setScenePreserveSize(stage, scene);
        stage.setTitle("Mode progression");
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

    public static class ProfileRow {
        private final String name;
        private final String step;
        private final int score;

        public ProfileRow(String name, String step, int score) {
            this.name = name;
            this.step = step;
            this.score = score;
        }

        public String getName() { return name; }
        public String getStep() { return step; }
        public int getScore() { return score; }
    }

    public Button getBackButton() { return btnBack; }
    public Button getCreateButton() { return btnCreate; }
    public Button getPlayButton() { return btnPlay; }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return stage.getScene();
    }
}
