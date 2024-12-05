package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.model.dto.MovieDto;
import com.epam.training.ticketservice.core.service.impl.MovieServiceImpl;
import com.epam.training.ticketservice.core.data.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MovieCommandTest {

    @Mock
    private MovieServiceImpl movieService;

    @InjectMocks
    private MovieCommand movieCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMovie_Success() {
        // Arrange
        String title = "Inception";
        String genre = "Action";
        int length = 148;

        when(movieService.createMovie(title, genre, length)).thenReturn(Result.success(null));

        // Act
        String result = movieCommand.createMovie(title, genre, length);

        // Assert
        assertEquals("Movie created", result);
        verify(movieService).createMovie(title, genre, length);
    }

    @Test
    void testCreateMovie_Failure() {
        // Arrange
        String title = "Inception";
        String genre = "Action";
        int length = 148;

        when(movieService.createMovie(title, genre, length))
                .thenReturn(Result.failure("Error: Movie already exists."));

        // Act
        String result = movieCommand.createMovie(title, genre, length);

        // Assert
        assertEquals("Error: Movie already exists.", result);
        verify(movieService).createMovie(title, genre, length);
    }

    @Test
    void testUpdateMovie_Success() {
        // Arrange
        String title = "Inception";
        String genre = "Sci-Fi";
        int length = 148;

        when(movieService.updateMovie(title, genre, length)).thenReturn(Result.success(null));

        // Act
        String result = movieCommand.updateMovie(title, genre, length);

        // Assert
        assertEquals("Movie updated", result);
        verify(movieService).updateMovie(title, genre, length);
    }

    @Test
    void testUpdateMovie_Failure() {
        // Arrange
        String title = "Inception";
        String genre = "Sci-Fi";
        int length = 148;

        when(movieService.updateMovie(title, genre, length))
                .thenReturn(Result.failure("Error: Movie does not exist."));

        // Act
        String result = movieCommand.updateMovie(title, genre, length);

        // Assert
        assertEquals("Error: Movie does not exist.", result);
        verify(movieService).updateMovie(title, genre, length);
    }

    @Test
    void testListMovies_Success() {
        // Arrange
        MovieDto movie1 = new MovieDto("Inception", "Action", 148, null);
        MovieDto movie2 = new MovieDto("The Dark Knight", "Action", 152, null);

        when(movieService.getMovies()).thenReturn(Result.success(List.of(movie1, movie2)));

        // Act
        String result = movieCommand.listMovies();

        // Assert
        String expected = "Inception (Action, 148 minutes)\nThe Dark Knight (Action, 152 minutes)";
        assertEquals(expected, result);
        verify(movieService).getMovies();
    }

    @Test
    void testListMovies_Failure() {
        // Arrange
        when(movieService.getMovies()).thenReturn(Result.failure("There are no movies at the moment"));

        // Act
        String result = movieCommand.listMovies();

        // Assert
        assertEquals("There are no movies at the moment", result);
        verify(movieService).getMovies();
    }

    @Test
    void testDeleteMovie_Success() {
        // Arrange
        String title = "Inception";

        when(movieService.deleteMovie(title)).thenReturn(Result.success(null));

        // Act
        String result = movieCommand.deleteMovie(title);

        // Assert
        assertEquals("Movie deleted", result);
        verify(movieService).deleteMovie(title);
    }

    @Test
    void testDeleteMovie_Failure() {
        // Arrange
        String title = "Inception";

        when(movieService.deleteMovie(title))
                .thenReturn(Result.failure("Error: Movie does not exist."));

        // Act
        String result = movieCommand.deleteMovie(title);

        // Assert
        assertEquals("Error: Movie does not exist.", result);
        verify(movieService).deleteMovie(title);
    }
}
