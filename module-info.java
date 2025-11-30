module QuanLyDuAn_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens application to javafx.graphics, javafx.fxml;
    opens application.controllers to javafx.fxml;
    opens application.models to javafx.base;
    opens application.utils to java.sql;

    exports application;
}
