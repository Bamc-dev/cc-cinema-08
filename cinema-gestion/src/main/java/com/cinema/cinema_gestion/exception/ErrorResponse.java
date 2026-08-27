package com.cinema.cinema_gestion.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

/**
 * Corps JSON d'une erreur HTTP renvoyé par le gestionnaire d'exceptions.
 *
 * @param status      code HTTP numérique
 * @param error       libellé HTTP (raison)
 * @param message     message applicatif
 * @param timestamp   instant de l'erreur
 * @param path        URI de la requête
 * @param fieldErrors erreurs de validation par nom de champ, ou {@code null}
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path,
        Map<String, String> fieldErrors) {

    /**
     * Construit une réponse d'erreur sans détail de champs.
     *
     * @param httpStatus statut HTTP
     * @param message    message applicatif
     * @param path       URI de la requête
     * @return réponse d'erreur horodatée
     */
    public static ErrorResponse of(HttpStatus httpStatus, String message, String path) {
        return new ErrorResponse(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                Instant.now(),
                path,
                null);
    }

    /**
     * Construit une réponse d'erreur avec les erreurs de validation par champ.
     *
     * @param httpStatus  statut HTTP
     * @param message     message applicatif
     * @param path        URI de la requête
     * @param fieldErrors erreurs indexées par nom de champ
     * @return réponse d'erreur horodatée
     */
    public static ErrorResponse of(HttpStatus httpStatus, String message, String path, Map<String, String> fieldErrors) {
        return new ErrorResponse(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                Instant.now(),
                path,
                fieldErrors);
    }
}
