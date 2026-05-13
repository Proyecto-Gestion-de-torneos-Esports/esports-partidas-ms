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
    public ResponseEntity<PartidaResponseDTO> crear(@Valid @RequestBody PartidaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partidaService.guardar(dto));
    }

    @PutMapping("/{partidaId}")
    public ResponseEntity<PartidaResponseDTO> actualizar(@PathVariable Long partidaId, @Valid @RequestBody PartidaRequestDTO dto) {
        return partidaService.actualizar(partidaId, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PartidaResponseDTO>> buscarPorEstado(@PathVariable com.torneos.partidas.model.EstadoPartida estado) {
        return ResponseEntity.ok(partidaService.buscarPorEstado(estado));
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<PartidaResponseDTO>> buscarPorEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.ok(partidaService.buscarPorEquipo(equipoId));
    }

    @PatchMapping("/{partidaId}/estado")
    public ResponseEntity<PartidaResponseDTO> cambiarEstado(
            @PathVariable Long partidaId,
            @RequestParam EstadoPartida nuevoEstado,
            @RequestHeader("UsuarioId") Long usuarioId) {

        PartidaResponseDTO actualizada = partidaService.actualizarEstado(partidaId, nuevoEstado, usuarioId);
        return ResponseEntity.ok(actualizada);
    }


}
