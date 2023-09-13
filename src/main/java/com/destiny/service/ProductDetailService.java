package com.destiny.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.destiny.model.Produto;
import com.destiny.repository.ProdutoRepository;

@Service
public class ProductDetailService {
    @Autowired
    private ProdutoRepository produtoRepository;

    public Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }
   
    public Produto update(long id, Produto produto) {
        Optional<Produto> optionalProduto = findById(id);

        if (optionalProduto.isPresent()) {
            Produto produtoExistente = optionalProduto.get();
            
            produtoExistente.setNome(produto.getNome());
            produtoExistente.setValor(produto.getValor());
            produtoExistente.setQuantidade(produto.getQuantidade());
            produtoExistente.setAvaliacao(produto.getAvaliacao());
            produtoExistente.setDescricao(produto.getDescricao());
            produtoExistente.setImagens(produto.getImagens());

            produtoRepository.save(produtoExistente);

            return produtoExistente;
        } else {
            throw new NoSuchElementException("Produto com ID " + id + " não encontrado.");
        }
    }

}
