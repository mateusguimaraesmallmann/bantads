package com.bantads.ms_conta.controller;

import com.bantads.ms_conta.model.dto.input.CriarContaDTOIn;
import com.bantads.ms_conta.model.dto.input.DepositarSacarDTOIn;
import com.bantads.ms_conta.model.dto.input.TransferirDTOIn;
import com.bantads.ms_conta.model.dto.output.ContaDTOOut;
import com.bantads.ms_conta.model.dto.output.DepositarSacarDTOOut;
import com.bantads.ms_conta.model.dto.output.SaldoDTOOut;
import com.bantads.ms_conta.model.dto.output.TransferirDTOOut;
import com.bantads.ms_conta.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @PostMapping("/criar")
    public ResponseEntity<ContaDTOOut> criarConta(@RequestBody CriarContaDTOIn dto) {
        return ResponseEntity.ok(contaService.criarConta(dto));
    }

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<SaldoDTOOut> buscarSaldo(@PathVariable String numero) {
        return ResponseEntity.ok(contaService.buscarSaldo(numero));
    }

    @PostMapping("/{numero}/depositar")
    public ResponseEntity<DepositarSacarDTOOut> depositar(
            @PathVariable String numero,
            @RequestBody DepositarSacarDTOIn dto) {
        return ResponseEntity.ok(contaService.depositar(numero, dto));
    }

    @PostMapping("/{numero}/sacar")
    public ResponseEntity<DepositarSacarDTOOut> sacar(
            @PathVariable String numero,
            @RequestBody DepositarSacarDTOIn dto) {
        return ResponseEntity.ok(contaService.sacar(numero, dto));
    }

    @PostMapping("/{numero}/transferir")
    public ResponseEntity<TransferirDTOOut> transferir(
            @PathVariable String numero,
            @RequestBody TransferirDTOIn dto) {
        return ResponseEntity.ok(contaService.transferir(numero, dto));
    }
}
