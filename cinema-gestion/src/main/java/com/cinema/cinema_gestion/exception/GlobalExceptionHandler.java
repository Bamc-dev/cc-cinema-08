package com.cinema.cinema_gestion.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestion centralisée des erreurs REST : statut HTTP homogène et corps {@link ErrorResponse}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Relais des {@link ResponseStatusException} métier.
     *
     * @param ex      exception levée
     * @param request requête courante
     * @return corps d'erreur
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return buildResponse(status, message, request.getRequestURI());
    }

    /**
     * Identifiants invalides (login).
     *
     * @param ex      exception Spring Security
     * @param request requête courante
     * @return 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Invalid credentials";
        return buildResponse(HttpStatus.UNAUTHORIZED, message, request.getRequestURI());
    }

    /**
     * Utilisateur introuvable : même message que des identifiants invalides (pas d'énumération).
     *
     * @param ex      exception Spring Security
     * @param request requête courante
     * @return 401
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials", request.getRequestURI());
    }

    /**
     * Contrainte d'intégrité : 409 si unicité, 400 sinon.
     *
     * @param ex      exception JPA / JDBC
     * @param request requête courante
     * @return 409 ou 400
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        if (isUniqueConstraintViolation(ex)) {
            return buildResponse(HttpStatus.CONFLICT, "Resource already exists", request.getRequestURI());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid data", request.getRequestURI());
    }

    /**
     * Erreurs de validation Bean Validation, avec détail par champ.
     *
     * @param ex      exception de binding
     * @param request requête courante
     * @return 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Corps JSON illisible.
     *
     * @param ex      exception de parsing
     * @param request requête courante
     * @return 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", request.getRequestURI());
    }

    /**
     * Accès refusé (autorisation).
     *
     * @param ex      exception Spring Security
     * @param request requête courante
     * @return 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI());
    }

    /**
     * Filet de sécurité : toute exception non mappée (log + 500).
     *
     * @param ex      exception
     * @param request requête courante
     * @return 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI());
    }

    /**
     * @param status  statut HTTP
     * @param message message utilisateur
     * @param path    URI
     * @return réponse JSON
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status, message, path));
    }

    /**
     * @param ex violation d'intégrité
     * @return {@code true} si contrainte d'unicité (nom ou code SQL 23505)
     */
    private boolean isUniqueConstraintViolation(DataIntegrityViolationException ex) {
        Throwable root = ex.getMostSpecificCause();
        if (root instanceof ConstraintViolationException constraintViolation) {
            if (constraintViolation.getConstraintName() != null) {
                return true;
            }
        }
        return isUniqueConstraintMessage(root.getMessage());
    }

    /**
     * @param message message JDBC / Hibernate
     * @return {@code true} si le texte évoque une unicité
     */
    private boolean isUniqueConstraintMessage(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return lower.contains("unique")
                || lower.contains("duplicate")
                || lower.contains("23505");
    }
}
