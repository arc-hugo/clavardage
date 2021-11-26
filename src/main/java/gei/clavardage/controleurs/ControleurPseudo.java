package gei.clavardage.controleurs;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class ControleurPseudo implements Initializable {
	
	@FXML
	private TextField pseudo;
	@FXML
	private Button validate_button;
	
	private String txt;
	
	public String getTxt() {
		return txt;
	}
	
	@FXML
	private void new_pseudo () {
		this.txt = pseudo.getText();
		this.pseudo.getScene().getWindow().hide();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	
}
