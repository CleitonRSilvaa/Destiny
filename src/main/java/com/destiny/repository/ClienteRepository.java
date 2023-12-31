package com.destiny.repository;

import com.destiny.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    Cliente findByEmail(String email);
    Cliente findByCpf(String cpf);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    public interface ClienteResumo {
        Long getId();
        String getNome();
        String getCpf();
        String getEmail();
        Byte statusConta();

    }

    @Query("SELECT new map(c.id as id, c.nome as nome, c.email as email, c.statusConta as statusConta ) FROM Cliente c")
    List<Map<String, Object>> findAllCustom();










}
