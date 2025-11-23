package test.java.fr.univlille.iut.sae302.madmaze.model;

import main.java.fr.univlille.iut.sae302.madmaze.model.Cell;
import main.java.fr.univlille.iut.sae302.madmaze.model.Direction;
import main.java.fr.univlille.iut.sae302.madmaze.model.FreeMode;
import main.java.fr.univlille.iut.sae302.madmaze.model.Game;
import main.java.fr.univlille.iut.sae302.madmaze.model.Maze;
import main.java.fr.univlille.iut.sae302.madmaze.model.MazeType;
import main.java.fr.univlille.iut.sae302.madmaze.model.Observable;
import main.java.fr.univlille.iut.sae302.madmaze.view.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private FreeMode mode;

    class TestObserver implements Observer {
        boolean notified = false;
        @Override
        public void update(Observable subject) {
            this.notified = true;
        }
    }

    @BeforeEach
    void setUp() {
        mode = new FreeMode();
        mode.setMaze(MazeType.RANDOM, 10, 10, 20);
        game = new Game(mode);
    }

    @Test
    void testInitialization() {
        assertNotNull(game.getMaze());
        assertNotNull(game.getMaze().getGrid());
        Cell start = game.getMaze().getStart();
        assertNotNull(start);
        assertEquals(start, game.getPlayerPosition());
        assertEquals(0, game.getMoveCount());
        assertTrue(game.isExplored(start));
    }

    @Test
    void testMovePlayerReal() {
        
        Cell currentPos = game.getPlayerPosition();
        Maze maze = game.getMaze();
        boolean moved = false;

        for (Direction dir : Direction.values()) {
            int newRow = currentPos.getRow() + dir.getDy();
            int newCol = currentPos.getCol() + dir.getDx();
            if (newRow >= 0 && newRow < maze.getHeight() && newCol >= 0 && newCol < maze.getWidth()) {
                Cell target = maze.getCell(newRow, newCol);
                if (maze.isDirectlyReachable(currentPos, target)) {
                    boolean result = game.movePlayer(dir);
                    
                    assertTrue(result);
                    assertEquals(target, game.getPlayerPosition());
                    assertEquals(1, game.getMoveCount());
                    moved = true;
                    break;
                }
            }
        }
        if (!moved) {
            System.out.println("Warning: Cellule de départ entouré par des murs de chaques côtés");
        }
    }

    @Test
    void testMoveIntoWall() {
        Cell currentPos = game.getPlayerPosition();
        Maze maze = game.getMaze();
        for (Direction dir : Direction.values()) {
            int newRow = currentPos.getRow() + dir.getDy();
            int newCol = currentPos.getCol() + dir.getDx();
            if (newRow < 0 || newRow >= maze.getHeight() || newCol < 0 || newCol >= maze.getWidth()) {
                boolean result = game.movePlayer(dir);
                assertFalse(result);
                assertEquals(currentPos, game.getPlayerPosition());
            }
            else {
                Cell target = maze.getCell(newRow, newCol);
                if (!maze.isDirectlyReachable(currentPos, target)) {
                    boolean result = game.movePlayer(dir);
                    assertFalse(result);
                    assertEquals(0, game.getMoveCount());
                }
            }
        }
    }

    @Test
    void testExploreVision() {
        int initialExploredCount = game.getExploredCells().size();
        game.exploreVision(1);
        
        assertTrue(game.getExploredCells().size() >= initialExploredCount);
    }

    @Test
    void testReset() {
        game.incrementMoveCount(); 
        game.reset();
        assertEquals(0, game.getMoveCount());
        assertEquals(game.getMaze().getStart(), game.getPlayerPosition());
        assertEquals(Direction.D, game.getJoueurDirection());
    }

    @Test
    void testObservers() {
        TestObserver obs = new TestObserver();
        game.addObserver(obs);
        game.notifyObservers();
        assertTrue(obs.notified);
        obs.notified = false;
        game.removeObserver(obs);
        game.notifyObservers();
        assertFalse(obs.notified);
    }
}