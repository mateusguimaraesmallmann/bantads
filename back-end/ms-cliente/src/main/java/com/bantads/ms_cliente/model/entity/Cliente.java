package com.bantads.ms_cliente.model.entity;

import com.bantads.ms_cliente.model.enums.StatusCliente;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private UUID id;

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
    private String salario;

}
