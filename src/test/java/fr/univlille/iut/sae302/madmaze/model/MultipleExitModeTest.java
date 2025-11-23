package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.Cell;
import main.java.fr.univlille.iut.sae302.madmaze.model.MazeType;
import main.java.fr.univlille.iut.sae302.madmaze.model.MultipleExitMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomMaze;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class MultipleExitModeTest {

    private MultipleExitMode multiMode;
    private final int WIDTH = 20;
    private final int HEIGHT = 20;

    @BeforeEach
    void setUp() {
        multiMode = new MultipleExitMode();
        multiMode.setMaze(MazeType.RANDOM, WIDTH, HEIGHT, 50);
    }

    @Test
    void testPrepareMazeRandom() {
        multiMode.setMaze(MazeType.RANDOM, 10, 10, 20);
        multiMode.prepareMaze();
        assertNotNull(multiMode.getMaze());
        assertTrue(multiMode.getMaze() instanceof RandomMaze);
        assertEquals(10, multiMode.getMaze().getWidth());
    }

    @Test
    void testPrepareMazePerfect() {
        multiMode.setMaze(MazeType.PERFECT, 15, 15, 1);
        multiMode.prepareMaze();
        assertNotNull(multiMode.getMaze());
        assertTrue(multiMode.getMaze() instanceof PerfectMaze);
        assertEquals(15, multiMode.getMaze().getWidth());
    }

    @Test
    void testFalseExitsGenerationDefault() {
        multiMode.prepareMaze();
        Cell[] exits = multiMode.getFalseExit();
        assertNotNull(exits);
        assertEquals(3, exits.length);
        for (Cell cell : exits) {
            assertNotNull(cell);
            int r = cell.getRow();
            int c = cell.getCol();
            boolean isOnBorder = (r == 0 || r == HEIGHT - 1 || c == 0 || c == WIDTH - 1);
            assertTrue(isOnBorder, "False exit should be on the border of the maze");
        }
    }

    @Test
    void testFalseExitsUniqueness() {
        multiMode.prepareMaze();
        Cell[] exits = multiMode.getFalseExit();
        Set<Cell> uniqueExits = new HashSet<>(Arrays.asList(exits));
        assertEquals(exits.length, uniqueExits.size(), "All generated false exits should be unique cells");
    }

    @Test
    void testGenerateCustomNumberOfFalseExits() {
        multiMode.prepareMaze();
        multiMode.generateFalseExits(5);
        Cell[] exits = multiMode.getFalseExit();
        assertEquals(5, exits.length);
        for (Cell cell : exits) {
            assertNotNull(cell);
        }
    }

    @Test
    void testGenerateFalseExitsWithoutMazeThrowsException() {
        MultipleExitMode emptyMode = new MultipleExitMode();
        emptyMode.setMaze(MazeType.RANDOM, 10, 10, 10);
        assertThrows(NullPointerException.class, () -> {
            emptyMode.generateFalseExits(3);
        });
    }
}