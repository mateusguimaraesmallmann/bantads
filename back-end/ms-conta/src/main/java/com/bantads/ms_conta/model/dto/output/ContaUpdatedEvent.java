package com.bantads.ms_conta.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContaUpdatedEvent {
    
    private Long id;
    private String numero;
    private Long gerenteId;

}