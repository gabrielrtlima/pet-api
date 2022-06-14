package com.example.petapi.repositories;

import com.example.petapi.models.entities.Peso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPesoRepository extends JpaRepository<Peso, Long> {

    public Optional<Peso> findFirstByUsuarioEmailOrderByDataDesc(String email);

}
