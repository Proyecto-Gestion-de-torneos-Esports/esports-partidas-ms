package com.torneos.partidas.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EquipoClient {

    private final WebClient webClient;

    public EquipoClient(@Value("${equipo-service.url}") String equipoServidor) {
        this.webClient = WebClient.builder().baseUrl(equipoServidor).build();
    }

    public void validarEquipoExiste(Long id, String tipoEquipo) {
        this.webClient.get()
                .uri("/" + id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("El equipo " + tipoEquipo + " con ID " + id + " no existe en el sistema.")))
                .bodyToMono(Void.class)
                .block();
    }
}
