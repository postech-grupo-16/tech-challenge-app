package com.fiap.tech_challenge_backend.cadastro.adapters.in.web;

import com.fiap.tech_challenge_backend.cadastro.application.ports.in.AtualizarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.BuscarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.CadastrarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.DeletarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.ListarVeiculosUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.dto.AtualizarVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculos")
@SecurityRequirement(name = "bearerAuth")
public class VeiculoController {

    private final CadastrarVeiculoUseCase cadastroVeiculoUseCase;
    private final ListarVeiculosUseCase listarVeiculosUseCase;
    private final BuscarVeiculoUseCase buscarVeiculoUseCase;
    private final AtualizarVeiculoUseCase atualizarVeiculoUseCase;
    private final DeletarVeiculoUseCase deletarVeiculoUseCase;

    public VeiculoController(
            CadastrarVeiculoUseCase cadastroVeiculoUseCase,
            ListarVeiculosUseCase listarVeiculosUseCase,
            BuscarVeiculoUseCase buscarVeiculoUseCase,
            AtualizarVeiculoUseCase atualizarVeiculoUseCase,
            DeletarVeiculoUseCase deletarVeiculoUseCase) {
        this.cadastroVeiculoUseCase = cadastroVeiculoUseCase;
        this.listarVeiculosUseCase = listarVeiculosUseCase;
        this.buscarVeiculoUseCase = buscarVeiculoUseCase;
        this.atualizarVeiculoUseCase = atualizarVeiculoUseCase;
        this.deletarVeiculoUseCase = deletarVeiculoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroVeiculoResponse create(@Valid @RequestBody CadastroVeiculoRequest request) {
        return cadastroVeiculoUseCase.execute(request);
    }

    @GetMapping
    public List<BuscarVeiculoResponse> list() {
        return listarVeiculosUseCase.execute();
    }

    @GetMapping("/{placa}")
    public BuscarVeiculoResponse getByPlaca(@PathVariable String placa) {
        return buscarVeiculoUseCase.execute(placa);
    }

    @PutMapping("/{placa}")
    public BuscarVeiculoResponse update(@PathVariable String placa, @Valid @RequestBody AtualizarVeiculoRequest request) {
        return atualizarVeiculoUseCase.execute(placa, request);
    }

    @DeleteMapping("/{placa}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RelatorioResponseDTO<Void>> delete(@PathVariable String placa) {
        deletarVeiculoUseCase.execute(placa);
        return ResponseEntity.ok(RelatorioResponseDTO.deleteVeiculoSucesso());
    }
}
