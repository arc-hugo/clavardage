module clavardage {
	requires javafx.base;
	requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
	requires java.prefs;
	requires java.sql;
	requires com.github.albfernandez.juniversalchardet;

    opens gei.barralberry.clavardage to javafx.fxml;
    opens gei.barralberry.clavardage.controleurs to javafx.fxml;
    
    exports gei.barralberry.clavardage;
    exports gei.barralberry.clavardage.concurrent;
    exports gei.barralberry.clavardage.controleurs;
    exports gei.barralberry.clavardage.modeles.session;
    exports gei.barralberry.clavardage.modeles.utilisateurs;
    exports gei.barralberry.clavardage.reseau;
    exports gei.barralberry.clavardage.reseau.messages;
    exports gei.barralberry.clavardage.reseau.services;
    exports gei.barralberry.clavardage.reseau.taches;
    exports gei.barralberry.clavardage.util;
}