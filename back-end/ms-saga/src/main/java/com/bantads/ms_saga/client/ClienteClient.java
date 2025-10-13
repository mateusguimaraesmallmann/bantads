package com.bantads.ms_saga.client;

import org.springframework.web.bind.annotation.GetMapping;
import com.bantads.ms_gerente.model.dto.input.CriarGerenteDTOIn;
import com.bantads.ms_gerente.model.dto.output.GerenteDTOOut;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-cliente", url = "${ms.cliente.url}")
public class ClienteClient {

    @GetMapping("/clientes/{cpf}")
    GerenteDTOOut buscarPorCpf(@PathVariable String cpf);

    @PostMapping("/clientes")
    CadastroClienteDTOOut criarCliente(@RequestBody CadastroClienteDTOIn dto);

    @DeleteMapping("/clientes/{cpf}")
    void removerPorCpf(@PathVariable String cpf);
    
}
