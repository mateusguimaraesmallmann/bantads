package com.bantads.ms_conta.repository;

import com.bantads.ms_conta.model.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByContaId(Long contaId);

}
