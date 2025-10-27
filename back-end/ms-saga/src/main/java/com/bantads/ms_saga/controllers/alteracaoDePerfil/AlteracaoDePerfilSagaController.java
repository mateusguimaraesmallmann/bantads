package com.bantads.ms_saga.controllers.alteracaoDePerfil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bantads.ms_saga.services.alteracaoDePerfil.AlteracaoDePerfilSagaService;

import com.bantads.ms_saga.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/alteracao-de-perfil/saga")
@RequiredArgsConstructor
public class AlteracaoDePerfilSagaController {
    
    private final AlteracaoDePerfilSagaService alteracaoDePerfilService;

    @PutMapping("/{cpf}")
    public ResponseEntity<EditarClienteDTOOut> atualizarCliente(@PathVariable String cpf, @RequestBody EditarClienteDTOIn editarClienteDTOIn) {
        try{
            EditarClienteDTOOut dto = alteracaoDePerfilService.atualizarCliente(cpf, editarClienteDTOIn);
            return ResponseEntity.ok(dto);            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}