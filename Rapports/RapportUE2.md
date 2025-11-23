# Rapport UE2 - Développement d'applications : Labyrinthes

## 1. Investissement dans le projet

*   **Numéro de groupe :** G4
*   **Membres du groupe :**
    *   Valentin : 35%
    *   Aurélien : 30%
    *   Martin : 20%
    *   Corentin : 15%

## 2. Structures de données pour les labyrinthes

Pour représenter les **labyrinthes aléatoires**, nous avons utilisé une structure de données simple et directe : une grille bi-dimensionnelle d'objets `RandomCell[][]`. Chaque objet `RandomCell` contient ses coordonnées et son état (mur ou chemin). Cette structure se trouve dans la classe `RandomMaze.java`.

Pour les **labyrinthes parfaits**, nous avons choisi une structure optimisée en mémoire, fondée sur deux tableaux à deux dimensions de booléens : `verticalWalls[][]` et `horizontalWalls[][]`. Ceux-ci permettent de représenter efficacement la présence ou l’absence de chaque mur entre deux cellules adjacentes. Cette organisation, directement reprise du TP, est implémentée dans la classe `PerfectMaze.java`.

## 3. Algorithmes de génération

### Labyrinthes aléatoires

**Pseudo-code :**
```
FONCTION generer_labyrinthe_aleatoire(largeur, hauteur, pourcentage_murs_cible)
  TANT QUE vrai:
    // 1. Initialisation
    grille = creer_grille_de_chemins(largeur, hauteur)
    placer_entree_et_sortie_sur_bords_opposes(grille)

    // 2. Placement des murs
    nombre_murs = calculer_nombre_murs(largeur, hauteur, pourcentage_murs_cible)
    placer_murs_aleatoirement(grille, nombre_murs) en evitant entree/sortie

    // 3. Garantie d'accessibilité
    ouvrir_un_voisin_autour_de(entree)
    ouvrir_un_voisin_autour_de(sortie)

    // 4. Vérification de la validité
    chemin_optimal = trouver_chemin_via_BFS(grille, entree, sortie)
    SI chemin_optimal n'est pas vide ALORS
      RETOURNER le labyrinthe généré
    FIN SI
    // 5. Gestion des échecs successifs
    // Si aucun chemin n'est trouvé après de nombreuses tentatives,
    pourcentage_murs_cible = reduire_pourcentage_murs(pourcentage_murs_cible)
```

**Justification du choix des structures de données :**
La structure `RandomCell[][]` est utilisée car elle permet une manipulation directe et intuitive de l'état de chaque cellule (mur/chemin). L'algorithme de génération plaçant les murs de manière aléatoire, il est simple de modifier l'attribut `isWall` de chaque objet `RandomCell`. Pour la vérification du chemin, un parcours en largeur (BFS) est implémenté, et une grille est une structure de données classique et efficace pour ce type d'algorithme.

**Implémentation :**
*   **Classe :** `RandomMaze.java`
*   **Méthode(s) :** 
    * `generate()` : en charge de la création de la grille et du placement des murs, mais ne garantit pas à elle seule la présence d’un chemin valide.
    * `findOptimalPath()` : (parcours en largeur, BFS) est utilisée pour vérifier la validité du chemin entre l’entrée et la sortie.
    * `generateMaze()` : assure qu’il existe un chemin entre l’entrée et la sortie. Elle appelle la méthode generate() en boucle et compte le nombre de tentatives. Si, après un certain nombre d’essais, aucun chemin n’est trouvé, elle réduit progressivement le pourcentage de murs, puis recommence, jusqu’à ce qu’un labyrinthe valide soit généré.

### Labyrinthes parfaits

**Pseudo-code (Algorithme "Growing Tree") :**
```
FONCTION generer_labyrinthe_parfait(largeur, hauteur)
  // 1. Initialisation
  initialiser_tous_les_murs_a_vrai() // verticalWalls et horizontalWalls
  grille_visitee = creer_grille_booleenne_initialisee_a_faux(largeur, hauteur)
  liste_cellules_actives = nouvelle_liste()

  // 2. Démarrage
  cellule_depart = choisir_cellule_aleatoire()
  marquer_visitee(cellule_depart)
  ajouter_a_liste(liste_cellules_actives, cellule_depart)

  // 3. Construction de l'arbre couvrant
  TANT QUE liste_cellules_actives n'est pas vide:
    // Choix de la cellule à explorer (mix entre DFS et Prim)
    cellule_courante = choisir_une_cellule_dans(liste_cellules_actives) // Soit la dernière, soit une au hasard

    voisins_non_visites = trouver_voisins_non_visites(cellule_courante)

    SI voisins_non_visites n'est pas vide:
      voisin_choisi = choisir_un_voisin_aleatoire_parmi(voisins_non_visites)
      casser_le_mur_entre(cellule_courante, voisin_choisi)
      marquer_visitee(voisin_choisi)
      ajouter_a_liste(liste_cellules_actives, voisin_choisi)
    SINON:
      // La cellule n'a plus d'extension possible, on la retire
      retirer_de_liste(liste_cellules_actives, cellule_courante)
  FIN TANT

  // 4. Sélection de l'entrée/sortie
  selectionner_entree_et_sortie_respectant_distance_minimale()
```

**Justification du choix des structures de données :**
Les tableaux de booléens `verticalWalls` et `horizontalWalls` sont idéaux pour les algorithmes de génération de labyrinthes parfaits comme "Growing Tree", Prim ou Kruskal. Ces algorithmes fonctionnent en retirant des murs pour connecter des cellules. Modifier un booléen dans un tableau est une opération très rapide et peu coûteuse en mémoire, ce qui rend l'algorithme globalement plus performant. Cette organisation se révèle particulièrement adaptée à la génération et la manipulation des labyrinthes sans cycles, où l’unicité du chemin est garantie par la structure des murs.

**Implémentation :**
*   **Classe :** `PerfectMaze.java`
*   **Méthode(s) :** 
    * `generateMaze()` : orchestre la génération du labyrinthe parfait selon l’algorithme "Growing Tree".
    * `removeWall()` : supprime un mur entre deux cellules dans les tableaux de booléens.
    * `selectStartAndEnd()` : sélectionne l’entrée et la sortie pour satisfaire une distance minimale.

**Autre algorithme de génération de labyrinthe parfait :**
Aucun autre algorithme n'a été implémenté. Nous nous sommes concentrés sur l'algorithme "Growing Tree" pour sa flexibilité et la qualité des labyrinthes générés.

## 4. Efficacité

### Temps de génération des labyrinthes aléatoires

| Taille | % Murs | Temps (ms) |
| --------------- | ------ | ---------- |
| (5, 10)        | 20%    | 3 ms |
| (5, 10)        | 35%    | 3 ms |
| (5, 10)        | 50%    | 3.5 ms |
| (25, 50)        | 20%    | 5 ms |
| (25, 50)        | 35%    | 5.2 ms |
| (25, 50)        | 50%    | 78.5 ms |
| (50, 100)       | 20%    | 8 ms |
| (50, 100)       | 35%    | 8 ms |
| (50, 100)       | 50%    | 460 ms |

### Temps de génération des labyrinthes parfaits

| Taille | Distance min | Temps (ms) |
| --------------- | ------------ | ---------------------------- |
| (5, 10)         | 5            | 3 ms |
| (5, 10)         | 10           | 3 ms |
| (5, 10)         | 30           | 12 ms |
| (25, 50)        | 25           | 8.5 ms |
| (25, 50)        | 50           | 8.5 ms |
| (25, 50)        | 150          | 16 ms |
| (50, 100)       | 50           | 21 ms |
| (50, 100)       | 100          | 21 ms |
| (50, 100)       | 300          | 25 ms |


**Conclusion :**

Les mesures réalisées montrent que les deux algorithmes de génération — aléatoire et parfait — offrent des performances très satisfaisantes, même pour des labyrinthes de grande taille. Pour des petites et moyennes grilles (jusqu’à 25 × 50), les temps de génération restent quasi instantanés (entre 3 et 16 ms pour les parfaits, 3 à 8 ms pour les aléatoires avec une faible densité de murs).

On observe cependant que, pour les labyrinthes aléatoires, le temps de génération augmente significativement avec la densité de murs sur de grandes grilles, illustrant l'impact des tentatives répétées nécessaires pour garantir la validité du parcours. À l’inverse, l’algorithme des labyrinthes parfaits, fondé sur la suppression progressive des murs, reste très efficace quelles que soient les contraintes imposées.

En résumé, ces performances confirment la robustesse des structures et algorithmes choisis : ils assurent une génération rapide, garantissent la jouabilité du labyrinthe, et permettent une adaptabilité facile selon les paramètres fixés par l’utilisateur, tout en restant adaptés à un usage interactif dans le cadre d’une application ludique ou pédagogique.

## 5. Complément et Bilan

### Complément

Nous avons mis en œuvre plusieurs optimisations pour garantir la fluidité et la robustesse de l'application :

*   **Gestion adaptative du taux de murs (`RandomMaze.java`) :** Un mécanisme de réduction automatique du pourcentage de murs a été implémenté. Si aucune configuration valide n'est trouvée après plusieurs tentatives, le taux de murs est diminué progressivement. Cela évite les blocages lors de la génération et assure toujours un labyrinthe jouable.
*   **Structures de données optimisées (`PerfectMaze.java`) :** L'utilisation de tableaux de booléens (`boolean[][]`) pour représenter les murs verticaux et horizontaux permet une gestion efficace de la mémoire et accélère les opérations de suppression de murs lors de la génération.
*   **Algorithme de recherche de chemin (BFS) :** L'utilisation d'un parcours en largeur (BFS) dans la méthode `findOptimalPath` assure de trouver le chemin le plus court de manière optimale, ce qui est crucial pour la validation des labyrinthes et l'aide au joueur.

### Bilan

**Difficultés rencontrées :**
*   **Complexité de la génération aléatoire :** L'algorithme de génération aléatoire peut devenir coûteux en temps de calcul lorsque le taux de murs est élevé, car il nécessite de nombreuses tentatives pour trouver une configuration valide. Nous avons dû implémenter une réduction dynamique du taux de murs pour pallier ce problème.
*   **Gestion de la mémoire pour les grands labyrinthes :** La représentation des labyrinthes, bien qu'optimisée, peut consommer de la mémoire pour des tailles très importantes (ex: 100x100), impactant potentiellement la fluidité de l'affichage JavaFX.


