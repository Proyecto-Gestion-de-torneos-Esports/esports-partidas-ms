package com.torneos.partidas.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Value("${usuario-service.url}") String usuarioUrl) {
        this.webClient = WebClient.builder().baseUrl(usuarioUrl).build();
    }

    public String obtenerRolUsuario(Long usuarioId) {
        return this.webClient.get()
                .uri("/" + usuarioId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Usuario no encontrado")))
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("rol").asText())//aqui se extrae el rol del Usuario
                .block();
    }

}
