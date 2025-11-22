package com.bantads.ms_conta.model.entity.read;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conta", schema = "contaleitura")
public class ContaLeitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="cliente_id", nullable = false)
    private Long idCliente;

    @Column(name="gerente_id", nullable = true)
    private Long idGerente;

    @Column(nullable = true)
    private String numero;

    private BigDecimal saldo;
    private BigDecimal limite;
    private String status;

    @Column(name="data_criacao", nullable=false)
    private LocalDateTime dataCriacao;
    
    @Column(name="motivo_rejeicao", nullable = true)
    private String motivoReprovacao;
}