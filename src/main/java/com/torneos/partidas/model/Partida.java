package com.torneos.partidas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PARTIDAS")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partidaId;

    @Column(name = "equipo_local_id", nullable = false)
    private Long equipoLocalId;

    @Column(name = "equipo_visitante_id", nullable = false)
    private Long equipoVisitanteId;

    @Column(name = "marcador_local", nullable = false)
    private Integer marcadorLocal = 0;

    @Column(name = "marcador_visitante", nullable = false)
    private Integer marcadorVisitante = 0;

    @Column(name = "fecha_partida")
    private LocalDateTime fechaPartida;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPartida estado;

    @Column(name = "torneo_id")
    private Long torneoId;

}
