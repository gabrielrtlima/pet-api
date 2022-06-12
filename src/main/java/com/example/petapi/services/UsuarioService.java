package com.example.petapi.services;

import com.example.petapi.models.entities.Usuario;
import com.example.petapi.repositories.IUsuarioRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public void incluir(Usuario usuario) {

        if(usuario == null){
            throw new RuntimeException("Usuario não pode ser nulo");
        }

        usuario.setId(null);
        verificarCampos(usuario);
        validarEmail(usuario.getEmail());

        if(usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            throw new RuntimeException("Email já cadastrado");
        }

        validarAltura(usuario.getAltura());
        validarPeso(usuario.getPesoInicial());

        usuario.setDataInicial(LocalDate.now());
        usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario buscar(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if(usuario.isEmpty()) {
            throw new RuntimeException("Usuario não encontrado");
        }
        return usuario.get();
    }

    public Usuario alterar(Usuario usuario) {
        verificarCampos(usuario);
        validarEmail(usuario.getEmail());

        if(usuarioRepository.findByEmail(usuario.getEmail()) == null) {
            throw new RuntimeException("Não há usuário cadastrado com o email:" + usuario.getEmail());
        }

        Usuario user = usuarioRepository.findByEmail(usuario.getEmail()).get();
        usuario.setId(user.getId());
        usuario.setDataInicial(user.getDataInicial());

        if (usuario.getId() == null || usuario.getId() == 0l) {
            throw new RuntimeException("Informe um identificador de usuário");
        }

        if (!this.usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Não existe usuario cadastrado com o identificador:" + usuario.getId());
        }

        validarAltura(usuario.getAltura());
        validarPeso(usuario.getPesoInicial());

        return usuarioRepository.save(usuario);
    }

    public void excluir(Long id) {
        if(id == null || id == 0){
            throw new RuntimeException("Informe um indentificador válido");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Não existe usuario cadastrado com o identificador:" + id);
        }

        usuarioRepository.deleteById(id);
    }

    private void verificarCampos(Usuario usuario) {
        if(!StringUtils.hasLength(usuario.getNome()) || !StringUtils.hasLength((usuario.getEmail()))
                || usuario.getAltura() == 0 || usuario.getPesoInicial() == 0 || usuario.getPesoDesejado() == 0
                || usuario.getDataObjetivo() == null || usuario.getSexo() == null){
            throw new RuntimeException("Todos os campos devem ser preenchidos");
        }
    }

    private void validarEmail(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (Exception e) {
            throw new RuntimeException("Email inválido");
        }

    }

    private void validarAltura(int altura) {
        if(altura < 100 || altura > 300){
            throw new RuntimeException("Altura inválida");
        }
    }

    private void validarPeso(double peso) {
        if(peso < 30 || peso > 300){
            throw new RuntimeException("Peso inválido");
        }
    }
}
