package com.fiap.tech_challenge_backend.cadastro.domain.entities;

import com.fiap.tech_challenge_backend.cadastro.domain.enums.ClienteStatus;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade de domínio que representa um Cliente da oficina mecânica.
 * Responsável pelo gerenciamento de dados cadastrais de clientes.
 * Contexto Delimitado: cadastro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    private UUID id;

    /**
     * Referência ao usuário do sistema associado a este cliente, por ID — nunca a entidade
     * completa de {@code acesso.Usuario}, para não acoplar os dois contextos via objeto JPA.
     * Pode ser nulo (cliente sem usuário associado).
     */
    private UUID usuarioId;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    private String nome;

    private CpfCnpj cpfCnpj;

    private Telefone telefone;

    private Cep cep;

    @Size(max = 150, message = "A rua deve ter no máximo 150 caracteres")
    private String rua;

    @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
    private String numero;

    @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres")
    private String estado;

    @Builder.Default
    private ClienteStatus status = ClienteStatus.ATIVO;
}

