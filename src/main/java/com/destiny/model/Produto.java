package com.destiny.model;

import java.math.BigDecimal;

import javax.persistence.*;

import org.springframework.data.annotation.Id;

public class Produto {
    
@Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
private long codigo;


private String nomeProduto;

private float avaliacao;

private String descricao;

private BigDecimal valor;

private int quantidade;

private String imagens;



}
