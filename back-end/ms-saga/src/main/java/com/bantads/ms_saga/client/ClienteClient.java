package com.bantads.ms_saga.client;

import org.springframework.web.bind.annotation.GetMapping;
import com.bantads.ms_saga.model.dto.input.CadastroClienteDTOIn;
import com.bantads.ms_saga.model.dto.input.EditarClienteDTOIn;
import com.bantads.ms_saga.model.dto.output.CadastroClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.GerenteDTOOut;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-cliente", url = "${ms.cliente.url}")
public class ClienteClient {

    @GetMapping("/clientes/{cpf}")
    GerenteDTOOut buscarPorCpf(@PathVariable String cpf){

    }

    @PostMapping("/clientes")
    CadastroClienteDTOOut criarCliente(@RequestBody CadastroClienteDTOIn dto){

    }

    @DeleteMapping("/clientes/{cpf}")
    void removerPorCpf(@PathVariable String cpf){

    }
    
    @PutMapping("/clientes/{cpf}")
    public
        EditarClienteDTOOut atualizarCliente(
                @PathVariable("cpf") String cpf, 
                @RequestBody EditarClienteDTOIn dto) {

        return null;
    }      
}
