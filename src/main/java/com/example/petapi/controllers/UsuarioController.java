package com.example.petapi.controllers;

import com.example.petapi.models.dtos.EmailDto;
import com.example.petapi.models.entities.Usuario;
import com.example.petapi.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api") @CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @PostMapping("/usuario")
    public void incluir(@RequestBody Usuario usuario) {
        usuarioService.incluir(usuario);
    }

    @PutMapping("/usuario")
    public void alterar(@RequestBody Usuario usuario) {
        usuarioService.alterar(usuario);
    }

    @GetMapping("/usuario/{id}")
    public Usuario buscar(@PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    @DeleteMapping("/usuario/{id}")
    public void excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
    }

    @GetMapping("/usuario/buscar")
    public Usuario buscar(@RequestBody EmailDto emailDto) {
        return usuarioService.buscar(emailDto.getEmail());
    }
}
