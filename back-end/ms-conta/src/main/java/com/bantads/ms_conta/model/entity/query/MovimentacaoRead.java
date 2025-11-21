package com.bantads.ms_conta.model.entity.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movimentacao", schema = "contaleitura")
public class MovimentacaoRead {

    @Id
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime data;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    private String contaOrigem;
    private String contaDestino;

    @ManyToOne
    @JoinColumn(name = "conta_id", nullable = false)
    private ContaRead contaRead;

}