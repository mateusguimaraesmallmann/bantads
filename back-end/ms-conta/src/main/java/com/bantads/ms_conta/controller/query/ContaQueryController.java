package com.bantads.ms_conta.controller.query;

import java.util.List;
import com.bantads.ms_conta.model.dto.output.SaldoDTOOut;
import com.bantads.ms_conta.model.entity.read.ContaLeitura;
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

    @GetMapping
    public List<ContaLeitura> listarTodas() {
        List <ContaLeitura> contas = contaService.listarTodas();
        return contas;
    }

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<SaldoDTOOut> buscarSaldo(@PathVariable Long numero) {
        return ResponseEntity.ok(contaService.buscarSaldo(numero));
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<ContaLeitura> buscarInfosContaPorIdCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(contaService.buscarInfosContaPorIdCliente(idCliente));
    }

    @GetMapping("/gerente/{idGerente}/pendentes")
    public ResponseEntity<List<ContaLeitura>> buscarContasPorGerente(@PathVariable Long idGerente) {
        return ResponseEntity.ok(contaService.buscarContasPorIdGerente(idGerente));
    }

    
    // @GetMapping("/{numero}/extrato")
    // public ResponseEntity<ExtratoDTOOut> gerarExtrato(
    //         @PathVariable Long numero
    // ) {
    //     return ResponseEntity.ok(contaService.buscarExtrato(numero));
    // }
}
