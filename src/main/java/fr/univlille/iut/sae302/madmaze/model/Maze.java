package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.List;

public interface Maze {

    void generateMaze();

    boolean isDirectlyReachable(Cell c1, Cell c2);
    boolean isValidPosition(int row, int col);

    List<Cell> findOptimalPath(Cell start, Cell end);

    void setStart(Cell start);
    void setEnd(Cell end);

    Cell getStart();
    Cell getEnd();

    int getWidth();
    int getHeight();

    Cell[][] getGrid();
    Cell getCell(int y, int x);

    boolean isEnd(Cell cell);

    int optimalPathLength();

    void setHeight(int i);
    void setWidth(int i);

}
