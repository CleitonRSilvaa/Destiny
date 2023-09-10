package com.destiny.controller;

import com.destiny.model.Imagem;
import com.destiny.model.Produto;
import com.destiny.model.StatusProduto;
import com.destiny.repository.ImagemRepository;
import com.destiny.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller()
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ImagemRepository imagemRepository;


    @GetMapping("/listar")
    public String telaProduto(Model model,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("nomeProduto") Optional<String> nome) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(10);

        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
        Page<Produto> produtoPage;

        if (nome.isPresent()) {
            produtoPage = produtoRepository.findByNomeContaining(nome.get(), pageRequest);
        } else {
            produtoPage = produtoRepository.findAll(pageRequest);
        }

        model.addAttribute("produtoPage", produtoPage);

        return "admin/admin-menager_produtos";
    }





    @GetMapping("/addProduto")
    public String telaCadastro(Model model) {


        model.getAttribute("nomeProduto");
        model.getAttribute("descricaoProduto");
        model.getAttribute("valor");
        model.getAttribute("qntProduto");
        model.getAttribute("");
        model.getAttribute("");

        return "estoque/addProduto.html";
    }


    @PostMapping("/add")
    @Transactional
    public String cadastrarProduto(@ModelAttribute("nome") String nome,
                                   @ModelAttribute("valor") double valor,
                                   @ModelAttribute("quantidade") int quantidade,
                                   @ModelAttribute("avaliacao") int avaliacao,
                                   @ModelAttribute("descricao") String descricao,
                                   @RequestParam("imagem") MultipartFile[] imagens,
                                   @RequestParam("imgPrincipal") int imgPrincipal,
                                     RedirectAttributes redirect ) {

        try {
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setValor(new BigDecimal(valor));
            produto.setQuantidade(quantidade);
            produto.setAvaliacao(avaliacao);
            produto.setDescricao(descricao);
            produto.setStatusProduto(StatusProduto.ATIVO);
            produtoRepository.save(produto);
            int p = 0;
            for (MultipartFile imagem : imagens) {
                if (imagem != null && !imagem.isEmpty()) {
                    String nomeImagem = dataHoraStrg().concat(StringUtils.cleanPath(imagem.getOriginalFilename().replace(" ", "_")));
                    String caminho = "imagens/produtos/" + nomeImagem;
                    Path path = Paths.get("src/main/resources/static/" + caminho);
                    try {
                        Files.copy(imagem.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.out.println("Falha ao armazenar a imagem " + nomeImagem + e);
                    }



                    Imagem novaImagem = new Imagem();
                    if (imgPrincipal == p){
                        novaImagem.setPrincipal(true);
                    }
                    p++;
                    novaImagem.setCaminho(caminho);
                    novaImagem.setProduto(produto);
                    System.out.println(novaImagem);
                    imagemRepository.save(novaImagem);
                } else {
                    String nomeImagem = "default.jpg";
                    String caminho = "imagens/produtos/" + nomeImagem;
                    Path path = Paths.get("src/main/resources/static/" + caminho);
                    Imagem novaImagem = new Imagem();
                    novaImagem.setCaminho(caminho);
                    novaImagem.setProduto(produto);
                    novaImagem.setPrincipal(true);
                    imagemRepository.save(novaImagem);
                    break;
                }
            }
            redirect.addFlashAttribute("tipo", "success");
            redirect.addFlashAttribute("mensagem", "Produto cadastrado com sucesso!");
            return "redirect:/produto/listar";
        } catch (Exception e) {
            redirect.addFlashAttribute("tipo", "error");
            redirect.addFlashAttribute("mensagem", "Erro ao cadastrar produto!");
            return "redirect:/produto/listar";
        }
    }


    private String dataHoraStrg() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String dataHoraFormatada = agora.format(formato).concat("-".trim().toLowerCase()).replace(':', '-');
        return dataHoraFormatada;
    }

    public String getAlertString(String tipo, String texto) {
        String template = "<div class=\"alert fade\" id=\"alert-{tipo}\" role=\"alert\" data-mdb-color=\"{tipo}\" data-mdb-position=\"top-right\" data-mdb-stacking=\"true\" data-mdb-width=\"535px\" data-mdb-width=\"535px\" data-mdb-append-to-body=\"true\" data-mdb-hidden=\"true\" data-mdb-autohide=\"true\" data-mdb-delay=\"2000\">{meu texto}</div>";
        return template.replace("{tipo}", tipo).replace("{meu texto}", texto);
    }



}