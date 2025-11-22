package com.bantads.ms_conta.model.dto.cqrs;

import com.bantads.ms_conta.model.entity.jpa.Conta;
import com.bantads.ms_conta.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaCqrsDTO {
    private Long idComando;
    private Long idCliente;
    private Long idGerente;
    private String numero;
    private BigDecimal saldo;
    private BigDecimal limite;
    private String status;
    private LocalDateTime dataCriacao;
    private String motivoReprovacao;
    
    public ContaCqrsDTO(Conta conta) {
        this.idComando = conta.getId();
        this.idCliente = conta.getIdCliente();
        this.idGerente = conta.getIdGerente();
        this.numero = conta.getNumero();
        this.saldo = conta.getSaldo();
        this.limite = conta.getLimite();
        this.status = conta.getStatus().toString();
        this.dataCriacao = conta.getDataCriacao();
        this.motivoReprovacao = conta.getMotivoReprovacao();
    }
}