package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.MovieDto;

import java.util.List;

/**
 * Service interface for managing movies.
 */
public interface MovieService {

    /**
     * Creates a new movie with the specified title, genre, and length.
     *
     * @param title  The unique title of the movie.
     * @param genre  The genre of the movie (e.g., Action, Comedy).
     * @param length The length of the movie in minutes.
     * @return A {@code Result} containing the created {@code MovieDto} or an error message if creation fails.
     */
    Result<MovieDto> createMovie(String title, String genre, int length);

    /**
     * Retrieves all existing movies.
     *
     * @return A {@code Result} containing a list of {@code MovieDto} objects representing all movies,
     *         or an error message if retrieval fails.
     */
    Result<List<MovieDto>> getMovies();

    /**
     * Updates the details of an existing movie.
     *
     * @param title  The unique title of the movie to update.
     * @param genre  The new genre of the movie.
     * @param length The new length of the movie in minutes.
     * @return A {@code Result} containing the updated {@code MovieDto} or an error message if the update fails.
     */
    Result<MovieDto> updateMovie(String title, String genre, int length);

    /**
     * Deletes a movie with the specified title.
     *
     * @param title The unique title of the movie to delete.
     * @return A {@code Result} containing the deleted {@code MovieDto} or an error message if deletion fails.
     */
    Result<MovieDto> deleteMovie(String title);

}
