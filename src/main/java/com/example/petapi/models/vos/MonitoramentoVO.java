package com.example.petapi.models.vos;

import lombok.Data;

@Data
public class MonitoramentoVO {

    private IMCVO IMC;
    private EvolucaoVO evolucao;
    private HistoricoVO historico;
    private ComparativoVO comparativo;

}
