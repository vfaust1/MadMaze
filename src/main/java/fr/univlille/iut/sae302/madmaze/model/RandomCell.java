package main.java.fr.univlille.iut.sae302.madmaze.model;

/**
 * Représente une cellule dans le labyrinthe avec une position (x, y) et un type.
 * Chaque cellule peut être un chemin, un mur ou contenir le joueur.
 * 
 * @author G4
 */
public class RandomCell implements Cell {
    protected int col;
    protected int row;
    protected CellType cellType;

    /**
     * Construit une cellule à la position spécifiée avec un type PATH par défaut.
     * Initialise une nouvelle cellule vide dans le labyrinthe.
     * 
     * @param row la coordonnée row de la cellule
     * @param col la coordonnée col de la cellule
     */
    public RandomCell(int row, int col) {
        this.col = col;
        this.row = row;
        this.cellType = CellType.PATH;
    }

    /**
     * Construit une cellule à la position spécifiée avec un type donné.
     * Initialise une nouvelle cellule dans le labyrinthe avec le type spécifié.
     * 
     * @param x la coordonnée x de la cellule
     * @param y la coordonnée y de la cellule
     * @param celltype le type de la cellule (PATH, WALL, ou FUEL)
     */
    public RandomCell(int row, int col, CellType celltype) {
        this.col = col;
        this.row = row;
        this.cellType = celltype;
    }

    /**
     * Retourne la coordonnée col de la cellule.
     * 
     * @return la position col de la cellule dans le labyrinthe
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Retourne la coordonnée row de la cellule.
     * 
     * @return la position row de la cellule dans le labyrinthe
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Retourne le type de la cellule.
     * 
     * @return le type de la cellule (PATH, WALL ou PLAYER)
     */
    public CellType getCellType() {
        return this.cellType;
    }

    /**
     * Vérifie si la cellule est un mur.
     * 
     * @return true si la cellule est de type WALL, false sinon
     */
    @Override
    public boolean isWall() {
        return this.cellType == CellType.WALL;
    }

    /**
     * Vérifie si la cellule contient du carburant.
     * @return true si la cellule est de type FUEL, false sinon
     */
    @Override
    public boolean isFuel() {
        return this.cellType == CellType.FUEL;
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


    /**
     * Définit la cellule comme contenant un bidon d'essence.
     * Change le type de la cellule en FUEL.
     */
    public void setFuel() {
        this.cellType = CellType.FUEL;
    }

    /**
     * Définit la cellule comme un mur.
     * Change le type de la cellule en WALL.
     */
    public void setWall() {
        this.cellType = CellType.WALL;
    }

    /**
     * Définit la cellule comme un chemin.
     * Change le type de la cellule en PATH.
     */
    public void setPath() {
        this.cellType = CellType.PATH;
    }

    /**
     * Retourne une représentation textuelle de la cellule.
     * 
     * @return une chaîne de caractères au format "Cell(x, y, status=TYPE)"
     */
    @Override
    public String toString() {
        return "Cell(" + this.col + ", " + this.row + ", status=" + this.cellType + ")";
    }

    /**
     * Compare cette cellule avec un autre objet pour déterminer l'égalité.
     * Deux cellules sont égales si elles ont les mêmes coordonnées col et row.
     * 
     * @param obj l'objet à comparer avec cette cellule
     * @return true si les cellules ont la même position, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RandomCell cell = (RandomCell) obj;
        return col == cell.col && row == cell.row;
    }

    /**
     * Calcule le code de hachage de la cellule basé sur ses coordonnées.
     * 
     * @return le code de hachage de la cellule
     */
    @Override
    public int hashCode() {
        return 31 * col + row;
    }

    @Override
    public boolean isPath() {
        return this.cellType == CellType.PATH;
    }
}
