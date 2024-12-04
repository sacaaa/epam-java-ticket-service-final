package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.data.Result;
import com.epam.training.ticketservice.core.model.Movie;
import com.epam.training.ticketservice.core.model.dto.MovieDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final ObjectMapper objectMapper;

    private final UserServiceImpl userService;

    @Override
    public Result<MovieDto> createMovie(String title, String genre, int length) {
        Result<Void> adminCheck = userService.checkAdminPrivileges();
        if (!adminCheck.isSuccess()) {
            return Result.failure(adminCheck.getMessage());
        }

        if (movieRepository.findByTitle(title).isPresent()) {
            return Result.failure("Error: Movie already exists.");
        }

        Movie movie = movieRepository.save(new Movie(title, genre, length));
        return Result.success(objectMapper.convertValue(movie, MovieDto.class));
    }

    @Override
    public Result<List<MovieDto>> getMovies() {
        List<MovieDto> movieDtos = movieRepository.findAll().stream()
                .map(movie -> objectMapper.convertValue(movie, MovieDto.class))
                .toList();

        return movieDtos.isEmpty()
                ? Result.failure("There are no movies at the moment")
                : Result.success(movieDtos);
    }

    @Override
    public Result<MovieDto> updateMovie(String title, String genre, int length) {
        Result<Void> adminCheck = userService.checkAdminPrivileges();
        if (!adminCheck.isSuccess()) {
            return Result.failure(adminCheck.getMessage());
        }

        return movieRepository.findByTitle(title)
                .map(movie -> {
                    movie.setGenre(genre);
                    movie.setLength(length);
                    movieRepository.save(movie);
                    return Result.success(objectMapper.convertValue(movie, MovieDto.class));
                })
                .orElseGet(() -> Result.failure("Error: Movie does not exist."));
    }

    @Override
    public Result<MovieDto> deleteMovie(String title) {
        Result<Void> adminCheck = userService.checkAdminPrivileges();
        if (!adminCheck.isSuccess()) {
            return Result.failure(adminCheck.getMessage());
        }

        return movieRepository.findByTitle(title)
                .map(movie -> {
                    movieRepository.delete(movie);
                    return Result.success(objectMapper.convertValue(movie, MovieDto.class));
                })
                .orElseGet(() -> Result.failure("Error: Movie does not exist."));
    }

}
