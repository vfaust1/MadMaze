package main.java.fr.univlille.iut.sae302.madmaze.model;

import java.util.ArrayList;
import java.util.List;

public class Profil {

    public static List<Profil> listePlayers = new ArrayList<>();

    private String name;
    private String pwd;
    private int score = 0;
    private boolean[] challenge = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};


    public Profil(String name, String pwd, String pwd2) throws IllegalArgumentException {

        // 1. Valider les entrées
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        if (!goodName(name)) {
            throw new IllegalArgumentException("Le nom est incorrect.");
        }
        if (pwd == null || pwd.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }
        if (!pwd.equals(pwd2)) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        // 2. Assigner les champs
        this.name = name;
        this.pwd = pwd;
    }

    public String getName() {
        return this.name;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return this.pwd;
    }

    public boolean[] getChallenge() {
        return this.challenge;
    }

    public boolean goodName(String name) {
        for(Profil p : listePlayers) {
            if(p.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public void finishChallenge(int step,int challenge){
        if(step==0){
            finishChallenge(challenge);
        }
        if(step==1){
            finishChallenge(challenge+3);
        }
        if(step==2){
            finishChallenge(challenge+6);
        }
        if(step==3){
            finishChallenge(challenge+9);
        }
        if(step==4){
            finishChallenge(challenge+12);
        }
        if(step==5){
            finishChallenge(challenge+15);
        }
    }

    public void finishChallenge(int challenge){
        this.challenge[challenge] =  true;
    }

    public boolean hasPermission(int step) {
        if(step == 1) {
            return true;
        }
        if(step == 2) {
            if(this.challenge[0] || this.challenge[1] || this.challenge[2]) {
                return true;
            }
        }
        if(step == 3) {
            if(this.challenge[3] || this.challenge[4] || this.challenge[5]) {
                return true;
            }
        }
        if(step == 4) {
            if(this.challenge[6] || this.challenge[7] || this.challenge[8]) {
                return true;
            }
        }
        if(step == 5) {
            if(this.challenge[9] || this.challenge[10] || this.challenge[11]) {
                return true;
            }
        }
        if(step == 6) {
            if(this.challenge[12] || this.challenge[13] || this.challenge[14]) {
                return true;
            }
        }

        return false;
    }

    public String getLastValidate() {

        // Parcourt le tableau à l'envers pour trouver la dernière case validée.
        // Le tableau 'challenge' est organisé par groupes de 3 difficultés par étape.
        // Avec 6 étapes, on a 6 * 3 = 18 entrées (indices 0..17).
        for (int i = challenge.length - 1; i >= 0; i--) {
            if (challenge[i]) {
                // Calculer l'étape (1-indexée) et la difficulté (1..3)
                int etape = (i / 3) + 1; // chaque 3 indices correspondent à une étape
                return etape + 1 + " ";
            }
        }

        // Aucun niveau terminé
        return "Aucun";
    }

}
