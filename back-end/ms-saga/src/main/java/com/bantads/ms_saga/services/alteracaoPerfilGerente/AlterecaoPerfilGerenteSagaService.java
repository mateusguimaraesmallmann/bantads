// package com.bantads.ms_saga.services.alteracaoPerfilGerente;

// import org.springframework.stereotype.Service;

// import com.bantads.ms_saga.client.ClienteClient;
// import com.bantads.ms_saga.client.ContaClient;
// import com.bantads.ms_saga.model.dto.input.EditarClienteDTOIn;
// import com.bantads.ms_saga.model.dto.output.ClienteDTOOut;
// import com.bantads.ms_saga.model.dto.output.EditarClienteDTOOut;
// import com.bantads.ms_saga.model.dto.output.RecalcularLimiteDTOOut;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class AlterecaoPerfilGerenteSagaService {

//     private final GerenteClient gerenteClient;
    
    
//     public EditarClienteDTOOut atualizarGerente(String cpf, EditarGerenteDTOIn editarGerente) {

//         try {
//             GerenteDTOOut dadosGerenteAntigo = gerenteClient.buscarGerentePorCpf(cpf);
//             EditarGerenteDTOOut gerenteAtualizadoDTO = gerenteClient.atualizarGerente(cpf, editarGerente);


//             if (editarGerente.getSalario() != null && 
//                 (editarGerente.getSalario().compareTo(dadosGerenteAntigo.getSalario()) != 0)) {
//                 RecalcularLimiteDTOOut dtoRecalcularLimite = new RecalcularLimiteDTOOut(
//                     dadosGerenteAntigo.getId(), 
//                     editarGerente.getSalario()
//                 );
//                 gerenteClient.recalcularLimite(dtoRecalcularLimite); 
//             }
//             return gerenteAtualizadoDTO;

//         } catch (Exception e) {
//             return null;
//         } 
//     }

//     public EditarClienteDTOOut removerGerente(String cpf) {

//         try {
//             GerenteDTOOut dadosGerenteAntigo = gerenteClient.buscarGerentePorCpf(cpf);
            
//             if (dadosGerenteAntigo != null && dadosGerenteAntigo.getId() != null) {
//                 return gerenteClient.deletarGerente(dadosGerenteAntigo);
//             } else {
//                 return null;
//             }
            
//         } catch (Exception e) {
//             return null;
//         } 
//     }
    
// }