package com.bantads.ms_conta.model.entity.jpa;

import com.bantads.ms_conta.model.dto.input.CriarContaDTOIn;
import com.bantads.ms_conta.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="numero", nullable = true, length = 4)
    private String numero;

    @Column(name="motivo_rejeicao", nullable=true)
    private String motivoReprovacao;

    @Column(name="data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal limite;

    @Column(name="cliente_id", nullable = false)
    private Long idCliente;

    @Column(name="gerente_id", nullable = true)
    private Long idGerente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    public Conta(CriarContaDTOIn dto) {
        this.saldo = dto.getSaldoInicial();
        this.limite = dto.getLimite();
        this.idCliente = dto.getIdCliente();
        this.idGerente = dto.getIdGerente();
        this.dataCriacao = LocalDateTime.now();
    }
}
