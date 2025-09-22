package com.bantads.ms_conta.controller.query;

import com.bantads.ms_conta.model.dto.output.ExtratoDTOOut;
import com.bantads.ms_conta.model.dto.output.SaldoDTOOut;
import com.bantads.ms_conta.service.query.ContaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaQueryController {

    private final ContaQueryService contaService;

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<SaldoDTOOut> buscarSaldo(@PathVariable Long numero) {
        return ResponseEntity.ok(contaService.buscarSaldo(numero));
    }

    @GetMapping("/{numero}/extrato")
    public ResponseEntity<ExtratoDTOOut> gerarExtrato(
            @PathVariable Long numero
    ) {
        return ResponseEntity.ok(contaService.buscarExtrato(numero));
    }
}
