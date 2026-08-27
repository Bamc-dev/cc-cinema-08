package com.cinema.cinema_gestion.dto.cinema;

import com.cinema.cinema_gestion.dto.BaseDTO;
import java.util.Set;

/**
 * DTO CRUD d'un cinéma : payload d'entrée/sortie pour créer ou modifier un établissement.
 */
public class CinemaDTOCRUD extends BaseDTO {
    
    /** Nom commercial du cinéma. */
    private String name;
    /** Ville d'implantation. */
    private String city;
    /** Rue de l'adresse. */
    private String street;
    /** Numéro de voie. */
    private String number;

    /** Identifiants des salles rattachées. */
    private Set<Long> roomIds;

    public CinemaDTOCRUD() {
        super(null);
    }

    public CinemaDTOCRUD(Long id, String name, String city, String street, String number, Set<Long> roomIds) {
        super(id);
        this.name = name;
        this.city = city;
        this.street = street;
        this.number = number;
        this.roomIds = roomIds;
    }

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

    public Set<Long> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(Set<Long> roomIds) {
        this.roomIds = roomIds;
    }
    
}
