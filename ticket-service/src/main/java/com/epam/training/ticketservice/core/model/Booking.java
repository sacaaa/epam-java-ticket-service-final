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

@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "booked_seats", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat")
    private List<String> seats;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    public Booking(User user, Screening screening, List<String> seats, int price, LocalDateTime bookingTime) {
        this.user = user;
        this.screening = screening;
        this.seats = seats;
        this.price = price;
        this.bookingTime = bookingTime;
    }

}
