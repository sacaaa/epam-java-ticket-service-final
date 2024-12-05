package com.epam.training.ticketservice.core.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;

import java.util.Set;


@Entity
@Table(name = "pricings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pricing {

    @Id
    @GeneratedValue()
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "amount", nullable = false)
    private int amount;

    @ManyToMany(mappedBy = "pricingComponents", fetch = FetchType.EAGER)
    private Set<Movie> movies;

    @ManyToMany(mappedBy = "pricingComponents", fetch = FetchType.EAGER)
    private Set<Room> rooms;

    @ManyToMany(mappedBy = "pricingComponents", fetch = FetchType.EAGER)
    private Set<Screening> screenings;

    public Pricing(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

}
