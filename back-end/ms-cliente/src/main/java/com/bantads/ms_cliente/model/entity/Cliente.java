package com.bantads.ms_cliente.model.entity;

import com.bantads.ms_cliente.model.enums.StatusCliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private StatusCliente status;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nome;

    @Embedded
    private Endereco endereco;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salario;

    @Column(precision = 10, scale = 2)
    private BigDecimal limiteSugerido;

}
