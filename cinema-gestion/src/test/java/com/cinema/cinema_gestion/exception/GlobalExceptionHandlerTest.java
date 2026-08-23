package com.cinema.cinema_gestion.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/auth/register");
        when(request.getMethod()).thenReturn("POST");
    }

    @Test
    void handleResponseStatusException_preservesStatusAndMessage() {
        ResponseEntity<ErrorResponse> response = handler.handleResponseStatusException(
                new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Email already registered");
        assertThat(response.getBody().path()).isEqualTo("/auth/register");
    }

    @Test
    void handleBadCredentialsException_returns401() {
        ResponseEntity<ErrorResponse> response = handler.handleBadCredentialsException(
                new BadCredentialsException("Invalid refresh token"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Invalid refresh token");
    }

    @Test
    void handleUsernameNotFoundException_returns401WithGenericMessage() {
        ResponseEntity<ErrorResponse> response = handler.handleUsernameNotFoundException(
                new UsernameNotFoundException("User not found: test@test.com"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Invalid credentials");
    }

    @Test
    void handleDataIntegrityViolationException_uniqueConstraint_returns409() {
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolationException(
                new DataIntegrityViolationException("Unique index or primary key violation"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().message()).isEqualTo("Resource already exists");
    }

    @Test
    void handleDataIntegrityViolationException_otherViolation_returns400() {
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolationException(
                new DataIntegrityViolationException("foreign key constraint fails"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Invalid data");
    }

    @Test
    void handleMethodArgumentNotValidException_returnsFieldErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValidException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().fieldErrors()).containsEntry("email", "must not be blank");
    }

    @Test
    void handleHttpMessageNotReadableException_returns400() {
        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("Malformed JSON"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Malformed JSON request");
    }

    @Test
    void handleAccessDeniedException_returns403() {
        ResponseEntity<ErrorResponse> response = handler.handleAccessDeniedException(
                new AccessDeniedException("Denied"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().message()).isEqualTo("Access denied");
    }

    @Test
    void handleGenericException_returns500() {
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(
                new RuntimeException("boom"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
    }

    @Test
    void errorResponse_includesTimestampAndErrorLabel() {
        ResponseEntity<ErrorResponse> response = handler.handleResponseStatusException(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required"),
                request);

        ErrorResponse body = response.getBody();
        assertThat(body.timestamp()).isNotNull();
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.fieldErrors()).isNull();
    }
}
