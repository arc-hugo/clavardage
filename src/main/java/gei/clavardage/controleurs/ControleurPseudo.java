package gei.clavardage.controleurs;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ControleurPseudo implements Initializable {
	
	@FXML
	private TextField pseudo;
	@FXML
	private Button validate_button;
	
	private String txt;
	
	public ControleurPseudo() {
		this.txt = "";
	}
	
	public String getTxt() {
		return txt;
	}
	
	@FXML
	private void new_pseudo () {
		this.txt = this.pseudo.getText();
		this.pseudo.getScene().getWindow().hide();
	}
	
	@FXML 
	private void ferme() {
		this.pseudo.getScene().getWindow().hide();
	}
	
	@FXML 
	private void diminue() {
		Stage st;
		st = (Stage)this.pseudo.getScene().getWindow();
		st.setFullScreen(false);
	}
	
	
	@FXML 
	private void augmente() {
		Stage st;
		st = (Stage)this.pseudo.getScene().getWindow();
		st.setFullScreen(true);
	}
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	
}
