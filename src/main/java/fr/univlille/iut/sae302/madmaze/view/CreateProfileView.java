package main.java.fr.univlille.iut.sae302.madmaze.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.CreateProfileController;

public class CreateProfileView {

    private final Stage stage;
    private Button back;
    private Button validate;
    private TextField nameField;
    private PasswordField pwdField;
    private PasswordField confirmField;
    private Label errorLabel;
    private CreateProfileController controller;

    public CreateProfileView(Stage stage) {
        this.stage = stage;
        back = new Button("Annuler");
        validate = new Button("Valider");

        show();
        this.controller = new CreateProfileController(stage, this);
        controller.setupListeners();
    }

    public void show() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 10);

        BorderPane root = new BorderPane();

        // Ajout de l'image de fond de manière robuste
        String imageUrl = getClass().getResource("/images/bg_newPlayer.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover;");

        Label title = new Label("Créer un nouveau profil");
        title.getStyleClass().add("create-profile-title");
        StackPane top = new StackPane(title);
        top.setPadding(new Insets(20, 0, 10, 0));
        root.setTop(top);

        StackPane rounded = new StackPane();
        rounded.getStyleClass().add("create-profile-panel");
        rounded.setMaxHeight(StackPane.USE_PREF_SIZE);
        rounded.setMaxWidth(StackPane.USE_PREF_SIZE);

        VBox form = new VBox(12);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(10, 40, 10, 40));
        form.setPrefWidth(460);

        Label nameLabel = new Label("Nom :");
        nameLabel.getStyleClass().add("form-label");
        nameField = new TextField();
        nameField.getStyleClass().add("form-field");
        HBox rowName = new HBox(12);
        rowName.getStyleClass().add("form-row");
        rowName.getChildren().addAll(nameLabel, nameField);

        Label pwdLabel = new Label("Mot de passe :");
        pwdLabel.getStyleClass().add("form-label");
        pwdField = new PasswordField();
        pwdField.getStyleClass().add("form-field");
        HBox rowPwd = new HBox(12);
        rowPwd.getStyleClass().add("form-row");
        rowPwd.getChildren().addAll(pwdLabel, pwdField);

        Label confirmLabel = new Label("Confirmer le mot de passe :");
        confirmLabel.getStyleClass().add("form-label");
        confirmField = new PasswordField();
        confirmField.getStyleClass().add("form-field");
        HBox rowConfirm = new HBox(12);
        rowConfirm.getStyleClass().add("form-row");
        rowConfirm.getChildren().addAll(confirmLabel, confirmField);

        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        form.getChildren().addAll(rowName, rowPwd, rowConfirm, errorLabel);
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
        scene.getStylesheets().add(getClass().getResource("/styles/create_profile.css").toExternalForm());
        
        double curW = stage.getWidth();
        double curH = stage.getHeight();
        boolean hadSize = curW > 0 && curH > 0;
        stage.setScene(scene);
        if (hadSize) { stage.setWidth(curW); stage.setHeight(curH); }
        stage.setTitle("Créer un nouveau profil");
        stage.show();
    }

    public Button getBackButton() { return back; }
    public Button getValidateButton() { return validate; }
    public TextField getNameValue() { return nameField; }
    public PasswordField getPasswordValue() { return pwdField; }
    public PasswordField getConfirmPasswordValue() { return confirmField; }
    public void setErrorMessage(String message) { errorLabel.setText(message); }
    public Label getErrorLabel() { return errorLabel; }
    public Scene getScene() { return stage.getScene(); }
}
