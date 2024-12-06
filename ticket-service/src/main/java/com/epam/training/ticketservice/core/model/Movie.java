package com.epam.training.ticketservice.core.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a movie with its details and associated pricing components.
 */
@Entity
@Table(name = "movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    /**
     * The unique identifier for the movie.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The title of the movie. Must be unique.
     */
    @Column(name = "title", unique = true)
    private String title;

    /**
     * The genre of the movie (e.g., Action, Comedy).
     */
    @Column(name = "genre")
    private String genre;

    /**
     * The length of the movie in minutes.
     */
    @Column(name = "length")
    private int length;

    /**
     * The set of pricing components associated with the movie.
     * This is used to define the movie's pricing rules.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_pricing",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "pricing_id")
    )
    private Set<Pricing> pricingComponents = new HashSet<>();

    /**
     * Constructor to create a Movie object with the specified details.
     *
     * @param title  The title of the movie.
     * @param genre  The genre of the movie.
     * @param length The length of the movie in minutes.
     */
    public Movie(String title, String genre, int length) {
        this.title = title;
        this.genre = genre;
        this.length = length;
    }

}
