package com.epam.training.ticketservice.core.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import java.util.Set;

/**
 * Represents a pricing component that can be associated
 * with movies, rooms, and screenings.
 */
@Entity
@Table(name = "pricings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"movies", "rooms", "screenings"})
public class Pricing {

    /**
     * The unique identifier for the pricing component.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The unique name of the pricing component (e.g., "Weekday Discount").
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * The amount associated with this pricing component.
     */
    @Column(name = "amount", nullable = false)
    private int amount;

    /**
     * The movies that use this pricing component.
     */
    @ManyToMany(mappedBy = "pricingComponents", fetch = FetchType.EAGER)
    private Set<Movie> movies;

    /**
     * The rooms that use this pricing component.
     */
    @ManyToMany(mappedBy = "pricingComponents", fetch = FetchType.EAGER)
    private Set<Room> rooms;

    /**
     * The screenings that use this pricing component.
     */
    @ManyToMany(mappedBy = "pricingComponents", fetch = FetchType.EAGER)
    private Set<Screening> screenings;

    /**
     * Constructor to create a Pricing component without associations.
     *
     * @param name   The unique name of the pricing component.
     * @param amount The amount associated with this pricing component.
     */
    public Pricing(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

}
