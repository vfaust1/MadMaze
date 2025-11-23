package main.java.fr.univlille.iut.sae302.madmaze.model;

/**
 * Représente une cellule d'un labyrinthe parfait.
 * Une PerfectCell est caractérisée par sa position (ligne, colonne) et un éventuel joueur présent dessus.
 */
public class PerfectCell implements Cell {

    private int row;
    private int col;
    private CellType cellType;

    public PerfectCell(int row, int col) {
        this.row = row;
        this.col = col;
        this.cellType = CellType.PATH;
    }

    public PerfectCell(int row, int col, CellType celltype) {
        this.row = row;
        this.col = col;
        this.cellType = celltype;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public CellType getCellType() {
        return this.cellType;
    }

    public void setFuel() {
        this.cellType = CellType.FUEL;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PerfectCell other = (PerfectCell) obj;
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public int hashCode() {
        return 31 * this.row + this.col;
    }

    @Override
    public String toString() {
        return "(" + this.row + ", " + this.col + ")";
    }

    @Override
    public boolean isWall() {
        return this.cellType == CellType.WALL;
    }

    @Override
    public boolean isFuel() {
        return this.cellType == CellType.FUEL;
    }

    public void setPath() {
        this.cellType = CellType.PATH;
    }

    /**
     * Définit la cellule comme recouverte de sable (tempête).
     * Change le type de la cellule en SAND.
     */
    public void setSand() {
        this.cellType = CellType.SAND;
    }

    /**
     * Vérifie si la cellule est recouverte de sable.
     *
     * @return true si la cellule est de type SAND, false sinon
     */
    public boolean isSand() {
        return this.cellType == CellType.SAND;
    }

    @Override
    public boolean isPath() {
        return true;
    }
}
