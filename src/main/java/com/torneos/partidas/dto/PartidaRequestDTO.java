package com.torneos.partidas.dto;

import com.torneos.partidas.model.EstadoPartida;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartidaRequestDTO {

    @NotNull(message = "El ID del equipo local es obligatorio")
    @Positive(message = "El ID del equipo local debe ser mayor a cero")
    private Long equipoLocalId;

    @NotNull(message = "El ID del equipo visitante es obligatorio")
    @Positive(message = "El ID del equipo visitante debe ser mayor a cero")
    private Long equipoVisitanteId;

    @NotNull(message = "El marcador local es obligatorio")
    @Min(value = 0, message = "El marcador local no puede ser negativo")
    private Integer marcadorLocal;

    @NotNull(message = "El marcador local es obligatorio")
    @Min(value = 0, message = "El marcador local no puede ser negativo")
    private Integer marcadorVisitante;

    @NotNull(message = "La fecha y hora de la partida es obligatoria")
    private LocalDateTime fechaPartida;

    @NotNull(message = "El estado de la partida es obligatorio")
    private EstadoPartida estado;

}
