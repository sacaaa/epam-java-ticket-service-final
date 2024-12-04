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

@Entity
@Table(name = "movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "title", unique = true)
    private String title;

    @Column(name = "genre")
    private String genre;

    @Column(name = "length")
    private int length;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_pricing",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "pricing_id")
    )
    private Set<Pricing> pricingComponents = new HashSet<>();

    public Movie(String title, String genre, int length) {
        this.title = title;
        this.genre = genre;
        this.length = length;
    }

}
