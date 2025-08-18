package com.bantads.ms_gerente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bantads.ms_gerente.model.entity.Gerente;

import java.util.Optional;
import java.util.UUID;

public interface GerenteRepository extends JpaRepository<Gerente, UUID> {
    
    Optional<Gerente> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
