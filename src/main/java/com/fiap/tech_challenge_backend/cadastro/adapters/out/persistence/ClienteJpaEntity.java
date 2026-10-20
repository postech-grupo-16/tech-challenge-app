package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.domain.enums.ClienteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade de persistência (JPA) de
 * {@link com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente}.
 * {@code usuarioId} é uma coluna escalar, não uma associação JPA — {@code acesso.Usuario} não é
 * referenciado como objeto pela camada de persistência de {@code cadastro}.
 */
@Entity(name = "Cliente")
@Table(name = "clientes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cliente_cpf_cnpj", columnNames = "cpf_cnpj")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 14)
    private String cpfCnpj;

    @Column(name = "telefone", length = 11)
    private String telefone;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "rua", length = 150)
    private String rua;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "estado", length = 2)
    private String estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private ClienteStatus status;
}
