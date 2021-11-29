module clavardage {
	requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens gei.clavardage to javafx.fxml;
    opens gei.clavardage.controleurs to javafx.fxml;
    
    exports gei.clavardage;
    exports gei.clavardage.controleurs;
}