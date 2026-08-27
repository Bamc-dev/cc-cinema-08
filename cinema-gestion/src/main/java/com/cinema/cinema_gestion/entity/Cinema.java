package com.cinema.cinema_gestion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un cinéma (établissement).
 * Un cinéma possède une adresse (ville, rue, numéro) et une collection de salles {@link Room}.
 */
@Entity
@Table(name = "cinema", indexes = {
        @Index(name = "idx_cinema_name", columnList = "name")
})
public class Cinema extends BaseEntity {

    /** Nom commercial du cinéma. */
    private String name;
    /** Ville d'implantation. */
    private String city;
    /** Rue de l'adresse. */
    private String street;
    /** Numéro de voie. */
    private String number;

    /** Salles rattachées à ce cinéma (cascade et suppression des orphelins). */
    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Room> rooms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = new HashSet<>(rooms);
    }
}
