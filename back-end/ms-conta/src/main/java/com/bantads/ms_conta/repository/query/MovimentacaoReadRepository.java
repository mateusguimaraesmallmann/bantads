package com.bantads.ms_conta.repository.query;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bantads.ms_conta.model.entity.query.MovimentacaoRead;

@Repository
public interface MovimentacaoReadRepository extends JpaRepository<MovimentacaoRead, Long>{
    
    Optional<MovimentacaoRead> findById(Long contaId);

}