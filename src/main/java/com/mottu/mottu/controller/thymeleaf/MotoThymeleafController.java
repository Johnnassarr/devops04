package com.mottu.mottu.controller.thymeleaf;

import com.mottu.mottu.model.DTO.MotoDTO;
import com.mottu.mottu.model.Moto;
import com.mottu.mottu.model.RoleName;
import com.mottu.mottu.model.Usuario;
import com.mottu.mottu.repository.GalpaoRepository;
import com.mottu.mottu.repository.MotoqueiroRepository;
import com.mottu.mottu.service.MotoMapper;
import com.mottu.mottu.service.MotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/motos-view")
public class MotoThymeleafController {

    @Autowired
    private MotoService motoService;

    @Autowired
    private MotoqueiroRepository motoqueiroRepository;

    @Autowired
    private GalpaoRepository galpaoRepository;

    // ✅ LISTAR — qualquer usuário autenticado
    @GetMapping("/todos")
    public String listar(Model model) {
        List<Moto> motos = motoService.listarTodas();
        model.addAttribute("motos", motos);
        if (motos.isEmpty()) {
            model.addAttribute("mensagem", "Nenhuma moto cadastrada.");
        }
        return "moto/listar";
    }

    // ✅ FORMULÁRIO ADICIONAR — somente ADMIN
    @GetMapping("/adicionar")
    public String mostrarFormularioAdicionar(Model model, @AuthenticationPrincipal Usuario usuario) {
        if (!isAdmin(usuario)) {
            return acessoNegado(model);
        }

        model.addAttribute("motoDTO", new MotoDTO());
        popularCombos(model);
        return "moto/adicionar";
    }

    @PostMapping("/adicionar")
    public String adicionar(@ModelAttribute MotoDTO dto,
                            @AuthenticationPrincipal Usuario usuario,
                            Model model) {
        if (!isAdmin(usuario)) {
            return acessoNegado(model);
        }

        try {
            motoService.salvar(dto);
            return "redirect:/motos-view/todos";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", e.getMessage());
            popularCombos(model);
            model.addAttribute("motoDTO", dto);
            return "moto/adicionar";
        }
    }

    // ✅ FORMULÁRIO EDITAR — somente ADMIN
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          @AuthenticationPrincipal Usuario usuario,
                                          Model model) {
        if (!isAdmin(usuario)) {
            return acessoNegado(model);
        }

        Optional<Moto> motoOpt = motoService.buscarPorId(id);
        if (motoOpt.isEmpty()) {
            model.addAttribute("mensagemErro", "Moto não encontrada.");
            return "redirect:/motos-view/todos";
        }

        MotoDTO dto = MotoMapper.toDTO(motoOpt.get());
        if (motoOpt.get().getMotoboyEmUso() != null)
            dto.setMotoboyId(motoOpt.get().getMotoboyEmUso().getId());
        if (motoOpt.get().getGalpao() != null)
            dto.setGalpaoId(motoOpt.get().getGalpao().getId());

        model.addAttribute("motoDTO", dto);
        popularCombos(model);
        return "moto/editar";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         @ModelAttribute MotoDTO dto,
                         @AuthenticationPrincipal Usuario usuario,
                         Model model) {
        if (!isAdmin(usuario)) {
            return acessoNegado(model);
        }

        try {
            motoService.editar(id, dto);
            return "redirect:/motos-view/todos";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", e.getMessage());
            popularCombos(model);
            model.addAttribute("motoDTO", dto);
            return "moto/editar";
        }
    }

    // ✅ FORMULÁRIO EXCLUIR — somente ADMIN
    @GetMapping("/excluir/{id}")
    public String mostrarFormularioExcluir(@PathVariable Long id,
                                           @AuthenticationPrincipal Usuario usuario,
                                           Model model) {
        if (!isAdmin(usuario)) {
            return acessoNegado(model);
        }

        Optional<Moto> motoOpt = motoService.buscarPorId(id);
        if (motoOpt.isEmpty()) {
            model.addAttribute("mensagemErro", "Moto não encontrada.");
            return "redirect:/motos-view/todos";
        }

        model.addAttribute("moto", motoOpt.get());
        return "moto/excluir";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          @AuthenticationPrincipal Usuario usuario,
                          Model model) {
        if (!isAdmin(usuario)) {
            return acessoNegado(model);
        }

        try {
            motoService.excluir(id);
            return "redirect:/motos-view/todos";
        } catch (Exception e) {
            motoService.buscarPorId(id).ifPresent(m -> model.addAttribute("moto", m));
            model.addAttribute("mensagemErro", e.getMessage());
            return "moto/excluir";
        }
    }

    // ✅ Métodos auxiliares
    private void popularCombos(Model model) {
        model.addAttribute("galpoes", galpaoRepository.findAll());
        model.addAttribute("motoqueiros", motoqueiroRepository.findAll());
    }

    private boolean isAdmin(Usuario usuario) {
        return usuario != null && usuario.getRole() != null &&
                usuario.getRole().getNome().equals(RoleName.ADMIN);
    }

    private String acessoNegado(Model model) {
        model.addAttribute("mensagemErro", "Apenas administradores podem realizar esta ação.");
        return "redirect:/motos-view/todos";
    }
}
