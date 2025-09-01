package com.bantads.ms_cliente.controller;

import com.bantads.ms_cliente.model.dto.input.CriarClienteDTOIn;
import com.bantads.ms_cliente.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_cliente.model.dto.output.ClienteAprovadoDTOOut;
import com.bantads.ms_cliente.model.dto.output.ClienteDTOOut;
import com.bantads.ms_cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteDTOOut> criarCliente(
            @Valid @RequestBody CriarClienteDTOIn criarClienteDTOIn) {
        ClienteDTOOut dto = clienteService.criarCliente(criarClienteDTOIn);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteDTOOut> buscarPorCpf(@PathVariable String cpf) {
        ClienteDTOOut dto = clienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(dto);
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
