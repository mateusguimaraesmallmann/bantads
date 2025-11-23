package com.bantads.ms_conta.repository.jpa;

import com.bantads.ms_conta.model.entity.jpa.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoJpaRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByContaId(Long contaId);
    List<Movimentacao> findByConta_NumeroOrderByDataDesc(String numeroConta);

}
