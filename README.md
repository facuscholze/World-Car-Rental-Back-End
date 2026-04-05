# 🚗 World Car Rental — Back-End

API REST desarrollada en **Java 17 + Spring Boot 3.0.7** para la gestión de una plataforma de alquiler de vehículos. Permite administrar vehículos, sucursales, usuarios y reservas, con autenticación basada en **JWT** y envío de correos electrónicos transaccionales.

---

## 📋 Tabla de Contenidos

- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Variables de Entorno / Configuración](#-variables-de-entorno--configuración)
- [Ejecución](#-ejecución)
- [Autenticación y Seguridad](#-autenticación-y-seguridad)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Modelos de Datos](#-modelos-de-datos)
- [Tests](#-tests)
- [CI/CD](#-cicd)
- [Estructura del Proyecto](#-estructura-del-proyecto)

---

## 🛠 Tecnologías

| Categoría | Tecnología |
|-----------|-----------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.0.7 |
| ORM | Spring Data JPA / Hibernate |
| Base de Datos (Producción) | MySQL 8 |
| Base de Datos (Desarrollo) | H2 (en memoria) |
| Seguridad | Spring Security + JWT (JJWT 0.11.5, HS256) |
| Email | Spring Boot Mail (SMTP Gmail) |
| Build | Maven (wrapper incluido) |
| Utilidades | Lombok 1.18.30, Log4j |
| Tests | JUnit 4/5 + Mockito |
| CI/CD | GitLab CI |

---

## 🏗 Arquitectura

El proyecto sigue una arquitectura en capas estándar de Spring Boot:

```
Controller  →  Service  →  Repository  →  Base de Datos
                ↑
            Seguridad JWT (Filters)
```

- **Controllers**: Exponen los endpoints REST y delegan la lógica a los servicios.
- **Services**: Contienen la lógica de negocio.
- **Repositories**: Interfaces JPA para acceso a datos.
- **Security**: Filtros JWT para autenticación y autorización basada en roles.
- **Model / DTO**: Entidades JPA y objetos de transferencia de datos.

---

## ✅ Requisitos Previos

- Java 17+
- Maven 3.8+ (o usar el wrapper `./mvnw`)
- MySQL 8 (para entorno de producción)
- Cuenta Gmail con contraseña de aplicación (para envío de emails)

---

## ⚙️ Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/facuscholze/World-Car-Rental-Back-End.git
   cd "World-Car-Rental-Back-End/Back-End app JAVA"
   ```

2. **Configurar la base de datos y propiedades** en `src/main/resources/application.properties` (ver sección siguiente).

3. **Instalar dependencias y compilar:**
   ```bash
   ./mvnw clean install
   ```

---

## 🔧 Variables de Entorno / Configuración

Editar `src/main/resources/application.properties`:

```properties
# ── Servidor ──────────────────────────────────────────────
server.port=8080

# ── Base de Datos MySQL ───────────────────────────────────
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://<HOST>:3306/<DB_NAME>
spring.datasource.username=<DB_USER>
spring.datasource.password=<DB_PASSWORD>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# ── JWT ───────────────────────────────────────────────────
jwt.secret.key=<BASE64_SECRET_KEY>
jwt.time.expiration=86400000   # 24 horas en milisegundos

# ── Email (Gmail SMTP) ────────────────────────────────────
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<GMAIL_ADDRESS>
spring.mail.password=<APP_PASSWORD>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Para desarrollo local con H2**, descomentar las líneas correspondientes en `application.properties` y comentar la configuración de MySQL.

---

## ▶️ Ejecución

```bash
# Con Maven wrapper
./mvnw spring-boot:run

# O ejecutando el JAR compilado
java -jar target/rentcard.jar
```

El servidor se levanta en `http://localhost:8080`.

---

## 🔐 Autenticación y Seguridad

### Flujo de autenticación

1. **Registro**: `POST /usuario/registrar` con los datos del usuario.
2. **Login**: `POST /login` con credenciales en JSON:
   ```json
   {
     "usuario": "nombre_de_usuario",
     "contraseña": "contraseña"
   }
   ```
3. **Respuesta**: El servidor devuelve un token JWT y lo incluye en el header `Authorization`.
4. **Requests protegidos**: Incluir el header en cada petición:
   ```
   Authorization: Bearer <token>
   ```

### Roles

| Rol | Descripción |
|-----|-------------|
| `USER` | Usuario regular: puede ver vehículos, crear reservas y calificar |
| `ADMIN` | Administrador: gestión completa de vehículos, sucursales y roles |

### Algoritmo y expiración

- **Algoritmo**: HS256 (HMAC SHA256)
- **Expiración**: 24 horas (configurable en `jwt.time.expiration`)

---

## 📡 Endpoints de la API

### Públicos (sin autenticación)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/demo` | Health check: devuelve `"HOLA RENT CARD"` |
| `POST` | `/login` | Autenticación, devuelve JWT |
| `POST` | `/usuario/registrar` | Registro de nuevo usuario |
| `GET` | `/vehiculo/vehiculos` | Listar todos los vehículos |
| `GET` | `/vehiculo/buscar/{id}` | Obtener vehículo por ID |
| `GET` | `/vehiculo/buscarPorMarca/{marca}` | Buscar vehículos por marca |
| `GET` | `/vehiculo/buscarPorTipo/{tipo}` | Buscar vehículos por tipo |
| `GET` | `/vehiculo/disponibles` | Vehículos disponibles por rango de fechas y sucursal |
| `GET` | `/sucursal/sucursales` | Listar todas las sucursales |

### Autenticados — Usuarios (`USER` o `ADMIN`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/usuario/usuarios` | Listar todos los usuarios |
| `PUT` | `/usuario/modificar` | Actualizar perfil del usuario |
| `GET` | `/usuario/buscar/{id}` | Obtener usuario por ID (DTO) |
| `GET` | `/usuario/buscarEmail?email=` | Buscar usuario por email (DTO) |
| `GET` | `/usuario/usuariosDTO` | Listar todos los usuarios como DTO |
| `GET` | `/usuario/{usuarioId}/reservas` | Reservas de un usuario |
| `POST` | `/usuario/{userId}/vehiculoAddFav` | Agregar/quitar vehículo de favoritos |
| `GET` | `/usuario/{userId}/vehiculosFavoritos` | Listar vehículos favoritos |
| `GET` | `/vehiculo/PuntuacionPorVehiuclo/{vehiculoId}` | Obtener puntuaciones de un vehículo |
| `POST` | `/reserva/reservar` | Crear una reserva |
| `GET` | `/reserva/reservasPorUsuario/{usuarioId}` | Reservas de un usuario |
| `POST` | `/reserva/darPuntaje` | Calificar un vehículo (1–5) |
| `PATCH` | `/reserva/{reservaId}/ocultar` | Cancelar (ocultar) una reserva |

### Solo ADMIN

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `PUT` | `/usuario/{userId}/modificar-rol` | Cambiar rol de usuario |
| `POST` | `/vehiculo/registrar` | Registrar nuevo vehículo |
| `PUT` | `/vehiculo/modificar` | Modificar vehículo |
| `PUT` | `/vehiculo/agregarCaracteristica` | Agregar características extra al vehículo |
| `DELETE` | `/vehiculo/eliminar/{id}` | Eliminar vehículo |
| `POST` | `/sucursal/registrar` | Registrar nueva sucursal |

---

## 🗂 Modelos de Datos

### Usuario
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Clave primaria |
| `nombre` | String | Nombre del usuario |
| `apellido` | String | Apellido |
| `dni` | String | Documento de identidad |
| `telefono` | String | Teléfono |
| `usuario` | String | Nombre de usuario (único) |
| `email` | String | Email (único) |
| `contraseña` | String | Contraseña encriptada (BCrypt) |
| `rol` | UserRol | `USER` o `ADMIN` |
| `direccion` | Direccion | Dirección (OneToOne) |
| `vehiculosFavoritos` | List\<Vehiculo\> | Favoritos (ManyToMany) |

### Vehiculo
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Clave primaria |
| `modelo` | String | Modelo del vehículo |
| `marca` | String | Marca |
| `precio` | Double | Precio por día |
| `tipo` | String | Tipo (ej. `"Automóvil"`, `"SUV"`) |
| `pasajeros` | int | Capacidad de pasajeros |
| `descripcion` | String | Descripción |
| `motor` | String | Motor |
| `cilindrada` | String | Cilindrada |
| `caja` | String | Transmisión (`"Automática"`, `"Manual"`) |
| `patente` | String | Patente (único) |
| `imagen` | List\<String\> | URLs de imágenes |
| `sucursal` | Sucursal | Sucursal asignada (ManyToOne) |
| `califiacion` | Double | Calificación promedio |
| `fechasNoDisponibles` | List\<Date\> | Fechas no disponibles |

### Reserva
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Clave primaria |
| `diaInicio` | Date | Fecha de inicio |
| `diaFinalizacion` | Date | Fecha de finalización |
| `usuario` | Usuario | Usuario que reserva |
| `vehiculo` | Vehiculo | Vehículo reservado |
| `precioFinal` | Double | Precio total calculado |
| `metodoDePago` | MetodoDePago | `MERCADOPAGO`, `PAYPAL`, `TARJETACREDITO` |
| `politicas` | boolean | Aceptación de políticas |
| `visibilidad` | boolean | Soft delete (false = cancelada) |

### Sucursal
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Clave primaria |
| `nombre` | String | Nombre de la sucursal |
| `direccion` | Direccion | Dirección (OneToOne) |

### Direccion
| Campo | Tipo |
|-------|------|
| `pais` | String |
| `provincia` | String |
| `localidad` | String |
| `calle` | String |
| `numero` | int |

---

## 🧪 Tests

Los tests se encuentran en `src/test/` y utilizan **JUnit 4/5 + Mockito**.

```bash
./mvnw test
```

### Suites disponibles

| Clase | Tipo | Descripción |
|-------|------|-------------|
| `ApplicationTests` | Integración | Verifica que el contexto de Spring carga correctamente |
| `VehiculoControllerTest` | Unitario | Tests del controlador de vehículos (registro, búsqueda, eliminación, características) |
| `SucursalServiceTest` | Unitario | Tests del servicio de sucursales (listar, buscar por ID, manejo de excepciones) |

---

## 🚀 CI/CD

El proyecto utiliza **GitLab CI/CD** (`.gitlab-ci.yml`). El pipeline se ejecuta únicamente sobre la rama `main` y consta de dos etapas:

### 1. Build
- **Imagen**: `maven:3.8.4-openjdk-17-slim`
- Ejecuta `mvn clean install`
- Genera el artefacto `target/rentcard.jar`
- Artefacto disponible por 1 semana

### 2. Deploy
- **Imagen**: `alpine:3.11`
- Copia el JAR al servidor de producción vía SCP
- Reinicia el servicio `consoleapp.service` en el servidor Ubuntu destino
- **Variables requeridas en GitLab CI/CD**:
  - `$SSH_PRIVATE_KEY` — Clave SSH privada para acceder al servidor
  - `$DEPLOY_SERVER_IP` — IP del servidor de despliegue

---

## 📁 Estructura del Proyecto

```
Back-End app JAVA/
├── src/
│   ├── main/
│   │   ├── java/com/world/rentcar/integrador/
│   │   │   ├── Application.java
│   │   │   ├── Component/
│   │   │   │   └── DataInitializer.java
│   │   │   ├── controller/
│   │   │   │   ├── DemoController.java
│   │   │   │   ├── ReservaController.java
│   │   │   │   ├── SucursalController.java
│   │   │   │   ├── UsuarioController.java
│   │   │   │   └── VehiculoController.java
│   │   │   ├── enums/
│   │   │   │   ├── MetodoDePago.java
│   │   │   │   └── UserRol.java
│   │   │   ├── exeptions/
│   │   │   │   ├── BadRequest.java
│   │   │   │   ├── ErrorRequest.java
│   │   │   │   └── GlobalExeptions.java
│   │   │   ├── model/
│   │   │   │   ├── Direccion.java
│   │   │   │   ├── ProductoCaracteristicas.java
│   │   │   │   ├── Puntuacion.java
│   │   │   │   ├── Reserva.java
│   │   │   │   ├── Sucursal.java
│   │   │   │   ├── Usuario.java
│   │   │   │   └── Vehiculo.java
│   │   │   ├── modelDTO/
│   │   │   │   └── UsuarioDTO.java
│   │   │   ├── repository/
│   │   │   │   ├── DireccionRepository.java
│   │   │   │   ├── PuntuacionRepository.java
│   │   │   │   ├── ReservaRepository.java
│   │   │   │   ├── SucursalRepository.java
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   └── VehiculoRepository.java
│   │   │   ├── security/
│   │   │   │   ├── EmailConfirmation/
│   │   │   │   │   └── EmailConfig.java
│   │   │   │   ├── filters/
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   └── JwtAuthorizationFilter.java
│   │   │   │   ├── jwt/
│   │   │   │   │   └── JwtUtils.java
│   │   │   │   └── SecurityConfig.java
│   │   │   └── service/
│   │   │       ├── ReservaService.java
│   │   │       ├── SucursalService.java
│   │   │       ├── UserDetailsServiceImpl.java
│   │   │       ├── UsuarioService.java
│   │   │       └── VehiculoService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/world/rentcar/integrador/
│           ├── ApplicationTests.java
│           ├── controllerTest/
│           │   └── VehiculoControllerTest.java
│           └── serviceTest/
│               └── SucursalServiceTest.java
└── pom.xml
```