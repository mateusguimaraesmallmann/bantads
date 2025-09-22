package com.bantads.ms_conta.repository.jpa;

import com.bantads.ms_conta.model.entity.jpa.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaJpaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumero(String numero);

    boolean existsByNumero(String numero);

    boolean existsByIdCliente(Long idCliente);

}
