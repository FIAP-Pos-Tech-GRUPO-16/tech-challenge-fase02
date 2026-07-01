package br.com.fiap.techchallengefase02.infrastructure.controller.handler;

import br.com.fiap.techchallengefase02.application.dto.CriarUsuarioRequest;
import br.com.fiap.techchallengefase02.domain.exception.CredenciaisInvalidasException;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Controller usado apenas em teste para exercitar o {@link ControllerExceptionHandler}
 * sem depender de um domínio real.
 */
@RestController
@RequestMapping("/test/exceptions")
public class TestExceptionController {

    @GetMapping("/not-found")
    public void notFound() {
        throw new NoSuchElementException("Usuário não encontrado");
    }

    @GetMapping("/illegal")
    public void illegal() {
        throw new IllegalArgumentException("Requisição inválida");
    }

    @GetMapping("/conflict")
    public void conflict() {
        throw new RecursoJaExistenteException("Recurso já existe");
    }

    @GetMapping("/unauthorized")
    public void unauthorized() {
        throw new CredenciaisInvalidasException("Não autorizado");
    }

    @GetMapping("/generic")
    public void generic() {
        throw new RuntimeException("Erro inesperado");
    }

    @PostMapping("/validate")
    public void validate(@RequestBody @Valid CriarUsuarioRequest dto) {
        // apenas para disparar validação
    }
}
