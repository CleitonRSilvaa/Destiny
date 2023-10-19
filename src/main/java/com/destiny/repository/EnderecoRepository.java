package com.destiny.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.destiny.model.Endereco;
import com.destiny.model.StatusConta;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Endereco e SET e.status = ?1 WHERE e.id = ?2")
    void updateStatusEndereco(StatusConta status, Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Endereco e SET e.principal = FALSE WHERE e.cliente.id = ?1 and e.tipo = 0")
    void updateAllEnderecoPadrao(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Endereco e SET e.principal= TRUE WHERE e.id = ?1 and e.tipo  = 0")
    void updateEnderecoPadrao(Long id);

    List<Endereco> findByClienteIdAndStatus(Long clienteId, StatusConta status);

    List<Endereco> findByClienteIdAndStatusAndTipo(Long clienteId, StatusConta status, Endereco.tipoEndereco tEndereco);

}
