package com.bantads.ms_saga.feign.GerenteFeign;

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
import com.bantads.ms_saga.model.dto.output.ClienteDTOOut;
import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;

@FeignClient(name = "ms-gerente", url = "${ms.gerente.url}")
public class GerenteClient {
    
    @GetMapping("/gerentes/public/check-cpf/{cpf}")
    ResponseEntity<Void> checkCpfExists(@PathVariable("cpf") String cpf);

    @PostMapping("/gerentes")
    ClienteDTOOut criarGerente(@RequestBody CadastroClienteDTOIn dto);

    @DeleteMapping("/gerentes/{cpf}")
    void removerPorCpf(@PathVariable("cpf") String cpf);

}