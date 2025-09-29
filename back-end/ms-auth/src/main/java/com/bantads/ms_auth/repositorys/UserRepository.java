package com.bantads.ms_auth.repositorys;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.bantads.ms_auth.models.User;

public interface UserRepository extends MongoRepository<User, String> {

    UserDetails findByEmail(String email);
    boolean existsByEmail(String email);
    
}