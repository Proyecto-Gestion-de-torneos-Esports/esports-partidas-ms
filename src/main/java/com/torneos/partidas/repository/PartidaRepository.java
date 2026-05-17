package com.torneos.partidas.repository;

import com.torneos.partidas.model.EstadoPartida;
import com.torneos.partidas.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {

    List<Partida> findByEstado(EstadoPartida estado);

    List<Partida> findByEquipoLocalIdOrEquipoVisitanteId(Long equipoLocalId, Long equipoVisitanteId);

    List<Partida> findByTorneoId(Long torneoId);

    List<Partida> findByFechaPartidaBetween(LocalDateTime ahora, LocalDateTime limite);

}
