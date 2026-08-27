package com.cinema.cinema_gestion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité représentant une séance (programmation d'un film dans une salle).
 * Relie un {@link Movie} à une {@link Room} et possède des horaires {@link Schedule}.
 */
@Entity
public class MovieShow extends BaseEntity {

    /** Prix du billet pour cette séance. */
    private Double price;
    /** Film projeté. */
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;
    /** Créneaux horaires de la séance (cascade et suppression des orphelins). */
    @OneToMany(mappedBy = "movieShow", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Schedule> schedules = new HashSet<>();
    /** Salle de projection. */
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Movie getMovie() {
        return movie;
    }
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    public Set<Schedule> getSchedules() {
        return schedules;
    }
    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = new HashSet<>(schedules);
    }
    public Room getRoom() {
        return room;
    }
    public void setRoom(Room room) {
        this.room = room;
    }
}
