package musicDatabase;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AlbumAndArtistAlert extends Alert {
	
	private TextField albumField = new TextField();
	private TextField artistField = new TextField();

	public AlbumAndArtistAlert() {
		super(AlertType.CONFIRMATION);
		GridPane grid = new GridPane(); 
		grid.addRow(1, new Label("Name of the album:  "), albumField); 
		grid.addRow(2, new Label("Name of the artist:  "), artistField); 
		grid.setVgap(12); 
		getDialogPane().setContent(grid); 
		setTitle("Enter the album name and the artist it belongs to"); 
		setHeaderText(null);
	}
	
	public String getAlbum() {
		return albumField.getText();
	}
	
	public String getArtist() {
		return artistField.getText();
	}
}
