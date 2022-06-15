package com.example.petapi.models.vo;

import com.example.petapi.models.enums.IMCEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IMCVO {

    private double IMC;

    private IMCEnum classificacao;

}
