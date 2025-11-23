package com.bantads.ms_conta.repository.read;

import com.bantads.ms_conta.model.entity.read.ContaLeitura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ContaLeituraRepository extends JpaRepository<ContaLeitura, Long> {
    Optional<ContaLeitura> findByNumero(String numero);
    
    Optional<ContaLeitura> findByIdCliente(Long idCliente);

    List<ContaLeitura> findByIdGerenteAndStatus(Long idGerente, String status);

    List<ContaLeitura> findAll();
}