package com.destiny.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 200)
    private String nome;

    private float avaliacao;

    @Column(length = 2000)
    private String descricao;

    private BigDecimal valor;

    private int quantidade;

    private StatusProduto statusProduto;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Imagem> imagens;

    @Override
    public String toString() {
        return "Produto \n[id: " + id + ",\nnome: " + nome + ",\navaliacao: " + avaliacao + ",\nvalor: " + valor
                + ",\nquantidade: " + quantidade + ",\nstatusProduto: " + statusProduto + ",\ndescricao: " + descricao
                + ",\nimagens: " + imagens
                + "]";
    }

}
