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

@Entity
@Table(name = "screenings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Screening {

    @Id
    @GeneratedValue()
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "screening_pricing",
            joinColumns = @JoinColumn(name = "screening_id"),
            inverseJoinColumns = @JoinColumn(name = "pricing_id")
    )
    private Set<Pricing> pricingComponents;

    public Screening(Movie movie, Room room, LocalDateTime startTime) {
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
    }

}
