package com.mottu.mottu.controller.thymeleaf;

import com.mottu.mottu.model.DTO.GalpaoDTO;
import com.mottu.mottu.model.Galpao;
import com.mottu.mottu.model.RoleName;
import com.mottu.mottu.model.Usuario;
import com.mottu.mottu.model.CustomUserDetails;
import com.mottu.mottu.repository.GalpaoRepository;
import com.mottu.mottu.service.GalpaoMapper;
import com.mottu.mottu.service.GalpaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/galpoes-view")
public class GalpaoThymeleafController {

    @Autowired
    private GalpaoService galpaoService;

    @Autowired
    private GalpaoRepository galpaoRepository;

    // LISTAR GALPÕES (TODOS VEEM)
    @GetMapping("/todos")
    public String listarGalpoes(Model model) {
        List<Galpao> galpoes = galpaoRepository.findAll();
        model.addAttribute("galpoes", galpoes);

        if (galpoes.isEmpty()) {
            model.addAttribute("mensagem", "Nenhum galpão cadastrado.");
        }

        return "galpao/listar";
    }

    // FORMULÁRIO ADICIONAR (só ADMIN pode submeter POST; aqui só mostramos o form)
    @GetMapping("/adicionar")
    public String mostrarFormularioAdicionar(Model model,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        // opcional: bloquear visualização do form via lógica do controller (mas ideal é esconder botão no Thymeleaf)
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem adicionar galpões.");
            return "redirect:/galpoes-view/todos";
        }

        model.addAttribute("galpaoDTO", new GalpaoDTO());
        return "galpao/adicionar";
    }

    // ADICIONAR GALPÃO (POST — segurança também é aplicada pelo SecurityConfig)
    @PostMapping("/adicionar")
    public String adicionarGalpao(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @Valid GalpaoDTO dto,
                                  Model model) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem adicionar galpões.");
            return "redirect:/galpoes-view/todos";
        }

        galpaoService.salvar(dto);
        return "redirect:/galpoes-view/todos";
    }

    // FORMULÁRIO EDITAR (só ADMIN pode ver/editar)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem editar galpões.");
            return "redirect:/galpoes-view/todos";
        }

        Galpao galpao = galpaoService.buscarPorId(id).orElse(null);
        if (galpao == null) {
            model.addAttribute("mensagemErro", "Galpão não encontrado");
            return "galpao/listar";
        }

        model.addAttribute("galpaoDTO", GalpaoMapper.toDTO(galpao));
        model.addAttribute("galpaoId", id);
        return "galpao/editar";
    }

    // EDITAR GALPÃO (POST)
    @PostMapping("/editar/{id}")
    public String editarGalpao(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable Long id,
                               GalpaoDTO dto,
                               Model model) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem editar galpões.");
            return "redirect:/galpoes-view/todos";
        }

        galpaoService.editar(id, dto);
        return "redirect:/galpoes-view/todos";
    }

    // FORMULÁRIO EXCLUIR (só ADMIN)
    @GetMapping("/excluir/{id}")
    public String mostrarFormularioExcluir(@PathVariable Long id,
                                           Model model,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem excluir galpões.");
            return "redirect:/galpoes-view/todos";
        }

        Galpao galpao = galpaoService.buscarPorId(id).orElse(null);
        if (galpao == null) {
            model.addAttribute("mensagemErro", "Galpão não encontrado.");
            return "galpao/listar";
        }

        model.addAttribute("galpao", galpao);
        return "galpao/excluir";
    }

    // EXECUTAR EXCLUSÃO (POST)
    @PostMapping("/excluir/{id}")
    public String excluirGalpao(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @PathVariable Long id,
                                Model model) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem excluir galpões.");
            return "redirect:/galpoes-view/todos";
        }

        try {
            galpaoService.excluir(id);
        } catch (IllegalStateException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            galpaoService.buscarPorId(id).ifPresent(g -> model.addAttribute("galpao", g));
            return "galpao/excluir";
        }

        return "redirect:/galpoes-view/todos";
    }
}
