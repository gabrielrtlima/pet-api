package com.example.petapi.models.vos;

import com.example.petapi.models.entities.Peso;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoricoVO {

    private List<Peso> pesos;
}
