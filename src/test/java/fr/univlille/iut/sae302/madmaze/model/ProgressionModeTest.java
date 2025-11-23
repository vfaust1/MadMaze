package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.Challenge;
import main.java.fr.univlille.iut.sae302.madmaze.model.Difficulty;
import main.java.fr.univlille.iut.sae302.madmaze.model.Level;
import main.java.fr.univlille.iut.sae302.madmaze.model.Maze;
import main.java.fr.univlille.iut.sae302.madmaze.model.MazeType;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.Profil;
import main.java.fr.univlille.iut.sae302.madmaze.model.ProgressionMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomMaze;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ProgressionModeTest {

    private ProgressionMode progressionMode;
    private Profil player;

    @BeforeEach
    void setUp() {
        player = new Profil("TestPlayer","test","test");
        progressionMode = new ProgressionMode(player);
    }

    @Test
    void testInitializationStructure() {
        assertEquals(player, progressionMode.getPlayer());
        List<Level> levels = progressionMode.getLevels();
        assertNotNull(levels);
        assertEquals(6, levels.size());
        Level level1 = levels.get(0);
        assertEquals(3, level1.getChallenges().size());
    }

    @Test
    void testLevelTypesConfiguration() {
        List<Level> levels = progressionMode.getLevels();
        for (int i = 0; i < 3; i++) {
            assertEquals(MazeType.RANDOM, levels.get(i).getMazeType());
        }
        for (int i = 3; i < 6; i++) {
            assertEquals(MazeType.PERFECT, levels.get(i).getMazeType());
        }
    }

    @Test
    void testChallengeDifficultyOrder() {
        for (Level level : progressionMode.getLevels()) {
            List<Challenge> challenges = level.getChallenges();
            assertEquals(Difficulty.EASY, challenges.get(0).getDifficulty());
            assertEquals(Difficulty.MEDIUM, challenges.get(1).getDifficulty());
            assertEquals(Difficulty.HARD, challenges.get(2).getDifficulty());
        }
    }

    @Test
    void testSelectionAndMazeGenerationRandom() {
        progressionMode.selectLevelAndChallenge(0, 1);
        assertEquals(0, progressionMode.getCurrentLevelIndex());
        assertEquals(1, progressionMode.getCurrentChallengeIndex());
        assertEquals(Difficulty.MEDIUM, progressionMode.getCurrentChallengeDifficulty());
        Maze maze = progressionMode.getMaze();
        assertNotNull(maze);
        assertTrue(maze instanceof RandomMaze);
    }

    @Test
    void testSelectionAndMazeGenerationPerfect() {
        progressionMode.selectLevelAndChallenge(3, 2);
        assertEquals(3, progressionMode.getCurrentLevelIndex());
        assertEquals(2, progressionMode.getCurrentChallengeIndex());
        assertEquals(Difficulty.HARD, progressionMode.getCurrentChallengeDifficulty());
        Maze maze = progressionMode.getMaze();
        assertNotNull(maze);
        assertTrue(maze instanceof PerfectMaze);
    }

    @Test
    void testRandomDimensionsValidity() {
        for (Level level : progressionMode.getLevels()) {
            for (Challenge challenge : level.getChallenges()) {
                assertTrue(challenge.getWidth() > 0);
                assertTrue(challenge.getHeight() > 0);
                assertTrue(challenge.getMazeParam() > 0);
            }
        }
    }
    
    @Test
    void testParameterThreeValues() {
        progressionMode.selectLevelAndChallenge(0, 0);
        assertEquals(40, progressionMode.getCurrentChallenge().getMazeParam());
        progressionMode.selectLevelAndChallenge(3, 0);
        assertEquals(7, progressionMode.getCurrentChallenge().getMazeParam());
    }
}