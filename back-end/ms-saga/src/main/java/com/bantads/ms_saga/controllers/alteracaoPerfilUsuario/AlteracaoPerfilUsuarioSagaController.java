package com.bantads.ms_saga.controllers.alteracaoPerfilUsuario;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.ms_saga.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;
import com.bantads.ms_saga.services.alteracaoPerfilUsuario.AlteracaoPerfilUsuarioService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/alteracao-de-perfil/saga")
@RequiredArgsConstructor
public class AlteracaoPerfilUsuarioSagaController {
    
    private final AlteracaoPerfilUsuarioService alteracaoPerfilUsuarioService;

    @PutMapping("/{cpf}")
    public ResponseEntity<EditarClienteDTOOut> atualizarCliente(@PathVariable String cpf, @RequestBody EditarClienteDTOIn editarClienteDTOIn) {
        
        EditarClienteDTOOut dto = alteracaoPerfilUsuarioService.atualizarCliente(cpf, editarClienteDTOIn);
        return ResponseEntity.ok(dto);            
        
    }
}