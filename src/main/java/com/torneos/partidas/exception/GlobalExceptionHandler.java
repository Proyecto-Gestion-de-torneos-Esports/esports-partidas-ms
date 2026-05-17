package com.torneos.partidas.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String, String> errores = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach((FieldError error) ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errores);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new LinkedHashMap<>();

        error.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(feign.FeignException.NotFound.class)
    public ResponseEntity<Map<String, String>> handleFeignNotFoundException(feign.FeignException.NotFound ex) {
        Map<String, String> error = new LinkedHashMap<>();
        String urlComprobar = ex.request().url();

        if (urlComprobar.contains("/api/usuarios")) {
            error.put("error", "Usuario no encontrado");
            error.put("mensaje", "El usuario ingresado no existe o fue eliminado.");
        } else if (urlComprobar.contains("/api/equipos")) {
            error.put("error", "Equipo no válido");
            error.put("mensaje", "El equipo ingresado no existe o está dado de baja.");
        } else {
            error.put("error", "Recurso no encontrado");
            error.put("mensaje", "El recurso solicitado en el microservicio externo no existe.");
        }

        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<Map<String, String>> handleGeneralFeignException(feign.FeignException ex) {
        Map<String, String> error = new LinkedHashMap<>();
        String urlComprobar = ex.request().url();

        if (urlComprobar.contains("/api/usuarios")) {
            error.put("error", "Error de validación de Usuario");
            error.put("mensaje", "No se pudo validar al usuario o no tiene los permisos necesarios.");
        } else if (urlComprobar.contains("/api/equipos")) {
            error.put("error", "Error de validación de Equipo");
            error.put("mensaje", "El equipo ingresado presenta problemas o está inactivo.");
        } else {
            error.put("error", "Error de comunicación externa");
            error.put("mensaje", "Ocurrió un error al comunicarse con otro microservicio.");
        }

        return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(error);
    }


}
