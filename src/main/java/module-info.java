<<<<<<< HEAD
module com.example.javafxdemo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.javafxdemo to javafx.fxml;
    exports com.example.javafxdemo;
}
=======
module com.game {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.game to javafx.fxml;
    exports com.game;
}
>>>>>>> 3c55dcba0d2effcf3f564f232a0abcca3dfd8008
