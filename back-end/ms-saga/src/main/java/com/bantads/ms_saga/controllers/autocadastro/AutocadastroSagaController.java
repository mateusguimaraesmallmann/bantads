package com.bantads.ms_saga.controllers.autocadastro;

import com.bantads.ms_saga.services.gerente.GerenteSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autocadastro/saga")
@RequiredArgsConstructor
public class AutocadastroSagaController {

    private final AutocadastroSagaService sagaService;

    @PostMapping()
    public ResponseEntity<?> autoCadastro(@Validated @RequestBody ClienteCadastroRequestDTO body) {
        return sagaService.autoCadastro(body);
    }
    
}