module com.application {
    requires java.sql;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.controls;
    requires org.xerial.sqlitejdbc;

    requires transitive javafx.graphics;

    opens com.controllers to javafx.fxml;
    exports com.application;
}
