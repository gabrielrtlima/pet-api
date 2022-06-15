package com.example.petapi.services;

import com.example.petapi.models.entities.Peso;
import com.example.petapi.models.entities.Usuario;
import com.example.petapi.models.enums.IMCEnum;
import com.example.petapi.models.vo.IMCVO;
import com.example.petapi.models.vo.MonitoramentoVO;
import com.example.petapi.repositories.IPesoRepository;
import com.example.petapi.repositories.IUsuarioRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return monitoramento;
    }

    private IMCVO calcularIMC(Optional<Usuario> optUsuario, Optional<Peso> optPeso) {

        double altura = optUsuario.get().getAltura();
        double peso = optPeso.get().getPeso();
        double IMC = peso/altura * altura;

        IMCEnum grau = IMCEnum.ABAIXO_DO_PESO;

//        if(IMC > 18.5 && IMC < 24.9){
//            grau = IMCEnum.PESO_IDEAL;
//        } else if(IMC > 25 && IMC < 29.9){
//            grau = IMCEnum.SOBREPESO;
//        } else if(IMC > 30 && IMC < 34.9){
//            grau = IMCEnum.OBESIDADE_GRAVE;
//        } else if(IMC > 35 && IMC < 39.9){
//            grau = IMCEnum.OBESIDADE_MÚLTIPLA;
//        } else if(IMC > 40){
//            grau = IMCEnum.OBESIDADE_MÚLTIPLA_GRAVE;
//        }

        return IMCVO.builder().IMC(IMC).classificacao(grau).build();
    }

}
