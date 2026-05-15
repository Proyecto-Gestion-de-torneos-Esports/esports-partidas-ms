package com.torneos.partidas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaResponseDTO {
    private Long idAuditoria;
    private String detalle;
    private LocalDate fecha;
}
