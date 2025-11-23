package com.bantads.ms_conta.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ExtratoDTO {
    private String conta;
    private BigDecimal saldo;
    private List<MovimentacaoExtratoDTO> movimentacoes;
}