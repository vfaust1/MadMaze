# Justifications des Choix de Conception - MadMaze

## Table des Matières
1. [Architecture Générale](#architecture-générale)
2. [Package Model](#package-model)
3. [Package View](#package-view)
4. [Package Controller](#package-controller)
5. [Patterns de Conception](#patterns-de-conception)

---

## Architecture Générale

### Organisation en Packages
Le projet suit une architecture MVC (Model-View-Controller) stricte :
- **`model/`** : Contient toute la logique métier (labyrinthes, modes de jeu, joueurs)
- **`view/`** : Gère l'affichage JavaFX (vues, rendering)
- **`controller/`** : Fait le lien entre le modèle et la vue, gère les événements utilisateur

**Justification** : Cette séparation permet une maintenance facilitée, des tests unitaires ciblés, et la possibilité de changer l'interface graphique sans toucher à la logique métier.

---

## Package Model

### 1. Interface `Cell`

**Rôle** : Définit le contrat minimal pour toutes les cellules du labyrinthe.

**Méthodes** :
- `int getRow()` : Position verticale de la cellule
- `int getCol()` : Position horizontale de la cellule
- `void setFuel` : Change un cellule en cellule fuel
- `void setSand` : Change un cellule en tempête
- `boolean isWall()` : Indique si la cellule est un mur
- `boolean isFuel()` : Indique si la cellule contient du carburant
- `boolean isSand()` : Indique si la cellule est prise dans la tempête

**Justifications** :
1. **Interface plutôt que classe abstraite** : Permet une flexibilité maximale. Les implémentations concrètes (`RandomCell`, `PerfectCell`) peuvent avoir des représentations internes différentes sans être contraintes par une hiérarchie de classes.

2. **Méthodes minimales** : L'interface se limite aux opérations de lecture communes. Cela évite de forcer toutes les implémentations à gérer des états qui ne les concernent pas.


**Avantages** :
- Polymorphisme : Le code client peut manipuler des cellules sans connaître leur type concret
- Extensibilité : On peut ajouter de nouveaux types de cellules sans modifier le code existant
- Testabilité : On peut créer des mocks de cellules pour les tests

---

### 2. Enum `CellType`

**Rôle** : Énumération des états possibles d'une cellule.

**Valeurs** :
- `WALL` : Mur infranchissable
- `PATH` : Chemin praticable
- `FUEL` : Bidon de carburant (mode fuel)
- `SAND` : Cellule recouverte de sable (mode tempête)

**Justifications** :
1. **Enum plutôt que constantes** : Type-safety à la compilation, impossible d'avoir une valeur invalide.

2. **Évolutivité** : Ajouter un nouveau type de cellule se fait en une ligne, et le compilateur signale tous les switchs incomplets.

3. **Sémantique claire** : `cellType == CellType.WALL` est plus lisible que `cellStatus == 1`.

**Alternative rejetée** : Utiliser des booléens séparés (`isWall`, `isFuel`, etc.) aurait créé des états incohérents possibles (une cellule à la fois mur ET fuel).

---

### 3. Classe `RandomCell`

**Rôle** : Implémentation concrète de `Cell` pour les labyrinthes aléatoires.

**Attributs** :
- `int row, col` : Coordonnées dans la grille
- `CellType cellType` : État actuel de la cellule (mutable)

**Constructeurs** :
- `RandomCell(int row, int col)` : Crée une cellule PATH par défaut
- `RandomCell(int row, int col, CellType celltype)` : Crée une cellule avec un type spécifique

**Méthodes principales** :
- `getCellType()` : Accède à l'état actuel
- `setWall()`, `setPath()`, `setFuel()`, `setSand()` : Mutations d'état
- `equals()` et `hashCode()` : Basés uniquement sur les coordonnées

**Justifications** :

1. **Attributs `protected`** : Permet l'héritage si nécessaire, mais maintient l'encapsulation vis-à-vis du reste du code. Les sous-classes potentielles peuvent accéder directement aux coordonnées.

2. **État mutable (`CellType`)** : Les cellules changent d'état durant le jeu (placement de fuel, propagation de sable). Créer de nouvelles instances à chaque changement serait coûteux en mémoire et compliquerait le suivi des références.

3. **Constructeur par défaut à PATH** : Dans la génération d'un labyrinthe aléatoire, on place les murs dans le labyrinthe après avoir initialisé la grille. Le constructeur par défaut évite donc de répéter `CellType.PATH` partout dans le code de génération.

4. **`equals()` ignore le type** : Deux cellules aux mêmes coordonnées sont considérées égales même si leurs types diffèrent. C'est essentiel pour les algorithmes de recherche de chemin (BFS, DFS) qui ne doivent pas revisiter une position déjà explorée, quel que soit son type.

5. **`hashCode()` cohérent** : Basé sur `31 * col + row`, une formule classique pour combiner deux entiers. Le facteur 31 (nombre premier) réduit les collisions dans les HashMaps.

---

### 4. Classe `PerfectCell`

**Rôle** : Implémentation de `Cell` pour les labyrinthes parfaits.

**Différences avec `RandomCell`** :
- Setters `setWall()`, `setPath()` : un labyrinthe parfait n'a pas de "mur" en tant que cellule, les murs sont entres les chemins. Toutes les cellules sont des chemins. Renvoie false à `setWall()` et true à `setPath()`.

**Justifications** :

1. **Ajout tardif de `setSand()`** : Initialement absents car les labyrinthes parfaits n'ont pas de murs comme objets dans la grille. Ajoutés pour supporter le mode tempête qui doit transformer n'importe quelle cellule en sable. Cela illustre l'évolution du design face à de nouveaux besoins.

2. **Constructeur avec type** : Permet de créer des cellules spéciales (fuel ou sand) dès la construction..

3. **Pas de méthode `setPath()`** : Dans un labyrinthe parfait, toutes les cellules non-fuel sont des chemins par défaut. Cette méthode n'est pas nécessaire (mais pourrait être ajoutée pour la cohérence avec `RandomCell`).

---

### 5. Enum `Direction`

**Rôle** : Représente les quatre directions cardinales pour les déplacements du joueur.

**Valeurs** : `Z` (haut), `Q` (gauche), `S` (bas), `D` (droite)

**Attributs** :
- `String direction` : Nom textuel ("top", "left", "bottom", "right")
- `int dx, dy` : Vecteurs de déplacement

**Méthodes** :
- `getDx()`, `getDy()` : Accès aux composantes du vecteur
- `nextCell(Cell cell)` : Calcule la cellule adjacente dans cette direction

**Justifications** :

1. **Enum avec attributs** : Associe directement les touches ZQSD aux vecteurs de déplacement. Évite un switch/case à chaque calcul de mouvement.

2. **Convention ZQSD** : Correspond au clavier AZERTY français (public cible du projet). Les touches sont aussi les directions dans les jeux français classiques.

3. **Méthode `nextCell()`** : Encapsule la logique de calcul de la prochaine cellule. Le code client n'a pas besoin de connaître les vecteurs dx/dy, il suffit d'appeler `direction.nextCell(currentCell)`.

4. **Méthode statique `allDirections()`** : Retourne toutes les directions possibles. Utilisé par les algorithmes de propagation (tempête, exploration) qui doivent parcourir les 4 voisins d'une cellule.

---

### 6. Interface `Maze`

**Rôle** : Contrat commun pour tous les types de labyrinthes.

**Méthodes principales** :
- `void generateMaze()` : Génère la structure du labyrinthe
- `boolean isDirectlyReachable(Cell c1, Cell c2)` : Teste l'accessibilité entre deux cellules adjacentes
- `List<Cell> findOptimalPath(Cell start, Cell end)` : Calcule le plus court chemin
- `Cell[][] getGrid()` : Retourne la grille de cellules
- `Cell getStart()`, `Cell getEnd()` : Points d'entrée et de sortie
- `int optimalPathLength()` : Longueur du chemin optimal

**Justifications** :

1. **Interface plutôt que classe abstraite** : Les implémentations `RandomMaze` et `PerfectMaze` ont des structures internes très différentes (grille de cellules vs tableaux de murs). Une classe abstraite aurait imposé une structure commune inadaptée.

2. **Méthode `generateMaze()` dans l'interface** : Chaque type de labyrinthe a un algorithme de génération différent (Growing Tree pour perfect, placement aléatoire pour random). L'interface force chaque implémentation à fournir cette méthode.

3. **`optimalPathLength()` séparé de `findOptimalPath()`** : L'un renvoie la distance minimal pour finir le labyrinthe tandis que l'autre renvoie le chemin optimal (liste de cellules).

---

### 7. Classe `RandomMaze`

**Rôle** : Labyrinthe aléatoire avec un pourcentage configurable de murs. Garantit l'existence d'un chemin entre l'entrée et la sortie.

**Attributs clés** :
- `RandomCell[][] grid` : Grille de cellules
- `int wallPercentage` : Pourcentage cible de murs (0-100)
- `RandomCell start, end` : Entrée et sortie

**Algorithme de génération** :
1. Créer une grille vide (toutes cellules PATH)
2. Placer start et end sur des bords opposés
3. Placer aléatoirement les murs selon le pourcentage
4. Ouvrir au moins un chemin autour de start et end
5. Vérifier qu'un chemin existe (BFS), sinon recommencer

**Justifications** :

1. **Grille persistante** : La grille est créée une fois et réutilisée. Les méthodes `getGrid()` et `getCell()` retournent les instances réelles, pas des copies. Cela permet aux modes de jeu (fuel, storm) de modifier les cellules et que ces modifications soient visibles partout.

2. **Génération avec réessais** : La méthode `generateMaze()` génère aléatoirement jusqu'à obtenir un labyrinthe valide. Si après 500 tentatives aucun chemin n'est trouvé, on réduit le `wallPercentage` de 1% et on continue. **Justification** : Simple et efficace. Les labyrinthes impossibles sont rares avec des pourcentages raisonnables (<60%).

3. **Placement start/end sur bords opposés** : La méthode `ensureStartEndOpposite()` garantit que l'entrée et la sortie sont sur des côtés opposés (haut/bas ou gauche/droite). **Justification** : Assure une distance minimale et rend le labyrinthe plus intéressant qu'avec des positions totalement aléatoires.

4. **`openNeighborPath()`** : Force l'ouverture d'au moins une case adjacente à start et end. **Justification** : Évite des scénarios où start/end sont complètement entourés de murs, ce qui rendrait le labyrinthe impossible même si le pourcentage de murs est faible.

5. **Méthode `findOptimalPath()` (BFS)** : Utilise un parcours en largeur avec une Queue et une HashMap pour tracer le chemin parent. **Justification** : BFS garantit de trouver le plus court chemin dans un graphe non pondéré (chaque déplacement a le même coût).

6. **Méthode `findLongerPath()` (DFS)** : Utilisée par le mode Race pour donner un avantage au bot. Cherche un chemin plus long que l'optimal en explorant en profondeur. **Justification** : DFS avec backtracking permet d'explorer des chemins alternatifs plus longs. La limite de profondeur évite les boucles infinies.

7. **Méthode `neighbors(int row, int col)`** : Retourne uniquement les voisins accessibles (non-murs). **Justification** : Simplifie les algorithmes de recherche qui n'ont pas à filtrer manuellement les murs.

8. **Validation progressive dans `generateMaze()`** : Le compteur `totalAttempts` et la réduction progressive du `wallPercentage` sont affichés en console. **Justification** : Feedback utilisateur/développeur sur la difficulté de génération. Si un labyrinthe nécessite beaucoup de tentatives, c'est un indicateur de paramètres trop contraignants.

---

### 8. Classe `PerfectMaze`

**Rôle** : Labyrinthe parfait (sans boucles) où il existe un unique chemin entre deux points quelconques.

**Attributs clés** :
- `int width, height` : Dimensions
- `boolean[][] verticalWalls, horizontalWalls` : Représentation des murs (tableau de booléens)
- `PerfectCell[][] grid` : Grille de cellules (ajoutée pour différents modes)
- `PerfectCell start, end` : Entrée et sortie
- `int minLength` : Longueur minimale exigée pour le chemin optimal

**Algorithme de génération (Growing Tree)** :
1. Partir d'une cellule aléatoire
2. Choisir une cellule de la liste (DFS si dernier, Prim si aléatoire)
3. Supprimer un mur vers un voisin non visité
4. Ajouter le voisin à la liste
5. Répéter jusqu'à ce que toutes les cellules soient visitées
6. Sélectionner start et end garantissant une distance minimale

**Justifications** :

1. **Représentation par murs plutôt que par cellules** : Contrairement à `RandomMaze`, `PerfectMaze` stocke les murs dans deux tableaux booléens séparés (verticaux et horizontaux). **Justification** : Cette représentation est plus efficace en mémoire pour un labyrinthe parfait où chaque "mur" est un segment entre deux cellules, pas une cellule entière.

2. **Algorithme Growing Tree hybride** : Mélange DFS (50% du temps, prend la dernière cellule) et Prim (50%, prend une cellule aléatoire). **Justification** : DFS crée des couloirs longs et sinueux, Prim crée des branches courtes. Le mélange 50/50 produit des labyrinthes visuellement intéressants et variés.

3. **Méthode `selectStartAndEnd()` avec distance minimale** : Utilise un BFS depuis start pour calculer toutes les distances, puis choisit end parmi les cellules à distance ≥ minLength. **Justification** : Garantit que le labyrinthe offre un challenge suffisant. Si aucune cellule ne satisfait minLength, on prend la plus éloignée possible et on affiche un warning dans le terminal.

4. **Méthode `shortestDistancesFrom()`** : BFS qui calcule la distance depuis une cellule vers toutes les autres. Retourne un tableau 2D de distances. **Justification** : Réutilisable pour plusieurs besoins (sélection de end, calcul de chemin optimal, validation).

5. **Vérification `adjacent()` dans `isWall()`** : La méthode `isWall(int row, int col, int row2, int col2)` vérifie d'abord que les cellules sont adjacentes avant de consulter les tableaux de murs. **Justification** : Sécurité. Évite les erreurs d'index et clarifie le contrat (on ne peut tester un mur qu'entre deux cellules voisines).

6. **`removeWall()` privée** : Utilisée uniquement par l'algorithme de génération. Modifie les tableaux `verticalWalls` ou `horizontalWalls` selon l'orientation. **Justification** : Encapsulation. Le code client n'a pas besoin d'accéder directement aux murs, il utilise `isDirectlyReachable()`.

7. **`findOptimalPath()` avec reconstruction du chemin** : Utilise `shortestDistancesFrom()` pour obtenir les distances, puis reconstruit le chemin en remontant les voisins de distance décroissante. **Justification** : Élégant et efficace. Pas besoin de stocker explicitement les parents comme dans un BFS classique.

https://weblog.jamisbuck.org/2011/1/27/maze-generation-growing-tree-algorithm

---

### 9. Interface `Mode`

**Rôle** : Contrat pour tous les modes de jeu.

**Méthodes obligatoires** :
- `Maze getMaze()` : Retourne le labyrinthe associé
- `void prepareMaze()` : Génère/configure le labyrinthe
- `String getModeName()` : Nom du mode pour l'affichage

**Méthodes par défaut** :
- `Profil getPlayer()` : Retourne null par défaut (seul ProgressionMode a un joueur)
- `int getScore()` : Retourne 0 par défaut
- `void setMaze(MazeType, int, int, int)` : Vide par défaut

**Justifications** :

1. **Interface avec méthodes par défaut** : Permet d'ajouter des méthodes optionnelles sans casser les implémentations existantes. **Justification** : `getPlayer()` n'a de sens que pour `ProgressionMode` (qui suit les progrès d'un joueur). Les autres modes retournent `null` par défaut.

2. **Séparation `prepareMaze()` et `getMaze()`** : `prepareMaze()` génère le labyrinthe, `getMaze()` le récupère. **Justification** : Permet de régénérer le labyrinthe sans changer l'instance du mode. Utile pour "Rejouer" sans recréer tout le mode.

3. **Méthode `setMaze()` optionnelle** : Permet de configurer les paramètres du labyrinthe (type, dimensions, paramètre spécifique). **Justification** : Certains modes (Tous sauf ProgressionMode) sont configurables par l'utilisateur.

4. **Pas de méthode `isLost()` ou `canMove()` dans l'interface** : Ces méthodes sont spécifiques à certains modes (FuelMode, StormMode, RaceMode). **Justification** : Pas tous les modes ont une condition de défaite. Les forcer dans l'interface obligerait des implémentations vides.

**Design pattern** : Strategy Pattern - Le mode de jeu est une stratégie interchangeable qui définit les règles du jeu.

**Extensibilité** : Ajouter un nouveau mode se fait en :
1. Créer une classe implémentant `Mode`
2. Implémenter les methodes de l'interface avec la logique spécifique au mode
3. Ajouter le mode dans l'UI de sélection

---

### 10. Classe `FreeMode`

**Rôle** : Mode de jeu libre où le joueur configure entièrement le labyrinthe.

**Attributs** :
- `Maze maze` : Instance du labyrinthe généré
- `MazeType mazeType` : RANDOM ou PERFECT
- `int width, height` : Dimensions choisies par l'utilisateur
- `int parameter3` : Pourcentage de murs (RANDOM) ou longueur min (PERFECT)

**Justifications** :

1. **Configuration différée** : Les paramètres sont stockés via `setMaze()` et appliqués dans `prepareMaze()`. **Justification** : Séparation des responsabilités. La vue collecte les paramètres, puis lance la génération. Le mode n'a pas besoin de connaître l'UI.

2. **Nom de paramètre générique `parameter3`** : Représente le wallPercentage pour RandomMaze ou le minLength pour PerfectMaze. **Justification** : Évite d'avoir deux attributs séparés qui seraient mutuellement exclusifs. Le nom générique indique que sa signification dépend du `mazeType`.

3. **Switch sur `mazeType` dans `prepareMaze()`** : Instancie `RandomMaze` ou `PerfectMaze` selon le choix. **Justification** : Simple et lisible. Une factory aurait été sur-ingénierie pour seulement deux types.

4. **Pas de logique de jeu spécifique** : `FreeMode` n'ajoute pas de règles particulières (pas de carburant, pas de tempête). **Justification** : C'est un mode "sandbox" pour explorer librement.

---

### 11. Classe `FuelMode`

**Rôle** : Mode avec gestion de carburant. Le joueur doit collecter des bidons d'essence pour continuer à se déplacer.

**Attributs clés** :
- `int fuelLeft` : Carburant restant
- `int maxFuel` : Capacité maximale (fixée à 10)
- `Set<Cell> fuelCells` : Positions des bidons non collectés

**Algorithme de placement des bidons** :
1. Calculer le nombre de bidons nécessaires : `ceil(pathLength / maxFuel)`
2. Placer les bidons à intervalles réguliers sur le chemin optimal
3. Vérifier que le dernier bidon permet d'atteindre la sortie
4. Ajouter des bidons aléatoires (5% des cellules) pour donner des alternatives

**Justifications** :

1. **`maxFuel = 10` codé en dur** : Valeur fixe trouvée par essai-erreur pour un équilibre difficulté/gameplay. **Justification** : Pourrait être paramétrable, mais 10 mouvements offrent un bon challenge sans être trop punitif.

2. **`Set<Cell>` pour les bidons** : Utilise un HashSet pour stocker les positions. **Justification** : Recherche en temps constant pour savoir si une cellule contient un bidon. Évite les doublons automatiquement.

3. **Placement sur le chemin optimal** : La méthode `calculateOptimalFuelPositions()` place les bidons le long du chemin solution. **Justification** : Garantit que le labyrinthe est solvable. Un placement totalement aléatoire pourrait rendre le jeu impossible.

4. **Méthode `ensureReachableEnd()`** : Vérifie que la distance entre le dernier bidon et la sortie est ≤ maxFuel. **Justification** : Évite les situations frustantes où le joueur atteint le dernier bidon mais ne peut pas finir.

5. **Bidons aléatoires supplémentaires** : 5% de bidons placés hors du chemin optimal. **Justification** : Offre de la flexibilité. Le joueur n'est pas obligé de suivre le chemin optimal s'il explore.

6. **Méthode `onMove(Cell newPosition)`** : Appelée par `Game` après chaque déplacement. Décrémente le fuel et vérifie si un bidon est collecté. **Justification** : Encapsule la logique du mode. `Game` n'a pas besoin de connaître les détails du système de carburant.

7. **`canMove()` et `isLost()`** : Méthodes publiques consultées par `Game` pour savoir si le joueur peut encore jouer. **Justification** : Séparation des responsabilités. Le mode gère sa propre condition de défaite.

8. **Protection contre les débordements** : Dans `calculateOptimalFuelPositions()`, les calculs d'index utilisent `long` puis sont clampés. **Justification** : Évite les ArrayIndexOutOfBoundsException sur de très grands labyrinthes.

---

### 12. Classe `StormMode`

**Rôle** : Mode tempête où le sable se propage progressivement depuis le départ. Le joueur doit fuir la tempête.

**Attributs clés** :
- `Set<Cell> sandCells` : Cellules recouvertes de sable
- `Set<Cell> stormFront` : Front actuel de la tempête (propagation en arc)
- `int propagationInterval` : Délai entre chaque propagation (1000ms = 1s)
- `boolean stormStarted` : La tempête démarre au premier mouvement du joueur

**Algorithme de propagation** :
1. Pour chaque cellule du front actuel
2. Ajouter ses 4 voisins au nouveau front
3. Transformer ces voisins en SAND
4. Le nouveau front devient le front actuel
5. Répéter toutes les 1 seconde

**Justifications** :

1. **Propagation en thread séparé** : La méthode `Game.startStormPropagation()` lance un thread daemon qui appelle `propagateStorm()` à intervalles réguliers. **Justification** : Évite de bloquer le thread UI. Le sable se propage pendant que le joueur joue.

2. **Démarrage au premier mouvement** : La tempête ne démarre pas immédiatement mais au premier déplacement du joueur. **Justification** : Donne au joueur le temps d'observer le labyrinthe et de planifier sa route.

3. **Propagation vers les murs** : Dans la version actuelle, même les murs de `RandomMaze` deviennent du sable. **Justification** : Après discussion, on a décidé que la tempête "recouvre" tout, y compris les obstacles. Cela simplifie le code et rend la tempête plus menaçante visuellement.

4. **Intervalle fixe de 1000ms** : Actuellement codé en dur. **Justification** : Un intervalle court (< 1s) rend le jeu trop difficile. Un intervalle long (> 2s) enlève le stress. 1s est un bon compromis.

**Évolutions possibles** :
- Intervalle décroissant (tempête accélère avec le temps)
- Cellules "refuge" qui ralentissent la propagation
- Affichage de l'historique de la tempête (couleurs dégradées)

---

### 13. Classe `RaceMode`

**Rôle** : Course contre un bot qui suit un chemin. Le joueur doit atteindre la sortie avant le bot.

**Attributs** :
- `List<Cell> botPerfectPath` : Chemin que le bot suivrait
- Dimensions et type de maze configurables

**Algorithme de préparation du bot** :
- **PerfectMaze** : Le bot suit le chemin optimal (unique)
- **RandomMaze** : Le bot part d'un point aléatoire et prend un chemin plus long que l'optimal (jusqu'à 2000 tentatives pour trouver un bon départ)

**Justifications** :

1. **Bot "fantôme"** : Le bot n'est pas affiché ni simulé en temps réel. On compare juste le nombre de mouvements du joueur à la longueur du chemin du bot. **Justification** : Simplicité. Pas besoin d'animation ou de logique de déplacement IA.

2. **Chemin plus long en RandomMaze** : La méthode `prepareBotPerfectPath()` cherche un point de départ aléatoire qui donne un chemin plus long que start→end optimal. **Justification** : Donne un handicap au bot, sinon il gagnerait toujours (il a le chemin optimal).

3. **Limite de 2000 tentatives** : Évite une boucle infinie si aucun bon départ n'est trouvé. **Justification** : Fallback vers le chemin optimal si on ne trouve pas mieux après 2000 essais.

4. **`isLost(int moveCount)`** : Compare les mouvements du joueur à la longueur du chemin du bot. **Justification** : Condition de défaite claire. Le joueur perd s'il dépasse le nombre de coups du bot.

5. **Ajout du start en double pour PerfectMaze** : `botPerfectPath.add(0, maze.getStart())` ajoute le start au début. **Justification** : Compense le fait que `findOptimalPath()` retourne un chemin de N cellules mais N-1 mouvements. Avec le start dupliqué, `size() - 1` donne le bon nombre de mouvements.

**Expérience de jeu** :
- Ajoute de la pression temporelle sans timer visible
- Le joueur doit optimiser son chemin

---

### 14. Classe `NightMode`

**Rôle** : Mode où le joueur ne voit le labyrinthe que lorsqu'il se trouve sur la case de départ.

**Attributs** :
- Hérite des attributs de configuration de base (mazeType, width, height, parameter3)
- Pas d'attributs spécifiques au mode (la logique de vision est dans `Game`)

**Justifications** :

1. **Classe très simple** : `NightMode` n'ajoute presque aucune logique. **Justification** : La gestion de la vision est déléguée à la classe `MazeRenderer`

**Implémentation de la vision** :
- Le rendu (dans `MazeRenderer`) applique un effet de masque noir sur le labyrinthe entier sauf le joueur, le départ et la sortie

**Experience de jeu** :
- Force le joueur à mémoriser le chemin

---

### 15. Classe `ProgressionMode`

**Rôle** : Mode campagne avec des étapes (levels) avec 3 défis chacune. Suit la progression du joueur.

**Attributs clés** :
- `List<Level> levels` : liste des étapes structurées
- `Profil player` : Joueur associé (progression sauvegardée)
- `int currentLevelIndex, currentChallengeIndex` : étape et difficulté selectionné par le joueur pour la partie

**Structure** :
- Étapes 1-3 : RandomMaze, tailles croissantes (15-45 cellules), pourcentages de murs croissants (40-56%)
- Étapes 4-6 : PerfectMaze, tailles moyennes (8-30 cellules), longueurs minimales croissantes (7-35)
- Chaque étape : 3 difficultés (EASY, MEDIUM, HARD)

**Justifications** :

1. **Classe `Profil` associée** : Le joueur doit être connecté pour jouer en mode progression. **Justification** : Permet de sauvegarder la progression (défis validés) et de débloquer progressivement les étapes.

2. **Progression linéaire avec déblocage** : La méthode `Profil.hasPermission(int step)` vérifie qu'au moins un défi de l'étape précédente est validé. **Justification** : Empêche de sauter directement plusieurs étapes. Le joueur doit progresser graduellement.

3. **Génération procédurale des paramètres** : Les méthodes `getRandomHeight()`, `getRandomWidth()`, `getParameterThree()` génèrent des valeurs dans des fourchettes selon l'étape. **Justification** : Variété. Deux parties de la même étape n'auront pas exactement le même labyrinthe.

4. **Étapes 1-3 = Random, 4-6 = Perfect** : Introduit progressivement le concept de labyrinthe parfait après que le joueur ait maîtrisé les labyrinthes aléatoires. **Justification** : Courbe d'apprentissage douce. Les parfaits sont plus déroutants (pas de boucles = un seul bon chemin).

5. **Méthode `buildAllLevels()`** : Construit toute la structure des 18 défis (6×3) au chargement du mode. **Justification** : Préparation en avance. Évite de recalculer à chaque fois. Les objets `Level` et `Challenge` sont légers.

6. **`selectLevelAndChallenge(int lvl, int ch)`** : Permet de choisir manuellement un défi spécifique. **Justification** : Utilisé par l'interface pour permettre au joueur de rejouer des défis déjà validés ou de continuer sa progression.

7. **Enregistrement de la complétion** : Quand le joueur finit un défi, `Game.endGame()` appelle `player.finishChallenge()` et `Saves.savePlayer()`. **Justification** : Persistance. La progression survit à la fermeture de l'application.

8. **Tableau de booléens dans `Profil`** : `boolean[18] challenge` représente l'état de chaque défi. **Justification** : Simple et efficace. L'index se calcule par `levelIndex * 3 + challengeIndex`.

**Avantages de cette structure** :
- Motivation par objectifs clairs (18 défis à valider)
- Rejouabilité (génération aléatoire dans les contraintes)
- Système de sauvegarde intégré
- Extensible (facile d'ajouter d'autres étapes)

---

### 16. Classe `Game`

**Rôle** : Classe centrale qui orchestre une partie. Relie le mode de jeu, le labyrinthe, la position du joueur et les observateurs.

**Attributs clés** :
- `Mode mode` : Mode de jeu actif
- `Cell playerPosition` : Position actuelle du joueur
- `int moveCount` : Nombre de déplacements effectués
- `Set<Cell> exploredCells` : Cellules visitées
- `List<Observer> observers` : Vues à notifier lors des changements

**Responsabilités** :
1. Gérer les déplacements du joueur avec validation
2. Détecter la fin de partie (victoire/défaite)
3. Notifier les observateurs (pattern Observer)
4. Orchestrer les mécaniques spécifiques aux modes (fuel, storm, race)

**Justifications** :

1. **Implémente `Observable`** : Permet aux vues de s'abonner et d'être notifiées automatiquement. **Justification** : Découplage. `Game` ne connaît pas les détails des vues, il appelle juste `notifyObservers()`.

2. **Méthode `movePlayer(Direction)`** : Vérifie la validité du mouvement, met à jour la position, incrémente le compteur, gère les effets du mode, et notifie. **Justification** : Point d'entrée unique pour tous les déplacements. Garantit la cohérence (impossible d'oublier de notifier ou d'incrémenter le compteur).

3. **Vérifications spécifiques aux modes** : Avant le mouvement, on teste `FuelMode.canMove()`. Après, on appelle `FuelMode.onMove()`. **Justification** : `Game` délègue au mode la logique spécifique. Il ne contient pas de gros switch/case sur le type de mode.

4. **Démarrage différé de la tempête** : `StormMode` ne démarre qu'au premier mouvement (`if (!stormMode.isStormStarted()) { stormMode.startStorm(); }`). **Justification** : Donne au joueur le temps d'observer avant de fuir.

5. **Thread daemon pour la propagation** : La méthode `startStormPropagation()` lance un thread qui appelle `propagateStorm()` en boucle. **Justification** : Non-bloquant. Le jeu reste jouable pendant la propagation. Le thread daemon se termine automatiquement à la fermeture de l'app.

6. **Méthode `isFinished()`** : Teste différentes conditions selon le mode (atteinte de la sortie, défaite par manque de fuel, rattrapage par tempête). **Justification** : Centralise la logique de fin. Les contrôleurs n'ont pas à connaître les règles de chaque mode.

7. **Méthode `reset()`** : Réinitialise le compteur, vide les cellules explorées, replace le joueur au départ. **Justification** : Permet de recommencer sans recréer tout l'objet `Game`. 

8. **`endGame()` et sauvegarde** : Si mode progression, marque le défi comme validé et sauvegarde le profil. **Justification** : Persistance automatique. Le joueur n'a pas à "sauvegarder manuellement".

9. **Attribut `joueurDirection`** : Stocke la dernière direction du joueur. **Justification** : Utilisé par le rendu pour orienter le sprite du joueur (flèche, personnage).

**Invariants maintenus** :
- `playerPosition` est toujours une cellule valide et accessible (non-mur)
- `moveCount` est incrémenté à chaque mouvement effectif
- Les observateurs sont notifiés après chaque changement d'état

**Complexité** :
- `movePlayer()` : O(1) en temps (hors notification des observateurs qui dépend du nombre d'observateurs et de leur complexité de rendu)
- `isFinished()` : O(1) avec des appels délégués aux modes

---

### 17. Classe `Profil`

**Rôle** : Représente un compte joueur avec nom, mot de passe, score et progression dans les défis.

**Attributs** :
- `String name` : Identifiant unique du joueur
- `String pwd` : Mot de passe 
- `int score` : Points accumulés
- `boolean[18] challenge` : Statut de chaque défi (validé ou non)
- `static List<Profil> listePlayers` : Liste globale de tous les joueurs chargés

**Justifications** :

1. **Validation dans le constructeur** : Le constructeur vérifie que les mots de passe correspondent, que le nom n'est pas vide et qu'il est unique. **Justification** : Fail-fast. Impossible de créer un profil invalide. Les erreurs sont détectées immédiatement via des exceptions.

2. **Liste statique `listePlayers`** : Tous les profils sont stockés dans une liste de classe. **Justification** : Simplifie l'accès global (pas besoin de passer un "PlayerManager" partout). Chargés une fois au démarrage via `Saves.loadAllPlayers()`.

3. **Méthode `goodName()`** : Parcourt `listePlayers` pour vérifier l'unicité. **Justification** : Évite les doublons. Chaque joueur a un nom unique comme clé primaire.

4. **Méthode `finishChallenge(int step, int challenge)`** : Surcharge qui convertit (étape, difficulté) en index global. **Justification** : API pratique. Le code appelant peut utiliser les indices naturels (step=2, challenge=1) sans calculer l'index absolu.

5. **Méthode `hasPermission(int step)`** : Vérifie qu'au moins un défi de l'étape précédente est validé. **Justification** : Contrôle de progression. L'étape 1 est toujours accessible, les suivantes nécessitent d'avoir réussi au moins un défi avant.

6. **Méthode `getLastValidate()`** : Retourne l'étape suivante du dernier défi validé. **Justification** : Affichage dans le menu ProgressionMode pour connaître la progression d'un profil avant de lancer.

7. **Score global** : Un entier unique, pas de score par défi. **Justification** : Le score représente le nombre total de défis réussis. Simple à comprendre et à afficher dans un leaderboard.

**Méthodes d'intégration avec `Saves`** :
- `Saves.createPlayer(Profil)` : Ajoute à la liste et écrit dans le CSV
- `Saves.savePlayer(Profil)` : Met à jour le CSV avec la progression
- `Saves.loadAllPlayers()` : Charge tous les profils au démarrage

**Extensibilité** :
- Facile d'ajouter des statistiques (temps moyen, nombre de tentatives)
- Possible d'implémenter un système d'achievements

---

### 18. Classe `Saves`

**Rôle** : Gestion de la persistance des profils joueurs dans un fichier CSV.

**Attributs statiques** :
- `String FILE_PATH = "src/main/resources/data/players.csv"`
- `String[] CSV_HEADERS` : En-têtes du fichier (Name, pwd, score, défis 1-1 à 6-3)

**Méthodes principales** :
- `loadAllPlayers()` : Charge tous les joueurs depuis le CSV dans `Profil.listePlayers`
- `createPlayer(Profil)` : Ajoute une nouvelle ligne au CSV
- `savePlayer(Profil)` : Met à jour une ligne existante avec la progression
- `updateScore(String name, int score)` : Modifie uniquement le score
- `deletePlayer(String name)` : Supprime une ligne du CSV

**Justifications** :

1. **Format CSV plutôt que base de données** : Fichier texte simple lisible avec Excel/LibreOffice. **Justification** : Projet académique ne nécessitant pas une DB complète. CSV est suffisant pour quelques dizaines de joueurs. Facile à inspecter et modifier manuellement pour les tests.

2. **Bloc static d'initialisation** : Crée le répertoire et le fichier avec en-têtes si ils n'existent pas. **Justification** : Garantit que le fichier est prêt dès le chargement de la classe. L'application ne crashe pas au premier lancement.

3. **Lecture complète en mémoire** : `loadAllPlayers()` charge tout le fichier dans `Profil.listePlayers`. **Justification** : Pour un petit nombre de joueurs (<1000), c'est plus rapide que des requêtes répétées. La liste en mémoire sert de cache.

4. **Écriture par réécriture complète** : Pour modifier un joueur, on relit tout le fichier, on modifie la ligne concernée en mémoire, puis on réécrit tout. **Justification** : Simple à implémenter. Pour un fichier de quelques Ko, la performance est acceptable. Alternative (append + garbage collection) serait plus complexe.

5. **Synchronisation du fichier et de la liste** : Après chaque modification (create, update, delete), on met à jour à la fois le CSV ET `listePlayers`. **Justification** : Cohérence. L'application n'a pas besoin de recharger le fichier après chaque opération.

6. **Méthode `playerExists()`** : Vérifie dans la liste en mémoire, pas dans le fichier. **Justification** : Plus rapide (O(n) sur la liste vs lecture I/O). Nécessite que la liste soit chargée au préalable.

7. **Gestion des erreurs avec messages console** : Les exceptions I/O sont catchées et affichées avec `System.err.println()`. **Justification** : Pour un projet académique, c'est suffisant. En production, on utiliserait un logger (Log4j, SLF4J) et des alertes utilisateur.

8. **Parsing CSV manuel** : Utilise `String.split(",")` plutôt qu'une bibliothèque CSV (OpenCSV, Apache Commons CSV). **Justification** : Dépendance en moins. Le format est simple (pas de valeurs avec virgules, pas de guillemets à échapper).

9. **Structure CSV : 3 colonnes fixes + 18 colonnes de défis** : `Name,pwd,score,1-1,1-2,1-3,...,6-3`. **Justification** : Lisible et prévisible. Chaque défi a sa colonne. Facile de voir d'un coup d'œil la progression d'un joueur.

10. **Messages de log détaillés** : Chaque opération affiche un message ("Joueur ajouté", "Score mis à jour", "X joueurs chargés"). **Justification** : Feedback développeur. Utile pour déboguer les problèmes de sauvegarde. En production, ces logs seraient configurables (niveau INFO/DEBUG).

**Sécurité et améliorations** :
- **Problème** : Mots de passe en clair dans le fichier
- **Amélioration** : Hasher les mots de passe avant écriture

**Robustesse** :
- Crée automatiquement le fichier si absent

---

### 19. Classes `Level`, `Challenge`, `Difficulty`

**Rôle** : Structurer la progression du mode campagne.

#### Classe `Level`
**Attributs** :
- `int number` : Numéro de l'étape
- `MazeType mazeType` : RANDOM ou PERFECT
- `List<Challenge> challenges` : 3 défis (Easy, Medium, Hard)

**Justifications** :
1. **Classe immuable** : Tous les attributs sont `final`. **Justification** : Une étape ne change pas après création. Immuabilité = thread-safe et sémantique claire.

2. **Méthode `getChallenges()` retourne une copie** : `return new ArrayList<>(challenges)`. **Justification** : Encapsulation. Le client ne peut pas modifier la liste interne.

3. **Méthode `toString()` détaillée** : Affiche le numéro, le type et les 3 défis. **Justification** : Utile pour le debugging et les logs. Permet de vérifier rapidement la structure d'une étape.

#### Classe `Challenge`
**Attributs** :
- `Difficulty difficulty` : Easy, Medium ou Hard
- `boolean isValid` : Défi complété ou non
- `int width, height, mazeParam` : Paramètres du labyrinthe à générer

**Justifications** :
1. **Paramètres stockés dans le défi** : Chaque challenge connaît les dimensions et paramètres de son labyrinthe. **Justification** : Cohésion. Un défi est une configuration complète, pas juste une difficulté abstraite.

2. **Méthode `validateChallenge()`** : Marque le défi comme validé. **Justification** : API explicite. Mieux que `setValid(true)` qui est moins expressif.

#### Enum `Difficulty`
**Valeurs** : `EASY`, `MEDIUM`, `HARD`

**Attributs** :
- `String label` : Nom lisible ("Facile", "Moyen", "Difficile")
- `int level` : Valeur numérique (1, 2, 3)

**Justifications** :
1. **Méthode `fromLevel(int)`** : Convertit un entier en difficulté. **Justification** : Utile pour parser des entrées utilisateur ou des fichiers de config.

2. **Labels en français** : "Facile" plutôt que "Easy". **Justification** : Application destinée à un public francophone (étudiants IUT Lille).

3. **Enum plutôt que String** : Type-safety. Impossible d'avoir une difficulté "Mdoyen" (typo). **Justification** : Robustesse à la compilation.

---

### 20. Enum `MazeType`

**Rôle** : Distinguer les deux types de labyrinthes générables.

**Valeurs** : `RANDOM`, `PERFECT`

**Justifications** :
1. **Enum minimal** : Seulement deux valeurs, pas d'attributs ni méthodes. **Justification** : Aucune logique associée nécessaire. Le type suffit pour le switch dans `prepareMaze()`.

2. **Nommage explicite** : RANDOM = aléatoire avec murs, PERFECT = parfait sans boucles. **Justification** : Noms clairs sans ambiguïté.

3. **Alternative rejetée** : Utiliser les classes (`RandomMaze`, `PerfectMaze`) directement comme types. **Justification** : L'enum permet de choisir le type avant l'instanciation. Nécessaire pour les formulaires de configuration.

---

### 21. Classe `MadMaxMode`

**Rôle** : Mode où le labyrinthe parfait change totalement (se regénère) toutes les 5 secondes

**Attributs** :
- Hérite des attributs de configuration de base (mazeType, width, height, parameter3)

**Justifications** :

1. **Régénération dynamique** : Le labyrinthe change périodiquement pour forcer le joueur à s'adapter constamment et à mémoriser rapidement son environnement immédiat avant qu'il ne disparaisse.

2. **Persistance de la sortie** : La sortie reste fixe (`savedEnd`) pour garantir que l'objectif reste atteignable et donner un point de repère stable dans le chaos.

3. **Héritage de Mode** : Implémente l'interface commune pour s'intégrer transparentement dans la boucle de jeu principale (`Game`), tout en ajoutant sa logique spécifique de mutation.

**Experience de jeu** :
- **Stress et urgence** : Le joueur doit agir vite. S'il hésite trop longtemps, le chemin qu'il avait repéré peut se fermer.
- **Adaptabilité** : Ce mode teste la capacité du joueur à réévaluer sa stratégie en temps réel plutôt que la planification à long terme.

--- 

### 22. Interfaces `Observable` et `Observer`

**Rôle** : Implémentation du pattern Observer pour le lien Model-View.

#### Interface `Observable`
**Méthodes** :
- `void addObserver(Observer)` : Abonner un observateur
- `void removeObserver(Observer)` : Désabonner
- `void notifyObservers()` : Notifier tous les observateurs
- `int getMoveCount()` : Accès au compteur (pour les observateurs)
- `Mode getMode()` : Accès au mode (pour les observateurs)

**Justifications** :

1. **Méthodes d'accès dans l'interface** : `getMoveCount()` et `getMode()` sont dans `Observable`, pas dans `Game` uniquement. **Justification** : Les observateurs ont besoin de ces infos pour s'actualiser. Inclure dans l'interface rend le contrat explicite.

2. **Pas de paramètre dans `notifyObservers()`** : Les observateurs reçoivent juste une notification, ils récupèrent les données via les getters. **Justification** : Simplicité. Alternative serait de passer un objet "Event" avec les changements, mais c'est plus complexe.

#### Interface `Observer`
**Méthode** :
- `void update(Observable modele)` : Appelée lors d'une notification

**Justifications** :
1. **Paramètre de type `Observable`** : L'observateur reçoit la référence de l'objet qui l'a notifié. **Justification** : Permet de consulter l'état actuel via les getters. Un observateur peut observer plusieurs observables.

2. **Nom `update()` plutôt que `onChanged()`** : Convention classique du pattern Observer en Java. **Justification** : Cohérence avec les implémentations standards (java.util.Observer, bien que dépréciée en Java 9+).

**Avantages du pattern Observer ici** :
- Découplage complet Model-View
- `Game` ne connaît pas `GameView`, juste l'interface `Observer`
- Facile d'ajouter plusieurs vues (mini-map, HUD séparé, spectateur)
- Testable : on peut créer des mocks d'observateurs

---

## Package View

### 23. Classe `GameView`

**Rôle** : Vue principale du jeu. Affiche le labyrinthe, le joueur, les infos (mouvements, taille) et les boutons de contrôle.

**Attributs clés** :
- `Canvas mainCanvas` : Canvas principal pour le rendu du labyrinthe (800×800px)
- `Canvas secondCanvas` : Canvas secondaire pour vue locale (200×200px)
- `Stage stage` : Fenêtre JavaFX
- `Game model` : Référence au modèle (via Observable)
- `GameController controller` : Contrôleur associé
- `int cellSize` : Taille calculée d'une cellule en pixels


**Justifications** :

1. **Implémente `Observer`** : S'abonne à `Game` dans le constructeur. **Justification** : La vue se met à jour automatiquement quand le modèle change.

2. **Deux canvas** : Un principal pour le jeu, un secondaire pour des infos additionnelles. **Justification** : Séparation des rendus. Le secondCanvas peut afficher une mini-map sans redessiner tout le labyrinthe.

3. **Calcul automatique de `cellSize`** : `cellSize = min(canvasWidth/nbCols, canvasHeight/nbRows)`. **Justification** : Adapte la taille des cellules aux dimensions du canvas et du labyrinthe. Garantit que tout le labyrinthe est visible.

4. **Boutons non focusables** : `btn.setFocusTraversable(false)`. **Justification** : Évite que les boutons capturent le focus clavier. Les touches ZQSD doivent toujours fonctionner, même si on a cliqué sur un bouton.

5. **Background image** : Méthode `setBackgroundImage()` charge une image de fons selon le mode de jeu. **Justification** : Esthétique. Rend le jeu plus attrayant qu'un fond blanc.

6. **Création du contrôleur dans la vue** : `this.controller = new GameController(game, this)`. **Justification** : La vue connaît son contrôleur. Alternative serait une injection (pattern MVC strict), mais ici la simplicité prime.

7. **Méthode `update(Observable)`** : Implémentation de `Observer`. **Justification** : Point d'entrée pour tous les rafraîchissements. Garantit la cohérence.

8. **Labels pour infos textuelles** : `movesLabel`, `sizeLabel`. **Justification** : Feedback continu au joueur. Le nombre de mouvements motive à optimiser.

9. **Boutons de zoom (+/-)** : Permettent d'agrandir/réduire la vue. **Justification** : Confort sur de grands labyrinthes. Le joueur peut zoomer sur sa position ou dézoomer pour voir l'ensemble.

10. **Bouton "Recommencer"** : Appelle `controller.onReset()`. **Justification** : UX importante. Évite de devoir quitter et relancer pour réessayer.

---

### 24. Classe `MazeRenderer`

**Rôle** : Classe utilitaire statique pour dessiner les labyrinthes sur un Canvas.

**Méthodes principales** :
- `drawMaze(Canvas, Game, int cellSize)` : Rendu complet selon le mode
- `cellColor(Cell, Game)` : Détermine la couleur d'une cellule
- `drawSimpleGame(...)` : Dessine le labyrinthe avec le joueur
- `drawPerfectMazeWalls(...)` Dessine les murs d'un labyrinthe parfait
- `drawLocalView(...)` Dessine la vue locale
- ...

**Justifications** :

1. **Classe statique** : Toutes les méthodes sont `static`. **Justification** : Pas d'état à maintenir. `MazeRenderer` est une collection d'utilitaires de dessin. Pas besoin d'instanciation.

2. **Méthode `cellColor()`** : Retourne une `Color` selon le type de cellule et le mode. **Justification** : Centralise la logique de coloration. SAND = rouge, FUEL = jaune, WALL = noir, PATH = blanc, etc.

3. **Methode `drawPerfectMazeWalls()`** : Dessine les murs comme des lignes entre les cellules. **Justification** : Représentation fidèle de la structure interne (tableaux de booléens pour les murs).

4. **Methodes de vues de mode** : Chaque mode à sa propre vue du labyrinthe

**Performances** :
- Redessine tout le labyrinthe à chaque `update()`.

---

### 25. Autres Vues

**`MainMenuView`** Menu de démarrage de l'application

**`EndGameWinView`** : Écran de victoire avec stats (nombre de mouvements, temps, score).

**`EndGameLoseView`** : Écran de défaite avec raison (manque de fuel, rattrapé par tempête, bot a gagné).

**`ProgressionModeView`** : Écran de selection du profil.

**`ProfilConnectionView`** : Écran de connexion à un profil.

**`EtapeChoiceView`** : Sélection de l'étape en mode progression.

**`BonusModeView`** : Menu de sélection des modes bonus (Fuel, Storm, Race, Night).

**`ParameterView`** : Ecran de choix des paramètres du labyrinthes (Mode Libre + Modes Bonus).

**`DifficultyChoiceView`** : Sélection de la difficulté (Easy/Medium/Hard).

**`CreateProfileView`** : Formulaire de création d'un nouveau profil.

**Justifications communes** :
1. **Vues dédiées** : Chaque écran est une classe séparée. **Justification** : Séparation des responsabilités. Facilite la maintenance et les modifications d'un écran sans affecter les autres.

2. **Passage du `Stage`** : Toutes les vues reçoivent le `Stage` en paramètre et changent sa scène. **Justification** : Réutilisation de la même fenêtre. Évite d'ouvrir plusieurs fenêtres.

3. **Style CSS** : Fichiers CSS externe pour l'apparence. **Justification** : Séparation contenu/présentation. Facilite les changements de thème.

---

## Package Controller

### 26. Classe `GameController`

**Rôle** : Gère les interactions utilisateur pendant une partie (déplacements, zoom, abandon, reset).

**Attributs** :
- `Game model` : Référence au modèle
- `GameView view` : Référence à la vue
- `double currentScale` : Niveau de zoom actuel
- `double MIN_SCALE = 0.3`, `MAX_SCALE = 4.0` : Limites de zoom

**Méthodes principales** :
- `setupListeners()` : Attache les événements aux boutons et au clavier
- `moveUp/Down/Left/Right()` : Délègue au modèle avec la bonne direction
- `zoomIn/Out()` : Modifie le scale du canvas
- `onAbandon()` : Retourne au menu
- `onReset()` : Redémarre la partie

**Justifications** :

1. **Attachement clavier via la scène** : `scene.setOnKeyPressed()` écoute les touches globalement. **Justification** : Le joueur peut utiliser ZQSD même si un bouton a le focus. Plus fluide que des boutons avec mnémoniques.

2. **Touches multiples pour chaque direction** : Z et UP pour haut, D et RIGHT pour droite. **Justification** : Compatibilité clavier AZERTY et QWERTY. Flèches pour ceux qui préfèrent.

3. **Méthode `move(Direction)` privée** : Factorisation du code commun aux 4 directions. **Justification** : DRY (Don't Repeat Yourself). La logique de fin de partie n'est écrite qu'une fois.

4. **Vérification de fin de partie après chaque mouvement** : `if (model.isFinished())` ouvre l'écran approprié (Win ou Lose). **Justification** : Feedback immédiat. Le joueur n'a pas à cliquer sur un bouton "Terminer".

5. **Désabonnement de l'observateur** : `model.removeObserver(view)` avant de changer d'écran. **Justification** : Évite les fuites mémoire. La vue ne sera plus notifiée alors qu'elle n'est plus affichée.

6. **Zoom avec limites** : Clamping entre MIN_SCALE et MAX_SCALE. **Justification** : Empêche un zoom infini qui rendrait le labyrinthe invisible (trop petit) ou hors de l'écran (trop grand).

7. **Zoom via transform du canvas** : `canvas.setScaleX/Y()`. **Justification** : Transformation graphique rapide. Pas besoin de redessiner, juste de rescaler l'image existante.

8. **Touches +/- pour zoom** : En plus des boutons. **Justification** : Raccourcis clavier pour les utilisateurs avancés.

9. **Retour au menu en mode progression** : Si en `ProgressionMode`, retourne à `EtapeChoiceView` plutôt qu'au menu principal. **Justification** : Préserve le contexte. Le joueur reste dans la campagne.

**Responsabilités du contrôleur** :
- Traduire les événements UI en appels au modèle
- Gérer les transitions d'écrans
- Ajuster les paramètres de vue (zoom)
- **Ne contient PAS** de logique métier (pas de vérification de mouvement valide, c'est dans `Game`)

---

### 27. Autres Contrôleurs

**`MainMenuController`** : Gère les actions du menu principal.

**`BonusModeController`** : Gère la sélection des modes bonus.

**`ParameterController`** : Valide les champs de saisie, crée le `Game`, lance `GameView`.

**`ProfilConnectionController`** : Vérifie les identifiants, charge le profil ou affiche une erreur.

**`CreateProfileController`** : Valide le formulaire de création (mots de passe identiques, nom unique), appelle `Saves.createPlayer()`.

**`EtapeChoiceController`** : Vérifie les permissions (`Profil.hasPermission()`), sélectionne le niveau et le défi, lance le jeu.

**`DifficultyChoiceController`** : Sélectionne la difficulté, configure le mode, continue vers `ParameterView` ou `GameView`.

**`EndGameWinController / EndGameLoseController`** : Gèrent les boutons "Rejouer", "Menu", "Étape suivante".

**Justifications communes** :
1. **Validation dans le contrôleur** : Les contrôleurs vérifient les entrées avant de les passer au modèle. **Justification** : Défense en profondeur. Le modèle ne reçoit que des données valides.

2. **Affichage des erreurs** : Via des `Alert` JavaFX ou des labels d'erreur. **Justification** : Feedback immédiat. L'utilisateur sait pourquoi son action a échoué.

3. **Pas de logique métier** : Les contrôleurs ne calculent pas de chemins, ne génèrent pas de labyrinthes. **Justification** : Respect de l'architecture MVC. La logique métier est dans le modèle.

---

## Patterns de Conception

### 28. Pattern MVC (Model-View-Controller)

**Application dans le projet** :
- **Model** : `Game`, `Maze`, `Mode`, `Profil`, `Cell`, ...
- **View** : `GameView`, `MainMenuView`, ... (JavaFX)
- **Controller** : `GameController`, `MainMenuController`, ...

**Justifications** :
1. **Séparation stricte** : Le modèle ne connaît pas la vue (interface `Observer` comme seul lien). **Avantage** : On peut changer l'UI sans toucher au modèle.

2. **Contrôleurs comme médiateurs** : Traduisent les événements UI en appels métier. **Avantage** : Logique de navigation centralisée, pas éparpillée dans les vues.

3. **Testabilité** : Le modèle peut être testé sans lancer l'UI. **Avantage** : Tests unitaires rapides et fiables.

---

### 29. Pattern Observer

**Implémentation** : `Observable` (interface), `Observer` (interface), `Game` (sujet), `GameView` (observateur).

**Justifications** :
1. **Notification automatique** : La vue se met à jour dès que le modèle change. **Avantage** : Cohérence garantie. Pas de risque d'oublier de rafraîchir.

2. **Découplage** : Le modèle ne dépend pas des vues concrètes. **Avantage** : Facilite les tests (mocks d'observateurs) et l'ajout de nouvelles vues.

3. **Performance** : Les observateurs sont notifiés uniquement quand nécessaire, pas en polling. **Avantage** : Économie de CPU.

---

### 30. Pattern Strategy

**Implémentation** : Interface `Mode`, classes `FreeMode`, `FuelMode`, `StormMode`, etc.

**Justifications** :
1. **Algorithmes interchangeables** : Le `Game` peut utiliser n'importe quel mode sans connaître les détails. **Avantage** : Extensibilité. Ajouter un mode = créer une nouvelle classe implémentant `Mode`.

2. **Élimination des conditionnelles** : Pas de `if (modeName == "fuel")` partout. **Avantage** : Code plus propre et maintenable.

3. **Polymorphisme** : `game.getMode().getMaze()` fonctionne quel que soit le mode. **Avantage** : Réutilisation du code client.

---

### 31. Pattern Singleton (pour `Saves`)

**Implémentation** : Méthodes et attributs statiques dans `Saves`.

**Justifications** :
1. **Instance unique implicite** : Pas besoin de passer un objet `SaveManager` partout. **Avantage** : Simplicité d'accès (`Saves.loadAllPlayers()`).

2. **État global** : `listePlayers` est partagé entre toutes les parties du code. **Avantage** : Cohérence. Tous les modules voient les mêmes joueurs.

---

## Conclusion

Ce projet démontre une solide maîtrise des concepts de programmation orientée objet et d'architecture logicielle :

**Séparation des responsabilités** : MVC strict, chaque classe a un rôle clair

**Utilisation de patterns** : Observer, Strategy...

**Extensibilité** : Facile d'ajouter de nouveaux modes, types de cellules, labyrinthes

**Robustesse** : Validations, gestion d'erreurs, génération avec réessais

**Lisibilité** : Nommage explicite, documentation Javadoc, code structuré

**Fonctionnalités complètes** : 6 modes de jeu, système de progression, sauvegarde

Les choix de conception reflètent un équilibre entre simplicité (pour un projet académique) et qualité professionnelle (patterns, architecture propre). Le code est maintenable et évolutif, prêt pour des extensions futures.

---

**Auteurs** : Groupe G4 - SAE 3.3  
**Date** : Novembre 2025  
**Technologies** : Java 17, JavaFX 20, Maven

