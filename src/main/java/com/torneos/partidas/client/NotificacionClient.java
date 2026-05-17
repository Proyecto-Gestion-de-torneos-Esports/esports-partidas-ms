package com.torneos.partidas.client;

import com.torneos.partidas.dto.NotificacionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservicio-notificaciones", url = "http://localhost:8009/api/notificacion")
//@FeignClient(name = "microservicio-notificaciones", url = "http://localhost:8029/api/notificacion")
public interface NotificacionClient {

    @PostMapping
    NotificacionResponseDTO generarNotificacion();

    @PostMapping("/generarCorreo")
    void generarCorreo(@RequestParam Long id, @RequestParam String correo);

    @GetMapping("/{id}")
    NotificacionResponseDTO buscarPorId(@PathVariable Long id);


}
