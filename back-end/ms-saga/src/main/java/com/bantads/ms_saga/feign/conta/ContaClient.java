package com.bantads.ms_saga.feign.conta;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bantads.ms_saga.model.dto.input.RecalcularLimiteDTOIn;
import com.bantads.ms_saga.model.dto.output.ContaResponse;
import com.bantads.ms_saga.model.dto.output.RecalcularLimiteDTOOut;


@FeignClient(name = "ms-conta", url = "${ms.conta.url}")
public interface ContaClient{

    @PostMapping("/contas/recalcular-limite") 
    RecalcularLimiteDTOIn recalcularLimite(@RequestBody RecalcularLimiteDTOOut recalculoDTO);

    @GetMapping("/contas/{idCliente}")
    ContaResponse buscarPorIdCliente(@PathVariable("idCliente") Long idCliente);
}