package com.example.petapi.services;

import com.example.petapi.models.entities.Usuario;
import com.example.petapi.repositories.IUsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public Usuario incluir(Usuario usuario) {

        if (usuario == null) {
            throw new RuntimeException("Usuario não pode ser nulo");
        }

        usuario.setId(null);
        verificarPreenchimentoCamposObrigatorios(usuario);
        validarPreenchimentoEmail(usuario.getEmail());

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        validarPreenchimentoAltura(usuario.getAltura());
        validarPreenchimentoPeso(usuario.getPesoInicial());

        usuario.setDataInicial(LocalDate.now());
        validarPreenchimentoData(usuario.getDataInicial(), usuario.getDataObjetivo());

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario buscar(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario não encontrado");
        }
        return usuario.get();
    }

    public Usuario alterar(Usuario usuario) {

        if(usuario == null || usuario.getId() == null) {
            log.error("Os dados do usuário devem ser informados para realizar a alteração");
            throw new RuntimeException("Os dados do usuário devem ser informados");
        }

        Optional<Usuario> optUsuarioBD = usuarioRepository.findById(usuario.getId());
        Usuario usuarioBD = optUsuarioBD.get();

        if (optUsuarioBD.isEmpty()) {
            log.error("Não há usuário cadastro com o id: {}", usuario.getId());
            throw new RuntimeException("Não existe usuario cadastrado com o identificador:" + usuario.getId());
        }

        verificarPreenchimentoCamposObrigatorios(usuario);
        validarPreenchimentoEmail(usuario.getEmail());
        validarPreenchimentoAltura(usuario.getAltura());
        validarPreenchimentoPeso(usuario.getPesoInicial());
        validarPreenchimentoData(usuarioBD.getDataInicial(), usuario.getDataObjetivo());

        if(usuarioRepository.findByEmailAndIdNot(usuario.getEmail(), usuario.getId()).isPresent()) {
            log.error("Usuário está tentando alterar para um email já cadastrado, email: ", usuario.getEmail());
            throw new RuntimeException("Usuário com email já cadastrado: " + usuario.getEmail());
        }

        usuarioBD.setNome(usuario.getNome());
        usuarioBD.setEmail(usuario.getEmail());
        usuarioBD.setAltura(usuario.getAltura());
        usuarioBD.setPesoInicial(usuario.getPesoInicial());
        usuarioBD.setDataObjetivo(usuario.getDataObjetivo());
        usuarioBD.setPesoDesejado(usuario.getPesoDesejado());
        usuarioBD.setSexo(usuario.getSexo());

        usuarioRepository.save(usuarioBD);
        log.debug("Usuario alterado: {}", usuarioBD);
        return usuarioBD;
    }

    public void excluir(Long id) {
        if (id == null || id == 0) {
            throw new RuntimeException("Informe um indentificador válido");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Não existe usuario cadastrado com o identificador:" + id);
        }

        usuarioRepository.deleteById(id);
    }

    private void verificarPreenchimentoCamposObrigatorios(Usuario usuario) {
        if (!StringUtils.hasLength(usuario.getNome()) || !StringUtils.hasLength((usuario.getEmail()))
                || usuario.getAltura() == 0 || usuario.getPesoInicial() == 0 || usuario.getPesoDesejado() == 0
                || usuario.getDataObjetivo() == null || usuario.getSexo() == null) {
            log.error("Os campos obrigatórios não foram preenchidos");
            throw new RuntimeException("Os campos: Nome, Email, Altura, Peso Inicial, Peso Desejado, Data Objetivo e Sexo são obrigatórios");
        }
    }

    private void validarPreenchimentoEmail(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (Exception e) {
            log.error("Ocorreu um erro ao validar o email usando a api de validar email", e);
            throw new RuntimeException("Email inválido", e);
        }

    }

    private void validarPreenchimentoAltura(int altura) {
        if (altura < 100 || altura > 300) {
            log.error("Preenchimento da altura veio fora do intervalo permitido");
            throw new RuntimeException("Altura deve está entre 100 e 300");
        }
    }

    private void validarPreenchimentoPeso(double peso) {
        if (peso < 30 || peso > 300) {
            log.error("Preenchimento do peso veio fora do intervalo permitido");
            throw new RuntimeException("Peso deve está entre 30 e 300");
        }
    }

    private void validarPreenchimentoData(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            log.error("Usuário informou a data final menor que a data inicial");
            throw new RuntimeException("Data inválida, a data do seu objetivo deve ser maior que a data de início");
        }

        if (Days.daysBetween(dataInicio, dataFim).getDays() < 7) {
            log.error("Usuário informou a data de início e fim menor que 7 dias");
            throw new RuntimeException("Data inválida, a data do seu objetivo deve ter no mínimo 7 dias de diferença");
        }
    }
}
