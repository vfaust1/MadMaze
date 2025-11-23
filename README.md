# MadMaze - Jeu de Labyrinthe (SAE 3.02)

---

## Sommaire

- [Présentation](#présentation)
- [Organisation du Projet](#organisation-du-projet)
- [Installation & Lancement](#installation--lancement)
- [Auteurs](#auteurs)

---

## Présentation

**MadMaze** est une application complète de génération, de résolution et de jeu de labyrinthes. Développée en Java avec l'interface graphique JavaFX, elle propose une expérience riche allant de la simple exploration à des défis de survie.

### Modes de Jeu
Le jeu propose plusieurs modes pour varier les plaisirs :
*   **Mode Libre** : Créez des labyrinthes sur mesure (taille, pourcentage de murs, type de génération).
*   **Mode Progression** : Une campagne structurée avec des niveaux de difficulté croissante.
*   **Modes Spéciaux** :
    *   **Fuel Mode** : Gérez votre carburant pour atteindre la sortie avant la panne sèche.
    *   **Night Mode** : Explorez le labyrinthe dans l'obscurité.
    *   **Storm Mode** : Depechez vous d'atteindre la sortie avant d'être emporté par la tempete.
    *   **Race Mode** : Une course contre un bot.
    *   **Multiple Exit** : Trouvez la véritable sortie parmi plusieurs leurres.
    *   **Mad Max Mode** : Le labyrinthe change régulièrement.

### Fonctionnalités Clés
*   **Génération Procédurale** : Algorithmes de labyrinthes parfaits (Growing Tree) et imparfaits (aléatoire avec garantie de chemin).
*   **Pathfinding** : Résolution automatique et vérification de faisabilité via l'algorithme BFS (Breadth-First Search).
*   **Système de Profils** : Sauvegarde de la progression, des scores et gestion multi-utilisateurs.
*   **Interface Graphique** : Navigation fluide, contrôles au clavier et à la souris.

---

## Organisation du Projet

Voici l'arborescence des fichiers importants du projet :

*   **Code Source** (\src/main/java/fr/univlille/iut/sae302/madmaze/\) :
    *   \model/\ : Contient toute la logique métier (Labyrinthes, Cellules, Modes de jeu).
    *   \View/\ : Gestion de l'interface graphique JavaFX.
    *   \controller/\ : Lien entre la vue et le modèle.
*   **Tests** (\src/test/java/\) :
    *   Tests unitaires JUnit 5 pour valider la logique de génération et les règles du jeu.
*   **Documentation** :
    *   [`RapportUE2.md`](Rapports/RapportUE2.md) : Rapport détaillé sur le développement, les choix techniques et le bilan (Dev. efficace UE2).
    *   [`suivi.md`](Rapports/suivi.md) : Journal de bord du projet (suivi hebdomadaire des avancés de l'équipe).
    *   [`JustificationsChoixDeConception.md`](Rapports/JustificationsChoixDeConception.md) : Justifications de tous les choix d'implémentation du projet.
    *   [`UML.pdf`](Rapports/UML.pdf) : UML du projet.
    *   [`UMLScreenshots/`](Rapports/UMLScreenshots/) : Screenshots de parties de l'UML.

---

## Installation et Lancement

### Prérequis
*   **Java 17** (ou version supérieure)
*   **Maven**

### Commandes
Ouvrez un terminal à la racine du projet et utilisez les commandes suivantes :

1.  **Lancer le jeu** :
    \\\bash
    mvn javafx:run
    \\\
    ou alors : 

    ./exec.bat (Windows)
    ./exec.sh (Linux)

2.  **Exécuter les tests** :
    \\\bash
    mvn test
    \\\

3.  **Compiler le projet** :
    \\\bash
    mvn clean install
    \\\

---

## Auteurs

Ce projet a été réalisé par une équipe d'étudiant de BUT Informatique de l'IUT de Lille (Villeneuve d'Ascq) :

*   **Valentin Faust** 
*   **Aurélien Dochy** 
*   **Martin Lecoester**
*   **Corentin Chocraux**

---

*Projet SAE 3.02 - Année 2024-2025* :P
