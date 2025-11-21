package com.bantads.ms_conta.repository.command;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut;
import com.bantads.ms_conta.model.dto.output.GerenteSaldoProjection;
import com.bantads.ms_conta.model.entity.command.Conta;

@Repository
public interface ContaCommandRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumero(String numero);

    @Query("SELECT new com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut(c.idGerente, COUNT(c)) FROM Conta c GROUP BY c.idGerente")
    List<ContaPorGerenteDTOOut> countContasByGerente();

    @Query("SELECT c.idGerente AS gerenteId, " +
           " SUM(CASE WHEN c.saldo > 0 THEN c.saldo ELSE 0 END) AS saldoTotalPositivo " +
           "FROM Conta c " +
           "WHERE c.idGerente IN :gerentes " +
           "GROUP BY c.idGerente " +
           "ORDER BY saldoTotalPositivo ASC")
    List<GerenteSaldoProjection> findGerentesOrderBySaldoPositivo(@Param("gerentes") List<Long> gerentes);

    Optional<Conta> findFirstByIdGerenteAndSaldoGreaterThanOrderBySaldoAsc(Long gerenteId, java.math.BigDecimal saldoMin);

    Optional<Conta> findFirstByIdGerenteOrderBySaldoAsc(Long gerenteId);

    boolean existsByNumero(String numero);

    boolean existsByIdCliente(Long idCliente);

    Optional<Conta> findByIdCliente(long idCliente);
}