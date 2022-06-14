package com.example.petapi.services;

import com.example.petapi.models.vo.IMCEnum;
import com.example.petapi.models.vo.IMCVO;
import com.example.petapi.models.vo.MonitoramentoVO;
import com.example.petapi.models.entities.Peso;
import com.example.petapi.models.entities.Usuario;
import com.example.petapi.repositories.IPesoRepository;
import com.example.petapi.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PesoService {

    @Autowired
    private IPesoRepository pesoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public MonitoramentoVO montarMonitoramento(String email){
        MonitoramentoVO retorno = new MonitoramentoVO();

        //TODO = VERIFICAR SE PASSOU EMAIL
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(email);
        if(optUsuario.isEmpty()) {
            throw new RuntimeException("Usuário não cadastrado");
        }

        double altura = optUsuario.get().getAltura() / 100;

        Optional<Peso> optPeso = pesoRepository.findFirstByUsuarioEmailOrderByDataDesc(email);
        if(optPeso.isEmpty()){
            throw new RuntimeException("Usuário não tem registro de peso para o cálculo de IMC");
        }

        double peso = optPeso.get().getPeso();

        double IMC = peso/(altura * altura);

        IMCEnum grau = IMCEnum.ABAIXO_DO_PESO;

        //TODO = IF PARA CALCULAR GRAU

        retorno.setIMC(IMCVO.builder().IMC(IMC).classificacao(grau).build());

        return retorno;
    }

}
