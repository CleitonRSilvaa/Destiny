package com.destiny;

import com.destiny.controller.ClienteController;
import com.destiny.model.Cliente;
import com.destiny.model.MensagemResponse;
import com.destiny.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ClienteControllerTest {

    @InjectMocks
    private ClienteController clienteController;

    @Mock
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnListWhenCallingList() {
        //TODO: Implement test logic for list method
    }

    @Test
    void shouldReturnListAllDetalhesWhenCallingListAllDetalhes() {
        //TODO: Implement test logic for listAllDetalhes method
    }

    @Test
    void shouldInsertCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cleiton SIlva");
        cliente.setEmail("test@test.com");
        cliente.setCpf("12365478989");
        cliente.setSenha("cleiton");

        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(null);

        ResponseEntity<MensagemResponse> response = clienteController.insertCliente(cliente);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        //TODO: Add more assertions and mock interactions as needed
    }

    @Test
    void shouldUpdateCliente() {
        //TODO: Implement test logic for updateCliente method
    }

    @Test
    void shouldDeleteCliente() {
        //TODO: Implement test logic for deleteCliente method
    }

    @Test
    void shouldReturnClienteWhenCallingBuscarCliente() {
        long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        Optional<Cliente> returnedCliente = clienteController.buscarCliente(String.valueOf(id));

        assertEquals(cliente, returnedCliente.orElse(null));
        //TODO: Add more assertions and mock interactions as needed
    }
}
