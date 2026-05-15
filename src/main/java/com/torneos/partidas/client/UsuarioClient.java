package com.torneos.partidas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "usuarios", url = "http://localhost:8014/api/usuarios")
public interface UsuarioClient {
    @GetMapping("/{usuarioId}")
    Map<String, Object> obtenerUsuarioPorId(@PathVariable("usuarioId") Long usuarioId);
}
