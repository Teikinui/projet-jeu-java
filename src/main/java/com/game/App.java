package com.game;

import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // On crée un conteneur vide (pas de FXML)
        Pane pane = new Pane();
        pane.setPrefSize(640, 480);

        // On crée et configure la scène
        Scene scene = new Scene(pane, 640, 480);


        // On crée le gestionnaire du clavier
        InputHandler inputHandler = new InputHandler();

        // On crée notre personnage
        Player monPersonnage = new Player(0, 400, 32, 32, 32, "/Images/Paysan2.png", inputHandler);

        // On ajoute l'image du personnage à la scène
        pane.getChildren().add(monPersonnage.sprite);

        // On initialise la gestion du clavier
        inputHandler.pollEvents(scene);

        // On crée la boucle de jeu
        AnimationTimer gameLoop = new AnimationTimer() {

            private long previous = 0;

            @Override
            public void handle(long now) {

                if (previous == 0) {
                    previous = now;
                    return;
                }

                double dt = (now - previous) / 1_000_000_000.0;

                System.out.println(dt);

                monPersonnage.update(dt);

                previous = now;
            }
        };

        gameLoop.start();

        // On affiche la fenêtre
        stage.setScene(scene);
        stage.setTitle("Mon jeu 2D");
        stage.show();
        pane.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}