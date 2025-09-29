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

    @PostMapping
    public ResponseEntity<GerenteDTOOut> criarGerente(
            @Valid @RequestBody CriarGerenteDTOIn criarGerenteDTOIn) {
        GerenteDTOOut dto = gerenteService.criarGerente(criarGerenteDTOIn);
        return ResponseEntity.ok(dto);
    }

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

    @PutMapping("/{cpf}")
    public ResponseEntity<GerenteDTOOut> atualizarGerente(
            @PathVariable String cpf,
            @Valid @RequestBody EditarGerenteDTOIn editarGerenteDTOIn) {
        GerenteDTOOut dto = gerenteService.atualizarGerente(cpf, editarGerenteDTOIn);
        return ResponseEntity.ok(dto);
    }
}
