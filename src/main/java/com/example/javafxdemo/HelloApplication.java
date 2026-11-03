package com.example.javafxdemo;

import javafx.application.Application;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HelloApplication extends Application {
    private final Pane root = new Pane();
    private Scene scene;
    private Timeline gameLoop;

    @Override
    public void start(Stage stage) {
        this.scene = new Scene(root, 1280, 720);

        GameEventHandler eventHandler = new GameEventHandler();
        scene.setOnKeyPressed(eventHandler::keyPressed);
        scene.setOnKeyReleased(eventHandler::keyReleased);

        Player player = new Player(
                "/com/example/javafxdemo/cube.png",
                new Point2D(100, 100),
                4,
                eventHandler
        );
        root.getChildren().add(player.sprite);

        gameLoop = new Timeline(new KeyFrame(Duration.seconds(1.0 / 60), event -> player.update()));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

        stage.setTitle("JavaFX 2D Game");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}
