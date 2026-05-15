package com.torneos.partidas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "equipos",url = "http://localhost:8002/api/equipos")
public interface EquipoClient {

    @GetMapping("/{equipoId}")
    void validarEquipoExiste(@PathVariable("equipoId") Long equipoId);
}
