# Résumé — Bootstrap JavaFX 2D

## Objectif actuel

Créer une fenêtre JavaFX contenant un cube que l’on déplace avec les **flèches du clavier**. Le PDF propose un personnage et ZQSD ; le projet a été adapté à la demande de Teikinui (cube + flèches).

## Fichiers mis en place

### `src/main/java/com/example/javafxdemo/HelloApplication.java`

- Point d’entrée JavaFX déjà utilisé par le lanceur du projet ; conservé sous le nom `HelloApplication` pour ne pas casser la configuration Maven existante.
- Crée une fenêtre de 1280 × 720 avec un `Pane` racine et une `Scene`.
- Crée le gestionnaire de touches, branche les événements de pression et de relâchement, crée le joueur et ajoute son sprite au `Pane`.
- Démarre une `Timeline` avec un intervalle d’environ 1/60 de seconde et appelle `player.update()` à chaque tick.
- Arrête la boucle dans `stop()` quand l’application se ferme.

### `src/main/java/com/example/javafxdemo/Entity.java`

- Classe abstraite de base des objets du jeu.
- Stocke une position `Point2D` privée et la fournit avec `getPosition()`.
- Charge l’image du sprite depuis les ressources du projet ; conserve l’image (`spriteSheet`) et l’`ImageView` (`sprite`).
- Le changement de position met à jour les coordonnées de l’`ImageView`.
- Déclare `update()` comme méthode abstraite.

> Le PDF demandait une position et un getter, sans imposer le type. `Point2D` a été choisi comme type par défaut pour contenir X et Y.

### `src/main/java/com/example/javafxdemo/Player.java`

- Joueur concret qui hérite de `Entity`.
- A une vitesse de 4 pixels par mise à jour et affiche son image à 64 × 64 pixels.
- `update()` déplace le cube selon les flèches maintenues. Les déplacements diagonaux sont normalisés afin de ne pas aller plus vite en diagonale.

### `src/main/java/com/example/javafxdemo/GameEventHandler.java`

- Mémorise les touches fléchées actuellement pressées et les retire lorsqu’elles sont relâchées.
- Utilise `KeyCode` : les flèches n’ont pas de caractère imprimable fiable à récupérer avec `getText()`.

### `src/main/resources/com/example/javafxdemo/cube.png`

- Sprite PNG d’un petit cube bleu isométrique, placé dans les ressources chargées par Maven.

## Configuration Java / Maven

- `pom.xml` cible Java 21 avec `maven.compiler.release` et `maven-compiler-plugin`.
- `.mvn/wrapper/maven-wrapper.properties` pointe vers Maven 3.9.11 et le wrapper 3.3.2.
- L’application conserve le nom de classe principal référencé dans la configuration Maven.
- Les fichiers FXML de démarrage du modèle peuvent rester présents, mais `HelloApplication` ne les charge plus.

## Vérification effectuée

- `clean test package` exécuté avec succès ; l’archive `target/JavaFX-demo-1.0-SNAPSHOT.jar` a été créée.
- Vérifié que Maven copie `cube.png` dans `target/classes/com/example/javafxdemo/cube.png`.
- Test de lancement JavaFX : le processus est resté actif pendant 8 secondes sans erreur de démarrage.
- **À vérifier manuellement :** confirmer visuellement que le cube apparaît puis suit les quatre flèches. Le déplacement au clavier n’a pas encore été testé par simulation.

## Pour lancer

Depuis le terminal IntelliJ, à la racine du projet :

```text
mvnw.cmd javafx:run
```

Si la commande ne fonctionne pas dans le terminal intégré, recharger le projet Maven dans IntelliJ puis lancer la configuration `HelloApplication`.

## Définitions utiles

### GDD — Game Design Document

Un document de conception qui décrit le jeu avant sa réalisation : son idée, son objectif, ses règles, ses commandes et les éléments qu’il contient. Il sert de plan pour savoir quoi construire. Pour ce projet, le GDD peut dire que le joueur déplace un cube dans une fenêtre avec les flèches.

### UML initial

Un schéma UML représente visuellement les classes d’un programme et leurs relations. « Initial » signifie qu’il montre la conception prévue au début ; il peut être mis à jour quand le projet évolue. Dans ce jeu, le schéma peut montrer que `Player` hérite de `Entity` et que `HelloApplication` crée le joueur et lance la boucle du jeu.

### Maven

Maven est un outil de construction de projets Java. Il lit `pom.xml` pour connaître la version de Java, les bibliothèques nécessaires et les étapes de construction. Ici, le projet cible Java 21 et utilise JavaFX. Le Maven Wrapper (`mvnw.cmd` sous Windows) permet d’utiliser la version de Maven configurée pour le projet sans demander à chacun de l’installer séparément.

### Design pattern Java — patron de conception

Un design pattern est une manière réutilisable d’organiser le code pour résoudre un problème courant. Ce n’est pas une bibliothèque ni une règle obligatoire : on l’emploie quand il clarifie réellement la conception.

Exemples courants :

- **MVC (Model–View–Controller)** sépare les données, leur affichage et la logique qui reçoit les actions. Le projet de départ utilisait FXML et un contrôleur ; le bootstrap du jeu demande toutefois de ne pas utiliser FXML.
- **Observer** permet à un objet d’être averti lorsqu’un événement survient. Le système d’événements JavaFX fonctionne selon une idée proche : la scène reçoit les événements clavier et le gestionnaire réagit aux touches pressées ou relâchées.
- **Factory** centralise la création d’objets lorsque leur création demande plusieurs choix ou étapes.
- **Strategy** permet de remplacer une manière de faire par une autre sans réécrire le code qui l’utilise.
- **Singleton** limite une classe à une seule instance ; il faut l’utiliser avec prudence, car il rend les dépendances et les tests plus difficiles.

Dans ce bootstrap, `Entity` et `Player` illustrent surtout l’héritage et le polymorphisme (`Player` fournit sa version de `update()`). Ce sont des mécanismes Java utiles, mais pas à eux seuls un design pattern formel.
