package com.bantads.ms_conta.model.entity.mongo;

import com.bantads.ms_conta.model.dto.input.SalvarContaMongoDTOIn;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contas")
public class Conta {

    @Id
    private String id;

    private Long idCliente;

    private Long numero;

    private BigDecimal saldo;

    private List<Movimentacao> movimentacoes = new ArrayList<>();

    public Conta(SalvarContaMongoDTOIn contaMongoDTOIn) {
        this.idCliente = contaMongoDTOIn.getIdCliente();
        this.numero = contaMongoDTOIn.getNumero();
        this.saldo = contaMongoDTOIn.getSaldoInicial();
    }
}
