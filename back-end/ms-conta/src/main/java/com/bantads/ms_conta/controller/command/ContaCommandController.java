package com.bantads.ms_conta.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.ms_conta.model.dto.input.CriarContaDTOIn;
import com.bantads.ms_conta.model.dto.input.DepositarSacarDTOIn;
import com.bantads.ms_conta.model.dto.input.RecalcularLimiteDTOIn;
import com.bantads.ms_conta.model.dto.input.TransferirDTOIn;
import com.bantads.ms_conta.model.dto.output.ContaCriadaDTOOut;
import com.bantads.ms_conta.model.dto.output.DepositarSacarDTOOut;
import com.bantads.ms_conta.model.dto.output.RecalcularLimiteDTOOut;
import com.bantads.ms_conta.model.dto.output.TransferirDTOOut;
import com.bantads.ms_conta.service.command.ContaCommandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaCommandController {

    private final ContaCommandService contaService;

    @PostMapping("/criar")
    public ResponseEntity<ContaCriadaDTOOut> criarConta(@RequestBody CriarContaDTOIn dto) {
        return ResponseEntity.ok(contaService.criarConta(dto));
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

    @PostMapping("recalcular-limite")
    public ResponseEntity<RecalcularLimiteDTOOut> recalcularLimite(@RequestBody RecalcularLimiteDTOIn dto){
        return ResponseEntity.ok(contaService.recalcularLimite(dto));
    }

}
