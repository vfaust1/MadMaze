package main.java.fr.univlille.iut.sae302.madmaze.model;

public class NightMode implements Mode {

    protected Maze maze;
    protected MazeType mazeType;
    protected int width;
    protected int height;
    protected int parameter3;

    public NightMode() {} 

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void prepareMaze() {
        switch(mazeType) {
            case RANDOM:
                // Le paramètre wallPercentage peut être ajusté par l'utilisateur
                this.maze = new RandomMaze(width, height, parameter3);
                break;
            case PERFECT:
                // Parfait = chemin unique. minLength = longueur min du chemin solution (pour chalenge supplémentaire)
                this.maze = new PerfectMaze(width, height, parameter3);
                break;
        }

    }

    @Override
    public void setMaze(MazeType mazeType, int width, int height, int parameter3) {
        this.mazeType = mazeType;
        this.width = width;
        this.height = height;
        this.parameter3 = parameter3;
    }

    @Override
    public String getModeName() {
        return "Night Mode";
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setParameter3(int parameter3) {
        this.parameter3 = parameter3;
    }
}
