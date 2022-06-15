package com.example.petapi.controllers;

import com.example.petapi.models.dtos.PesoDTO;
import com.example.petapi.models.vo.MonitoramentoVO;
import com.example.petapi.services.PesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PesoController {

    @Autowired
    private PesoService pesoService;

    @PostMapping("/add-peso")
    public void addPeso(@RequestBody PesoDTO pesoDTO) {
        pesoService.addPeso(pesoDTO.getEmail(), pesoDTO.getPeso());
    }

    @GetMapping("/monitoramento")
    public MonitoramentoVO montarMonitoramento(@RequestParam String email) {
        return pesoService.montarMonitoramento(email);
    }
}
