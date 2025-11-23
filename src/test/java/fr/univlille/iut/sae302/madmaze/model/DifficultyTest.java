package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.Difficulty;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class DifficultyTest {
    @Test
    void testEnumProperties() {
        assertEquals("Facile", Difficulty.EASY.getLabel());
        assertEquals(1, Difficulty.EASY.getLevel());
        assertEquals("Moyen", Difficulty.MEDIUM.getLabel());
        assertEquals(2, Difficulty.MEDIUM.getLevel());
        assertEquals("Difficile", Difficulty.HARD.getLabel());
        assertEquals(3, Difficulty.HARD.getLevel());
    }

    @Test
    void testToString() {
        assertEquals("Facile", Difficulty.EASY.toString());
        assertEquals("Moyen", Difficulty.MEDIUM.toString());
        assertEquals("Difficile", Difficulty.HARD.toString());
    }

    @Test
    void testFromLevelValid() {
        assertEquals(Difficulty.EASY, Difficulty.fromLevel(1));
        assertEquals(Difficulty.MEDIUM, Difficulty.fromLevel(2));
        assertEquals(Difficulty.HARD, Difficulty.fromLevel(3));
    }

    @Test
    void testFromLevelInvalid() {
        Exception exceptionLow = assertThrows(IllegalArgumentException.class, () -> {
            Difficulty.fromLevel(0);
        });
        assertEquals("Niveau inconnu", exceptionLow.getMessage());
        Exception exceptionHigh = assertThrows(IllegalArgumentException.class, () -> {
            Difficulty.fromLevel(4);
        });
        assertEquals("Niveau inconnu", exceptionHigh.getMessage());
    }
}