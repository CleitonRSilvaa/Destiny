package com.destiny.controller;

import com.destiny.model.CustomUserDetails;
import com.destiny.model.MensagemResponse;
import com.destiny.model.Produto;
import com.destiny.model.StatusProduto;
import com.destiny.service.ProductDetailService;
import com.destiny.service.ProdutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller()
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProductDetailService service;

    @Autowired
    private ProdutoService produtoService;

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @GetMapping("/listar")
    public String telaProduto(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("nomeProduto") Optional<String> nome) {

        Page<Produto> produtoPage = produtoService.buscarProdutos(page, size, nome);
        model.addAttribute("produtoPage", produtoPage);
        return "admin/admin-menager_produtos";
    }

    @PostMapping("/add")
    public String cadastrarProduto(@ModelAttribute Produto produto,
            @RequestParam("imagem") MultipartFile[] imagens,
            @RequestParam("imgPrincipal") int imgPrincipal,
            RedirectAttributes redirect) {

        try {
            produtoService.cadastrarProduto(produto, imagens, imgPrincipal);
            redirect.addFlashAttribute("tipo", "success");
            redirect.addFlashAttribute("mensagem", "Produto cadastrado com sucesso!");
            return "redirect:/produto/listar";
        } catch (Exception e) {
            logger.error("Erro ao cadastrar produto", e);
            redirect.addFlashAttribute("tipo", "error");
            redirect.addFlashAttribute("mensagem", "Erro ao cadastrar produto!");
            return "redirect:/produto/listar";
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable long id, @RequestBody Produto produto) {
        Produto novProduto = service.update(id, produto);
        return ResponseEntity.ok().body(novProduto);
    }

    @PostMapping("/editarProduto")
    public String editarProduto(@ModelAttribute Produto produto,
            @RequestParam(required = false) MultipartFile[] imagemInput,
            @RequestParam("imagensParaRemover") String imagensParaRemover,
            @RequestParam("imagenPrinclAt") String imagensParaAtualizar,
            @RequestParam(required = false) String imgPrincipal, RedirectAttributes redirect) {

        return produtoService.editarProduto(
                produto,
                imagemInput,
                imagensParaRemover,
                imagensParaAtualizar,
                imgPrincipal,
                redirect);

    }

    @GetMapping("/preview")
    public String trazerPorId(@RequestParam(name = "id", required = false) Long id, Model model,
            RedirectAttributes redirect) {

        Optional<Produto> produtoOptional = produtoService.buscarProdutoPorId(id);

        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            model.addAttribute("produto", produto);
            return "admin/preview-produto";
        } else {
            redirect.addFlashAttribute("tipo", "error");
            redirect.addFlashAttribute("mensagem", "Produto não encontrado!");
            return "redirect:/produto/listar";
        }
    }

    @ResponseBody
    @PostMapping("/updateStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> updateStatus(
            @RequestBody StatusUpdateRequestProduto statusUpdateRequestProduto) {
        return produtoService.updateStatus(statusUpdateRequestProduto);
    }

    @GetMapping("/informacao")
    public String buscar(@RequestParam(name = "id", required = false) Long id, Model model,
            RedirectAttributes redirect) {
        Optional<Produto> produtoOptional = produtoService.buscarProdutoPorId(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String nome = auth.getName();
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            nome = userDetails.getNome();
            System.out.println(nome);
        }

        model.addAttribute("nomeUsuario", nome);

        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            model.addAttribute("produto", produto);
        } else {
            redirect.addFlashAttribute("tipo", "error");
            redirect.addFlashAttribute("mensagem", "Produto não encontrado!");
        }
        return "info_produto";
    }

    @GetMapping("/detalhes")
    public String buscarProdutoPorId(@RequestParam(name = "id", required = false) Long id, Model model,
            RedirectAttributes redirect) {

        Optional<Produto> produtoOptional = produtoService.buscarProdutoPorId(id);

        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            model.addAttribute("produto", produto);
            return "admin/alteration_produtos";
        } else {
            redirect.addFlashAttribute("tipo", "error");
            redirect.addFlashAttribute("mensagem", "Produto não encontrado!");
            return "redirect:/produto/listar";
        }
    }

    public String getAlertString(String tipo, String texto) {
        String template = "<div class=\"alert fade\" id=\"alert-{tipo}\" role=\"alert\" data-mdb-color=\"{tipo}\" data-mdb-position=\"top-right\" data-mdb-stacking=\"true\" data-mdb-width=\"535px\" data-mdb-width=\"535px\" data-mdb-append-to-body=\"true\" data-mdb-hidden=\"true\" data-mdb-autohide=\"true\" data-mdb-delay=\"2000\">{meu texto}</div>";
        return template.replace("{tipo}", tipo).replace("{meu texto}", texto);
    }

    public static class StatusUpdateRequestProduto {

        private StatusProduto status;
        private Long id;

        public StatusProduto getStatus() {
            return status;
        }

        public void setStatus(StatusProduto status) {
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}