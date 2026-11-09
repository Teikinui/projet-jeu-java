# TripTrap — jeu libGDX

Jeu Java/libGDX pour ordinateur. Le projet Gradle contient `core` (code du jeu) et `lwjgl3` (lanceur desktop); les fichiers de niveaux et ressources sont dans `assets/`.

## Prérequis

Le projet utilise Java 21, configuré comme toolchain Gradle dans les modules `core` et `lwjgl3`.

## Lancer le jeu

À la racine du projet, lance :

```text
./gradlew :lwjgl3:run
```

Sous Windows, l’équivalent est `gradlew.bat :lwjgl3:run`. Dans IntelliJ IDEA, ouvre le dossier comme projet Gradle, synchronise-le, puis lance la tâche `lwjgl3 > application > run`.

La classe de jeu est `core/src/main/java/com/teikinui/game/TripTrapGame.java`; la carte Tahiti est `assets/tahiti.txt`.