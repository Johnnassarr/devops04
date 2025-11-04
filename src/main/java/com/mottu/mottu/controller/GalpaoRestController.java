    package com.mottu.mottu.controller;

    import com.mottu.mottu.model.DTO.GalpaoDTO;
    import com.mottu.mottu.model.Galpao;
    import com.mottu.mottu.service.GalpaoService;
    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/galpoes")
    public class GalpaoRestController {

        private final GalpaoService galpaoService;

        public GalpaoRestController(GalpaoService galpaoService) {
            this.galpaoService = galpaoService;
        }

        @GetMapping("/listar")
        public ResponseEntity<List<Galpao>> getAll() {
            List<Galpao> listaGalpao = galpaoService.listarTodos();
            return ResponseEntity.status(HttpStatus.OK).body(listaGalpao);
        }

        @PostMapping("/save")
        public ResponseEntity<Galpao> salvar(@Valid @RequestBody GalpaoDTO dto) {
            Galpao galpao = galpaoService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(galpao);
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> editar(@Valid @PathVariable Long id, @RequestBody GalpaoDTO dto) {
            return galpaoService.editar(id, dto)
                    .<ResponseEntity<?>>map(galpao -> ResponseEntity.status(HttpStatus.OK).body(galpao))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Galpão não encontrado"));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<String> excluir(@PathVariable Long id) {
            boolean excluido = galpaoService.excluir(id);
            if (excluido) {
                return ResponseEntity.status(HttpStatus.OK).body("Galpão deletado");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Galpão não encontrado");
            }
        }
    }
