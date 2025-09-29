package com.bantads.ms_saga.controllers.gerente;

import com.bantads.ms_saga.model.dto.input.RemoverGerenteDTOIn;
import com.bantads.ms_saga.model.dto.output.RemoverGerenteDTOOut;
import com.bantads.ms_saga.services.gerente.GerenteSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gerentes/saga")
@RequiredArgsConstructor
public class GerenteSagaController {

    private final GerenteSagaService sagaService;

    @DeleteMapping
    public ResponseEntity<RemoverGerenteDTOOut> removerGerente(
            @RequestBody RemoverGerenteDTOIn dto) {

        RemoverGerenteDTOOut resultado = sagaService.removerGerente(dto);
        return ResponseEntity.ok(resultado);
    }
}
