# TripTrap

TripTrap est un jeu de plateformes 2D en Java et libGDX dans lequel tu traverses huit pays aux pièges et aux règles surprenantes. Observe, apprends de tes morts et atteins chaque objectif avant la fin du chrono.

## Aperçu / Démo

Le voyage : **Tahiti → Chine → Tunisie → Vietnam → Sri Lanka → Turquie → France → Laponie**.

Exemple de déroulement :

```text
Menu → Choix du pays → Exploration et pièges → Objectif → Pays suivant
                              ↓
                             Mort → Retour au départ du pays
```

Chaque pays dispose de **180 secondes**. Après une mort, le joueur revient au départ et le niveau est rechargé, mais le chrono ne repart pas à zéro. Au Vietnam, les commandes horizontales sont inversées ; en Laponie, le sol est glissant.

*Aucune capture d’écran ni aucun GIF de démonstration n’est encore inclus dans ce README.*

## Prérequis & Installation

### Prérequis

- **JDK 21** installé, accessible via `JAVA_HOME` ou le `PATH`.
- **Git** pour cloner le dépôt.
- Une connexion Internet au premier lancement pour télécharger Gradle et les dépendances.
- Un environnement graphique pour jouer sur ordinateur.

Le **Gradle Wrapper** est inclus : aucune installation séparée de Gradle ou de Maven n’est nécessaire.

### Installation

```sh
git clone https://github.com/Teikinui/projet-jeu-java.git
cd projet-jeu-java
java -version
```

### Lancement

Depuis la racine du dépôt, sous **Windows / PowerShell** :

```powershell
.\gradlew.bat :lwjgl3:run
```

Sous **Linux / macOS / Git Bash** :

```sh
./gradlew :lwjgl3:run
```

Sous Linux ou macOS, si nécessaire : `chmod +x gradlew`.

Dans **IntelliJ IDEA**, ouvre le dossier comme projet Gradle, sélectionne le JDK 21 pour la JVM Gradle, synchronise puis lance **lwjgl3 → application → run**. Cette tâche configure le répertoire de travail pour retrouver les ressources.

## Utilisation

### Jouer

1. Sélectionne ton pays de départ avec **↑ / ↓**, puis appuie sur **Entrée**.
2. Déplace-toi avec **← / →** et saute avec **↑** ou **Espace**.
3. Évite les pièges et atteins l’objectif pour passer au pays suivant.
4. En cas de mort, recommence depuis le départ du pays avec le temps restant.

| Contexte | Touche | Action |
| --- | --- | --- |
| Partie | M | Activer ou couper les sons |
| Partie | Échap | Retourner au menu |
| Écran de fin | R | Relancer une partie |
| Écran de fin | Entrée ou Échap | Retourner au menu |

### Modifier un niveau

Les cartes sont dans `assets/*.txt` : **30 colonnes × 17 lignes**, avec des cases de **32 pixels**.

Par exemple, pour déplacer une coco à Tahiti, déplace son caractère `c` dans `assets/tahiti.txt`, puis relance le jeu.

| Symbole | Signification |
| --- | --- |
| `#` | Mur ou sol solide |
| `.` | Espace vide |
| `P` | Point de départ du joueur |
| `G` | Position initiale de l’objectif |
| `c` | Coco tombante |
| `s` | Étoile de mer |
| `q` | Sable mouvant |

Conserve les dimensions et un seul `P` et `G` par carte. Les autres types de pièges sont définis dans [`TrapType.java`](core/src/main/java/com/teikinui/game/model/traps/TrapType.java).

## Structure du projet

```text
assets/
├── *.txt                     Cartes des huit pays
├── sprites/                  Images du jeu et de l’interface
└── sounds/                   Ressources audio
core/src/
├── main/java/com/teikinui/game/
│   ├── TripTrapGame.java      Point d’entrée logique du jeu
│   ├── app/                  Lecture du clavier
│   ├── level/                Chargement, pays et progression
│   ├── model/                Joueur, collisions, événements et pièges
│   └── view/                 Écrans, rendu, HUD et audio
└── test/java/                Tests automatisés
lwjgl3/src/main/java/         Lanceur desktop LWJGL3
.github/workflows/            Vérifications GitHub Actions
```

**`core`** contient la logique et les écrans ; **`lwjgl3`** démarre l’application sur ordinateur. libGDX fournit les outils d’affichage, d’entrée et d’audio ; les règles et les collisions sont implémentées dans le projet.

## Tests & Contribution

### Lancer les tests

Exécute les tests et compile le lanceur desktop, comme dans la CI :

```sh
./gradlew test :lwjgl3:classes
```

Sous PowerShell :

```powershell
.\gradlew.bat test :lwjgl3:classes
```

Pour cibler une classe :

```sh
./gradlew :core:test --tests '*TahitiMapTest'
```

Les tests utilisent **JUnit Jupiter** et la couverture est calculée avec **JaCoCo**.

- Rapport de tests : `core/build/reports/tests/test/index.html`.
- Rapport de couverture : `core/build/reports/jacoco/test/html/index.html`.
- Workflow CI : [`.github/workflows/java-tests.yml`](.github/workflows/java-tests.yml).

### Contribuer

1. Crée une branche depuis la version actuelle de `main`.
2. Réalise des changements ciblés et ajoute ou adapte les tests associés.
3. Lance les tests et vérifie le comportement en jeu si nécessaire.
4. Enregistre tes changements dans des commits, pousse ta branche, puis ouvre une **Pull Request** vers `main` en décrivant les modifications.
5. Attends la réussite des vérifications avant la fusion.

Après une modification de carte, certains tests peuvent nécessiter une adaptation du scénario ou des positions attendues. Vérifie la cause de l’échec sans désactiver les contrôles du comportement du jeu.

## Licence

Aucun fichier de licence n’est actuellement présent dans le dépôt. Les conditions de réutilisation du code et des ressources restent à préciser par les auteurs ; le projet ne doit pas être considéré comme libre de droits.
