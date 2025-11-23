package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import main.java.fr.univlille.iut.sae302.madmaze.model.CellType;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomCell;

public class RandomCellTest {

    private RandomCell cell;

    @BeforeEach
    public void setUp() {
        cell = new RandomCell(4, 3); 
    }
    @Test
    public void testConstructor() {
        RandomCell cell2 = new RandomCell(0, 0, CellType.WALL);
        assertEquals(4, cell.getRow());
        assertEquals(3, cell.getCol());
        assertEquals(CellType.PATH, cell.getCellType());
        assertEquals(CellType.WALL, cell2.getCellType());
    }

    @Test
    public void testSetters() {
        cell.setWall();
        assertEquals(CellType.WALL, cell.getCellType());
        assertTrue(cell.isWall());
        assertFalse(cell.isFuel());

        cell.setFuel();
        assertEquals(CellType.FUEL, cell.getCellType());
        assertTrue(cell.isFuel());
        assertFalse(cell.isWall());

        cell.setPath();
        assertEquals(CellType.PATH, cell.getCellType());
        assertFalse(cell.isWall());
        assertFalse(cell.isFuel());
    }  

    @Test
    public void testEqualsAndHashCode() {
        RandomCell cell2 = new RandomCell(cell.getRow(), cell.getCol(), cell.getCellType());
        assertEquals(cell, cell2);
        assertEquals(cell.hashCode(), cell2.hashCode());

        RandomCell cell3 = new RandomCell(888, 999);
        assertNotEquals(cell, cell3);
        assertNotEquals(cell.hashCode(), cell3.hashCode());
    }

}
