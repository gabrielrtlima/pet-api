package com.example.petapi.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Entity
@Data
public class Peso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double peso;

    private LocalDate data;

    @ManyToOne
    @JsonIgnore
    private Usuario usuario;
}
