package com.torneos.partidas.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaRequestDTO {

    @NotBlank(message = "El detalle no puede ser vacio ni nulo")
    private String detalle;

    @NotNull(message = "La fecha no puede ser nula")
    private LocalDate fecha;
}
