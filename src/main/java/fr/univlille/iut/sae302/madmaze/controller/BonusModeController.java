package main.java.fr.univlille.iut.sae302.madmaze.controller;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import main.java.fr.univlille.iut.sae302.madmaze.model.FuelMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.MadMaxMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.MultipleExitMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.NightMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.RaceMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.StormMode;
import main.java.fr.univlille.iut.sae302.madmaze.view.BonusModeView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MadMaxParameterView;
import main.java.fr.univlille.iut.sae302.madmaze.view.MainMenuView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ParameterView;
import main.java.fr.univlille.iut.sae302.madmaze.view.ViewsUtils;

/**
 * Contrôleur pour la vue des modes de jeu bonus (BonusModeView).
 * 
 * Ce contrôleur gère la navigation et les interactions de l'écran des modes de jeu bonus.
 * Il permet à l'utilisateur de sélectionner l'un des modes spéciaux :
 * - Mode Nuit (NightMode) : Labyrinthe avec vision limitée
 * - Mode Carburant (FuelMode) : Labyrinthe avec gestion de carburant
 * - Mode Sorties Multiples (MultipleExitMode) : Labyrinthe avec plusieurs sorties (pièges)
 * - Mode Course (RaceMode) : Compétition contre un bot virtuel
 * - Mode Tempête (StormMode) : Labyrinthe avec obstacles mobiles
 * 
 * Chaque mode sélectionné déclenche l'affichage de la vue de paramétrage (ParameterView)
 * où l'utilisateur peut configurer les paramètres du labyrinthe avant de commencer le jeu.
 * 
 * @author G4
 * @see BonusModeView
 * @see ParameterView
 * @see NightMode
 * @see FuelMode
 * @see MultipleExitMode
 * @see RaceMode
 * @see StormMode
 * @see MadMaxMode
 */

public class BonusModeController {

    /** La scène (stage) JavaFX associée à ce contrôleur */
    private final Stage stage;
    /** La vue des modes bonus (BonusModeView) gérée par ce contrôleur */
    private final BonusModeView view;

    /**
     * Constructeur du contrôleur des modes bonus.
     * Initialise le contrôleur avec la vue et la scène correspondantes.
     * 
     * @param view la vue des modes bonus (BonusModeView) associée à ce contrôleur
     */
    public BonusModeController(BonusModeView view) {
        this.stage = view.getStage();
        this.view = view;
    }
    /**
     * Configure les écouteurs d'événements pour tous les boutons de la vue.
     * Chaque bouton est associé à sa méthode de gestion correspondante.
     * 
     * Cette méthode doit être appelée après la création du contrôleur pour activer
     * les interactions utilisateur.
     */
    public void setupListeners() {
        view.getNightModeButton().setOnAction(e -> onNightMode());
        view.getBackButton().setOnAction(this::onBack);
        view.getFuelModeButton().setOnAction(e -> onFuelMode());
        view.getMultipleExitModeButton().setOnAction(e -> onMultipleExitMode());
        view.getRaceModeButton().setOnAction(e -> onRaceMode());
        view.getStormModeButton().setOnAction(e -> onStormMode());
        view.getMadMaxModeButton().setOnAction(e -> onMadMaxMode());
    }

    /**
     * Gère la sélection du mode Nuit (NightMode).
     * Crée une nouvelle instance de NightMode et affiche la vue de paramétrage.
     */
    public void onNightMode() {
        NightMode nightMode = new NightMode();

        ViewsUtils.saveStageSize(stage);
        new ParameterView(stage, nightMode);
        ViewsUtils.restoreStageSize(stage);
    }

    /**
     * Gère la sélection du mode Sorties Multiples (MultipleExitMode).
     * Crée une nouvelle instance de MultipleExitMode et affiche la vue de paramétrage.
     */
    public void onMultipleExitMode() {
        MultipleExitMode decoyMode = new MultipleExitMode();

        ViewsUtils.saveStageSize(stage);
        new ParameterView(stage, decoyMode);
        ViewsUtils.restoreStageSize(stage);
    }

    /**
     * Gère la sélection du mode Carburant (FuelMode).
     * Crée une nouvelle instance de FuelMode et affiche la vue de paramétrage.
     */
    public void onFuelMode() {
        FuelMode fuelMode = new FuelMode();

        ViewsUtils.saveStageSize(stage);
        new ParameterView(stage, fuelMode);
        ViewsUtils.restoreStageSize(stage);
    }

    /**
     * Gère la sélection du mode Course (RaceMode).
     * Crée une nouvelle instance de RaceMode et affiche la vue de paramétrage.
     */
    public void onRaceMode() {
        RaceMode raceMode = new RaceMode();

        ViewsUtils.saveStageSize(stage);
        new ParameterView(stage, raceMode);
        ViewsUtils.restoreStageSize(stage);
    }

    /**
     * Gère la sélection du mode Tempête (StormMode).
     * Crée une nouvelle instance de StormMode et affiche la vue de paramétrage.
     */
    public void onStormMode() {
        StormMode stormMode = new StormMode();
        
        ViewsUtils.saveStageSize(stage);
        new ParameterView(stage, stormMode);
        ViewsUtils.restoreStageSize(stage);
    }

    /**
     * Gère le bouton retour.
     * Sauvegarde la taille de la fenêtre actuelle et retourne à la vue du menu principal (MainMenuView).
     * 
     * @param event l'événement d'action déclenché par le clic sur le bouton retour
     */
    public void onBack(ActionEvent event) {
        System.out.println("[BonusModeController] Back pressed");
        if (stage != null) {
            new MainMenuView().start(stage);
        }
    }

    /**
     * Gère la sélection du mode Mad Max (MadMaxMode).
     * Crée une nouvelle instance de MadMaxMode et affiche la vue de paramétrage spécifique.
     */
    public void onMadMaxMode() {
        MadMaxMode madMaxMode = new MadMaxMode();

        ViewsUtils.saveStageSize(stage);
        new MadMaxParameterView(stage, madMaxMode);
        ViewsUtils.restoreStageSize(stage);
    }
}