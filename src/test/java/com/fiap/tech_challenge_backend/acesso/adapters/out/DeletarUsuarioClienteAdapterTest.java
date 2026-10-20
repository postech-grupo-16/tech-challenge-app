package com.fiap.tech_challenge_backend.acesso.adapters.out;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.UsuarioNaoEncontradoException;
import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeletarUsuarioClienteAdapter")
class DeletarUsuarioClienteAdapterTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks
    private DeletarUsuarioClienteAdapter adapter;

    private UUID usuarioId;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuario = Usuario.builder().id(usuarioId).build();
    }

    @Nested
    @DisplayName("deletar")
    class Deletar {

        @Test
        @DisplayName("busca por CPF/CNPJ e deleta pelo id encontrado")
        void deletaComSucesso() {
            when(usuarioRepository.procuraPorCpfCnpj(new CpfCnpj("12345678901")))
                    .thenReturn(Optional.of(usuario));

            adapter.deletar("12345678901");

            verify(usuarioRepository).deletar(usuarioId);
        }

        @Test
        @DisplayName("lança UsuarioNaoEncontradoException quando não encontra o CPF/CNPJ")
        void lancaExcecaoQuandoNaoEncontrado() {
            when(usuarioRepository.procuraPorCpfCnpj(new CpfCnpj("12345678901")))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> adapter.deletar("12345678901"))
                    .isInstanceOf(UsuarioNaoEncontradoException.class);

            verify(usuarioRepository, never()).deletar(any());
        }
    }
}
