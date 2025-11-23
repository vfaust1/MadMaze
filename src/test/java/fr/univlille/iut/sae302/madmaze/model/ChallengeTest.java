package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.Challenge;
import main.java.fr.univlille.iut.sae302.madmaze.model.Difficulty;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {

    private Challenge challenge;
    private final Difficulty TEST_DIFFICULTY = Difficulty.EASY; 
    private final int TEST_WIDTH = 20;
    private final int TEST_HEIGHT = 15;
    private final int TEST_PARAM = 50;

    @BeforeEach
    void setUp() {
        challenge = new Challenge(TEST_DIFFICULTY, TEST_WIDTH, TEST_HEIGHT, TEST_PARAM);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(TEST_DIFFICULTY, challenge.getDifficulty());
        assertEquals(TEST_WIDTH, challenge.getWidth());
        assertEquals(TEST_HEIGHT, challenge.getHeight());
        assertEquals(TEST_PARAM, challenge.getMazeParam());
    }

    @Test
    void testInitialState() {
        assertFalse(challenge.isValid());
    }

    @Test
    void testValidateChallenge() {
        challenge.validateChallenge();
        assertTrue(challenge.isValid());
    }

    @Test
    void testToStringNotValidated() {
        String result = challenge.toString();
        assertTrue(result.contains(TEST_DIFFICULTY.toString()));
        assertTrue(result.contains(TEST_HEIGHT + "x" + TEST_WIDTH));
        assertTrue(result.contains("param: " + TEST_PARAM));
        assertFalse(result.contains("[VALIDÉ]"));
    }

    @Test
    void testToStringValidated() {
        challenge.validateChallenge();
        String result = challenge.toString();
        assertTrue(result.contains("[VALIDÉ]"));
    }
}