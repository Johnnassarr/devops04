package com.mottu.mottu.controller;

import com.mottu.mottu.model.DTO.UsuarioDTO;
import com.mottu.mottu.model.Role;
import com.mottu.mottu.model.Usuario;
import com.mottu.mottu.repository.RoleRepository;
import com.mottu.mottu.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/list")
    public ResponseEntity<List<Usuario>> getAll() {
        List<Usuario> listUsuarios = usuarioRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(listUsuarios);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO userDTO) {
        try {
            if (usuarioRepository.existsByEmail(userDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Erro: já existe um usuário com esse email");
            }

            Role role = roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role não encontrada"));

            Usuario user = new Usuario();
            user.setEmail(userDTO.getEmail());
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setRole(role);

            return ResponseEntity.ok(usuarioRepository.save(user));
        } catch (Exception e) {
            e.printStackTrace(); // mostra o erro real no console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody UsuarioDTO userDTO) {
        Usuario usuario = usuarioRepository.findByEmail(userDTO.getEmail());
        if (usuario == null || !usuario.getPassword().equals(userDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário ou senha inválidos");
        }
        // Retorna apenas o username para salvar no frontend
        return ResponseEntity.ok(usuario.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado");
        }
        usuarioRepository.delete(usuario.get());
        return ResponseEntity.status(HttpStatus.OK).body("Usuario deletado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado");
        }
        Usuario usuarioModel = usuario.get();
        BeanUtils.copyProperties(dto, usuarioModel);
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.save(usuarioModel));
    }
}
