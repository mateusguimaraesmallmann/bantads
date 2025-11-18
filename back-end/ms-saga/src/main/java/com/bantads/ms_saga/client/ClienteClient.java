package com.bantads.ms_saga.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.bantads.ms_saga.model.dto.input.CadastroClienteDTOIn;
import com.bantads.ms_saga.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.CadastroClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.ClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.GerenteDTOOut;

@FeignClient(name = "ms-cliente", url = "${ms.cliente.url}")
public interface ClienteClient {

    @GetMapping("/clientes/{cpf}")
    ClienteDTOOut buscarPorCpf(@PathVariable("cpf") String cpf);

    @GetMapping("/clientes/public/check-cpf/{cpf}")
    ResponseEntity<Void> checkCpfExists(@PathVariable("cpf") String cpf);

    @PostMapping("/clientes")
    ClienteDTOOut criarCliente(@RequestBody CadastroClienteDTOIn dto);

    @DeleteMapping("/clientes/{cpf}")
    void removerPorCpf(@PathVariable("cpf") String cpf);

    @PutMapping("/clientes/{cpf}")
    EditarClienteDTOOut atualizarCliente(
            @PathVariable("cpf") String cpf,
            @RequestBody EditarClienteDTOIn dto);

    //public CadastroClienteDTOIn cadastrar(CadastroClienteDTOIn dto);

}