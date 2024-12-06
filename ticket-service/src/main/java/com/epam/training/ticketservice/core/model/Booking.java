package com.epam.training.ticketservice.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.ElementCollection;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.FetchType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a booking made by a user for a specific screening.
 */
@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    /**
     * The unique identifier for the booking.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The user who made the booking.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The screening the booking is associated with.
     */
    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    /**
     * The list of seats booked for the screening.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "booked_seats", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat")
    private List<String> seats;

    /**
     * The total price for the booking.
     */
    @Column(name = "price", nullable = false)
    private int price;

    /**
     * The time when the booking was made.
     */
    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    /**
     * Constructor to create a Booking object.
     *
     * @param user         The user who made the booking.
     * @param screening    The screening for which the booking was made.
     * @param seats        The list of seats booked.
     * @param price        The total price of the booking.
     * @param bookingTime  The time when the booking was made.
     */
    public Booking(User user, Screening screening, List<String> seats, int price, LocalDateTime bookingTime) {
        this.user = user;
        this.screening = screening;
        this.seats = seats;
        this.price = price;
        this.bookingTime = bookingTime;
    }

}
