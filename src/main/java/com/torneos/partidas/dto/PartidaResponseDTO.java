package com.torneos.partidas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.torneos.partidas.model.EstadoPartida;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartidaResponseDTO {

    private Long partidaId;
    private Long equipoLocalId;
    private Long equipoVisitanteId;
    private Integer marcadorLocal;
    private Integer marcadorVisitante;
    private LocalDateTime fechaPartida;
    private EstadoPartida estado;
    private Long torneoId;

}
