# Microservicio Partidas
Este microservicio es el encargado de la gestión de encuentros entre un equipo y otro, cada partida tiene que estar vinculada a un torneo. Tambien tiene sus respectivos estados (pendiente,en curso,etc).
Esta conectado a Notificaciones cuando se cree una partida automaticamente generara una notificacion para que se envie a dichos equipos mediante su correo de contacto 5 minutos antes de que comience la partida.

## Dependencias
* Spring Web
* Validation
* Spring Data JPA
* OpenFeign
* MySQL Driver
* Lombok
