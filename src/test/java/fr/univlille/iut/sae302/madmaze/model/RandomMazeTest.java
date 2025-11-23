package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

import main.java.fr.univlille.iut.sae302.madmaze.model.CellType;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomCell;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomMaze;
import main.java.fr.univlille.iut.sae302.madmaze.model.Cell;

public class RandomMazeTest {

    private RandomMaze maze;

    @BeforeEach
    public void setUp() {
        maze = new RandomMaze(8, 12, 10); 
    }

    @Test
    public void testConstructor() {
        assertEquals(8, maze.getWidth());
        assertEquals(12, maze.getHeight());
        assertEquals(10, maze.getWallPercentage());
        assertNotNull(maze.getGrid());
        assertEquals(12, maze.getGrid().length);
        assertEquals(8, maze.getGrid()[0].length); 
    }

    @Test
    public void testGridStartEnd() {
        assertNotNull(maze.getStart());
        assertNotNull(maze.getEnd());
        assertNotEquals(maze.getStart(), maze.getEnd());
    } 
    
    @Test
    public void testOptimalPath() {
        List<Cell> chemin = maze.findOptimalPath(maze.getStart(), maze.getEnd());
        assertNotNull(chemin);                    
        assertFalse(chemin.isEmpty());
        assertEquals(maze.getStart(), chemin.get(0)); 
        assertEquals(maze.getEnd(), chemin.get(chemin.size()-1));
    }

    @Test
    public void testNeighbors() {
        // Cellule centrale
        Cell centre = maze.getGrid()[5][4];
        var voisinsCentre = maze.neighbors(centre.getRow(), centre.getCol());
        assertTrue(voisinsCentre.size() >= 1); // Il doit y avoir des voisins

        // Cellule en bord (0,0)
        Cell coin = maze.getGrid()[0][0];
        var voisinsCoin = maze.neighbors(coin.getRow(), coin.getCol());
        // En bord, il ne doit pas y avoir plus de 2 voisins
        assertTrue(voisinsCoin.size() <= 2);
    }

    @Test
    public void testValidPosition() {
        assertTrue(maze.isValidPosition(0, 0)); // Coin
        assertTrue(maze.isValidPosition(maze.getHeight()-1, maze.getWidth()-1)); // Bord opposé (row, col)
        assertFalse(maze.isValidPosition(-1, 0)); // Négatif
        assertFalse(maze.isValidPosition(maze.getHeight(), maze.getWidth())); // Hors bornes
    }

    @Test
    public void testSetWall() {
        maze.setWall(2, 3);
        assertTrue(maze.getGrid()[2][3].isWall());
    }

    @Test
    public void testAdjacentCells() {
        Cell cellA = maze.getGrid()[2][2];
        Cell cellB = maze.getGrid()[2][3]; // à droite
        Cell cellC = maze.getGrid()[2][4]; // loin

        assertTrue(maze.adjacent(cellA, cellB));  // Devraient être voisins
        assertFalse(maze.adjacent(cellA, cellC)); // Pas voisins
    }

    @Test
    public void testIsDirectlyReachable() {
        RandomCell cellA = (RandomCell) maze.getGrid()[2][2];
        RandomCell cellB = (RandomCell) maze.getGrid()[2][3];

        // Pour être sur qu'ils ne sont pas murs
        cellA.setPath();
        cellB.setPath();
        assertTrue(maze.isDirectlyReachable(cellA, cellB));

        cellB.setWall();
        assertFalse(maze.isDirectlyReachable(cellA, cellB)); // Un mur bloque le passage

        // Non adjacent
        RandomCell cellC = (RandomCell) maze.getGrid()[2][4];
        assertFalse(maze.isDirectlyReachable(cellA, cellC));
    }









    
}
