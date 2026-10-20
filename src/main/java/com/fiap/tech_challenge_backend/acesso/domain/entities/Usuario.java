package com.fiap.tech_challenge_backend.acesso.domain.entities;

import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade de domínio que representa um Usuário do sistema.
 * Responsável pelo gerenciamento de credenciais, perfis e segurança.
 * Contexto Delimitado: acesso
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    private UUID id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    private String nome;

    private Email email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 60, max = 255, message = "A senha deve conter um hash válido")
    @JsonIgnore
    private String senha;

    private Telefone telefone;

    @NotNull(message = "O perfil é obrigatório")
    private PerfilUsuario perfil;

    private CpfCnpj cpfCnpj;
}

