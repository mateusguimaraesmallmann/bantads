package com.bantads.ms_conta.model.entity.query;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conta", schema = "contaleitura")
public class ContaRead {

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 4)
    private String numero;

    @Column(name = "data_criacao")
    private OffsetDateTime dataCriacao;

    @Column(name = "saldo", precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "limite", precision = 15, scale = 2)
    private BigDecimal limite;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "gerente_id", nullable = false)
    private Long gerenteId;

    @OneToMany(mappedBy = "contaRead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentacaoRead> movimentacoes = new ArrayList<>();

}