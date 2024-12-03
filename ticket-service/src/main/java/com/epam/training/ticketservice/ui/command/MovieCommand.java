package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.service.impl.MovieServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class MovieCommand {

    private final MovieServiceImpl movieService;

    @ShellMethod(key = "create movie", value = "Create a new movie")
    public String createMovie(String title, String genre, int length) {
        var result = movieService.createMovie(title, genre, length);
        return result.isSuccess() ? "Movie created" : result.getMessage();
    }

    @ShellMethod(key = "update movie", value = "Update an existing movie")
    public String updateMovie(String title, String genre, int length) {
        var result = movieService.updateMovie(title, genre, length);
        return result.isSuccess() ? "Movie updated" : result.getMessage();
    }

    @ShellMethod(key = "list movies", value = "List all movies")
    public String listMovies() {
        var result = movieService.getMovies();
        return result.isSuccess()
                ? result.getData().stream()
                    .map(movie -> String.format("%s (%s, %d minutes)",
                            movie.getTitle(), movie.getGenre(), movie.getLength()))
                    .collect(Collectors.joining("\n"))
                : result.getMessage();
    }

    @ShellMethod(key = "delete movie", value = "Delete an existing movie")
    public String deleteMovie(String title) {
        var result = movieService.deleteMovie(title);
        return result.isSuccess() ? "Movie deleted" : result.getMessage();
    }

}
