package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.Maze;
import main.java.fr.univlille.iut.sae302.madmaze.model.MazeType;
import main.java.fr.univlille.iut.sae302.madmaze.model.NightMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomMaze;

import static org.junit.jupiter.api.Assertions.*;

class NightModeTest {

    private NightMode nightMode;

    @BeforeEach
    void setUp() {
        nightMode = new NightMode();
    }

    @Test
    void testPrepareRandomMaze() {
        nightMode.setMaze(MazeType.RANDOM, 12, 14, 50);
        nightMode.prepareMaze();
        Maze maze = nightMode.getMaze();
        assertNotNull(maze);
        assertTrue(maze instanceof RandomMaze);
        assertEquals(12, maze.getWidth());
        assertEquals(14, maze.getHeight());
    }

    @Test
    void testPreparePerfectMaze() {
        nightMode.setMaze(MazeType.PERFECT, 10, 10, 1);
        nightMode.prepareMaze();
        Maze maze = nightMode.getMaze();
        assertNotNull(maze);
        assertTrue(maze instanceof PerfectMaze);
        assertEquals(10, maze.getWidth());
        assertEquals(10, maze.getHeight());
    }

    @Test
    void testIndividualSetters() {
        nightMode.setMaze(MazeType.RANDOM, 5, 5, 10);
        nightMode.setWidth(20);
        nightMode.setHeight(25);
        nightMode.setParameter3(30);
        nightMode.prepareMaze();
        Maze maze = nightMode.getMaze();
        assertEquals(20, maze.getWidth());
        assertEquals(25, maze.getHeight());
    }

    @Test
    void testPrepareWithoutConfigThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            nightMode.prepareMaze();
        });
    }
}