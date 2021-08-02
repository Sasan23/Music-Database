package musicDatabase;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

public class ArtistsAlert extends Alert {
	
	private TextField artistField = new TextField();

	public ArtistsAlert() {
		super(AlertType.CONFIRMATION);
		GridPane grid = new GridPane(); 
		grid.addRow(1, new Label("Name of the artist:  "), artistField); 
		grid.setVgap(12); 
		getDialogPane().setContent(grid); 
		setTitle("Enter the name"); 
		setHeaderText(null);
	}

	public String getArtist() {
		return artistField.getText();
	}
}



