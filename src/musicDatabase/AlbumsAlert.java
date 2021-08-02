package musicDatabase;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;

public class AlbumsAlert extends Alert {
	
	private TextField albumField = new TextField();

	public AlbumsAlert() {
		super(AlertType.CONFIRMATION);
		GridPane grid = new GridPane(); 
		grid.addRow(1, new Label("Name of the album:  "), albumField); 
		grid.setVgap(12); 
		getDialogPane().setContent(grid); 
		setTitle("Enter the name"); 
		setHeaderText(null);
	}

	public String getAlbum() {
		return albumField.getText();
	}
}
