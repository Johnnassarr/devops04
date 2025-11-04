package com.mottu.mottu.controller;

import com.mottu.mottu.model.DTO.MotoqueiroDTO;
import com.mottu.mottu.model.Motoqueiro;
import com.mottu.mottu.service.MotoqueiroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/motoqueiros")
public class MotoqueiroRestController {

    @Autowired
    private MotoqueiroService motoqueiroService;

    @GetMapping("/listar")
    public ResponseEntity<List<Motoqueiro>> getAll() {
        return ResponseEntity.ok(motoqueiroService.listarTodos());
    }

    @PostMapping("/save")
    public ResponseEntity<Motoqueiro> cadastrarMotoqueiro(@Valid @RequestBody MotoqueiroDTO dto) {
        Motoqueiro motoqueiro = motoqueiroService.criarMotoqueiro(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(motoqueiro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @PathVariable Long id, @RequestBody MotoqueiroDTO dto) {
        return motoqueiroService.atualizarMotoqueiro(id, dto)
                .<ResponseEntity<?>>map(m -> ResponseEntity.ok(m))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Motoqueiro não encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        boolean excluido = motoqueiroService.excluirMotoqueiro(id);
        if (excluido) {
            return ResponseEntity.ok("Motoqueiro deletado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Motoqueiro não encontrado");
        }
    }
}
