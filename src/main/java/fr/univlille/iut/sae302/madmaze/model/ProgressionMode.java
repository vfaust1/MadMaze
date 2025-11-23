package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Représente le mode de jeu en progression avec des étapes et des défis de difficulté croissante.
 * Ce mode implémente l'interface Mode et organise le jeu en 3 étapes, chacune contenant
 * plusieurs défis avec des labyrinthes de tailles et difficultés variables.
 * 
 * @author G4
 */
public class ProgressionMode implements Mode {

    private final List<Level> levels;
    private Maze maze;
    private final Profil player;
    private int currentLevelIndex;
    private int currentChallengeIndex;

    public ProgressionMode(Profil player) {
        this.levels = buildAllLevels();
        this.player = player;
        this.currentLevelIndex = 0;
        this.currentChallengeIndex = 0;
        this.maze = null;
    }

    @Override
    public void prepareMaze() {
        Challenge challenge = getCurrentChallenge();
        Level level = getCurrentLevel();
        MazeType type = level.getMazeType();

        if (type == MazeType.PERFECT) {
            maze = new PerfectMaze(challenge.getWidth(), challenge.getHeight(), challenge.getMazeParam()); // mazeParam = minSolutionLength
        } else {
            maze = new RandomMaze(challenge.getWidth(), challenge.getHeight(), challenge.getMazeParam());  // mazeParam = wall percentage
        }
    }

    public List<Level> buildAllLevels() {
        List<Level> allLevels = new ArrayList<>();

        // Etapes 1 à 3 : labyrinthes aléatoires avec tailles et pourcentages de murs croissants
        for (int numLevel = 0; numLevel < 3; numLevel++) {
            List<Challenge> challenges = new ArrayList<>();
            challenges.add(new Challenge(Difficulty.EASY, getRandomHeight(numLevel), getRandomWidth(numLevel), getParameterThree(numLevel, Difficulty.EASY)));
            challenges.add(new Challenge(Difficulty.MEDIUM, getRandomHeight(numLevel), getRandomWidth(numLevel), getParameterThree(numLevel, Difficulty.MEDIUM)));
            challenges.add(new Challenge(Difficulty.HARD, getRandomHeight(numLevel), getRandomWidth(numLevel), getParameterThree(numLevel, Difficulty.HARD)));
            allLevels.add(new Level(numLevel + 1, MazeType.RANDOM, challenges));
        }

        // Étapes 4, 5, 6 = PerfectMaze
        for (int numLevel = 3; numLevel < 6; numLevel++) {
            List<Challenge> challenges = new ArrayList<>();
            challenges.add(new Challenge(Difficulty.EASY, getRandomHeight(numLevel), getRandomWidth(numLevel), getParameterThree(numLevel, Difficulty.EASY)));
            challenges.add(new Challenge(Difficulty.MEDIUM, getRandomHeight(numLevel), getRandomWidth(numLevel), getParameterThree(numLevel, Difficulty.MEDIUM)));
            challenges.add(new Challenge(Difficulty.HARD, getRandomHeight(numLevel), getRandomWidth(numLevel), getParameterThree(numLevel, Difficulty.HARD)));
            allLevels.add(new Level(numLevel + 1, MazeType.PERFECT, challenges));
        }

        return allLevels;
    }

    public List<Level> getLevels() {
        return this.levels;
    }

    public Level getLevel(int index) {
        return this.levels.get(index);
    }

    @Override
    public Maze getMaze() {
        return this.maze;
    }   

    @Override
    public String getModeName() {
        return "Mode Progression";
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public int getCurrentChallengeIndex() {
        return currentChallengeIndex;
    }

    public Difficulty getCurrentChallengeDifficulty() {
        return getCurrentChallenge().getDifficulty();
    }

    @Override
    public Profil getPlayer() {
        return this.player;
    }

    // Pour changer d'étape ou de défi
    public void selectLevelAndChallenge(int lvl, int ch) {
        if (lvl < 0 || lvl >= levels.size()) {
            throw new IndexOutOfBoundsException("Index level out of bounds");
        }
        if (ch < 0 || ch >= levels.get(lvl).getChallenges().size()) {
            throw new IndexOutOfBoundsException("Index challenge out of bounds");
        }
        this.currentLevelIndex = lvl;
        this.currentChallengeIndex = ch;
        prepareMaze(); // régénère automatiquement le bon maze adapté
    }

    public Level getCurrentLevel() { return levels.get(currentLevelIndex); }
    public Challenge getCurrentChallenge() { return getCurrentLevel().getChallenge(currentChallengeIndex); }

    // Méthodes utilitaires privées pour chaque paramètre (tu peux les adapter si besoin)
    private int getRandomHeight(int numLevel) {
        Random rand = new Random();
        if (numLevel == 0) return rand.nextInt(15) + 15;
        if (numLevel == 1) return rand.nextInt(15) + 30;
        if (numLevel == 2) return rand.nextInt(15) + 30;
        if (numLevel == 3) return rand.nextInt(4) + 8;
        if (numLevel == 4) return rand.nextInt(10) + 20;
        if (numLevel == 5) return rand.nextInt(10) + 20;
        return 10; // Fallback
    }

    private int getRandomWidth(int numLevel) {
        Random rand = new Random();
        if (numLevel == 0) return rand.nextInt(15) + 15;
        if (numLevel == 1) return rand.nextInt(15) + 30;
        if (numLevel == 2) return rand.nextInt(15) + 30;
        if (numLevel == 3) return rand.nextInt(4) + 8;
        if (numLevel == 4) return rand.nextInt(10) + 20;
        if (numLevel == 5) return rand.nextInt(10) + 20;
        return 15; // Fallback
    }

    private int getParameterThree(int numLevel, Difficulty difficulty) {
        if (numLevel >= 0 && numLevel < 3) { // RandomMaze
            switch (difficulty) {
                case EASY: return 40;
                case MEDIUM: return 48;
                case HARD: return 56;
            }
        } else if (numLevel >= 3 && numLevel < 6) { // PerfectMaze
            if (numLevel == 3) {
                switch (difficulty) {
                    case EASY: return 7;
                    case MEDIUM: return 11;
                    case HARD: return 15;
                }
            } else {
                switch (difficulty) {
                    case EASY: return 17;
                    case MEDIUM: return 25;
                    case HARD: return 35;
                }
            }
        }
        return 30; // Fallback par défaut
    }

    
}
