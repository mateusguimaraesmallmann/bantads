package com.bantads.ms_saga.client;

import org.springframework.web.bind.annotation.GetMapping;
import com.bantads.ms_gerente.model.dto.input.CriarGerenteDTOIn;
import com.bantads.ms_gerente.model.dto.output.GerenteDTOOut;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-gerente", url = "${ms.gerente.url}")
public interface GerenteClient {

    @GetMapping("/gerentes/{cpf}")
    GerenteDTOOut buscarPorCpf(@PathVariable String cpf);

    @DeleteMapping("/gerentes/{cpf}")
    void removerPorCpf(@PathVariable String cpf);

    @PostMapping("/gerentes/rollback")
    void recriarGerente(@RequestBody CriarGerenteDTOIn dto);
}
