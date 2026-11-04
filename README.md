# Java_projet — base libGDX

Projet Java libGDX minimal pour ordinateur, généré avec Gradle : un module `core` pour le jeu et un module `lwjgl3` pour le lancer sur desktop.

## Lancer dans IntelliJ IDEA

1. Ouvre le dossier `Java_projet` comme projet Gradle.
2. Attends la synchronisation Gradle.
3. Lance la tâche Gradle `lwjgl3 > Tasks > application > run`.

Le projet utilise le JDK 21 déjà installé sur ton ordinateur. Le wrapper Gradle (`gradlew.bat`) sera ajouté dès que le téléchargement de Gradle sera accessible. En attendant, utilise Gradle intégré à IntelliJ pour synchroniser et lancer le projet.

Le code du jeu commence dans `core/src/main/java/com/teikinui/game/MyGdxGame.java`.

commande : gradlew.bat :lwjgl3:run