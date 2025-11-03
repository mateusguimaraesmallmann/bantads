package com.bantads.ms_saga.client;

import com.bantads.ms_saga.model.dto.input.RecalcularLimiteDTOIn; 

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "ms-conta", url = "${ms.conta.url}")
public interface ContaClient{

    @PostMapping("/contas/recalcular-limite") 
    void recalcularLimite(@RequestBody RecalcularLimiteDTOIn recalculoDTO);
}