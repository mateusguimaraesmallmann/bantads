package com.bantads.ms_auth.enums;

public enum  Tipo {

    CLIENTE("cliente"),
    GERENTE("gerente"),
    ADMINISTRADOR("administrador");

    private String role;

    Tipo(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
    
}