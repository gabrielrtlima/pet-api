package com.example.petapi.models.vos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.joda.time.Days;

@Data
@Builder
public class EvolucaoVO {

    private double progresso;
    private double tempo;

}
