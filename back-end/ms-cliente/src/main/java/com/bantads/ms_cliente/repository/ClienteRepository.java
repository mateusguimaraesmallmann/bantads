package com.bantads.ms_cliente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.ms_cliente.model.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

}
