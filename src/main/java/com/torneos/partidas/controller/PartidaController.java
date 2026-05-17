package com.torneos.partidas.controller;

import com.torneos.partidas.dto.PartidaRequestDTO;
import com.torneos.partidas.dto.PartidaResponseDTO;
import com.torneos.partidas.model.EstadoPartida;
import com.torneos.partidas.service.PartidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/partidas")
@RequiredArgsConstructor
public class PartidaController {
    private final PartidaService partidaService;

    @GetMapping
    public ResponseEntity<List<PartidaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(partidaService.listarTodas());
    }

    @GetMapping("/{partidaId}")
    public ResponseEntity<PartidaResponseDTO> buscarPorId(@PathVariable Long partidaId) {
        return partidaService.buscarPorId(partidaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PartidaResponseDTO> crear(@Valid @RequestBody PartidaRequestDTO dto, @RequestHeader("usuarioId") Long usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partidaService.guardar(dto, usuarioId));
    }

    @PutMapping("/{partidaId}")
    public ResponseEntity<PartidaResponseDTO> actualizar(@PathVariable Long partidaId, @RequestBody PartidaRequestDTO dto,
                                                         @RequestHeader("usuarioId") Long usuarioId) {

        Optional<PartidaResponseDTO> actualizada = partidaService.actualizar(partidaId, dto, usuarioId);
        return actualizada.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PartidaResponseDTO>> buscarPorEstado(@PathVariable EstadoPartida estado) {
        return ResponseEntity.ok(partidaService.buscarPorEstado(estado));
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<PartidaResponseDTO>> buscarPorEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.ok(partidaService.buscarPorEquipo(equipoId));
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<PartidaResponseDTO>> buscarPorTorneo(@PathVariable Long torneoId) {
        return ResponseEntity.ok(partidaService.buscarPorTorneoId(torneoId));
    }


}
