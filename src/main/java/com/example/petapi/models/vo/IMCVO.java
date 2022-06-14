package com.example.petapi.models.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IMCVO {

    private double IMC;

    private IMCEnum classificacao;

}
