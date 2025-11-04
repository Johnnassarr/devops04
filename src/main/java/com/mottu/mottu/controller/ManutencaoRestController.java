package com.mottu.mottu.controller;

import com.mottu.mottu.model.DTO.ManutencaoDTO;
import com.mottu.mottu.model.Manutencao;
import com.mottu.mottu.service.ManutencaoService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/manutencoes")
public class ManutencaoRestController {

    @Autowired
    private ManutencaoService manutencaoService;

    // Listar todas
    @GetMapping("/listar")
    public ResponseEntity<List<Manutencao>> listarTodos() {
        return ResponseEntity.ok(manutencaoService.listarTodos());
    }

    // Salvar nova manutenção
    @PostMapping("/save")
    public ResponseEntity<?> salvar(@Valid @RequestBody ManutencaoDTO dto) {
        try {
            Manutencao manutencao = manutencaoService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(manutencao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Atualizar manutenção
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@Valid @PathVariable Long id, @RequestBody ManutencaoDTO dto) {
        Optional<Manutencao> manutencaoOpt = manutencaoService.atualizar(id, dto);
        if (manutencaoOpt.isPresent()) {
            return ResponseEntity.ok(manutencaoOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Manutenção não encontrada");
        }
    }

    // Deletar manutenção
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            manutencaoService.deletar(id);
            return ResponseEntity.ok("Serviço de manutenção deletado");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}