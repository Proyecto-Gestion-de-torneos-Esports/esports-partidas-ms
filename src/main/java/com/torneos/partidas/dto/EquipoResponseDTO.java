package com.torneos.partidas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoResponseDTO {

    private Long equipoId;
    private String nombre;
    private String region;
    private LocalDate fechaFundacion;
    private String correoContacto;
    private Boolean activo;
}
