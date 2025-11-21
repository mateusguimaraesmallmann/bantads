package com.bantads.ms_conta.repository.query;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut;
import com.bantads.ms_conta.model.dto.output.GerenteSaldoProjection;
import com.bantads.ms_conta.model.entity.query.ContaRead;

@Repository
public interface ContaReadRepository extends JpaRepository<ContaRead, Long> {
    
    Optional<ContaRead> findByNumero(String numero);
    
    Optional<ContaRead> findByClienteId(Long clienteId);
    
    List<ContaRead> findByGerenteId(Long gerenteId);

    @Query("SELECT new com.bantads.ms_conta.model.dto.output.ContaPorGerenteDTOOut(c.gerenteId, COUNT(c)) " +
           "FROM ContaRead c GROUP BY c.gerenteId")
    List<ContaPorGerenteDTOOut> countContasByGerente();

    @Query("SELECT c.gerenteId AS gerenteId, " +
           "SUM(CASE WHEN c.saldo > 0 THEN c.saldo ELSE 0 END) AS saldoTotalPositivo " +
           "FROM ContaRead c " +
           "WHERE c.gerenteId IN :gerentes " +
           "GROUP BY c.gerenteId " +
           "ORDER BY saldoTotalPositivo ASC")
    List<GerenteSaldoProjection> findGerentesOrderBySaldoPositivo(@Param("gerentes") List<Long> gerentes);

    Optional<ContaRead> findFirstByGerenteIdAndSaldoGreaterThanOrderBySaldoAsc(Long gerenteId, Double saldoMin);

    Optional<ContaRead> findFirstByGerenteIdOrderBySaldoAsc(Long gerenteId);
}