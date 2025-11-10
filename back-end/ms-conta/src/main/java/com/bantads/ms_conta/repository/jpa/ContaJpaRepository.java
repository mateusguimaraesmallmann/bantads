package com.bantads.ms_conta.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bantads.ms_conta.model.entity.jpa.Conta;

@Repository
public interface ContaJpaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumero(String numero);

    boolean existsByNumero(String numero);

    boolean existsByIdCliente(Long idCliente);

    @Query("SELECT c FROM Conta c JOIN FETCH c.gerente g WHERE c.idCliente = :idCliente")
    Optional<Conta> findByIdCliente(long idCliente);

}
