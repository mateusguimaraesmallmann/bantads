package com.bantads.ms_auth.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import com.bantads.ms_auth.enums.Tipo;
import com.bantads.ms_auth.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Setter 
@Getter
@NoArgsConstructor 
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    @JsonIgnore
    @Column(nullable = true)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return java.util.Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getCpf(){
        return cpf;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    
    @Override public boolean isAccountNonLocked() { return true; }
    
    @Override public boolean isCredentialsNonExpired() { return true; }
    
    public String userStatus() { return this.status.toString(); }
    
}