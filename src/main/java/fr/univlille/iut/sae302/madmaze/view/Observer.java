package main.java.fr.univlille.iut.sae302.madmaze.view;

import main.java.fr.univlille.iut.sae302.madmaze.model.Observable;

public interface Observer {
    void update(Observable modele);
}
