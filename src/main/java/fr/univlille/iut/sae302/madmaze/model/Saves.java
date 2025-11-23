package main.java.fr.univlille.iut.sae302.madmaze.model;

import static main.java.fr.univlille.iut.sae302.madmaze.model.Profil.listePlayers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe gérant la sauvegarde et le chargement des joueurs dans un fichier CSV.
 */
public class Saves {

    public static final String directory = "src/main/resources/data/";
    public static final String FILENAME = "players.csv";
    public static final String FILE_PATH = directory + FILENAME;
    public static final String[] CSV_HEADERS = {"Name", "pwd", "score", "1-1","1-2","1-3", "2-1","2-2","2-3","3-1","3-2","3-3"};
    public static final int NB_COLS_MIN = 3; // name, pwd, score
    
    static {
        try {
            File dir = new File(directory);
            if (!dir.exists()) dir.mkdirs(); 
            File file = new File(FILE_PATH);
            if (file.createNewFile()) {
                System.out.println("Fichier " + FILENAME + " créé.");
                try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
                    writer.println(String.join(",", CSV_HEADERS));
                }
            } else {
                System.out.println("Fichier " + FILENAME + " déjà existant.");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'initialisation du fichier de sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Ajoute un nouveau joueur au fichier CSV et à la liste en mémoire.
     * @param player Le joueur à créer
     */
    public static boolean createPlayer(Profil player) {

        if (playerExists(player.getName())) {
            System.err.println("Erreur : Le joueur '" + player.getName() + "' existe déjà. Création annulée.");
            return false;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(playerToCSVLine(player));
            System.out.println("Données pour '" + player.getName() + "' ajoutées à " + FILENAME);
            listePlayers.add(player);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ajout de données : " + e.getMessage());
            return false;
        }
    }

    private static String playerToCSVLine(Profil player) {
        // Peut prendre en compte automatiquement le nombre de challenges
        List<String> data = new ArrayList<>();
        data.add(player.getName());
        data.add(player.getPwd());
        data.add("0"); // initial score
        for (int i = 0; i < player.getChallenge().length; ++i) {
            data.add("false");
        }
        return String.join(",", data);
    }

    /**
     * Charge tous les joueurs depuis le fichier CSV dans la liste statique 'listePlayers'.
     * Pensez à appeler cette méthode au démarrage de votre application.
     */
    public static void loadAllPlayers() {
        System.out.println("\n--- Lecture de " + FILENAME + " ---");
        listePlayers.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= NB_COLS_MIN) {
                    Profil player = new Profil(values[0], values[1], values[1]);
                    try {
                        player.setScore(Integer.parseInt(values[2]));
                    } catch (NumberFormatException e) {
                        System.err.println("Score invalide pour " + values[0]);
                        player.setScore(0);
                    }
                    // CSV stores 18 challenge columns starting at index 3 (columns 3..20).
                    // Map CSV column index to player.challenge[] index by subtracting 3.
                    for(int idx = 3; idx < values.length; idx++) {
                        if (values[idx].equals("true")) {
                            int challengeIndex = idx - 3;
                            if (challengeIndex >= 0 && challengeIndex < player.getChallenge().length) {
                                player.finishChallenge(challengeIndex);
                            } else {
                                System.err.println("Ignored out-of-range challenge index: " + challengeIndex + " for player " + values[0]);
                            }
                        }
                    }

                    listePlayers.add(player);
                } else {
                    System.err.println("Ligne ignorée (colonnes insuffisantes) : " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        System.out.println("--- Fin de la lecture, " + listePlayers.size() + " joueurs chargés ---");
    }

    /**
     * Met à jour le score d'un joueur dans le fichier CSV et dans la liste en mémoire.
     * @param nameToUpdate Le nom du joueur à mettre à jour
     * @param newScore Le nouveau score
     */
    public static void updateScore(String nameToUpdate, int newScore) {
        List<String> lines = new ArrayList<>();
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Erreur (lecture) lors de la mise à jour : " + e.getMessage());
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            boolean updated = false;
            for (String currentLine : lines) {
                String[] values = currentLine.split(",");
                if (values.length > 0 && values[0].equals(nameToUpdate)) {
                    values[2] = String.valueOf(newScore);
                    writer.println(String.join(",", values));
                    updated = true;
                } else {
                    writer.println(currentLine);
                }
            }
            if (updated) {
                System.out.println("Le score pour '" + nameToUpdate + "' a été mis à jour dans " + FILENAME);
                for(Profil player : listePlayers) {
                    if(player.getName().equals(nameToUpdate)) {
                        player.setScore(newScore);
                        break;
                    }
                }
            } else {
                System.out.println("Aucun joueur nommé '" + nameToUpdate + "' trouvé pour la mise à jour.");
            }

        } catch (IOException e) {
            System.err.println("Erreur (écriture) lors de la mise à jour : " + e.getMessage());
        }
    }

    public static void savePlayer(Profil playerToSave) {
        List<String> lines = new ArrayList<>();
        String line;
        String nameToUpdate = playerToSave.getName();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Erreur (lecture) lors de la sauvegarde du joueur : " + e.getMessage());
            return;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            boolean updated = false;
            for (String currentLine : lines) {
                String[] values = currentLine.split(",");

                if (values.length > 0 && values[0].equals(nameToUpdate)) {

                    List<String> updatedValues = new ArrayList<>();
                    updatedValues.add(playerToSave.getName());
                    updatedValues.add(playerToSave.getPwd());
                    updatedValues.add(String.valueOf(playerToSave.getScore()));

                    boolean[] challenges = playerToSave.getChallenge();
                    for (boolean challengeStatus : challenges) {
                        updatedValues.add(String.valueOf(challengeStatus));
                    }
                    writer.println(String.join(",", updatedValues));
                    updated = true;
                } else {
                    writer.println(currentLine);
                }
            }
            if (updated) {
                System.out.println("Progrès pour '" + nameToUpdate + "' sauvegardé dans " + FILENAME);

                for (int i = 0; i < listePlayers.size(); i++) {
                    if (listePlayers.get(i).getName().equals(nameToUpdate)) {
                        listePlayers.set(i, playerToSave);
                        break;
                    }
                }
            } else {
                System.out.println("Aucun joueur nommé '" + nameToUpdate + "' trouvé pour la sauvegarde.");
            }
        } catch (IOException e) {
            System.err.println("Erreur (écriture) lors de la sauvegarde du joueur : " + e.getMessage());
        }
    }

    /**
     * Supprime un joueur du fichier CSV et de la liste en mémoire.
     * @param nameToDelete Le nom du joueur à supprimer
     */
    public static void deletePlayer(String nameToDelete) {
        List<String> lines = new ArrayList<>();
        String line;
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Erreur (lecture) lors de la suppression : " + e.getMessage());
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String currentLine : lines) {
                String[] values = currentLine.split(",");
                if (values.length > 0 && values[0].equals(nameToDelete)) {
                    deleted = true;
                } else {
                    writer.println(currentLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur (écriture) lors de la suppression : " + e.getMessage());
            return;
        }

        if (deleted) {
            System.out.println("Le joueur '" + nameToDelete + "' a été supprimé de " + FILENAME);
            listePlayers.removeIf(player -> player.getName().equals(nameToDelete));
        } else {
            System.out.println("Aucun joueur nommé '" + nameToDelete + "' n'a été trouvé pour suppression.");
        }
    }

    /**
     * Vérifie si un joueur existe déjà dans la liste en mémoire.
     * @param name Le nom à vérifier (insensible à la casse)
     * @return true si le joueur existe, false sinon
     */
    public static boolean playerExists(String name) {
        for (Profil player : listePlayers) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}