package com.epam.training.ticketservice.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents a screening of a movie in a specific
 * room at a scheduled time.
 */
@Entity
@Table(name = "screenings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Screening {

    /**
     * The unique identifier for the screening.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The movie being screened.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    /**
     * The room where the screening is held.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /**
     * The start time of the screening.
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * The pricing components associated with the screening.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "screening_pricing",
            joinColumns = @JoinColumn(name = "screening_id"),
            inverseJoinColumns = @JoinColumn(name = "pricing_id")
    )
    private Set<Pricing> pricingComponents;

    /**
     * Constructor to create a Screening without pricing components.
     *
     * @param movie      The movie being screened.
     * @param room       The room where the screening is held.
     * @param startTime  The start time of the screening.
     */
    public Screening(Movie movie, Room room, LocalDateTime startTime) {
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
    }

}
