package controller;

import app.App;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.dao.JdbcMovieDao;
import model.movie.Genres;
import model.movie.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    // KLASA WIERSZA
    public static class CustomRow {
        private String title;
        private String genre;

        public CustomRow(String title, String genre) {
            this.title = title;
            this.genre = genre;
        }

        public String getTitle() {
            return title;
        }

        public String getGenre() {
            return genre;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    public AnchorPane mainAnchorPane;
    public SplitMenuButton genreSearcher;
    public Text welcomeText;
    public VBox vBoxList;

    private List<Movie> movies;
    private static final ObservableList<CustomRow> movieList = FXCollections.observableArrayList();


    public void initialize(){
        //welcomeText.setText(UserController.getUser().welcomeText());
        MenuItem mi = new MenuItem(Genres.Action.name());
        MenuItem mi2 = new MenuItem(Genres.Adventure.name());
        MenuItem mi3 = new MenuItem(Genres.Comedy.name());
        MenuItem mi4 = new MenuItem(Genres.Drama.name());
        MenuItem mi5 = new MenuItem(Genres.Fantasy.name());
        MenuItem mi6 = new MenuItem(Genres.Horror.name());
        MenuItem mi7 = new MenuItem(Genres.Romance.name());
        MenuItem mi8 = new MenuItem(Genres.Mystery.name());
        MenuItem mi9 = new MenuItem(Genres.Thriller.name());
        MenuItem mi10 = new MenuItem(Genres.Western.name());
        MenuItem mi11 = new MenuItem(Genres.ScienceFiction.name());
        genreSearcher.getItems().addAll(mi,mi2,mi3,mi4,mi5,mi6,mi7,mi8,mi9,mi10,mi11);

        try (JdbcMovieDao movieDao = new JdbcMovieDao()) {
            logger.info("Getting list of movies");
            movies = movieDao.findAll();
            MovieController.setMovies(movies);
        } catch (SQLException e) {
            logger.warn("Cant get movies from database");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ListView<CustomRow> listView = new ListView<CustomRow>(movieList);
        listView.getItems().clear();
        listView.setPrefSize(200, 500);
        listView.setEditable(true);

        for(Movie movie : movies) {
            movieList.add(new CustomRow(movie.getTitle(), movie.getGenre().toString()));
        }
        listView.setItems(movieList);
        vBoxList.getChildren().add(listView);

        listView.setCellFactory(new Callback<ListView<CustomRow>, ListCell<CustomRow>>() {
            @Override
            public ListCell<CustomRow> call(ListView<CustomRow> customRowListView) {
                return new CustomListCell();
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomRow>() {
            @Override
            public void changed(ObservableValue<? extends CustomRow> observableValue, CustomRow s, CustomRow t1) {
                if(t1 != null) {
                    MovieController.setPickedMovie(MovieController.getMovieByTitle(t1.getTitle()));
                    App.changeScene(mainAnchorPane, "filmWindow");
                }
            }
        });

    }

    public void logOut() {
        logger.info("User has been logged out");
        UserController.logout();
        App.changeScene(mainAnchorPane, "loginWindow");
    }

    public void showProfile() throws IOException {
        App.changeScene(mainAnchorPane, "profileWindow");
    }

    // KLASA KOMORKI
    private class CustomListCell extends ListCell<CustomRow> {
        private HBox content;
        private Text title;
        private Text genre;

        public CustomListCell() {
            super();
            title = new Text();
            genre = new Text();
            VBox vBox = new VBox(title, genre);
            content = new HBox(new Label("Graphic"), vBox);
            content.setSpacing(10);
        }

        @Override
        protected void updateItem(CustomRow item, boolean empty) {
            super.updateItem(item, empty);
            if(item != null && !empty) {
                title.setText(item.getTitle());
                genre.setText(item.getGenre());
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

}
