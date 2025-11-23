package main.java.fr.univlille.iut.sae302.madmaze.view;

import java.io.InputStream;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.controller.MainMenuController;

public class MainMenuView extends Application {

	private Button freeModeBtn;
	private Button progressionBtn;
    private Button bonusBtn;
	//private Button creditsBtn;
	private Button quitBtn;
	private MainMenuController controller;

	@Override
	public void start(Stage stage) {
		// Use a StackPane so we can place an ImageView as background and
		// the existing VBox content on top.
		stage.getIcons().add(new Image("images/M.png"));

		StackPane stackRoot = new StackPane();
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);

		// Attempt to load a background image and place it behind the content.
		InputStream bgStream = getClass().getResourceAsStream("/images/Menu.png");
		if (bgStream != null) {
			try {
				Image bgImg = new Image(bgStream);
				ImageView background = new ImageView(bgImg);
				// apply the styles requested
				background.setPreserveRatio(false);
				background.setFitWidth(stage.getWidth());
				background.setFitHeight(stage.getHeight());
				// ensure background is behind
				stackRoot.getChildren().add(background);
			} catch (Exception ex) {
				// fallback: white background
				root.setStyle("-fx-background-color: white;");
			}
		} else {
			root.setStyle("-fx-background-color: white;");
		}

		// put the regular VBox content on top of the background
		stackRoot.getChildren().add(root);

		// Top: title image
		ImageView titleView = loadTitleImage();
		StackPane topPane = new StackPane(titleView);
		topPane.setPadding(new Insets(30, 0, 0, 0));

		// Spacer flexible entre le titre et les boutons
		javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
		VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
		spacer.setMaxHeight(150); // Limite l'espace maximum
		spacer.setMinHeight(20);  // Espace minimum

		// Center: vertical buttons
		VBox buttons = new VBox(20);
		buttons.setAlignment(Pos.CENTER);

		freeModeBtn = styledButton("Mode libre");
		progressionBtn = styledButton("Mode progression");
        bonusBtn = styledButton("Modes Bonus");
		//creditsBtn = styledButton("Crédits");
		quitBtn = styledButton("Quitter");

		buttons.getChildren().addAll(freeModeBtn, progressionBtn, bonusBtn, /*creditsBtn,*/ quitBtn);

		// Spacer flexible après les boutons pour les centrer
		Region spacerBottom = new Region();
		VBox.setVgrow(spacerBottom, Priority.ALWAYS);
		spacerBottom.setMaxHeight(150);

		root.getChildren().addAll(topPane, spacer, buttons, spacerBottom);

		Scene scene = new Scene(stackRoot, 1200, 600, Color.WHITE);
		setScenePreserveSize(stage, scene);
		stage.setTitle("Mad Maze - Menu");
		stage.setMaximized(true);

		controller = new MainMenuController(stage);
		stage.show();
		controller.setupListeners(this);
	}

	private Button styledButton(String text) {
		Font myFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Teko-Bold.ttf"), 28);
		
		Button btn = new Button(text);
		btn.setFont(myFont);
		btn.setPrefWidth(400);
		btn.setPrefHeight(65);
		btn.setStyle(
				"-fx-background-color: #d6ab12;" +
				"-fx-text-fill: #000a2e;" +
				"-fx-background-radius: 20;" +
				"-fx-border-radius: 20;" +
				"-fx-border-color: transparent;"
		);
		// subtle shadow using clip to keep rounded corners crisp on all platforms
		Rectangle clip = new Rectangle(400, 65);
		clip.setArcWidth(40);
		clip.setArcHeight(40);
		btn.setClip(clip);
		return btn;
	}

	private ImageView loadTitleImage() {
		// Try loading a bundled image named 'title.png' in the resources folder.
		// If not found, create a fallback text-like image using an empty ImageView
		// so layout still matches the screenshot proportions.
		ImageView view = new ImageView();
		try {
			// First try classpath resource (after Maven resource copying it will be /images/MadMaze.png)
			java.io.InputStream is = getClass().getResourceAsStream("/images/MadMaze.png");
			if (is != null) {
				Image img = new Image(is);
				view.setImage(img);
				view.setPreserveRatio(true);
				view.setFitWidth(1000);

				// subtle dark outline to separate the (transparent) logo from background
				DropShadow outline = new DropShadow();
				outline.setRadius(10);
				outline.setOffsetX(0);
				outline.setOffsetY(0);
				outline.setSpread(0.08); // small spread for a thin edge
				outline.setColor(Color.rgb(0, 0, 0, 1)); // very faint black
				view.setEffect(outline);
				return view;
			}

			throw new IllegalStateException("title image not found in classpath");
		} catch (Exception ex) {
			System.err.println("Failed to load title image: " + ex.getMessage());
			// Fallback: use a placeholder rectangle styled to occupy similar space
			Rectangle placeholder = new Rectangle(700, 120, Color.web("#f5f5f5"));
			placeholder.setArcWidth(10);
			placeholder.setArcHeight(10);
			// wrap rectangle in an ImageView by snapshotting is heavier; instead return empty ImageView
			view.setFitWidth(700);
			view.setFitHeight(120);
		}
		return view;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Helper to set the scene while preserving the stage size (static since this class is used as Application)
	 */
	private static void setScenePreserveSize(Stage stage, javafx.scene.Scene newScene) {
		double curW = stage.getWidth();
		double curH = stage.getHeight();
		boolean hadSize = curW > 0 && curH > 0;
		stage.setScene(newScene);
		if (hadSize) {
			stage.setWidth(curW);
			stage.setHeight(curH);
		}
	}

	public Button getFreeModeButton() {
		return freeModeBtn;
	}

	public Button getProgressionButton() {
		return progressionBtn;
	}

    public Button getBonusButton() {return bonusBtn;}

	/*public Button getCreditsButton() {
		return creditsBtn;
	}*/

	public Button getQuitButton() {
		return quitBtn;
	}


}
