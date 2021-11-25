module gei.clavardage {
    requires javafx.controls;
    requires javafx.fxml;

    opens gei.clavardage to javafx.fxml;
    exports gei.clavardage;
}
