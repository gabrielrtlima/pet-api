package com.example.petapi.services;

import com.example.petapi.models.entities.Peso;
import com.example.petapi.models.entities.Usuario;
import com.example.petapi.models.enums.IMCEnum;
import com.example.petapi.models.vos.*;
import com.example.petapi.repositories.IPesoRepository;
import com.example.petapi.repositories.IUsuarioRepository;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class PesoService {

    @Autowired
    private IPesoRepository pesoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public void addPeso(String email, double peso) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            Peso ps = new Peso();
            ps.setPeso(peso);
            ps.setData(LocalDate.now());
            ps.setUsuario(usuario.get());
            pesoRepository.save(ps);
            usuario.get().getPesos().add(ps);
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public MonitoramentoVO montarMonitoramento(String email){

        MonitoramentoVO monitoramento = new MonitoramentoVO();

        //TODO = VERIFICAR SE PASSOU EMAIL
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(email);
        if(optUsuario.isEmpty()) {
            throw new RuntimeException("Usuário não cadastrado");
        }

        Optional<Peso> optPeso = pesoRepository.findFirstByUsuarioEmailOrderByDataDesc(email);
        if(optPeso.isEmpty()){
            throw new RuntimeException("Usuário não tem registro de peso para o cálculo de IMC");
        }

        IMCVO IMC = calcularIMC(optUsuario, optPeso);
        monitoramento.setIMC(IMC);
        EvolucaoVO evolucao = calcularEvo(optUsuario, optPeso);
        monitoramento.setEvolucao(evolucao);
        HistoricoVO historico = montarHistorico(email);
        monitoramento.setHistorico(historico);
        ComparativoVO comparativo = montarComparativo(optUsuario);
        monitoramento.setComparativo(comparativo);
        return monitoramento;
    }

    private IMCVO calcularIMC(Optional<Usuario> optUsuario, Optional<Peso> optPeso) {

        double altura = optUsuario.get().getAltura()/100.0;
        double peso = optPeso.get().getPeso();
        double IMC = (peso/(altura * altura));
        double IMCAproximado = BigDecimal.valueOf(IMC).setScale(2, RoundingMode.HALF_UP).doubleValue();

        IMCEnum grau = IMCEnum.ABAIXO_DO_PESO;

        if(IMC > 18.5 && IMC < 24.9){
            grau = IMCEnum.PESO_IDEAL;
        }
        else if(IMC > 25){
            grau = IMCEnum.ACIMA_DO_PESO;
        }
        else if(IMC < 18.5){
            grau = IMCEnum.ABAIXO_DO_PESO;
        }

        return IMCVO.builder().IMC(IMCAproximado).classificacao(grau).build();
    }

    private EvolucaoVO calcularEvo(Optional<Usuario> optUsuario, Optional<Peso> optPeso) {

        double pesoDesejado = optUsuario.get().getPesoDesejado();
        double pesoInicial = optUsuario.get().getPesoInicial();
        double pesoAtual = optPeso.get().getPeso();

        double progresso = (1 - (pesoAtual - pesoDesejado)/(pesoInicial - pesoDesejado)) * 100;
        double progressoAprox = BigDecimal.valueOf(progresso).setScale(1, RoundingMode.HALF_EVEN).doubleValue();

        double diasPassados = Days.daysBetween(optUsuario.get().getDataInicial(), optPeso.get().getData()).getDays();
        double diasPrevistos = Days.daysBetween(optUsuario.get().getDataInicial(), optUsuario.get().getDataObjetivo()).getDays();

        double tempo = (diasPassados/diasPrevistos) * 100;
        double tempoAprox = BigDecimal.valueOf(tempo).setScale(1, RoundingMode.HALF_EVEN).doubleValue();

        return EvolucaoVO.builder().progresso(progressoAprox).tempo(tempoAprox).build();

    }

    private HistoricoVO montarHistorico(String email) {

        List<Peso> pesos = pesoRepository.findByUsuarioEmailOrderByDataAsc(email);

        HistoricoVO historico = HistoricoVO.builder().pesos(pesos).build();

        return historico;
    }

    private ComparativoVO montarComparativo(Optional<Usuario> optUsuario) {
        List<Peso> pesos = pesoRepository.findByUsuarioEmailOrderByDataAsc(optUsuario.get().getEmail());
        ComparativoVO comparativo = new ComparativoVO();

        pesos.forEach(peso -> {
            if(Days.daysBetween(optUsuario.get().getDataInicial(), peso.getData()).getDays() == 7){
                comparativo.set_7dias(peso.getPeso());
            } else if (Days.daysBetween(optUsuario.get().getDataInicial(), peso.getData()).getDays() == 30) {
                comparativo.set_30dias(peso.getPeso());
            } else if (Days.daysBetween(optUsuario.get().getDataInicial(), peso.getData()).getDays() == 365) {
                comparativo.set_1ano(peso.getPeso());
            }
        });

        comparativo.setUltimaMedida(pesos.get(pesos.size()-1).getPeso());
        return comparativo;
    }
}
