package com.epam.training.ticketservice.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
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
import javax.persistence.Transient;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "row_count", nullable = false)
    private int rows;

    @Column(name = "columns", nullable = false)
    private int columns;

    @Transient
    private int seats;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "room_pricing",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "pricing_id")
    )
    private Set<Pricing> pricingComponents = new HashSet<>();

    public Room(String name, int rows, int columns) {
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.seats = rows * columns;
    }

    @PostLoad
    @PostPersist
    public void calculateSeats() {
        this.seats = this.rows * this.columns;
    }

}
