package com.bantads.ms_gerente.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bantads.ms_gerente.model.dto.input.CriarGerenteDTOIn;
import com.bantads.ms_gerente.model.dto.input.EditarGerenteDTOIn;
import com.bantads.ms_gerente.model.dto.output.GerenteDTOOut;
import com.bantads.ms_gerente.service.GerenteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/gerentes")
@RequiredArgsConstructor
public class GerenteController {

    private final GerenteService gerenteService;

    @GetMapping("/{cpf}")
    public ResponseEntity<GerenteDTOOut> buscarPorCpf(@PathVariable String cpf) {
        GerenteDTOOut dto = gerenteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<GerenteDTOOut>> listarTodos() {
        List<GerenteDTOOut> lista = gerenteService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/public/check-cpf/{cpf}")
    public ResponseEntity<Void> checkCpfExists(@PathVariable String cpf) {
        boolean exists = gerenteService.cpfExists(cpf); 
        if (exists) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<GerenteDTOOut> criarGerente(@Valid @RequestBody CriarGerenteDTOIn criarGerenteDTOIn) {
        GerenteDTOOut dto = gerenteService.criarGerente(criarGerenteDTOIn);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<GerenteDTOOut> atualizarGerente(@PathVariable String cpf, @Valid @RequestBody EditarGerenteDTOIn editarGerenteDTOIn) {
        GerenteDTOOut dto = gerenteService.atualizarGerente(cpf, editarGerenteDTOIn);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/obter-por-id/{id}")
    public ResponseEntity<GerenteDTOOut> buscarPorId(@PathVariable Long id) {
        GerenteDTOOut dto = gerenteService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }    

}