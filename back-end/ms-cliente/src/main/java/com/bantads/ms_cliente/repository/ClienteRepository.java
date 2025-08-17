package com.bantads.ms_cliente.repository;

import com.bantads.ms_cliente.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

}
