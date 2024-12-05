package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.dto.MovieDto;

import java.util.List;

public interface MovieService {

    Result<MovieDto> createMovie(String title, String genre, int length);

    Result<List<MovieDto>> getMovies();

    Result<MovieDto> updateMovie(String title, String genre, int length);

    Result<MovieDto> deleteMovie(String title);

}
