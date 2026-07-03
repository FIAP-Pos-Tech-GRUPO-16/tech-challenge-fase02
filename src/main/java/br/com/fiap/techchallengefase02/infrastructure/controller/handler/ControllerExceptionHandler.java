package br.com.fiap.techchallengefase02.infrastructure.controller.handler;

import br.com.fiap.techchallengefase02.domain.exception.CredenciaisInvalidasException;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.infrastructure.controller.response.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Erro de validação");
        problemDetail.setDetail("Um ou mais campos possuem valores inválidos");

        List<ValidationError> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        problemDetail.setProperty("errors", errors);

        return ResponseEntity.status(status).headers(headers).body(problemDetail);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElement(NoSuchElementException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(404);
        problemDetail.setTitle("Recurso não encontrado");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(404).body(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(400);
        problemDetail.setTitle("Requisição inválida");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(400).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(500);
        problemDetail.setTitle("Erro interno do servidor");
        problemDetail.setDetail("Ocorreu um erro inesperado. Tente novamente mais tarde.");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(500).body(problemDetail);
    }

    @ExceptionHandler(RecursoJaExistenteException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyExists(RecursoJaExistenteException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(409);
        problemDetail.setTitle("Recurso já existe");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(409).body(problemDetail);
    }

    /**
     * Violação de integridade referencial no banco (ex: excluir um usuário
     * que é dono de restaurante). Responde 409 em vez de vazar um erro 500.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(409);
        problemDetail.setTitle("Conflito de dados");
        problemDetail.setDetail("Não é possível concluir a operação: o registro possui vínculos com outros dados.");
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(409).body(problemDetail);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(CredenciaisInvalidasException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(401);
        problemDetail.setTitle("Não autorizado");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(401).body(problemDetail);
    }
}
