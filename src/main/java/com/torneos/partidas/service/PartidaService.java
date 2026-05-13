package com.torneos.partidas.service;

import com.torneos.partidas.dto.PartidaRequestDTO;
import com.torneos.partidas.dto.PartidaResponseDTO;
import com.torneos.partidas.model.EstadoPartida;
import com.torneos.partidas.model.Partida;
import com.torneos.partidas.repository.PartidaRepository;
import com.torneos.partidas.webclient.EquipoClient;
import com.torneos.partidas.webclient.UsuarioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final EquipoClient equipoClient;
    private final UsuarioClient usuarioClient;

    @Transactional
    public PartidaResponseDTO guardar(PartidaRequestDTO dto){
        if (dto.getEquipoLocalId().equals(dto.getEquipoVisitanteId())) {
            throw new IllegalArgumentException("Un equipo no puede jugar contra sí mismo.");
        }

        validarEquipo(dto.getEquipoLocalId(), "Local");
        validarEquipo(dto.getEquipoVisitanteId(), "Visitante");

        Partida partida = new Partida();
        partida.setEquipoLocalId(dto.getEquipoLocalId());
        partida.setEquipoVisitanteId(dto.getEquipoVisitanteId());
        partida.setMarcadorLocal(dto.getMarcadorLocal());
        partida.setMarcadorVisitante(dto.getMarcadorVisitante());
        partida.setFechaPartida(dto.getFechaPartida());
        partida.setEstado(dto.getEstado());

        PartidaResponseDTO respuesta = mapToDto(partidaRepository.save(partida));
        log.info("Partida entre ID Local {} y ID Visitante {} creada y guardada correctamente", dto.getEquipoLocalId(), dto.getEquipoVisitanteId());
        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<PartidaResponseDTO> listarTodas(){
        log.info("Listando todas las partidas");
        List<Partida> partidas = partidaRepository.findAll();
        log.info("Hay {} partidas en total", partidas.size());
        return partidas.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PartidaResponseDTO> buscarPorId(Long partidaId){
        Optional<PartidaResponseDTO> resultado = partidaRepository.findById(partidaId).map(this::mapToDto);

        resultado.ifPresentOrElse(
                dto -> log.info("Partida con ID: {} encontrada correctamente", partidaId),
                () -> log.warn("No se encontró ninguna partida con el ID: {}", partidaId)
        );
        return resultado;
    }

    @Transactional
    public Optional<PartidaResponseDTO> actualizar(Long partidaId,PartidaRequestDTO dto){
        return partidaRepository.findById(partidaId).map(existente -> {
            log.info("Partida con ID: {} encontrada. Actualizando marcadores y/o estado", partidaId);

            if (!existente.getEquipoLocalId().equals(dto.getEquipoLocalId())) {
                validarEquipo(dto.getEquipoLocalId(), "Local");
            }
            if (!existente.getEquipoVisitanteId().equals(dto.getEquipoVisitanteId())) {
                validarEquipo(dto.getEquipoVisitanteId(), "Visitante");
            }

            existente.setEquipoLocalId(dto.getEquipoLocalId());
            existente.setEquipoVisitanteId(dto.getEquipoVisitanteId());
            existente.setMarcadorLocal(dto.getMarcadorLocal());
            existente.setMarcadorVisitante(dto.getMarcadorVisitante());
            existente.setFechaPartida(dto.getFechaPartida());
            existente.setEstado(dto.getEstado());

            PartidaResponseDTO respuesta = mapToDto(partidaRepository.save(existente));
            log.info("La Partida (ID: {}) fue actualizada correctamente", partidaId);
            return respuesta;
        });
    }

    @Transactional(readOnly = true)
    public List<PartidaResponseDTO> buscarPorEstado(com.torneos.partidas.model.EstadoPartida estado) {
        log.info("Buscando partidas filtradas por estado: {}", estado);
        List<Partida> partidas = partidaRepository.findByEstado(estado);
        return partidas.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartidaResponseDTO> buscarPorEquipo(Long equipoId) {
        log.info("Buscando historial de partidas para el equipo ID: {}", equipoId);
        List<Partida> partidas = partidaRepository.findByEquipoLocalIdOrEquipoVisitanteId(equipoId, equipoId);
        return partidas.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public PartidaResponseDTO actualizarEstado(Long partidaId, EstadoPartida nuevoEstado, Long usuarioId) {
        String rol = usuarioClient.obtenerRolUsuario(usuarioId);
        if (!rol.equalsIgnoreCase("ADMIN") && !rol.equalsIgnoreCase("ARBITRO")) {
            throw new RuntimeException("Acceso denegado: Solo Árbitros o Administradores pueden cambiar el estado.");
        }
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada."));

        partida.setEstado(nuevoEstado);
        return mapToDto(partidaRepository.save(partida));
    }



    private void validarEquipo(Long equipoId, String tipo) {
        if (equipoId != null) {
            // Se comunica con el metodo que esta en el webclient
            equipoClient.validarEquipoExiste(equipoId, tipo);
            log.info("Equipo {} (ID: {}) validado con éxito a través del WebClient", tipo, equipoId);
        }
    }



    private PartidaResponseDTO mapToDto(Partida partida) {
        return new PartidaResponseDTO(
                partida.getPartidaId(),
                partida.getEquipoLocalId(),
                partida.getEquipoVisitanteId(),
                partida.getMarcadorLocal(),
                partida.getMarcadorVisitante(),
                partida.getFechaPartida(),
                partida.getEstado()
        );
    }


}
