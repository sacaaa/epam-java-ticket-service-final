package com.epam.training.ticketservice.core.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Movie;
import com.epam.training.ticketservice.core.model.dto.MovieDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMovie_WhenAdminPrivilegesAreValidAndMovieDoesNotExist() {
        // Arrange
        String title = "Test Movie";
        String genre = "Action";
        int length = 120;

        Movie movie = new Movie(title, genre, length);
        MovieDto movieDto = new MovieDto(title, genre, length, null);

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.success(null));
        when(movieRepository.findByTitle(title))
                .thenReturn(Optional.empty());
        when(movieRepository.save(any(Movie.class)))
                .thenReturn(movie);
        when(objectMapper.convertValue(movie, MovieDto.class))
                .thenReturn(movieDto);

        // Act
        Result<MovieDto> result = movieService.createMovie(title, genre, length);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(title, result.getData().getTitle());
        assertEquals(genre, result.getData().getGenre());
        assertEquals(length, result.getData().getLength());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }


    @Test
    void testCreateMovie_WhenAdminPrivilegesAreInvalid() {
        // Arrange
        String title = "Test Movie";
        String genre = "Action";
        int length = 120;

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.failure("Error: Admin privileges are required."));

        // Act
        Result<MovieDto> result = movieService.createMovie(title, genre, length);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
        verifyNoInteractions(movieRepository);
    }

    @Test
    void testCreateMovie_WhenMovieAlreadyExists() {
        // Arrange
        String title = "Test Movie";
        String genre = "Action";
        int length = 120;

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.success(null));
        when(movieRepository.findByTitle(title))
                .thenReturn(Optional.of(new Movie(title, genre, length)));

        // Act
        Result<MovieDto> result = movieService.createMovie(title, genre, length);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Movie already exists.", result.getMessage());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testGetMovies_WhenMoviesExist() {
        // Arrange
        List<Movie> movies = List.of(
                new Movie("Movie1", "Genre1", 90),
                new Movie("Movie2", "Genre2", 120)
        );

        when(movieRepository.findAll()).thenReturn(movies);

        // Act
        Result<List<MovieDto>> result = movieService.getMovies();

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    void testGetMovies_WhenNoMoviesExist() {
        // Arrange
        when(movieRepository.findAll()).thenReturn(List.of());

        // Act
        Result<List<MovieDto>> result = movieService.getMovies();

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("There are no movies at the moment", result.getMessage());
    }

    @Test
    void testUpdateMovie_WhenMovieExists() {
        // Arrange
        String title = "Test Movie";
        String newGenre = "Drama";
        int newLength = 100;
        Movie existingMovie = new Movie(title, "Action", 120);
        Movie updatedMovie = new Movie(title, newGenre, newLength);
        MovieDto updatedMovieDto = new MovieDto(title, newGenre, newLength, null);

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.success(null));
        when(movieRepository.findByTitle(title))
                .thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(existingMovie)).thenReturn(updatedMovie);
        when(objectMapper.convertValue(updatedMovie, MovieDto.class)).thenReturn(updatedMovieDto);

        // Act
        Result<MovieDto> result = movieService.updateMovie(title, newGenre, newLength);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(newGenre, result.getData().getGenre());
        assertEquals(newLength, result.getData().getLength());
        verify(movieRepository, times(1)).save(existingMovie);
    }

    @Test
    void testUpdateMovie_WhenMovieDoesNotExist() {
        // Arrange
        String title = "Test Movie";
        String newGenre = "Drama";
        int newLength = 100;

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.success(null));
        when(movieRepository.findByTitle(title))
                .thenReturn(Optional.empty());

        // Act
        Result<MovieDto> result = movieService.updateMovie(title, newGenre, newLength);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Movie does not exist.", result.getMessage());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testDeleteMovie_WhenMovieExists() {
        // Arrange
        String title = "Test Movie";
        Movie existingMovie = new Movie(title, "Action", 120);

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.success(null));
        when(movieRepository.findByTitle(title))
                .thenReturn(Optional.of(existingMovie));

        // Act
        Result<MovieDto> result = movieService.deleteMovie(title);

        // Assert
        assertTrue(result.isSuccess());
        verify(movieRepository, times(1)).delete(existingMovie);
    }

    @Test
    void testDeleteMovie_WhenMovieDoesNotExist() {
        // Arrange
        String title = "Test Movie";

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.success(null));
        when(movieRepository.findByTitle(title))
                .thenReturn(Optional.empty());

        // Act
        Result<MovieDto> result = movieService.deleteMovie(title);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Movie does not exist.", result.getMessage());
        verify(movieRepository, never()).delete(any(Movie.class));
    }

    @Test
    void testDeleteMovie_WhenAdminPrivilegesAreInvalid() {
        // Arrange
        String title = "Test Movie";

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.failure("Error: Admin privileges are required."));

        // Act
        Result<MovieDto> result = movieService.deleteMovie(title);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
        verifyNoInteractions(movieRepository);
    }

    @Test
    void testUpdateMovie_WhenAdminPrivilegesAreInvalid() {
        // Arrange
        String title = "Test Movie";
        String newGenre = "Drama";
        int newLength = 100;

        when(userService.checkAdminPrivileges())
                .thenReturn(Result.failure("Error: Admin privileges are required."));

        // Act
        Result<MovieDto> result = movieService.updateMovie(title, newGenre, newLength);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Error: Admin privileges are required.", result.getMessage());
        verifyNoInteractions(movieRepository);
    }

}
