package com.bantads.ms_cliente.feign;

import com.bantads.ms_cliente.feign.dto.ContaCriadaDTOOut;
import com.bantads.ms_cliente.feign.dto.CriarContaDTOIn;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-conta", url = "${ms.conta.url}")
public interface ContaClient {

    @PostMapping("/contas/criar")
    ContaCriadaDTOOut criarConta(@RequestBody CriarContaDTOIn request);
}