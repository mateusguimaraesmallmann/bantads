package com.bantads.ms_auth.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationContext;

import com.bantads.ms_auth.dtos.LoginRequestDTO;
import com.bantads.ms_auth.dtos.TokenDTO;
import com.bantads.ms_auth.models.User;
import com.bantads.ms_auth.repositorys.UserRepository;
import com.bantads.ms_auth.security.TokenService;

@Service
public class AuthorizationService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ApplicationContext context;

    private AuthenticationManager manager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            return userRepository.findByEmail(username);
        } catch(UsernameNotFoundException e){
            logger.error(e.getMessage());
            throw new UsernameNotFoundException("Usuario não encontrado: " + username);
        } 
    }

    public TokenDTO login(LoginRequestDTO loginRequestDTO) {
        
        manager = context.getBean(AuthenticationManager.class);
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.senha());
        var authentication = this.manager.authenticate(authenticationToken);
        var usuarioAutenticado = (User) authentication.getPrincipal();
        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
        return new TokenDTO(tokenJWT, usuarioAutenticado.getRole().toString(), usuarioAutenticado.getStatus().toString(),usuarioAutenticado.getCpf().toString(),usuarioAutenticado.getEmail().toString());
    }
    
}