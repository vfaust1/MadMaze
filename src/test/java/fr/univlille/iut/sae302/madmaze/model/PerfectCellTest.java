package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.CellType;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectCell;

import static org.junit.jupiter.api.Assertions.*;

public class PerfectCellTest {

    private PerfectCell cell;

    @BeforeEach
    public void setUp() {
        cell = new PerfectCell(2, 3);
    }

    @Test
    public void testConstructor() {
        assertEquals(2, cell.getRow());
        assertEquals(3, cell.getCol());
        assertEquals(CellType.PATH, cell.getCellType());

        PerfectCell cell2 = new PerfectCell(1, 1, CellType.FUEL);
        assertEquals(CellType.FUEL, cell2.getCellType());
    }

    @Test
    public void testSetters() {
        cell.setFuel();
        assertEquals(CellType.FUEL, cell.getCellType());
        assertTrue(cell.isFuel());

        cell.setPath();
        assertEquals(CellType.PATH, cell.getCellType());
        assertFalse(cell.isFuel());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        PerfectCell same = new PerfectCell(2, 3, CellType.PATH);
        PerfectCell diff = new PerfectCell(4, 5);
        assertEquals(cell, same);
        assertEquals(cell.hashCode(), same.hashCode());
        assertNotEquals(cell, diff);
    }
}
