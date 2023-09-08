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

    private String nome;

    private float avaliacao;

    private String descricao;

    private BigDecimal valor;

    private int quantidade;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Imagem> imagens;


    @Override
    public String toString() {
        return "Produto [id=" + id + ", nome=" + nome + " img="+ imagens+"]";
    }



}

