module battle.arena {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;

    exports application;
    exports characters;
    exports weapons;
    exports projectiles;
    exports animations;
    exports ui;
    exports audio;
    exports input;
    exports physics;
    exports config;

    opens application to javafx.fxml;
    opens characters to javafx.fxml;
    opens weapons to javafx.fxml;
}