package com.destiny.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web.Client;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.destiny.model.Cliente;
import com.destiny.model.CustomUserDetails;
import com.destiny.model.Endereco;
import com.destiny.model.StatusConta;
import com.destiny.repository.ClienteRepository;
import com.destiny.repository.EnderecoRepository;
import com.destiny.repository.UsuarioRepository;

@Service
public class ClienteService {

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private EnderecoRepository enderecoRepository;

  public Cliente getClienteBySection(Authentication auth) {

    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    Optional optionalCliente = clienteRepository.findById(userDetails.getId());
    Cliente cliente = (Cliente) optionalCliente.get();
    cliente.setSenha("");
    cliente.setEnderecos(null);
    List enderecos = enderecoRepository.findByClienteIdAndStatusAndTipo(cliente.getId(), StatusConta.ATIVA,
        Endereco.tipoEndereco.ENTREGA);
    cliente.setEnderecos(enderecos);
    return cliente;

  }

  public List<Endereco> findByClienteIdAndStatusAndTipoOrderByPrincipalDesc(Cliente cliente) {
    List<Endereco> enderecos = enderecoRepository.findByClienteIdAndStatusAndTipo(cliente.getId(),
        StatusConta.ATIVA,
        Endereco.tipoEndereco.ENTREGA);

    enderecos.sort((e1, e2) -> {
      if (e1.getPrincipal() && !e2.getPrincipal()) {
        return -1;
      } else if (!e1.getPrincipal() && e2.getPrincipal()) {
        return 1;
      }
      return 0;
    });

    return enderecos;
  }

}
