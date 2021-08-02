package musicDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MusicDatabaseManager extends Application {
	
    public static final String CONNECTION_STRING = "jdbc:sqlite:music.db"; // The path. Use SQLite.  
    private Connection conn; 
	private Stage stage;
	private Pane center;
	private BorderPane root;
	private ListView<String> songs;
	private ListView<String> artists;
	private ListView<String> albums;
	private String latestQuery;
	private RadioButton asc;
	private RadioButton desc;
    
	@Override
	public void start(Stage stage) throws Exception {
		
		this.stage = stage;
		root = new BorderPane();
		VBox top = new VBox();
	    root.setTop(top);
	    FlowPane flow = new FlowPane();
	    top.getChildren().add(flow);
	    flow.setAlignment(Pos.CENTER);
	    flow.setHgap(10); 
	    flow.setVgap(5); 
	    flow.setPadding(new Insets(10)); 
	    
	    // Buttons for showing songs, artists, and albums. 
	    Button songsButton = new Button("Show all songs");
	    songsButton.setPrefWidth(150);
	    songsButton.setPrefHeight(40);
	    songsButton.setFont(new Font(15));
	    Button artistsButton = new Button("Show all artists");
	    artistsButton.setPrefWidth(150);
	    artistsButton.setPrefHeight(40);
	    artistsButton.setFont(new Font(15));
	    Button albumsButton = new Button("Show all albums");
	    albumsButton.setPrefWidth(150);
	    albumsButton.setPrefHeight(40);
	    albumsButton.setFont(new Font(15));
	    flow.getChildren().addAll(songsButton, artistsButton, albumsButton);
	    
	    songsButton.setOnMouseClicked(new SongsHandler());
	    artistsButton.setOnMouseClicked(new artistsHandler());
	    albumsButton.setOnMouseClicked(new albumsHandler());
	    
	    // Buttons for ascending and descending order. 
	    asc = new RadioButton("Ascending order");
	    desc = new RadioButton("Descending order");
	    ToggleGroup group = new ToggleGroup();
	    asc.setToggleGroup(group);
	    desc.setToggleGroup(group);
        flow.getChildren().addAll(asc, desc);
        asc.setOnAction(new ascHandler());
        desc.setOnAction(new descHandler());
	    
        // The ListViews for the songs, artists, albums. 
	    songs = new ListView<String>();
	    songs.setPrefWidth(600);
	    songs.setPrefHeight(473);
	    artists = new ListView<String>();
	    artists.setPrefWidth(600);
	    artists.setPrefHeight(473);
	    albums = new ListView<String>();
	    albums.setPrefWidth(600);
	    albums.setPrefHeight(473);
		
	    center = new Pane(); 
	    root.setCenter(center);
	    center.getChildren().add(songs);
	    
	    // The bottom pane. 
	    VBox bottom = new VBox();
	    root.setBottom(bottom);
	    FlowPane bottomFlow = new FlowPane();
	    bottom.getChildren().add(bottomFlow);
	    bottomFlow.setHgap(10); 
	    bottomFlow.setVgap(5); 
	    bottomFlow.setPadding(new Insets(10)); 
	    bottomFlow.setAlignment(Pos.CENTER);
		bottomFlow.setStyle("-fx-background-color: #89CFF0;"); 
	    
		// The menu. 
	    MenuBar menuBar = new MenuBar();
	    bottomFlow.getChildren().add(menuBar);
	    Menu menu = new Menu("Search or Add"); 
	    menuBar.getMenus().add(menu);
        MenuItem songsByArtist = new MenuItem("Find all songs by an artist");  
        menu.getItems().add(songsByArtist); 
        songsByArtist.setOnAction(new songsByArtistHandler());
        MenuItem albumsByArtist = new MenuItem("Find all albums by an artists"); 
        menu.getItems().add(albumsByArtist);  
        albumsByArtist.setOnAction(new albumsByArtistHandler());
        MenuItem songsByAlbum = new MenuItem("Find all songs in an album"); 
        menu.getItems().add(songsByAlbum); 
        songsByAlbum.setOnAction(new songsByAlbumHandler());

	    MenuItem insertSong = new MenuItem("Add a new song");  
	    menu.getItems().add(insertSong); 
	    insertSong.setOnAction(new insertSongHandler()); 
        MenuItem insertArtist = new MenuItem("Add a new artist"); 
        menu.getItems().add(insertArtist);  
	    insertArtist.setOnAction(new insertArtistHandler());
        MenuItem insertAlbum = new MenuItem("Add a new album"); 
        menu.getItems().add(insertAlbum); 
	    insertAlbum.setOnAction(new insertAlbumHandler());
	    
	    open();
	    
	    Scene scene = new Scene(root, 600, 600); 
        stage.setScene(scene);
        stage.setTitle("Music Database");
        stage.show();   
	}
	
	 public void open() { // Creates the connection to the database. 
	        try {
	            conn = DriverManager.getConnection(CONNECTION_STRING); 
	        } catch(SQLException e) {
	            System.out.println("Couldn't connect to database: " + e.getMessage());
	        }
	    }
	
	
	class SongsHandler implements EventHandler<Event> { // For showing all songs in the ListView. 

		@Override
		public void handle(Event arg0) {
			center.getChildren().clear();
			songs.getItems().clear();
			center.getChildren().add(songs);
			asc.setSelected(false);
			desc.setSelected(false);
			loadSongs();
			
		}
		
		private void loadSongs() {
			
			String sql = "SELECT title FROM " + "songs"; // We select the title column from songs. 
			latestQuery = sql;
			
			try (Statement statement = conn.createStatement(); 
		            ResultSet results = statement.executeQuery(sql)) {
				songs.getItems().add("List of all songs: ");
		        while(results.next()) { 
	                songs.getItems().add(results.getString(1)); 
	            } 
				
			} catch(SQLException e) {
	            System.out.println("Query failed: " + e.getMessage());
	        }
		}
	}
	
	class artistsHandler implements EventHandler<Event> { // For showing all artists in the ListView. 

		@Override
		public void handle(Event arg0) {
			center.getChildren().clear();
			artists.getItems().clear();
			center.getChildren().add(artists);
			asc.setSelected(false);
			desc.setSelected(false);
			loadArtists(); 
			
		}
		
		private void loadArtists() {
			
			String sql = "SELECT name FROM " + "artists"; 
			latestQuery = sql;
			
			try (Statement statement = conn.createStatement(); 
		            ResultSet results = statement.executeQuery(sql)) {
		        
				artists.getItems().add("List of all artists: ");
		        while(results.next()) {   
	                artists.getItems().add(results.getString(1));  
	            } 
				
			} catch(SQLException e) {
	            System.out.println("Query failed: " + e.getMessage());
	        }
		}
	}
	
	class albumsHandler implements EventHandler<Event> { // For showing all albums in the ListView. 

		@Override
		public void handle(Event arg0) {
			center.getChildren().clear();
			albums.getItems().clear();
			center.getChildren().add(albums);
			asc.setSelected(false);
			desc.setSelected(false);
			loadAlbums();
			
		}
		
		private void loadAlbums() {
			
			String sql = "SELECT name FROM " + "albums"; 
			latestQuery = sql;
			
			try (Statement statement = conn.createStatement(); 
		            ResultSet results = statement.executeQuery(sql)) {
		        
				albums.getItems().add("List of all albums: ");
		        while(results.next()) { 
		        	albums.getItems().add(results.getString(1)); 
	            } 
				
			} catch(SQLException e) {
	            System.out.println("Query failed: " + e.getMessage());
	        }
		}
	}
	
	class songsByArtistHandler implements EventHandler<ActionEvent> { // For showing all songs by a given artist. 

		@Override
		public void handle(ActionEvent arg0) {
			
			String artistName = "";
			
			ArtistsAlert alert = new ArtistsAlert(); // Create a new object of the custom ArtistsAlert Alert class. 
			Optional<ButtonType> result = alert.showAndWait();
        	
        	if (result.isPresent() && result.get() == ButtonType.CANCEL) { 
        		return;
        	} else if (result.isPresent() && result.get() == ButtonType.OK) { 
        		artistName = alert.getArtist();
        	}
        	
        	if (!findArtistQuery(artistName)) { // Calling the help method findArtistQuery. 
        		return;
        	}
			
			String sql = "SELECT DISTINCT songs.title FROM songs, artists, albums "
					+ "WHERE songs.album = albums._id "
					+ "AND albums.artist = artists._id "
					+ "AND artists.name = \"" + artistName + "\""
					+ " COLLATE NOCASE";
			latestQuery = sql;
			
			executeSelectQuery(sql, "Songs by " + artistName + ":", "songs"); // Call the help method executeSelectQuery. 
		}	
	}
	
	private boolean findArtistQuery(String artistName) {
		int artistId = 0;
    	String findArtistQuery = "SELECT artists._id " + 
    			"FROM artists " + 
    			"WHERE artists.name = " + "\"" + artistName + "\" COLLATE NOCASE";
    	
    	artistId = findQuery(findArtistQuery);
		
		if (artistId == 0) {
			Alert al = new Alert(Alert.AlertType.ERROR, "There is no artist of that name in the database!");
			al.showAndWait();
			return false; // To indicate failure. 
		}
		return true; // To indicate success. 
	}
	
	class albumsByArtistHandler implements EventHandler<ActionEvent> { // For showing all albums by a given artist. 

		@Override
		public void handle(ActionEvent arg0) {
			
			String artistName = "";
			
			ArtistsAlert alert = new ArtistsAlert();
			Optional<ButtonType> result = alert.showAndWait();
			
        	if (result.isPresent() && result.get() == ButtonType.CANCEL) { 
        		return;
        	} else if (result.isPresent() && result.get() == ButtonType.OK) { 
        		artistName = alert.getArtist();
        	}
        	
        	if (!findArtistQuery(artistName)) {
        		return;
        	}
			
			String sql = "SELECT DISTINCT albums.name FROM artists, albums "
					+ "WHERE albums.artist = artists._id "
					+ "AND artists.name = \"" + artistName + "\""
					+ " COLLATE NOCASE";
			latestQuery = sql;
			
			executeSelectQuery(sql, "Albums from " + artistName + ":", "albums");
		}	
	}
	
	class songsByAlbumHandler implements EventHandler<ActionEvent> { // For showing all songs in a given album. 

		@Override
		public void handle(ActionEvent arg0) {
			
			String albumName = "";

			AlbumsAlert alert = new AlbumsAlert();
        	Optional<ButtonType> result = alert.showAndWait();
        	if (result.isPresent() && result.get() == ButtonType.CANCEL) { 
        		return;
        	} else if (result.isPresent() && result.get() == ButtonType.OK) {  
        		albumName = alert.getAlbum();
        	}
        	
    		int albumid = 0;
        	String findArtistQuery = "SELECT albums._id " + 
        			"FROM albums " + 
        			"WHERE albums.name = " + "\"" + albumName + "\" COLLATE NOCASE";
        	
        	albumid = findQuery(findArtistQuery);
    		
    		if (albumid == 0) {
    			Alert al = new Alert(Alert.AlertType.ERROR, "There is no album of that name in the database!");
    			al.showAndWait();
    			return;
    		}
			
			String sql = "SELECT DISTINCT songs.title FROM songs, albums "
					+ "WHERE songs.album = albums._id "
					+ "AND albums.name = \"" + albumName + "\""
					+ " COLLATE NOCASE";
			latestQuery = sql;
			
			executeSelectQuery(sql, "Songs from " + albumName + ":", "songs");
		}	
	}
	
	class ascHandler implements EventHandler<ActionEvent> { // Handler for making the ListView show items in ascending order. 

		@Override
		public void handle(ActionEvent arg0) {
			ascDesc(true);
		}	
	}
	
	class descHandler implements EventHandler<ActionEvent> { // Descending order. 

		@Override
		public void handle(ActionEvent arg0) {
			ascDesc(false);
		}	
	}
	
	private void ascDesc(boolean asc) {
		
		String firstLine = "";
		String table = "";
		String sql;
		
		if (asc) {
			sql = latestQuery + " ORDER BY 1 ASC";
		} else {
			sql = latestQuery + " ORDER BY 1 DESC";
		}
		if (center.getChildren().contains(songs)) {
			firstLine = songs.getItems().get(0);
			table = "songs";
		} else if (center.getChildren().contains(albums)) {
			firstLine = albums.getItems().get(0);
			table = "albums";
		} else if (center.getChildren().contains(artists)){
			firstLine = artists.getItems().get(0);
			table = "artists";
		}
		
		executeSelectQuery(sql, firstLine, table);
	}
	
	private void executeSelectQuery(String sql, String firstLine, String table) { // The help method for executing SELECT queries. 
		
		if (table.equals("songs")) {
			
			center.getChildren().clear();
			center.getChildren().add(songs);
			songs.getItems().clear();
			
			try (Statement statement = conn.createStatement(); 
		            ResultSet results = statement.executeQuery(sql)) {
				
				if (!firstLine.isBlank()) {
					songs.getItems().add(firstLine);
				}
	            while(results.next()) { 
	            	songs.getItems().add(results.getString(1)); 
	            } 
				
			} catch(SQLException e) {
	            System.out.println("Query failed: " + e.getMessage());
	        }
		} else if (table.equals("albums")){
			
			center.getChildren().clear();
			center.getChildren().add(albums);
			albums.getItems().clear();
			
			try (Statement statement = conn.createStatement(); // Standard try with resources. 
		            ResultSet results = statement.executeQuery(sql)) {
				
				if (!firstLine.isBlank()) {
					albums.getItems().add(firstLine);
				}
	            while(results.next()) { 
	            	albums.getItems().add(results.getString(1)); 
	            } 
				
			} catch(SQLException e) {
	            System.out.println("Query failed: " + e.getMessage());
	        }
		} else {
			
			center.getChildren().clear();
			center.getChildren().add(artists);
			artists.getItems().clear();
			
			try (Statement statement = conn.createStatement(); // Standard try with resources. 
		            ResultSet results = statement.executeQuery(sql)) {
				
				if (!firstLine.isBlank()) {
					artists.getItems().add(firstLine);
				}
	            while(results.next()) { 
	            	artists.getItems().add(results.getString(1)); 
	            } 
				
			} catch(SQLException e) {
	            System.out.println("Query failed: " + e.getMessage());
	        }
		}
	}
	
	class insertArtistHandler implements EventHandler<ActionEvent> { // For inserting a new artist. 

		@Override
		public void handle(ActionEvent arg0) {
			
			String artistName = "";
			int id = 0;

			ArtistsAlert alert = new ArtistsAlert();
        	Optional<ButtonType> result = alert.showAndWait();
        	if (result.isPresent() && result.get() == ButtonType.CANCEL) { 
        		return;
        	} else if (result.isPresent() && result.get() == ButtonType.OK) { 
        		artistName = alert.getArtist();
        	}
			
			String maxQuery = "SELECT MAX(_id) FROM artists";
			id = maxQuery(maxQuery); // Another help method. 
			
			String sql = "INSERT INTO artists VALUES(" + id + ", " + "\"" + artistName + "\"" + ")";
			executeInsertQuery(sql); // A help method for insert statements. 
			
			if (center.getChildren().contains(artists)) {
				artists.getItems().add(artistName);
			}
		}	
	}
	
	private void executeInsertQuery(String sql) {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
	}
	
	private int maxQuery(String query) { // A help method for generating a new id by adding 1 to the current max. 
		int id = 0;
		try (Statement statement = conn.createStatement(); 
	            ResultSet results = statement.executeQuery(query)) {
			id = results.getInt(1) + 1;	
		} catch(SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
		return id;
	}
	
	class insertAlbumHandler implements EventHandler<ActionEvent> { // For inserting a new album. 

		@Override
		public void handle(ActionEvent arg0) {
			
			String albumName = "";
			String artistName = "";
			int artistId = 0;
			int albumId = 0;

			AlbumAndArtistAlert alert = new AlbumAndArtistAlert();
        	Optional<ButtonType> result = alert.showAndWait();
        	if (result.isPresent() && result.get() == ButtonType.CANCEL) { 
        		return;
        	} else if (result.isPresent() && result.get() == ButtonType.OK) { // If the user presses OK.  
        		albumName = alert.getAlbum();
        		artistName = alert.getArtist();
        	}
        	
        	String findArtistQuery = "SELECT artists._id " + 
        			"FROM artists " + 
        			"WHERE artists.name = " + "\"" + artistName + "\"";
        	artistId = findQuery(findArtistQuery); // Calling the help method findQuery. 
			
			if (artistId == 0) {
				Alert al = new Alert(Alert.AlertType.ERROR, "There is no artist of that name in the database!");
				al.showAndWait();
				return;
			}
			
			String maxQuery = "SELECT MAX(_id) FROM albums";
			albumId = maxQuery(maxQuery);
			
			String sql = "INSERT INTO albums VALUES(" + albumId + ", " + "\"" + albumName + "\", " + artistId + ")";
			executeInsertQuery(sql);
			
			if (center.getChildren().contains(albums)) {
				albums.getItems().add(albumName);
			}
		}	
	}
	
	private int findQuery(String sql) { // A help method for finding the ID based on the given String. 
		int id = 0;
		try (Statement statement = conn.createStatement(); 
	            ResultSet results = statement.executeQuery(sql)) {
			id = results.getInt(1);	
		} catch(SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
		return id;
	}
	
	class insertSongHandler implements EventHandler<ActionEvent> { // For inserting a new song. 
		
		@Override
		public void handle(ActionEvent arg0) {
			
			String albumName = "";
			String songName = "";
			int songId = 0;
			int albumId = 0;
			int trackId = 0; // The track number within the album itself. 

			AlbumAndSongAlert alert = new AlbumAndSongAlert();
        	Optional<ButtonType> result = alert.showAndWait();
        	if (result.isPresent() && result.get() == ButtonType.CANCEL) { 
        		return;
        	} else if (result.isPresent() && result.get() == ButtonType.OK) { // If the user presses OK.  
        		albumName = alert.getAlbum();
        		songName = alert.getSong();
        	}
        	
        	String findAlbumQuery = "SELECT albums._id " + 
        			"FROM albums " + 
        			"WHERE albums.name = " + "\"" + albumName + "\" COLLATE NOCASE";
        	
        	albumId = findQuery(findAlbumQuery);
			
			if (albumId == 0) {
				Alert al = new Alert(Alert.AlertType.ERROR, "There is no album of that name in the database!");
				al.showAndWait();
				return;
			}
			
			String maxQuery = "SELECT MAX(_id) FROM songs";
			songId = maxQuery(maxQuery);
			
			String maxTrackQuery = "SELECT MAX(track) FROM " + 
					"(SELECT DISTINCT songs._id, songs.track, songs.title " + 
					"FROM songs, albums WHERE songs.album = albums._id " +
					"AND albums.name = \"" + albumName + "\"" + 
					"COLLATE NOCASE)";
			trackId = maxQuery(maxTrackQuery);
			
			String sql = "INSERT INTO songs VALUES(" + songId + ", " + trackId + ", " + "\"" + songName + "\", " + albumId + ")";
			executeInsertQuery(sql);
			
			if (center.getChildren().contains(songs)) {
				songs.getItems().add(songName);
			}
		}	
	}
}
