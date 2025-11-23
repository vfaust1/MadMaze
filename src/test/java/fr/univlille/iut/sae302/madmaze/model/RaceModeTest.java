package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.Cell;
import main.java.fr.univlille.iut.sae302.madmaze.model.Maze;
import main.java.fr.univlille.iut.sae302.madmaze.model.MazeType;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.RaceMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomMaze;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class RaceModeTest {

    private RaceMode raceMode;

    @BeforeEach
    void setUp() {
        raceMode = new RaceMode();
    }

    @Test
    void testInitialization() {
        assertNull(raceMode.getMaze());
        assertNotNull(raceMode.getBotPerfectPath());
        assertTrue(raceMode.getBotPerfectPath().isEmpty());
    }

    @Test
    void testPrepareMazePerfect() {
        raceMode.setMaze(MazeType.PERFECT, 10, 10, 1);
        raceMode.prepareMaze();
        Maze maze = raceMode.getMaze();
        assertNotNull(maze);
        assertTrue(maze instanceof PerfectMaze);
        List<Cell> path = raceMode.getBotPerfectPath();
        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertEquals(maze.getStart(), path.get(0));
    }

    @Test
    void testPrepareMazeRandom() {
        raceMode.setMaze(MazeType.RANDOM, 15, 15, 40);
        raceMode.prepareMaze();
        Maze maze = raceMode.getMaze();
        assertNotNull(maze);
        assertTrue(maze instanceof RandomMaze);
        List<Cell> path = raceMode.getBotPerfectPath();
        assertNotNull(path);
        assertFalse(path.isEmpty());
    }

    @Test
    void testIsLostLogic() {
        raceMode.setMaze(MazeType.PERFECT, 10, 10, 1);
        raceMode.prepareMaze();
        List<Cell> path = raceMode.getBotPerfectPath();
        int pathSize = path.size();
        int maxMoves = Math.max(0, pathSize - 1);
        assertFalse(raceMode.isLost(0));
        if (maxMoves > 0) {
            assertFalse(raceMode.isLost(maxMoves - 1));
        }
        assertTrue(raceMode.isLost(maxMoves));
        assertTrue(raceMode.isLost(maxMoves + 1));
    }

    @Test
    void testIsLostWithNoPath() {
        assertTrue(raceMode.getBotPerfectPath().isEmpty());
        assertFalse(raceMode.isLost(100));
    }
    
    @Test
    void testSetMazeParameters() {
        raceMode.setMaze(MazeType.RANDOM, 20, 25, 50);
        raceMode.prepareMaze();
        assertEquals(MazeType.RANDOM, raceMode.getMazeType());
        assertEquals(20, raceMode.getMaze().getWidth());
        assertEquals(25, raceMode.getMaze().getHeight());
    }
}