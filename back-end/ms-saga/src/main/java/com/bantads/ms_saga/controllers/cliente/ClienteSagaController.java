package com.bantads.ms_saga.controllers.cliente;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.ms_saga.model.dto.input.CadastroClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.AprovarClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.ClienteDTOOut;
import com.bantads.ms_saga.services.cliente.ClienteSagaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes/saga")
@RequiredArgsConstructor
public class ClienteSagaController {

    private final ClienteSagaService sagaService;

    @PostMapping("aprovar/{cpf}")
    public ResponseEntity<?> aprovarCliente(@PathVariable String cpf) {

        AprovarClienteDTOOut response = sagaService.aprovarCliente(cpf);
        return ResponseEntity.ok(response);
    }

    @PostMapping("cadastrar")
    public ResponseEntity<?> cadastrarCliente(@RequestBody CadastroClienteDTOIn dto) {

        ClienteDTOOut response = sagaService.cadastrarCliente(dto);
        return ResponseEntity.ok(response);
    }
    
}