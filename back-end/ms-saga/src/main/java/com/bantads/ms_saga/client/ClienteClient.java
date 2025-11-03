package com.bantads.ms_saga.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.bantads.ms_saga.model.dto.input.CadastroClienteDTOIn;
import com.bantads.ms_saga.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.CadastroClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.GerenteDTOOut;

@FeignClient(name = "ms-cliente", url = "${ms.cliente.url}")
public interface ClienteClient {

    @GetMapping("/clientes/{cpf}")
    GerenteDTOOut buscarPorCpf(@PathVariable("cpf") String cpf);

    @PostMapping("/clientes")
    CadastroClienteDTOOut criarCliente(@RequestBody CadastroClienteDTOIn dto);

    @DeleteMapping("/clientes/{cpf}")
    void removerPorCpf(@PathVariable("cpf") String cpf);

    @PutMapping("/clientes/{cpf}")
    EditarClienteDTOOut atualizarCliente(
            @PathVariable("cpf") String cpf,
            @RequestBody EditarClienteDTOIn dto);

    public CadastroClienteDTOIn cadastrar(CadastroClienteDTOIn dto);
}