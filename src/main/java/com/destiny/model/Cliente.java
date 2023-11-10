package com.destiny.model;

import com.destiny.utils.EncriptSenha;
import javax.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private LocalDate dataNacimento;

    private String genero;

    @Column(length = 12)
    private String telefone;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private StatusConta statusConta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipoConta;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate dataCriacao;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Endereco> enderecos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    public void setSenha(String senha) {
        this.senha = new BCryptPasswordEncoder().encode(senha);
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }

    public Cliente() {
    }

    public Cliente(String nome, LocalDate dataNascimento, String genero) {
        this.nome = nome.toUpperCase();
        this.genero = genero;
        this.dataNacimento = dataNascimento;
    }

    @Data
    public static class AlterarSenhaDTO {
        private String email;
        private String senhaAntiga;
        private String senhaNova;
    }

}
