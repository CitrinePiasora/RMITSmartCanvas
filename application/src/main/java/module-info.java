module com.application {
    requires transitive java.desktop;

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires jai.imageio.core;
    requires javafx.swing;
    requires org.apache.commons.io;

    requires transitive javafx.graphics;
    requires transitive json.simple;
    

    opens com.controllers to javafx.fxml;
    exports com.application;
}
