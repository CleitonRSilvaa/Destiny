package com.destiny.controller;

import com.destiny.model.Imagem;
import com.destiny.model.Produto;
import com.destiny.repository.ImagemRepository;
import com.destiny.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller()
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ImagemRepository imagemRepository;


    @GetMapping("/listar")
    public String telaProduto(){
        List<Produto> produtoList = produtoRepository.findAll();
        for (Produto p:produtoList) {
            for (Imagem img:p.getImagens()) {
                System.out.println(img.getCaminho());
            }
        }


        return "admin/admin-menager_produtos";
    }

    @GetMapping("/addProduto")
    public String telaCadastro(Model model){





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
    public String cadastrarProduto(@RequestParam("nome") String nome, @RequestParam("imagem") MultipartFile[] imagens) {
        if (imagens == null || imagens.length == 0) {
            throw new IllegalArgumentException("Nome e imagens são requeridos");
        }

        Produto produto = new Produto();
        produto.setNome(nome);
        produtoRepository.save(produto);



        for (MultipartFile imagem : imagens) {
            System.out.println("entrou no for");
            if (imagem != null && !imagem.isEmpty()) {
                String nomeImagem =dataHoraStrg().concat(StringUtils.cleanPath(imagem.getOriginalFilename().replace(" ","_")));
                String caminho = "imagens/produtos/" + nomeImagem;
                Path path = Paths.get("src/main/resources/static/" + caminho);
                try {
                    Files.copy(imagem.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Falha ao armazenar a imagem " + nomeImagem + e);
                }

                Imagem novaImagem = new Imagem();
                novaImagem.setCaminho(caminho);
                novaImagem.setProduto(produto);
                System.out.println(novaImagem);
                imagemRepository.save(novaImagem);
            }else {
                String nomeImagem = "default.jpg";
                String caminho = "imagens/produtos/" + nomeImagem;
                Path path = Paths.get("src/main/resources/static/" + caminho);
                Imagem novaImagem = new Imagem();
                novaImagem.setCaminho(caminho);
                novaImagem.setProduto(produto);
                imagemRepository.save(novaImagem);
                break;
            }
        }



        return "redirect:/produto/listar";
    }





    private String dataHoraStrg(){
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String dataHoraFormatada = agora.format(formato).concat("-".trim().toLowerCase()).replace(':','-');
        return dataHoraFormatada;
    }


}