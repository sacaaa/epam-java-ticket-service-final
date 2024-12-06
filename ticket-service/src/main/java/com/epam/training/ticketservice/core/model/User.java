package com.epam.training.ticketservice.core.model;

import com.epam.training.ticketservice.core.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Represents a user in the system with a username,
 * password, and assigned role.
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The unique username of the user.
     */
    @Column(name = "username", unique = true)
    private String username;

    /**
     * The user's password.
     */
    @Column(name = "password")
    private String password;

    /**
     * The role assigned to the user, indicating their permissions.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    /**
     * Constructor to create a User without an ID.
     *
     * @param username The unique username of the user.
     * @param password The password of the user.
     * @param role     The role assigned to the user.
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
