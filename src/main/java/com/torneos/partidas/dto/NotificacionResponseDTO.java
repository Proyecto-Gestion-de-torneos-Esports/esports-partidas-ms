package com.torneos.partidas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionResponseDTO {

    private Long idNotificacion;
    private String tipo;
    private String mensaje;
    private String fechaEnvio;
    private String estado;


}