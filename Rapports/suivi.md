# Suivi de la SAE - Projet Labyrinthe - Mad Maze

## 📅 Suivi hebdomadaire détaillé par membre

###  Semaine du 6 octobre

**Valentin Faust** :
- Création architecture MVC (arborescence model, view, controller)
- Première version classe Cell (coordonnées, mur)
- Structure Labyrinth (initialisation, début génération)
- Init repository Git, .gitignore, config
- Première version suivi.md (mise à jour ce fichier chaque semaine)

**Aurélien Dochy** :
- Participation design architecture générale (MVC)
- Première version classe Player (gestion nom, mouvement)
- Démarrage Saves et gestion fichiers CSV
- Relecture/compléments sur suivi.md

**Corentin Chocraux** :
- Structure de base View et Controller
- Recherche labyrinthe en JavaFX
- Début fiches de projet et doc analyse

**Martin Lecoester** :
- Enum Direction (ZQSD, déplacements, opposés)
- Premiers diagrammes UML, collaboration sur rapport d’analyse
- Relecture des choix d'architecture, conseils sur les patterns MVC
- Documentation préliminaire

---

### 📆 Semaine du 13 octobre

**Valentin Faust** :
- Méthodes avancées Labyrinth :
- Génération entrée/sortie sur chaque bordure, contrainte distance min
- Ajustement automatique du % murs, passage de fonctions spécialisées (carvePath, adjustWallPercentage...)
- findOptimalPath, reconstruction du chemin optimal
- Mise à jour suivi.md

**Aurélien Dochy** :
- Liaison Player ↔ Partie, gestion état partie
- Enum ModeJeu, ajout ModeLibre et ModeProgression
- Déploiement système Saves (lecture/écriture, création CSV séparés)
- Test manuels persistance, validation interaction Player ↔ Sauvegarde

**Corentin Chocraux** :
- Intégration View ↔ Controller
- Poursuite JavaFX : rendu cases, tests UI
- Documentation rapport d’analyse

**Martin Lecoester** :
- UML : Finalisation classes projet, adaptation selon avancement code réel
- Participation rapport d’analyse et relecture collaborative
- Début des classes de test.
- Mise à jour des diagrammes UML.
- Relecture suivi.md

---

### 📆 Semaine du 20 octobre

**Valentin Faust** :
- Optimisation tolérance mur (%)
- Guide README et doc tests, finalisation guide utilisateur
- Debogage continue (controlleurs)
- Relecture finale suivi.md

**Aurélien Dochy** :
- Derniers tests manuels Saves, sauvegardes multi-profils
- Debug sauvegarde/progression
- Corrections de training sur l'approche multi-profils et challenges

**Corentin Chocraux** :
- Mise à jour suivi.md
- Corrections UI/UX pendant l'intégration des vues
- Vérification structuration des menus

**Martin Lecoester** :
- Ecriture de classes de test.
- Débogage avancé sur la synchronisation mode progression/défis
- Développement interface graphique (menus, choix paramètre, choix étape/défi)

---

### 📆 Semaine du 27 octobre

**Valentin Faust** :
- Refactorisation architecture : plusieurs interface (cell et maze)

**Aurélien Dochy** :
- Mise en place d'une To-Do List

**Corentin Chocraux** :
- Ajout Javadoc Controller
- Réflexion Mode Potentiellement Intéressant et comment les réaliser

**Martin Lecoester** :
- Bonnes vacances bien méritées

---

### 📆 Semaine du 3 novembre

**Valentin Faust** :
- Implémentation PerfectMaze avec algorithme Growing Tree
- Séparation des deux types de labyrinthes et de leur cellules (Random..., Perfect...)
- Création classes RandomCell et PerfectCell héritant de Cell

**Aurélien Dochy** :
- Ajout Observer/Observable

**Corentin Chocraux** :
- Ajout NightMode (vue avec brouillard, visibilité limitée)
- Optimisation Night Mode
- Vérification Fonctionnement en Labyrinthes Aléatoires et Parfaits

**Martin Lecoester** :
- Implémentation du FuelMode (mode essence)
- Implémentation de la vue du Fuel Mode

---

### 📆 Semaine du 10 novembre

**Valentin Faust** :
- Création classe MazeRederer pour les différentes vues du labyrinthes
- Création de tests :
    - RandomCellTest (4 tests)
    - RandomMazeTest (8 tests)
    - PerfectCellTest (3 tests)
    - PerfectMazeTest (6 tests)
- Affichage joueur avec rotation selon direction


**Aurélien Dochy** :
- Ajout pop-up erreurs pour choix étapes invalides
- Résolution bug choix étape verroullié
- Développement vues et controlleurs pour modes Bonus

**Corentin Chocraux** :
- Implémentation MultipleExitMode (sorties multiples dont fausses)
- Ajout de la vue de drawMultipleExitView
- Rendu plus réaliste des fausses sorties

**Martin Lecoester** :
- Correction bugs affichage vue hybride (zones hors limites)
- Implémentation Storm Mode
- Implémentation de la vue du Sotrm Mode


---

### 📆 Semaine du 17 novembre

**Valentin Faust** :
- Suite rapport suivi.md
- Rapport Dev. Efficace suite
- Rapport de justifications des choix de conception
- Refonte graphique du Main Menu, Choix Parametres, Ecrans de fin

**Aurélien Dochy** :
- Tests intégration avec tous les modes de jeu
- Refonte Graphique du Mode Bonus / Progression
- Rapport Dev. Efficace

**Corentin Chocraux** :
- Polissage final vues et rendu
- Tests visuels sur différentes résolutions
- Ajout Mode Course (Course contre un Bot pour limiter le nombre de mouvement Maximum)
- Vérification fonctionnement sous Labyrinthes Aléatoires et Parfaits
- Ajout Image Bot basé sur l'image du joueur
- Optimisation visuel Bot Mode Course (Joueur premier plan, Rotation image Bot)
- Rajout de Javadoc
- Rajout fichier exécutable qui lance le jeu

**Martin Lecoester** :
- Validation finale navigation et UX
- Rapport Dev. Efficace
- Création de tests :
    - ChallengeTest : 5 tests
    - DifficultyTest : 4 tests
    - DirectionTest : 7 tests
    - FreeModeTest : 4 tests
    - GameTest : 6 tests
    - MultipleExitModeTest : 6 tests
    - NightModeTest : 4 tests
    - ProgressionModeTest : 7 tests
    - RaceModeTest : 6 tests

