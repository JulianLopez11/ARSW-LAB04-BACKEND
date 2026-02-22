## Laboratorio #4 – REST API Blueprints (Java 21 / Spring Boot 3.3.x)
# Escuela Colombiana de Ingeniería – Arquitecturas de Software  

---

## 📋 Requisitos
- Java 21
- Maven 3.9+

### Instalación
---

1. Clonar el repositorio a la maquina local:
   ```bash
    git clone <URL_DEL_REPOSITORIO>
    ```
2. Navegar al directorio del repositorio
    ```bash
    cd <NOMBRE_DEL_PROYECTO>
    ```

## ▶️ Ejecución del proyecto
```bash
mvn clean install
En local correr docker compose up -d
mvn spring-boot:run
Para ver la base de datos `docker exec -it postgres-db psql -U julian -d baseDeDatos`
```
Probar con `curl`:
```bash
curl -s http://localhost:8080/api/v1/blueprints
curl -s http://localhost:8080/api/v1/blueprints/john
curl -s http://localhost:8080/api/v1/blueprints/john/house 
curl -i -X POST http://localhost:8080/api/v1/blueprints -H 'Content-Type: application/json' -d '{ "author":"john","name":"kitchen","points":[{"x":1,"y":1},{"x":2,"y":2}] }'
curl -i -X PUT  http://localhost:8080/api/v1/blueprints/john/kitchen/points -H 'Content-Type: application/json' -d '{ "x":3,"y":3 }'
```
#### Pruebas curl en bash

![alt text](docs/img/bash.png)

> Si deseas activar filtros de puntos (reducción de redundancia, *undersampling*, etc.), implementa nuevas clases que implementen `BlueprintsFilter` y cámbialas por `IdentityFilter` con `@Primary` o usando configuración de Spring.
---

Abrir en navegador:  
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
#### Evidencia Swagger UI

![alt text](docs/img/swagger.png)
---

### Implementacion @ApiResponse

![alt text](docs/img/apiResponse.png)

- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  
### Evidencia Api-docs

![alt text](docs/img/apidocs.png)

---

## 🗂️ Estructura de carpetas (arquitectura)

```
src/main/java/edu/eci/arsw/blueprints
  ├── model/         # Entidades de dominio: Blueprint, Point
  ├── persistence/   # Interfaz + repositorios (InMemory, Postgres)
  │    └── impl/     # Implementaciones concretas
  ├── services/      # Lógica de negocio y orquestación
  ├── filters/       # Filtros de procesamiento (Identity, Redundancy, Undersampling)
  ├── controllers/   # REST Controllers (BlueprintsAPIController)
  └── config/        # Configuración (Swagger/OpenAPI, etc.)
  └── dto/           # Implementacion ApiResponseRecord
  └── exception/     # Separa de la capa de persistencia las excepciones usadas por el sistema



```

> Esta separación sigue el patrón **capas lógicas** (modelo, persistencia, servicios, controladores), facilitando la extensión hacia nuevas tecnologías o fuentes de datos.

---

## 📖 Actividades del laboratorio

### 1. Familiarización con el código base
- Revisa el paquete `model` con las clases `Blueprint` y `Point`.  
Point usa record que es para modelar datos inmutables con una sintaxis muy concisa, eliminando código repetitivo

- Entiende la capa `persistence` con `InMemoryBlueprintPersistence`.
Lo que hace esta capa es guardar los blueprintsen un Map dentro de la aplicación, sin una base de datos `:(`

- Analiza la capa `services` (`BlueprintsServices`) y el controlador `BlueprintsAPIController`.
La capa services y los controladores tienen como principal funcion añadir blueprints, point y unos metodos get para consultar o conseguir todos los blueprints que existen, filtrarlos por autor y por ultimo por blueprint y por autor

### 2. Migración a persistencia en PostgreSQL
- Configura una base de datos PostgreSQL (puedes usar Docker).  
- Implementa un nuevo repositorio `PostgresBlueprintPersistence` que reemplace la versión en memoria.  
- Mantén el contrato de la interfaz `BlueprintPersistence`.

#### Implementación PostgreSQL Evidencia Docker 
Mediante El archivo docker-compose.yml se realiza la configuracion con la cual se va a ejectutar la aplicación,
donde dentro esta la base de datos

![alt text](docs/img/dockerPost.png)
---

#### Implementación PostgreSQL Evidencia PostMan
Se realiza un post en http://localhost:8080/blueprints con una prueba el cual es exitoso

![alt text](docs/img/postman.png)
--- 

#### Implementación PostgreSQL Evidencia Query
Se hace un SELECT * FROM blueprints; y se puede observar que los resultados son correctos

![alt text](docs/img/query.png)
---

### 3. Buenas prácticas de API REST
- Cambia el path base de los controladores a `/api/v1/blueprints`.  
- Usa **códigos HTTP** correctos:  
  - `200 OK` (consultas exitosas).  
  - `201 Created` (creación).  
  - `202 Accepted` (actualizaciones).  
  - `400 Bad Request` (datos inválidos).  
  - `404 Not Found` (recurso inexistente).  
- Implementa una clase genérica de respuesta uniforme:
  ```java
  public record ApiResponse<T>(int code, String message, T data) {}
  ```
  Ejemplo JSON:
  ```json
  {
    "code": 200,
    "message": "execute ok",
    "data": { "author": "john", "name": "house", "points": [...] }
  }
  ```

#### Pruebas de ejecución 
POST para añadir un blueprint 

![alt text](docs/img/POST.png)
---
GET para verificar que haya sido añadido y ver los mensajes de la respuesta

![alt text](docs/img/GET.png)
---

PUT para añadir points al anteriormente creado

![alt text](docs/img/PUT.png)
---

Verificacion en la base de datos 

![alt text](docs/img/verificacion.png)

### 4. OpenAPI / Swagger
- Configura `springdoc-openapi` en el proyecto.
En el application.properties en el directorio de resources podemos añadir las siguientes lineas para añadir la documentacion

```java
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
```

- Expón documentación automática en `/swagger-ui.html`. 

```java
springdoc.swagger-ui.path=/swagger-ui.html
```

- Anota endpoints con `@Operation` y `@ApiResponse`.

### 5. Filtros de *Blueprints*

- Implementa filtros:
Para implementarlos con spring podemos apregar esto al archivo .properties
Pero solo puede haber uno activo

```bash
#Punto 5
spring.profiles.active=redundancy
#spring.profiles.active=undersampling
```
  - **RedundancyFilter**: elimina puntos duplicados consecutivos.  
  #### Prueba ejecutada con el comando curl desde bash

  ![alt text](docs/img/redudancy.png)
  ---
  - **UndersamplingFilter**: conserva 1 de cada 2 puntos. 
   #### Prueba ejecutada con el comando curl desde bash y activando `#spring.profiles.active=undersampling`

  ![alt text](docs/img/under.png)

- Activa los filtros mediante perfiles de Spring (`redundancy`, `undersampling`).  

---

## ✅ Entregables

1. Repositorio en GitHub con:  
   - Código fuente actualizado.  
   - Configuración PostgreSQL (`application.yml` o script SQL).  
   - Swagger/OpenAPI habilitado.  
   - Clase `ApiResponse<T>` implementada.  

2. Documentación:  
   - Informe de laboratorio con instrucciones claras.  
   - Evidencia de consultas en Swagger UI y evidencia de mensajes en la base de datos.  
   - Breve explicación de buenas prácticas aplicadas.  

---

## Autor

* **Julian Camilo Lopez Barrero** - [JulianLopez11](https://github.com/JulianLopez11)