package com.torneos.partidas.service;

import com.torneos.partidas.client.AuditoriaClient;
import com.torneos.partidas.client.EquipoClient;
import com.torneos.partidas.client.NotificacionClient;
import com.torneos.partidas.client.UsuarioClient;
import com.torneos.partidas.dto.*;
import com.torneos.partidas.model.EstadoPartida;
import com.torneos.partidas.model.Partida;
import com.torneos.partidas.repository.PartidaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final EquipoClient equipoClient;
    private final UsuarioClient usuarioClient;
    private final AuditoriaClient auditoriaClient;
    private final NotificacionClient notificacionClient;

    private PartidaResponseDTO mapToDto(Partida partida) {
        return new PartidaResponseDTO(
                partida.getPartidaId(),
                partida.getEquipoLocalId(),
                partida.getEquipoVisitanteId(),
                partida.getMarcadorLocal(),
                partida.getMarcadorVisitante(),
                partida.getFechaPartida(),
                partida.getEstado(),
                partida.getTorneoId(),
                partida.getIdNotificacion()
        );
    }

    @Transactional
    public PartidaResponseDTO guardar(PartidaRequestDTO dto, Long usuarioId){
        String rol = obtenerRolUsuario(usuarioId);
        if (!rol.equalsIgnoreCase("ADMIN")&& !rol.equalsIgnoreCase("ARBITRO")){
            throw new RuntimeException("Acceso denegado: Solo Árbitros o Administradores pueden crear una partida");
        }
        if (dto.getEquipoLocalId().equals(dto.getEquipoVisitanteId())) {
            throw new IllegalArgumentException("Un equipo no puede jugar contra sí mismo.");
        }

        validarEquipoExiste(dto.getEquipoLocalId(), "Local");
        validarEquipoExiste(dto.getEquipoVisitanteId(), "Visitante");
        NotificacionResponseDTO noti = notificacionClient.generarNotificacion();

        Partida partida = new Partida();
        partida.setEquipoLocalId(dto.getEquipoLocalId());
        partida.setEquipoVisitanteId(dto.getEquipoVisitanteId());
        partida.setMarcadorLocal(dto.getMarcadorLocal());
        partida.setMarcadorVisitante(dto.getMarcadorVisitante());
        partida.setFechaPartida(dto.getFechaPartida());
        partida.setEstado(dto.getEstado());
        partida.setTorneoId(dto.getTorneoId());
        partida.setIdNotificacion(noti.getIdNotificacion());

        PartidaResponseDTO respuesta = mapToDto(partidaRepository.save(partida));
        log.info("Partida entre ID Local {} y ID Visitante {} creada y guardada correctamente", dto.getEquipoLocalId(), dto.getEquipoVisitanteId());
        String detalleAuditoria = "se creo una nueva partida con ID: " + respuesta.getPartidaId();
        generarAuditoria(detalleAuditoria);

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
    public Optional<PartidaResponseDTO> actualizar(Long partidaId,PartidaRequestDTO dto, Long usuarioId){
        /* Antes de actualizar se verifica el rol de quien hara el cambio si es Admin o Arbitro se puede sino lanzara el error*/
        String rol = obtenerRolUsuario(usuarioId);
        if (!rol.equalsIgnoreCase("ADMIN") && !rol.equalsIgnoreCase("ARBITRO")) {
            throw new RuntimeException("Acceso denegado: Solo Árbitros o Administradores pueden actualizar la partida.");
        }
        return partidaRepository.findById(partidaId).map(existente -> {
            log.info("Partida con ID: {} encontrada. Actualizando marcadores y/o estado", partidaId);
            existente.setMarcadorLocal(dto.getMarcadorLocal());
            existente.setMarcadorVisitante(dto.getMarcadorVisitante());
            existente.setEstado(dto.getEstado());

            PartidaResponseDTO respuesta = mapToDto(partidaRepository.save(existente));
            log.info("La Partida (ID: {}) fue actualizada correctamente", partidaId);
            String detalleAuditoria = "Se actualizó la partida con ID: " + partidaId;
            generarAuditoria(detalleAuditoria);
            return respuesta;
        });
    }

    public void generarCorreo(Long id, String correoLocal, String correoVisitante){
        notificacionClient.generarCorreo(id,correoLocal);
        notificacionClient.generarCorreo(id,correoVisitante);

    }

    @Scheduled(fixedRate = 60000)
    public void revisarPartidas() {
        log.info("Revisando partidas...");
        LocalDateTime ahora = LocalDateTime.now();

        LocalDateTime limite = LocalDateTime.now().plusMinutes(5);
        //Busca partidas entre la hora actual y la hora de inicio del torneo en un rango de 5 minutos para avisar antes
        List<Partida> partidas = partidaRepository.findByFechaPartidaBetween(ahora, limite);

        for (Partida partida : partidas) {
            EquipoResponseDTO local = equipoClient.buscarPorId(partida.getEquipoLocalId());
            EquipoResponseDTO visitante = equipoClient.buscarPorId(partida.getEquipoVisitanteId());
            NotificacionResponseDTO noti = notificacionClient.buscarPorId(partida.getIdNotificacion());

            //Esto para evitar que se manden correos repetidos y cambiar el estado de la notificacion a enviado
            if (noti.getEstado().equalsIgnoreCase("Pendiente")) {
                generarCorreo(noti.getIdNotificacion(), local.getCorreoContacto(), visitante.getCorreoContacto());
            }

        }
    }

    @Transactional(readOnly = true)
    public List<PartidaResponseDTO> buscarPorEstado(com.torneos.partidas.model.EstadoPartida estado) {
        log.info("Buscando partidas filtradas por estado: {}", estado);
        List<Partida> partidas = partidaRepository.findByEstado(estado);
        return partidas.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartidaResponseDTO> buscarPorEquipo(Long equipoId) {
        log.info("Validando existencia del equipo ID: {}", equipoId);
        equipoClient.buscarPorId(equipoId);//validar equipo
        log.info("Buscando historial de partidas para el equipo ID: {}", equipoId);
        List<Partida> partidas = partidaRepository.findByEquipoLocalIdOrEquipoVisitanteId(equipoId, equipoId);
        if (partidas.isEmpty()){
            log.warn("El equipo existe, pero aún no tiene partidas registradas. ID: {}", equipoId);
        }
        return partidas.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public String obtenerRolUsuario(Long usuarioId) {
        Map<String, Object> usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        return usuario.get("rol").toString();
    }

    @Transactional(readOnly = true)
    public List<PartidaResponseDTO> buscarPorTorneoId(Long torneoId) {
        log.info("Validando existencia del torneo ID: {}", torneoId);
        log.info("Buscando todas las partidas asociadas al torneo ID: {}", torneoId);
        List<Partida> partidas = partidaRepository.findByTorneoId(torneoId);

        if (partidas.isEmpty()) {
            log.warn("No se encontraron partidas para el torneo ID: {}", torneoId);
        } else {
            log.info("Se encontraron {} partidas para el torneo ID: {}", partidas.size(), torneoId);
        }
        return partidas.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void validarEquipoExiste(Long equipoId, String tipo) {
        if (equipoId != null) {
            equipoClient.buscarPorId(equipoId);//validar equipo
            log.info("Equipo {} (ID: {}) validado con éxito a través del Client", tipo, equipoId);
        }
    }

    public void generarAuditoria(String detalle){
        AuditoriaRequestDTO dto = new AuditoriaRequestDTO();
        LocalDate ahora = LocalDate.now();
        dto.setDetalle(detalle);
        dto.setFecha(ahora);
        auditoriaClient.generarAuditoria(dto);
        log.info("Auditoria generada con exito!");
    }




}
