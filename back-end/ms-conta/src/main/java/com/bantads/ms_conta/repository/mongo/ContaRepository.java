package com.bantads.ms_conta.repository.mongo;

import com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut;
import com.bantads.ms_conta.model.entity.mongo.Conta;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ContaRepository extends MongoRepository<Conta, Long> {

    Optional<Conta> findByNumero(Long numero);

    boolean existsByNumero(String numero);

    boolean existsByIdCliente(Long idCliente);

    @Aggregation(pipeline = {
            "{ $group: { _id: '$idGerente', count: { $sum: 1 } } }",
            "{ $project: { _id: 0, idGerente: '$_id', count: 1 } }"
    })
    List<ContaPorGerenteDTOOut> countContasByGerente();

}
