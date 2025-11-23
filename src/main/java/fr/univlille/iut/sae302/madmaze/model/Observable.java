package main.java.fr.univlille.iut.sae302.madmaze.model;

import main.java.fr.univlille.iut.sae302.madmaze.view.Observer;

public interface Observable {
    
    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();

    int getMoveCount();

    Mode getMode();
}
