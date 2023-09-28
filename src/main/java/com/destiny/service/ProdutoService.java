package com.destiny.service;

import com.destiny.controller.ProdutoController.StatusUpdateRequestProduto;
import com.destiny.model.Imagem;
import com.destiny.model.MensagemResponse;
import com.destiny.model.Produto;
import com.destiny.model.StatusProduto;
import com.destiny.model.ValidationException;
import com.destiny.repository.ImagemRepository;
import com.destiny.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProdutoService {

  @Value("${image.storage.path}")
  private String directoryPathString;

  // @Value("${relative.image.storage.path}")
  // private String directoryRelativePathImgProduto;

  private static final Logger logger = LoggerFactory.getLogger(ProdutoService.class);

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private ImagemRepository imagemRepository;

  @Transactional
  public Produto cadastrarProduto(Produto produto, MultipartFile[] imagens, int imgPrincipal) {
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
      logger.info("Produto cadastrado com sucesso: {}", produto);
      return produto;
    } catch (Exception e) {
      logger.error("Erro ao cadastrar produto", e);
      throw e;
    }
  }

  @Transactional
  public String editarProduto(Produto produto, MultipartFile[] imagemInput,
      String imagensParaRemover, String imagensParaAtualizar,
      String imgPrincipal, RedirectAttributes redirect) {

    try {
      Optional<Produto> produtoOptional = produtoRepository.findById(produto.getId());
      if (!produtoOptional.isPresent()) {
        addRedirectAttributes(redirect, "error", "Produto não encontrado!");
        return "redirect:/produto/listar";
      }

      Produto produtoExistente = produtoOptional.get();
      atualizarProduto(produtoExistente, produto);
      manipularImagens(produto, imagemInput, imagensParaRemover, imagensParaAtualizar, imgPrincipal);

      addRedirectAttributes(redirect, "success", "Produto atualizado com sucesso!");
      logger.info("Produto atualizado com sucesso: id: {}", produto.getId());
      return "redirect:/produto/listar";
    } catch (Exception e) {
      logger.error("Erro ao cadastrar produto", e);
      e.printStackTrace();
      addRedirectAttributes(redirect, "error", "Erro ao atualizar produto!");
      return "redirect:/produto/listar";
    }
  }

  @Transactional
  public ResponseEntity<MensagemResponse> updateStatus(StatusUpdateRequestProduto statusUpdateRequestProduto) {
    MensagemResponse mensagemResponse = new MensagemResponse();
    List<String> detalhes = new ArrayList<>();
    long longId = 0;

    try {
      longId = Long.valueOf(statusUpdateRequestProduto.getId());
    } catch (NumberFormatException e) {
      detalhes.add("id not int");
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

  @Transactional
  public Page<Produto> buscarProdutos(Optional<Integer> page, Optional<Integer> size, Optional<String> nome) {
    int currentPage = page.orElse(1) - 1;
    int pageSize = size.orElse(10);

    PageRequest pageRequest = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());

    if (nome.isPresent()) {
      return produtoRepository.findByNomeContaining(nome.get(), pageRequest);
    } else {
      return produtoRepository.findAll(pageRequest);
    }
  }

  public List<Produto> buscarProdutosPorStatus(StatusProduto statusProduto) {
    return produtoRepository.findAllByStatusProduto(statusProduto);
  }

  @Transactional
  public Optional<Produto> buscarProdutoPorId(Long id) {
    return produtoRepository.findById(id);
  }

  private void addRedirectAttributes(RedirectAttributes redirect, String tipo, String mensagem) {
    redirect.addFlashAttribute("tipo", tipo);
    redirect.addFlashAttribute("mensagem", mensagem);
  }

  @Transactional
  private void atualizarProduto(Produto produtoExistente, Produto produto) {
    produtoRepository.save(produto);
  }

  private void manipularImagens(Produto produto, MultipartFile[] imagemInput,
      String imagensParaRemover, String imagensParaAtualizar,
      String imgPrincipal) {
    int indiceImgPrincipal = -1;

    Boolean fotoPrinipalInImagens = false;
    if (imgPrincipal != null) {
      if ((!imgPrincipal.isEmpty() || !imgPrincipal.isBlank())
          && (imagensParaAtualizar.isEmpty() || imagensParaAtualizar.isBlank())) {
        fotoPrinipalInImagens = true;
        try {
          indiceImgPrincipal = Integer.parseInt(imgPrincipal);
        } catch (NumberFormatException e) {
          // TODO: handle exception
        }

      }
    }

    if (imgPrincipal != null) {
      for (String id : imagensParaRemover.split(",")) {
        if (!id.isBlank() || !id.isEmpty()) {
          var longId = Long.parseLong(id);
          var imagemDell = imagemRepository.findById(longId);
          if (imagemDell.isPresent()) {
            var img = imagemDell.get();
            imagemRepository.deleteById(img.getId());
            removeImagemDoServidor(img.getCaminho());
          }
        }
      }
    }

    if (imgPrincipal != null) {
      if (!fotoPrinipalInImagens) {
        var imgList = imagemRepository.findAllByProduto(produto);
        var IdImgUpdate = Long.parseLong(imagensParaAtualizar);
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
    }

    if (imagemInput != null) {
      MultipartFile[] imagens = (MultipartFile[]) imagemInput;
      int p = 0;
      for (MultipartFile img : imagens) {
        if (img != null && !img.isEmpty()) {
          try {
            String imgFileName = salvaImagemNoServidor(img);
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
            String nomeImg = img.getOriginalFilename();
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
    }
  }

  private String salvaImagemNoServidor(MultipartFile imagem) throws IOException {
    String nomeOriginal = StringUtils.cleanPath(imagem.getOriginalFilename()).replace(" ", "_");
    String imgFileName = UUID.randomUUID() + "-" + nomeOriginal;
    Path directoryPath = Paths.get("./imagens/produtos/");
    // Files.createDirectories(directoryPath);
    Path filePath = directoryPath.resolve(imgFileName);
    imagem.transferTo(filePath);
    return imgFileName;
  }

  private void removeImagemDoServidor(String nomeArquivo) {
    Path caminho = Paths.get(directoryPathString + nomeArquivo);
    try {
      Files.delete(caminho);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
