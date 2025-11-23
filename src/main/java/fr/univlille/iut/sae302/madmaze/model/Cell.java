package main.java.fr.univlille.iut.sae302.madmaze.model;

public interface Cell {

    int getRow();
    int getCol();
    
    void setFuel();
    void setSand();

    boolean isWall();
    boolean isFuel();
    boolean isSand();
    boolean isPath();
}
