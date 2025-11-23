package com.bantads.ms_conta.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MovimentacaoExtratoDTO {
    private LocalDateTime data;
    private String tipo;    
    private String origem;  
    private String destino; 
    private BigDecimal valor;
}