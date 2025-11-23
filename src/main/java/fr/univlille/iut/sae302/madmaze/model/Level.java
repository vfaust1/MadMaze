package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.ArrayList;
import java.util.List;
/**
 * Représente une étape du mode progression contenant trois défis de difficultés croissantes.
 * Chaque étape est numérotée et contient systématiquement un défi facile, moyen et difficile.
 * 
 * @author G4
 */
public class Level {
    private final List<Challenge> challenges;
    private final int number;
    private final MazeType mazeType;

    public Level(int number, MazeType mazeType, List<Challenge> challenges) {
        this.number = number;
        this.mazeType = mazeType;
        this.challenges = challenges;
    }

    public int getNumber() {
        return this.number;
    }

    public List<Challenge> getChallenges() { return new ArrayList<>(challenges); }

    public Challenge getChallenge(int index) {
        if (index < 0 || index >= challenges.size()) throw new IllegalArgumentException("Index challenge out of bounds");
        return challenges.get(index);
    }

    public MazeType getMazeType() {
        return mazeType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Étape ").append(number)
        .append(" [").append(mazeType).append("] ")
        .append("avec ").append(challenges.size()).append(" défis :\n");
        for (int i = 0; i < challenges.size(); i++) {
            sb.append("  ").append((char)('A' + i)).append(") ")
            .append(challenges.get(i).toString()).append("\n");
        }
        return sb.toString();
    }

}