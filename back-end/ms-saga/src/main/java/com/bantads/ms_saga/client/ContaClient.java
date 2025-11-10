package com.bantads.ms_saga.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bantads.ms_saga.model.dto.input.RecalcularLimiteDTOIn;
import com.bantads.ms_saga.model.dto.output.RecalcularLimiteDTOOut;


@FeignClient(name = "ms-conta", url = "${ms.conta.url}")
public interface ContaClient{

    @PostMapping("/contas/recalcular-limite") 
    RecalcularLimiteDTOIn recalcularLimite(@RequestBody RecalcularLimiteDTOOut recalculoDTO);
}