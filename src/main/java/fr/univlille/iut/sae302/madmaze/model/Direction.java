package main.java.fr.univlille.iut.sae302.madmaze.model;

/**
 * Représente les quatre directions de déplacement possibles dans le labyrinthe.
 * Chaque direction est associée à une touche du clavier (ZQSD) et contient
 * les modifications de coordonnées correspondantes.
 * 
 * @author G4
 */
public enum Direction {
    /**
     * Direction vers le haut, activée par la touche Z.
     * Décrément de la coordonnée y de 1.
     */
    Z("top", 0, -1),
    /**
     * Direction vers la gauche, activée par la touche Q.
     * Décrément de la coordonnée x de 1.
     */
    Q("left", -1, 0),
    /**
     * Direction vers le bas, activée par la touche S.
     * Incrément de la coordonnée y de 1.
     */
    S("bottom", 0, 1),
    /**
     * Direction vers la droite, activée par la touche D.
     * Incrément de la coordonnée x de 1.
     */
    D("right", 1, 0);

    private final String direction;
    private final int dx;
    private final int dy;
 
    /**
     * Construit une direction avec son nom et ses déplacements associés.
     * 
     * @param direction le nom de la direction (top, left, bottom, right)
     * @param dx le déplacement horizontal (-1 pour gauche, 0 pour aucun, 1 pour droite)
     * @param dy le déplacement vertical (-1 pour haut, 0 pour aucun, 1 pour bas)
     */
    Direction(String direction, int dx, int dy) {
        this.direction = direction;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Retourne le nom textuel de la direction.
     * 
     * @return le nom de la direction (top, left, bottom ou right)
     */
    public String getDirection() {
        return this.direction;
    }

    /**
     * Retourne le déplacement horizontal associé à cette direction.
     * 
     * @return -1 pour gauche, 0 pour aucun déplacement horizontal, 1 pour droite
     */
    public int getDx() {
        return this.dx;
    }

    /**
     * Retourne le déplacement vertical associé à cette direction.
     * 
     * @return -1 pour haut, 0 pour aucun déplacement vertical, 1 pour bas
     */
    public int getDy() {
        return this.dy;
    }

    /**
     * Calcule la nouvelle position d'une cellule après un déplacement dans cette direction.
     * 
     * @param cell la cellule de départ
     * @return une nouvelle cellule avec les coordonnées mises à jour selon la direction
     */
    public Cell nextCell(Cell cell) {
        if (cell instanceof RandomCell) {
            RandomCell rc = (RandomCell) cell;
            return new RandomCell(rc.getRow() + dy, rc.getCol() + dx, rc.getCellType());
        } else if (cell instanceof PerfectCell) {
            PerfectCell pc = (PerfectCell) cell;
            return new PerfectCell(pc.getRow() + dy, pc.getCol() + dx);
        } else {
            throw new IllegalArgumentException("type de cellule inconnu");
        }       
    }

    /**
     * Retourne un tableau contenant toutes les directions disponibles.
     * 
     * @return un tableau contenant les quatre directions (Z, Q, S, D)
     */
    public static Direction[] allDirections() {
        return values();
    }       
}
