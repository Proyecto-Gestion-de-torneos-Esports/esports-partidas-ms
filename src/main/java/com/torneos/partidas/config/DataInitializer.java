package com.torneos.partidas.config;

import com.torneos.partidas.model.EstadoPartida;
import com.torneos.partidas.model.Partida;
import com.torneos.partidas.repository.PartidaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PartidaRepository partidaRepository;

    @Override
    public void run(String... args){
        if (partidaRepository.count()>0){
            log.info("La base de datos ya tiene {} partidas. Omitiendo carga inicial", partidaRepository.count());
            return;
        }

        log.info("Base de datos vacía. Cargando partidas de prueba para el torneo");

        partidaRepository.saveAll(List.of(
                new Partida(null, 1L, 2L, 0, 0, LocalDateTime.now().plusDays(2), EstadoPartida.PENDIENTE),
                new Partida(null, 3L, 4L, 5, 5, LocalDateTime.now(), EstadoPartida.EN_CURSO),
                new Partida(null, 5L, 6L, 13, 11, LocalDateTime.now().minusDays(1), EstadoPartida.FINALIZADA)
        ));

        log.info("Carga de resplado completada. {} partidas insertadas", partidaRepository.count());
    }
}
