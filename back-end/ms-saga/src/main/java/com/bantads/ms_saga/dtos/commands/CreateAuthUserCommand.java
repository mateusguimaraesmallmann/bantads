package com.bantads.ms_saga.dtos.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthUserCommand {
    private String email;
    private String password; 
    private String role;    
}