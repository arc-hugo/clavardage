package gei.barralberry.clavardage.controleurs;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class ControleurPseudo implements Initializable {

	@FXML
	private TextField pseudo;
	@FXML
	private Button validate_button;
	@FXML
	private ButtonBar buttonbar;

	private String txt;
	private int x = 0;
	private int y = 0;

	static private boolean actif = false;

	public ControleurPseudo() {
		ControleurPseudo.actif = true;
		this.txt = "";
	}

	public String getTxt() {
		return txt;
	}

	public static boolean estActif() {
		return ControleurPseudo.actif;
	}

	@FXML
	private void new_pseudo() {
		this.txt = this.pseudo.getText();
		ControleurPseudo.actif = false;
		this.pseudo.getScene().getWindow().hide();
	}

	@FXML
	private void ferme() {
		Stage st;
		st = (Stage) this.pseudo.getScene().getWindow();
		st.close();
	}

	@FXML
	private void diminue() {
		Stage st;
		st = (Stage) this.pseudo.getScene().getWindow();
		st.setIconified(true);
	}

	@FXML
	private void change() {
		Stage st;
		st = (Stage) this.pseudo.getScene().getWindow();
		if (st.isFullScreen()) {
			st.setFullScreen(false);
		} else {
			st.setFullScreen(true);
		}
	}

	@FXML
	private void dragged(MouseEvent event) {
		Stage stage = (Stage) buttonbar.getScene().getWindow();
		stage.setY(event.getScreenY() - y);
		stage.setX(event.getScreenX() - x);
	}

	@FXML
	private void pressed(MouseEvent event) {
		x = (int) event.getSceneX();
		y = (int) event.getSceneY();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.pseudo.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				new_pseudo();
			}
		});
	}
}
