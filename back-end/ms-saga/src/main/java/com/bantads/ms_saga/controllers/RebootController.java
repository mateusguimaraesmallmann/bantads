package com.bantads.ms_saga.controllers; 

import com.bantads.ms_saga.dtos.request.AutocadastroRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saga/reboot")

public class RebootController {

    @GetMapping
    public ResponseEntity<?> Reboot(@RequestBody AutocadastroRequest dados) {
        return ResponseEntity.ok().build();
    }

}