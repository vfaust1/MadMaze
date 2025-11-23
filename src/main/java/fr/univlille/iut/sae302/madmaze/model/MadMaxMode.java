package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.Timer;

public class MadMaxMode implements Mode {

    protected Maze maze;
    protected MazeType mazeType;
    protected int width;
    protected int height;
    protected int parameter3;
    protected Cell savedEnd; // Sauvegarde de la sortie pour qu'elle ne change pas

    public MadMaxMode() {
        this.mazeType = MazeType.PERFECT;
    }
    
    @Override
    public String getModeName() {
        return "MadMax Mode";
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void setMaze(MazeType mazeType, int width, int height, int parameter3) {
        this.mazeType = mazeType;
        this.width = width;
        this.height = height;
        this.parameter3 = parameter3;
    }

    @Override
    public void prepareMaze() {
        switch(mazeType) {
            case RANDOM:
                maze = new RandomMaze(width, height, parameter3);
                break;
            case PERFECT:
                maze = new PerfectMaze(width, height, parameter3);
                break;
        }
        // Sauvegarder la position de la sortie initiale
        if (maze != null) {
            savedEnd = maze.getEnd();
        }
    }

    public void changeWalls(){
        if (maze != null) {
            switch(mazeType) {
                case RANDOM:
                    maze = new RandomMaze(width, height, parameter3);
                    break;
                case PERFECT:
                    maze = new PerfectMaze(width, height, parameter3);
                    break;
            }
            // Restaurer la sortie sauvegardée
            if (savedEnd != null) {
                maze.setEnd(savedEnd);
            }
        }
    }

}
