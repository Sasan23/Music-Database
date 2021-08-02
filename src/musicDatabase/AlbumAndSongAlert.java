package musicDatabase;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AlbumAndSongAlert extends Alert {
	
	private TextField albumField = new TextField();
	private TextField songField = new TextField();

	public AlbumAndSongAlert() {
		super(AlertType.CONFIRMATION);
		GridPane grid = new GridPane(); 
		grid.addRow(1, new Label("Name of the album:  "), albumField);
		grid.addRow(2, new Label("Name of the song:  "), songField); 
		grid.setVgap(12); 
		getDialogPane().setContent(grid); 
		setTitle("Enter the song and the album it belongs to"); 
		setHeaderText(null);
	}
	
	public String getAlbum() {
		return albumField.getText();
	}
	
	public String getSong() {
		return songField.getText();
	}
}
