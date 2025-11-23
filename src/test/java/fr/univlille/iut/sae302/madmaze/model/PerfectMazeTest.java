package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;
import java.util.List;

import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.Cell;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectCell;

public class PerfectMazeTest {
    private PerfectMaze maze;

    @BeforeEach
    public void setUp() {
        maze = new PerfectMaze(8, 12, 5); // width, height, minLength
    }

    @Test
    public void testConstructor() {
        assertEquals(8, maze.getWidth());
        assertEquals(12, maze.getHeight());
        assertNotNull(maze.getStart());
        assertNotNull(maze.getEnd());
        assertNotEquals(maze.getStart(), maze.getEnd());
        assertThrows(IllegalArgumentException.class, () -> {
            new PerfectMaze(1, 2, 5); // width < 2
        });
    }

    @Test
    public void testGridStartEnd() {
        assertNotNull(maze.getStart());
        assertNotNull(maze.getEnd());
        assertNotEquals(maze.getStart(), maze.getEnd());
    }

    @Test 
    public void testOptimalPath() {
        List<Cell> path = maze.findOptimalPath(maze.getStart(), maze.getEnd());
        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertEquals(maze.getStart(), path.get(0));
        assertEquals(maze.getEnd(), path.get(path.size()-1));
        assertTrue(path.size() >= maze.getMinLength());

    }

    @Test
    public void testNeighborsValidCells() {
        // On choisit une cellule intérieure
        Cell cell = maze.getCell(3, 4);
        List<Cell> voisins = maze.neighbors(cell.getRow(), cell.getCol());

        assertNotNull(voisins);
        assertTrue(voisins.size() >= 1 && voisins.size() <= 4);

        // Tous les voisins doivent être adjacents orthogonalement
        for (Cell voisin : voisins) {
            int dRow = Math.abs(voisin.getRow() - cell.getRow());
            int dCol = Math.abs(voisin.getCol() - cell.getCol());
            assertTrue((dRow == 1 && dCol == 0) || (dRow == 0 && dCol == 1));
        }
    }

    @Test
    public void testWallBetweenAdjacentCells() {
        Cell cellA = maze.getCell(2, 2);
        Cell cellB = maze.getCell(2, 3);

        boolean wallAB = maze.isWall(cellA.getRow(), cellA.getCol(), cellB.getRow(), cellB.getCol());
        boolean wallBA = maze.isWall(cellB.getRow(), cellB.getCol(), cellA.getRow(), cellA.getCol());
        assertEquals(wallAB, wallBA);

        Cell cellC = maze.getCell(2, 5);
        assertThrows(IllegalArgumentException.class, () -> maze.isWall(cellA.getRow(), cellA.getCol(), cellC.getRow(), cellC.getCol()));
    }

    @Test
    public void testIsDirectlyReachable() {
        Cell cellA = maze.getCell(4, 5);
        Cell cellB = maze.getCell(4, 6);

        if (!maze.isWall(cellA.getRow(), cellA.getCol(), cellB.getRow(), cellB.getCol())) {
            assertTrue(maze.isDirectlyReachable(cellA, cellB));
            assertTrue(maze.isDirectlyReachable(cellB, cellA));
        } else {
            assertFalse(maze.isDirectlyReachable(cellA, cellB));
            assertFalse(maze.isDirectlyReachable(cellB, cellA));
        }

        Cell cellC = maze.getCell(4, 8);
        assertFalse(maze.isDirectlyReachable(cellA, cellC));
    }
    
}
