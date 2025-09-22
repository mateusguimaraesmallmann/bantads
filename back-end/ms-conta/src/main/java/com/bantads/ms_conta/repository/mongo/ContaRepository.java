package com.bantads.ms_conta.repository.mongo;

import com.bantads.ms_conta.model.entity.mongo.Conta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ContaRepository extends MongoRepository<Conta, Long> {

    Optional<Conta> findByNumero(Long numero);

    boolean existsByNumero(String numero);

    boolean existsByIdCliente(Long idCliente);

}
