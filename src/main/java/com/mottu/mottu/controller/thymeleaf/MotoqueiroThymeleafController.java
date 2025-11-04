package com.mottu.mottu.controller.thymeleaf;

import com.mottu.mottu.model.DTO.MotoqueiroDTO;
import com.mottu.mottu.model.Motoqueiro;
import com.mottu.mottu.model.RoleName;
import com.mottu.mottu.model.Usuario;
import com.mottu.mottu.model.CustomUserDetails;
import com.mottu.mottu.service.MotoqueiroMapper;
import com.mottu.mottu.service.MotoqueiroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/motoqueiros-view")
public class MotoqueiroThymeleafController {

    @Autowired
    private MotoqueiroService motoqueiroService;

    // LISTAR — qualquer usuário autenticado
    @GetMapping("/todos")
    public String listarMotoqueiros(Model model) {
        List<Motoqueiro> motoqueiros = motoqueiroService.listarTodos();
        model.addAttribute("motoqueiros", motoqueiros);

        if (motoqueiros.isEmpty()) {
            model.addAttribute("mensagem", "Nenhum motoqueiro cadastrado.");
        }

        return "motoqueiro/listar";
    }

    // FORMULÁRIO ADICIONAR — somente ADMIN
    @GetMapping("/adicionar")
    public String mostrarFormularioAdicionar(Model model,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem adicionar motoqueiros.");
            return "redirect:/motoqueiros-view/todos";
        }

        model.addAttribute("motoqueiroDTO", new MotoqueiroDTO());
        return "motoqueiro/adicionar";
    }

    // ADICIONAR — somente ADMIN
    @PostMapping("/adicionar")
    public String adicionarMotoqueiro(@Valid MotoqueiroDTO dto,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      Model model) {
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem adicionar motoqueiros.");
            return "redirect:/motoqueiros-view/todos";
        }

        motoqueiroService.criarMotoqueiro(dto);
        return "redirect:/motoqueiros-view/todos";
    }

    // FORMULÁRIO EDITAR — somente ADMIN
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem editar motoqueiros.");
            return "redirect:/motoqueiros-view/todos";
        }

        Optional<Motoqueiro> optionalMotoqueiro = motoqueiroService.buscarPorId(id);
        if (optionalMotoqueiro.isEmpty()) {
            model.addAttribute("mensagemErro", "Motoqueiro não encontrado.");
            return "motoqueiro/listar";
        }

        Motoqueiro motoqueiro = optionalMotoqueiro.get();
        model.addAttribute("motoqueiroDTO", MotoqueiroMapper.toDTO(motoqueiro));
        model.addAttribute("motoqueiroId", id);

        return "motoqueiro/editar";
    }

    // EDITAR — somente ADMIN
    @PostMapping("/editar/{id}")
    public String editarMotoqueiro(@PathVariable Long id,
                                   @Valid MotoqueiroDTO dto,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model) {
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem editar motoqueiros.");
            return "redirect:/motoqueiros-view/todos";
        }

        motoqueiroService.atualizarMotoqueiro(id, dto);
        return "redirect:/motoqueiros-view/todos";
    }

    // FORMULÁRIO EXCLUIR — somente ADMIN
    @GetMapping("/excluir/{id}")
    public String mostrarFormularioExcluir(@PathVariable Long id,
                                           Model model,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem excluir motoqueiros.");
            return "redirect:/motoqueiros-view/todos";
        }

        Motoqueiro motoqueiro = motoqueiroService.buscarPorId(id).orElse(null);
        if (motoqueiro == null) {
            model.addAttribute("mensagemErro", "Motoqueiro não encontrado.");
            return "motoqueiro/listar";
        }

        model.addAttribute("motoqueiro", motoqueiro);
        return "motoqueiro/excluir";
    }

    // EXCLUIR — somente ADMIN
    @PostMapping("/excluir/{id}")
    public String excluirMotoqueiro(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model) {
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem excluir motoqueiros.");
            return "redirect:/motoqueiros-view/todos";
        }

        try {
            motoqueiroService.excluirMotoqueiro(id);
        } catch (IllegalStateException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            motoqueiroService.buscarPorId(id).ifPresent(m -> model.addAttribute("motoqueiro", m));
            return "motoqueiro/excluir";
        }

        return "redirect:/motoqueiros-view/todos";
    }
}
