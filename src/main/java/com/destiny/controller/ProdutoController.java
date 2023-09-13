package com.destiny.controller;

import com.destiny.controller.UsuarioController.StatusUpdateRequest;
import com.destiny.model.Imagem;
import com.destiny.model.MensagemResponse;
import com.destiny.model.Produto;
import com.destiny.model.StatusConta;
import com.destiny.model.StatusProduto;
import com.destiny.model.ValidationException;
import com.destiny.repository.ImagemRepository;
import com.destiny.repository.ProdutoRepository;
import com.destiny.service.ProductDetailService;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller()
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ImagemRepository imagemRepository;
    @Autowired
    private ProductDetailService service;

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

    @PostMapping("/add")
    @Transactional
    public String cadastrarProduto(@ModelAttribute Produto produto,
            @RequestParam("imagem") MultipartFile[] imagens,
            @RequestParam("imgPrincipal") int imgPrincipal,
            RedirectAttributes redirect) {

        try {

            produto.setStatusProduto(StatusProduto.ATIVO);
            produtoRepository.save(produto);
            int p = 0;
            for (MultipartFile imagem : imagens) {
                if (imagem != null && !imagem.isEmpty()) {
                    try {
                        String imgFileName = salvaImagemNoServidor(imagem);
                        Imagem novaImagem = new Imagem();
                        if (imgPrincipal == p) {
                            novaImagem.setPrincipal(true);
                        }
                        p++;
                        novaImagem.setCaminho("imagens/produtos/" + imgFileName);
                        novaImagem.setProduto(produto);
                        imagemRepository.save(novaImagem);
                    } catch (Exception e) {
                        String nomeImg = imagem.getOriginalFilename();
                        System.out.println("Falha ao armazenar a imagem " + nomeImg + e);
                        String nomeImagem2 = "default.jpg";
                        String caminho2 = "imagens/produtos/" + nomeImagem2;
                        Imagem novaImagem = new Imagem();
                        novaImagem.setCaminho(caminho2);
                        novaImagem.setProduto(produto);
                        novaImagem.setPrincipal(true);
                        imagemRepository.save(novaImagem);
                        break;
                    }

                } else {
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

    @PutMapping(value = "/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable long id, @RequestBody Produto produto) {
        Produto novProduto = service.update(id, produto);
        return ResponseEntity.ok().body(novProduto);
    }

    @PostMapping("/editarProduto")
    public String editarProduto(@ModelAttribute Produto produto, @RequestParam("imagem") MultipartFile[] imagens,
            @RequestParam("imagensParaRemover") String imagensParaRemover,
            @RequestParam("imagenPrinclAt") String imagensParaAtualizar,
            @RequestParam("imgPrincipal") String imgPrincipal, RedirectAttributes redirect) {

        System.out.println("imgPrincipal: " + imgPrincipal);
        System.out.println("imagensParaAtualizar: " + imagensParaAtualizar);

        try {
            int indiceImgPrincipal = -1;

            Boolean fotoPrinipalInImagens = false;
            if ((!imgPrincipal.isEmpty() || !imgPrincipal.isBlank())
                    && (imagensParaAtualizar.isEmpty() || imagensParaAtualizar.isBlank())) {
                fotoPrinipalInImagens = true;
                try {
                    indiceImgPrincipal = Integer.parseInt(imgPrincipal);
                } catch (NumberFormatException e) {
                    // TODO: handle exception
                }

            }

            for (String id : imagensParaRemover.split(",")) {
                if (!id.isBlank() || !id.isEmpty()) {
                    System.out.println("id para remover :" + id);
                    var longId = Long.parseLong(id);
                    var imagemDell = imagemRepository.findById(longId);
                    if (imagemDell.isPresent()) {
                        System.out.println(imagemDell.get());
                        var img = imagemDell.get();
                        imagemRepository.deleteById(img.getId());
                        removeImagemDoServidor(img.getCaminho());

                    }
                }
            }

            if (!fotoPrinipalInImagens) {
                var imgList = imagemRepository.findAllByProduto(produto);
                var IdImgUpdate = Long.parseLong(imagensParaAtualizar);
                System.out.println("IdImgUpdate: " + IdImgUpdate);
                for (Imagem img : imgList) {
                    if (img.getId() == IdImgUpdate) {
                        img.setPrincipal(true);
                        imagemRepository.save(img);
                    } else {
                        img.setPrincipal(false);
                        imagemRepository.save(img);
                    }
                }
            } else {
                var imgList = imagemRepository.findAllByProduto(produto);
                for (Imagem img : imgList) {
                    if (img.getPrincipal()) {
                        img.setPrincipal(false);
                        imagemRepository.save(img);
                    }
                }
            }

            int p = 0;
            for (MultipartFile imagem : imagens) {
                if (imagem != null && !imagem.isEmpty()) {
                    try {
                        String imgFileName = salvaImagemNoServidor(imagem);
                        Imagem novaImagem = new Imagem();

                        if (indiceImgPrincipal == p && fotoPrinipalInImagens) {
                            novaImagem.setPrincipal(true);
                        } else {
                            novaImagem.setPrincipal(false);
                        }
                        p++;
                        novaImagem.setCaminho("imagens/produtos/" + imgFileName);
                        novaImagem.setProduto(produto);
                        imagemRepository.save(novaImagem);
                    } catch (Exception e) {
                        String nomeImg = imagem.getOriginalFilename();
                        System.out.println("Falha ao armazenar a imagem " + nomeImg + e);
                        String nomeImagem2 = "default.jpg";
                        String caminho2 = "imagens/produtos/" + nomeImagem2;
                        Imagem novaImagem = new Imagem();
                        novaImagem.setCaminho(caminho2);
                        novaImagem.setProduto(produto);
                        novaImagem.setPrincipal(true);
                        imagemRepository.save(novaImagem);
                        break;
                    }

                } else {
                    break;
                }
            }

            redirect.addFlashAttribute("tipo", "success");
            redirect.addFlashAttribute("mensagem", "Produto atualizado com sucesso!");
            return "redirect:/produto/listar";
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            redirect.addFlashAttribute("tipo", "error");
            redirect.addFlashAttribute("mensagem", "Erro ao atualizar produto!");
            return "redirect:/produto/listar";
        }

    }

    @ResponseBody
    @PostMapping("/updateStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> updateStatus(
            @RequestBody StatusUpdateRequestProduto statusUpdateRequestProduto) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        long longId = 0;

        try {
            longId = Long.valueOf(statusUpdateRequestProduto.getId());
        } catch (NumberFormatException e) {
            detalhes.add("id not INT");
        }
        if (!detalhes.isEmpty()) {
            throw new ValidationException("parametro invalido", detalhes);
        }

        if (!produtoRepository.existsById(longId)) {
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            detalhes.add("Id não existe");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
        }

        produtoRepository.updateStatusProduto(statusUpdateRequestProduto.getStatus(), longId);
        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    private String salvaImagemNoServidor(MultipartFile imagem) throws IOException {
        String nomeOriginal = StringUtils.cleanPath(imagem.getOriginalFilename()).replace(" ", "_");
        String nomeArquivo = UUID.randomUUID() + "-" + nomeOriginal;
        Path caminho = Paths.get("src/main/resources/static/imagens/produtos/" + nomeArquivo);
        Files.copy(imagem.getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);
        return nomeArquivo;
    }

    private void removeImagemDoServidor(String nomeArquivo) {
        Path caminho = Paths.get("src/main/resources/static/" + nomeArquivo);
        try {
            Files.delete(caminho);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/detalhes")
    public String buscarProdutoPorId(@RequestParam(name = "id", required = false) Long id, Model model) {
        if (id != null) {
            Optional<Produto> produto = service.findById(id);
            // List<Imagem> imagens = imagemRepository.findAllByProductId(id);
            model.addAttribute("produto", produto.orElse(null));

        } else {

        }
        return "admin/alteration_produtos";
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