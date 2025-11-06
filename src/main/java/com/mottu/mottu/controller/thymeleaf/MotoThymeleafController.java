package com.mottu.mottu.controller.thymeleaf;

import com.mottu.mottu.model.CustomUserDetails;
import com.mottu.mottu.model.DTO.MotoDTO;
import com.mottu.mottu.model.Moto;
import com.mottu.mottu.model.RoleName;
import com.mottu.mottu.model.Usuario;
import com.mottu.mottu.repository.GalpaoRepository;
import com.mottu.mottu.repository.MotoqueiroRepository;
import com.mottu.mottu.service.MotoMapper;
import com.mottu.mottu.service.MotoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // LISTAR MOTOS (TODOS VEEM)
    @GetMapping("/todos")
    public String listarMotos(Model model) {
        List<Moto> motos = motoService.listarTodas();
        model.addAttribute("motos", motos);

        if (motos.isEmpty()) {
            model.addAttribute("mensagem", "Nenhuma moto cadastrada.");
        }

        return "moto/listar";
    }

    // FORMULÁRIO ADICIONAR (só ADMIN pode submeter POST; aqui só mostramos o form)
    @GetMapping("/adicionar")
    public String mostrarFormularioAdicionar(Model model,
                                             @AuthenticationPrincipal CustomUserDetails userDetails,
                                             RedirectAttributes redirectAttributes) {
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "❌ Apenas administradores podem adicionar motos.");
            return "redirect:/motos-view/todos";
        }

        model.addAttribute("motoDTO", new MotoDTO());
        popularCombos(model);
        return "moto/adicionar";
    }

    // ADICIONAR MOTO (POST)
    @PostMapping("/adicionar")
    public String adicionarMoto(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @Valid MotoDTO dto,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "❌ Apenas administradores podem adicionar motos.");
            return "redirect:/motos-view/todos";
        }

        try {
            motoService.salvar(dto);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "✅ Moto adicionada com sucesso!");
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "❌ " + e.getMessage());
            model.addAttribute("motoDTO", dto);
            popularCombos(model);
            return "moto/adicionar";
        }

        return "redirect:/motos-view/todos";
    }

    // FORMULÁRIO EDITAR (só ADMIN pode ver/editar)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                          RedirectAttributes redirectAttributes) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "❌ Apenas administradores podem editar motos.");
            return "redirect:/motos-view/todos";
        }

        Optional<Moto> motoOpt = motoService.buscarPorId(id);
        if (motoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "❌ Moto não encontrada.");
            return "redirect:/motos-view/todos";
        }

        MotoDTO dto = MotoMapper.toDTO(motoOpt.get());
        if (motoOpt.get().getMotoboyEmUso() != null)
            dto.setMotoboyId(motoOpt.get().getMotoboyEmUso().getId());
        if (motoOpt.get().getGalpao() != null)
            dto.setGalpaoId(motoOpt.get().getGalpao().getId());

        model.addAttribute("motoDTO", dto);
        model.addAttribute("motoId", id);
        popularCombos(model);
        return "moto/editar";
    }

    // EDITAR MOTO (POST)
    @PostMapping("/editar/{id}")
    public String editarMoto(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long id,
                             MotoDTO dto,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "❌ Apenas administradores podem editar motos.");
            return "redirect:/motos-view/todos";
        }

        try {
            motoService.editar(id, dto);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "✅ Moto editada com sucesso!");
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "❌ " + e.getMessage());
            model.addAttribute("motoDTO", dto);
            popularCombos(model);
            return "moto/editar";
        }

        return "redirect:/motos-view/todos";
    }

    // FORMULÁRIO EXCLUIR (só ADMIN)
    @GetMapping("/excluir/{id}")
    public String mostrarFormularioExcluir(@PathVariable Long id,
                                           Model model,
                                           @AuthenticationPrincipal CustomUserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "❌ Apenas administradores podem excluir motos.");
            return "redirect:/motos-view/todos";
        }

        Optional<Moto> motoOpt = motoService.buscarPorId(id);
        if (motoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "❌ Moto não encontrada.");
            return "redirect:/motos-view/todos";
        }

        model.addAttribute("moto", motoOpt.get());
        return "moto/excluir";
    }

    // EXECUTAR EXCLUSÃO (POST)
    @PostMapping("/excluir/{id}")
    public String excluirMoto(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PathVariable Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "❌ Apenas administradores podem excluir motos.");
            return "redirect:/motos-view/todos";
        }

        try {
            motoService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "✅ Moto excluída com sucesso!");
        } catch (Exception e) {
            motoService.buscarPorId(id).ifPresent(m -> model.addAttribute("moto", m));
            model.addAttribute("mensagemErro", "❌ " + e.getMessage());
            return "moto/excluir";
        }

        return "redirect:/motos-view/todos";
    }

    // 🔧 MÉTODOS AUXILIARES
    private void popularCombos(Model model) {
        model.addAttribute("galpoes", galpaoRepository.findAll());
        model.addAttribute("motoqueiros", motoqueiroRepository.findAll());
    }
}
