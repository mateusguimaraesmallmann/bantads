package com.bantads.ms_cliente.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.ms_cliente.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_cliente.model.dto.output.ClienteAprovadoDTOOut;
import com.bantads.ms_cliente.model.dto.output.ClienteDTOOut;
import com.bantads.ms_cliente.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteDTOOut> buscarPorCpf(@PathVariable String cpf) {
        ClienteDTOOut dto = clienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/public/check-cpf/{cpf}")
    public ResponseEntity<Void> checkCpfExists(@PathVariable String cpf) {
        boolean exists = clienteService.cpfExists(cpf); 
        if (exists) {
            return ResponseEntity.ok().build(); // CPF existe (200 OK)
        } else {
            return ResponseEntity.notFound().build(); // CPF não existe (404 Not Found)
        }
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTOOut>> listarTodos() {
        List<ClienteDTOOut> lista = clienteService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<ClienteDTOOut> atualizarCliente(
            @PathVariable String cpf,
            @Valid @RequestBody EditarClienteDTOIn editarClienteDTOIn) {
        ClienteDTOOut dto = clienteService.atualizarCliente(cpf, editarClienteDTOIn);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<ClienteAprovadoDTOOut> aprovarCliente(@PathVariable String cpf) {
        ClienteAprovadoDTOOut dto = clienteService.aprovarCliente(cpf);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{cpf}/rejeitar")
    public ResponseEntity<Object> rejeitarCliente(
            @PathVariable String cpf) {
        Object dto = clienteService.rejeitarCliente(cpf);
        return ResponseEntity.ok(dto);
    }
}
