package com.fiap.tech_challenge_backend.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Testes de arquitetura (fitness functions) pra hexagonal architecture.
 * Contexto Delimitado: nenhum específico — regras cross-cutting sobre toda a base.
 */
@AnalyzeClasses(packages = "com.fiap.tech_challenge_backend")
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule dominio_nao_depende_de_jpa =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..");

    @ArchTest
    static final ArchRule dominio_nao_depende_de_spring =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("org.springframework..");

    /**
     * Restrita aos 5 contextos delimitados (exclui {@code shared}): os value objects de
     * {@code shared.domain.valueobjects} (Cep, CpfCnpj, Email, Placa, Telefone) dependem por
     * desenho de {@code shared.application.exceptions.ValorInvalidoException} pra validação no
     * construtor — um padrão pré-existente e fora do escopo deste plano (que já trata
     * {@code shared}/{@code config} como infraestrutura cross-cutting, não bounded context).
     */
    @ArchTest
    static final ArchRule dominio_nao_depende_de_application_ou_adapters =
            noClasses().that().resideInAPackage("..domain..")
                    .and().resideOutsideOfPackage("..shared..")
                    .should().dependOnClassesThat().resideInAnyPackage("..application..", "..adapters..");

    @ArchTest
    static final ArchRule application_nao_depende_de_adapters =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("..adapters..");

    @ArchTest
    static final ArchRule controllers_rest_ficam_em_adapters_in_web =
            classes().that().areAnnotatedWith(RestController.class)
                    .and().resideOutsideOfPackage("..shared..")
                    .should().resideInAPackage("..adapters.in.web..");

    @ArchTest
    static final ArchRule implementacoes_de_porta_de_entrada_ficam_em_application_services =
            classes().that().implement(resideInAPackage("..application.ports.in.."))
                    .should().resideInAPackage("..application.services..");

    @ArchTest
    static final ArchRule entidades_jpa_ficam_em_adapters_out_persistence =
            classes().that().areAnnotatedWith(Entity.class)
                    .should().resideInAPackage("..adapters.out.persistence..");

    /**
     * Congela (não resolve) o acoplamento cross-context já existente — cada contexto delimitado
     * (pacote de 1º nível abaixo de {@code com.fiap.tech_challenge_backend}) ainda importa tipos de
     * domínio de outros contextos diretamente (ex.: {@code atendimento} importa
     * {@code cadastro.domain.entities.Cliente}). Isolamento de bounded context é um eixo diferente
     * de nomenclatura e não é resolvido por este plano — a regra só impede que o acoplamento cresça
     * além da baseline capturada em {@code src/test/resources/archunit_store/}.
     */
    @ArchTest
    static final ArchRule contextos_nao_ganham_novo_acoplamento_cruzado =
            FreezingArchRule.freeze(
                    slices().matching("com.fiap.tech_challenge_backend.(*)..")
                            .namingSlices("$1")
                            .should().notDependOnEachOther()
            );
}
