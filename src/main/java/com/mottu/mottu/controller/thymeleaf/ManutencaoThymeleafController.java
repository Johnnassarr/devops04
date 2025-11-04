package com.mottu.mottu.controller.thymeleaf;

import com.mottu.mottu.model.CustomUserDetails;
import com.mottu.mottu.model.DTO.ManutencaoDTO;
import com.mottu.mottu.model.Manutencao;
import com.mottu.mottu.model.RoleName;
import com.mottu.mottu.model.Usuario;
import com.mottu.mottu.service.ManutencaoService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manutencoes-view")
public class ManutencaoThymeleafController {

    @Autowired
    private ManutencaoService manutencaoService;

    // LISTAR MANUTENÇÕES (TODOS PODEM VER)
    @GetMapping("/todos")
    public String listarManutencoes(Model model) {
        List<Manutencao> manutencoes = manutencaoService.listarTodos();
        model.addAttribute("manutencoes", manutencoes);
        if (manutencoes.isEmpty()) {
            model.addAttribute("mensagem", "Nenhuma manutenção cadastrada.");
        }
        return "manutencao/listar";
    }

    // FORMULÁRIO ADICIONAR (somente ADMIN)
    @GetMapping("/adicionar")
    public String mostrarFormularioAdicionar(Model model,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem adicionar manutenções.");
            return "redirect:/manutencoes-view/todos";
        }

        model.addAttribute("manutencaoDTO", new ManutencaoDTO());
        return "manutencao/adicionar";
    }

    // ADICIONAR (POST)
    @PostMapping("/adicionar")
    public String adicionarManutencao(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @Valid ManutencaoDTO dto,
                                      BindingResult bindingResult,
                                      Model model) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem adicionar manutenções.");
            return "redirect:/manutencoes-view/todos";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("manutencaoDTO", dto);
            return "manutencao/adicionar";
        }

        String mensagem = validarDatas(dto);
        if (!mensagem.isEmpty()) {
            model.addAttribute("mensagem", mensagem);
            model.addAttribute("manutencaoDTO", dto);
            return "manutencao/adicionar";
        }

        if (dto.getDataFechamento() != null) {
            dto.setEmAndamento(false);
        }

        dto.setId(null);
        manutencaoService.salvar(dto);
        return "redirect:/manutencoes-view/todos";
    }

    // FORMULÁRIO EDITAR (somente ADMIN)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          Model model,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem editar manutenções.");
            return "redirect:/manutencoes-view/todos";
        }

        Manutencao manutencao = manutencaoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Manutenção não encontrada"));

        ManutencaoDTO dto = new ManutencaoDTO();
        BeanUtils.copyProperties(manutencao, dto);
        model.addAttribute("manutencaoDTO", dto);
        model.addAttribute("manutencaoId", id);

        return "manutencao/editar";
    }

    // EDITAR (POST)
    @PostMapping("/editar/{id}")
    public String editarManutencao(@PathVariable Long id,
                                   @Valid ManutencaoDTO dto,
                                   BindingResult bindingResult,
                                   Model model,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem editar manutenções.");
            return "redirect:/manutencoes-view/todos";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("manutencaoDTO", dto);
            return "manutencao/editar";
        }

        String mensagem = validarDatas(dto);
        if (!mensagem.isEmpty()) {
            model.addAttribute("mensagem", mensagem);
            model.addAttribute("manutencaoDTO", dto);
            return "manutencao/editar";
        }

        if (dto.getDataFechamento() != null) {
            dto.setEmAndamento(false);
        }

        Optional<Manutencao> manutencaoAtualizada = manutencaoService.atualizar(id, dto);
        if (manutencaoAtualizada.isEmpty()) {
            model.addAttribute("mensagemErro", "Manutenção não encontrada para atualização.");
            model.addAttribute("manutencaoDTO", dto);
            return "manutencao/editar";
        }

        return "redirect:/manutencoes-view/todos";
    }

    // FORMULÁRIO EXCLUIR (somente ADMIN)
    @GetMapping("/excluir/{id}")
    public String mostrarFormularioExcluir(@PathVariable Long id,
                                           Model model,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem excluir manutenções.");
            return "redirect:/manutencoes-view/todos";
        }

        Manutencao manutencao = manutencaoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Manutenção não encontrada"));
        model.addAttribute("manutencao", manutencao);

        return "manutencao/excluir";
    }

    // EXCLUIR (POST)
    @PostMapping("/excluir/{id}")
    public String excluirManutencao(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model) {

        Usuario usuario = userDetails.getUsuario();
        if (!usuario.getRole().getNome().equals(RoleName.ADMIN)) {
            model.addAttribute("mensagemErro", "Apenas administradores podem excluir manutenções.");
            return "redirect:/manutencoes-view/todos";
        }

        manutencaoService.deletar(id);
        return "redirect:/manutencoes-view/todos";
    }

    // Validações de datas
    private String validarDatas(ManutencaoDTO dto) {
        LocalDateTime agora = LocalDateTime.now();

        if (dto.getDataAbertura() != null && dto.getDataAbertura().isAfter(agora)) {
            return "A data de abertura não pode ser no futuro.";
        }

        if (dto.getDataFechamento() != null && dto.getDataFechamento().isAfter(agora)) {
            return "A data de fechamento não pode ser no futuro.";
        }

        if (dto.getDataAbertura() != null && dto.getDataFechamento() != null &&
                dto.getDataAbertura().isAfter(dto.getDataFechamento())) {
            return "A data de abertura deve ser anterior à data de fechamento.";
        }

        return "";
    }
}
