package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.ProfilConnectionController;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;

public class ProfilConnectionView {

    private final Stage stage;
    private Button back;
    private Button validate;
    private ProfilConnectionController controller;
    private final Profil player;
    private PasswordField pwdField;
    private Label errorLabel;

    public ProfilConnectionView(Stage stage, Profil player) {
        this.stage = stage;
        this.player = player;

        validate = new Button("Confirmer");
        back = new Button("Retour");

        show();
        controller = new ProfilConnectionController(player, this);
        controller.setupListeners(this);
    }

    public void show() {
        javafx.scene.text.Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 10);

        BorderPane root = new BorderPane();

        // Ajout de l'image de fond de manière robuste
        String imageUrl = getClass().getResource("/images/bg_newPlayer.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover;");

        Label title = new Label("Se connecter");
        title.getStyleClass().add("profil-connection-title");
        StackPane top = new StackPane(title);
        top.setPadding(new Insets(20, 0, 10, 0));
        root.setTop(top);

        StackPane rounded = new StackPane();
        rounded.getStyleClass().add("profil-connection-panel");

        rounded.setMaxWidth(Region.USE_PREF_SIZE);
        rounded.setMaxHeight(Region.USE_PREF_SIZE);

        VBox form = new VBox(12);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(10, 40, 10, 40));
        form.setPrefWidth(460);

        //Label nameLabel = new Label("Profil :");
        //nameLabel.getStyleClass().add("form-label");
        Label profileName = new Label(getProfileName());
        profileName.getStyleClass().add("profile-name-value");
        HBox rowName = new HBox(12);
        rowName.getStyleClass().add("form-row");
        rowName.setAlignment(Pos.CENTER);
        rowName.getChildren().addAll(/*nameLabel,*/ profileName);

        Label pwdLabel = new Label("Mot de passe :");
        pwdLabel.getStyleClass().add("form-label");
        pwdField = new PasswordField();
        pwdField.getStyleClass().add("form-field");
        HBox rowPwd = new HBox(12);
        rowPwd.getStyleClass().add("form-row");
        rowPwd.setAlignment(Pos.CENTER);
        rowPwd.getChildren().addAll(pwdLabel, pwdField);

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        form.getChildren().addAll(rowName, rowPwd, errorLabel);
        rounded.getChildren().add(form);

        root.setCenter(rounded);

        back.getStyleClass().add("bottom-button");
        validate.getStyleClass().add("bottom-button");

        HBox bottom = new HBox(20);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(20, 40, 30, 40));
        bottom.getChildren().addAll(back, validate);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/profil_connection.css").toExternalForm());
        
        double curW = stage.getWidth();
        double curH = stage.getHeight();
        boolean hadSize = curW > 0 && curH > 0;
        stage.setScene(scene);
        if (hadSize) { stage.setWidth(curW); stage.setHeight(curH); }
        stage.setTitle("Se connecter");
        stage.show();
    }

    public String getProfileName() { return this.player.getName(); }
    public Button getBackButton() { return back; }
    public Button getValidateButton() { return validate; }
    public PasswordField getPasswordField() { return pwdField; }
    public Label getErrorLabel() { return errorLabel; }
    public Stage getStage() { return stage; }
    public Scene getScene() { return stage.getScene(); }
}
