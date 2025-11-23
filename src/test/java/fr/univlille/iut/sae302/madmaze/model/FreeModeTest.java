package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.FreeMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.Maze;
import main.java.fr.univlille.iut.sae302.madmaze.model.MazeType;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomMaze;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class FreeModeTest {

    private FreeMode freeMode;

    @BeforeEach
    void setUp() {
        freeMode = new FreeMode();
    }

    @Test
    void testSetMaze() {
        freeMode.setMaze(MazeType.RANDOM, 20, 15, 50);
        assertEquals(MazeType.RANDOM, freeMode.getMazeType());
    }

    @Test
    void testPrepareMazeRandom() {
        freeMode.setMaze(MazeType.RANDOM, 10, 10, 40);
        freeMode.prepareMaze();
        Maze resultMaze = freeMode.getMaze();
        assertNotNull(resultMaze);
        assertTrue(resultMaze instanceof RandomMaze);
    }

    @Test
    void testPrepareMazePerfect() {
        freeMode.setMaze(MazeType.PERFECT, 15, 15, 10);
        freeMode.prepareMaze();
        Maze resultMaze = freeMode.getMaze();
        assertNotNull(resultMaze, "Maze should not be null after prepareMaze()");
        assertTrue(resultMaze instanceof PerfectMaze, "The created maze should be an instance of PerfectMaze");
    }

    @Test
    void testPrepareMazeWithoutSetup() {
        assertThrows(NullPointerException.class, () -> {
            freeMode.prepareMaze();
        }, "Calling prepareMaze without setMaze should throw NullPointer because mazeType is null");
    }
}