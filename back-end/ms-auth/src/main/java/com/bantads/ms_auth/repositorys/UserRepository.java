package com.bantads.ms_auth.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.bantads.ms_auth.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByEmail(String email);
    boolean existsByEmail(String email);
    
}