package com.bantads.ms_saga.controllers;

import com.bantads.ms_saga.dtos.request.AlteracaoPerfilRequest;
import com.bantads.ms_saga.dtos.saga.DadosClienteResponseDTO;
import com.bantads.ms_saga.services.ClienteSagaService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("clientes/saga/")
public class ClienteSagaController {

    @Autowired
    private ClienteSagaService clienteSagaService;
  
    @GetMapping("/{cpf}")
    public ResponseEntity<?> consultarDadosCompletos(@PathVariable String cpf) {
        try {
            DadosClienteResponseDTO dados = clienteSagaService.montarClienteCompleto(cpf);
            return ResponseEntity.ok(dados);

        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("Cliente não encontrado com o CPF informado.");
            
        } catch (FeignException e) {
            return ResponseEntity.status(503).body("Erro ao comunicar com os microsserviços internos.");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno ao montar relatório.");
        }
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<?> alterarPerfil(@PathVariable String cpf, @RequestBody AlteracaoPerfilRequest request) {
        return clienteSagaService.alterarPerfil(cpf,request);
    }    
}