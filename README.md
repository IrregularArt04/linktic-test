1. Se utilizó PostgreSQL para priorizar el rendimiento y tiempos de respuesta en escenarios con consultas de alto volumen.
2. El endpoint de compras se establece en el microservicio de inventario ya que es el encargado de gestionar dicho apartado, por lo tanto es el mas indicado para gestionar esta labor al ser una operacion que afecta directamente el inventario.
3. Se utiliza un APIKEY estatico debido a que al ser una aplicacion mono usuario, no es necesario implementar un sistema de autenticación mas robusto como lo es JWT. En caso de haber múltiples usuarios con inicio de sesion incluido, se implementaría un sistema de autenticación mas robusto como el mencionado.

4. Se utiliza Antigravity como copiloto de desarrollo, para mejorar la productividad y calidad del codigo. Tambien se agregan las skills "canvas-design", "frontend-design", "theme-factory" y "web-artifacts-builder"para mejorar la calidad visual y diseño de la aplicacion al igual que la experiencia del usuario.

5. Se implementaron pruebas unitarias y de integracion para asegurar la calidad y funcionalidad del codigo. 

6. se utliza docker y docker-compose para facilitar el despliegue de la aplicacion y sus dependencias creando 3 contenedores y una red para su ejecucion y comunicacion.

7. Se utiliza Swagger como documentacion de la API la cual se puede consultar en http://localhost:8080/swagger-ui.html en caso de ejecutar en debug.

8. Se utiliza lombok para disminuir las lineas de codigo en los getter y setter de las entidades y DTO.


