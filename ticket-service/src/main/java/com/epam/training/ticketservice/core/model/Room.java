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

/**
 * Represents a room in a cinema, with a defined number
 * of rows and columns, and associated pricing components.
 */
@Entity
@Table(name = "rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    /**
     * The unique identifier for the room.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The unique name of the room.
     */
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    /**
     * The number of rows in the room.
     */
    @Column(name = "row_count", nullable = false)
    private int rows;

    /**
     * The number of columns in the room.
     */
    @Column(name = "columns", nullable = false)
    private int columns;

    /**
     * The total number of seats in the room. This is
     * calculated as {@code rows * columns}.
     * It is a transient field and not stored in the database.
     */
    @Transient
    private int seats;

    /**
     * The pricing components associated with this room.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "room_pricing",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "pricing_id")
    )
    private Set<Pricing> pricingComponents = new HashSet<>();

    /**
     * Constructor to create a Room without pricing components.
     *
     * @param name   The unique name of the room.
     * @param rows   The number of rows in the room.
     * @param columns The number of columns in the room.
     */
    public Room(String name, int rows, int columns) {
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.seats = rows * columns;
    }

    /**
     * Calculates the total number of seats in the room.
     * This method is invoked automatically after the room
     * is loaded or persisted.
     */
    @PostLoad
    @PostPersist
    public void calculateSeats() {
        this.seats = this.rows * this.columns;
    }

}
