package com.bantads.ms_conta.repository;

import com.bantads.ms_conta.model.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumero(String numero);

    boolean existsByNumero(String numero);

    boolean existsByIdCliente(Long idCliente);

}
