package com.game;

import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        // On charge le fichier FXML
        FXMLLoader loader = new FXMLLoader(App.class.getResource("primary.fxml"));
        Parent root = loader.load();

        // On récupère le conteneur
        Pane pane = (Pane) root;

        // On crée le gestionnaire du clavier
        InputHandler inputHandler = new InputHandler();

        // On crée notre personnage
        Player monPersonnage = new Player(
                100, 100, 5,
                "/images/Paysan2.png",
                inputHandler
        );

        // On ajoute l'image du personnage à la scène
        pane.getChildren().add(monPersonnage.sprite);

        // On crée et configure la scène
        scene = new Scene(root, 640, 480);

        // On initialise la gestion du clavier
        inputHandler.pollEvents(scene);

        // On crée la boucle de jeu
        AnimationTimer gameLoop = new AnimationTimer() {

        private long previous = 0;

        @Override
        public void handle(long now) {

            // Calcul du temps écoulé depuis la dernière image
            if (previous == 0) {
            previous = now;
            return;
            }

            double dt = (now - previous) / 1_000_000_000.0;

            // Afficher le temps écoulé
            System.out.println(dt);

            // Mettre à jour le personnage
            monPersonnage.update();

            // Mémoriser le temps actuel
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

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource(fxml + ".fxml")
        );
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
