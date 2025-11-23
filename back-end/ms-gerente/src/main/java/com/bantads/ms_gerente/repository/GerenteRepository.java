package com.bantads.ms_gerente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bantads.ms_gerente.model.entity.Gerente;
import java.util.List;
import java.util.Optional;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    
    Optional<Gerente> findByCpf(String cpf);

    Optional<Gerente> findById(Long id);

    boolean existsByCpf(String cpf);

    List<Gerente> findAll();
}
