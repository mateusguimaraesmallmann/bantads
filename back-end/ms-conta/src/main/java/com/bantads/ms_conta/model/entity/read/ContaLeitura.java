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

    @Column(nullable = false)
    private Long idCliente;

    @Column(nullable = false)
    private Long idGerente;

    @Column(nullable = false)
    private String numero;

    private BigDecimal saldo;
    private BigDecimal limite;
    private String status;
    private LocalDateTime dataCriacao;
    
    @Column(nullable = true)
    private String motivoReprovacao;
}