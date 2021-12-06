module clavardage {
	requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.base;

    opens gei.clavardage to javafx.fxml;
    opens gei.clavardage.controleurs to javafx.fxml;
    
    exports gei.clavardage;
    exports gei.clavardage.controleurs;
    exports gei.clavardage.modeles;
    exports gei.clavardage.reseau;
    exports gei.clavardage.reseau.services;
}