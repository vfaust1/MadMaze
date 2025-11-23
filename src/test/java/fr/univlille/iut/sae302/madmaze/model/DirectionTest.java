package test.java.fr.univlille.iut.sae302.madmaze.model;

import org.junit.jupiter.api.Test;

import main.java.fr.univlille.iut.sae302.madmaze.model.Cell;
import main.java.fr.univlille.iut.sae302.madmaze.model.Direction;
import main.java.fr.univlille.iut.sae302.madmaze.model.PerfectCell;
import main.java.fr.univlille.iut.sae302.madmaze.model.RandomCell;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void testDirectionZ() {
        Direction d = Direction.Z;
        assertEquals("top", d.getDirection());
        assertEquals(0, d.getDx(), "Dx should be 0 for Top");
        assertEquals(-1, d.getDy(), "Dy should be -1 for Top");
    }

    @Test
    void testDirectionQ() {
        Direction d = Direction.Q;
        assertEquals("left", d.getDirection());
        assertEquals(-1, d.getDx(), "Dx should be -1 for Left");
        assertEquals(0, d.getDy(), "Dy should be 0 for Left");
    }

    @Test
    void testDirectionS() {
        Direction d = Direction.S;
        assertEquals("bottom", d.getDirection());
        assertEquals(0, d.getDx(), "Dx should be 0 for Bottom");
        assertEquals(1, d.getDy(), "Dy should be 1 for Bottom");
    }

    @Test
    void testDirectionD() {
        Direction d = Direction.D;
        assertEquals("right", d.getDirection());
        assertEquals(1, d.getDx(), "Dx should be 1 for Right");
        assertEquals(0, d.getDy(), "Dy should be 0 for Right");
    }

    @Test
    void testAllDirections() {
        Direction[] dirs = Direction.allDirections();
        assertEquals(4, dirs.length);
        assertArrayEquals(new Direction[]{Direction.Z, Direction.Q, Direction.S, Direction.D}, dirs);
    }

    @Test
    void testNextCellPerfect() {
        PerfectCell startCell = new PerfectCell(10, 10);
        Cell resultRight = Direction.D.nextCell(startCell);
        assertTrue(resultRight instanceof PerfectCell);
        assertEquals(10, resultRight.getRow());
        assertEquals(11, resultRight.getCol());
        Cell resultUp = Direction.Z.nextCell(startCell);
        assertEquals(9, resultUp.getRow());
        assertEquals(10, resultUp.getCol());
    }

    @Test
    void testNextCellRandom() {
        RandomCell startCell = new RandomCell(5, 5, null);
        Cell resultDown = Direction.S.nextCell(startCell);
        assertTrue(resultDown instanceof RandomCell);
        assertEquals(6, resultDown.getRow());
        assertEquals(5, resultDown.getCol());
        Cell resultLeft = Direction.Q.nextCell(startCell);
        assertEquals(5, resultLeft.getRow());
        assertEquals(4, resultLeft.getCol());
    }
}