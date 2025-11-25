# Solver-Java 🚀

API REST desarrollada con Spring Boot para la gestión de servicios y catálogos empresariales, con integración a ServiceNow.

**Creada por Ivan Hills** 

## 📋 Descripción

Solver-Java es una API RESTful robusta construida con Spring Boot que proporciona servicios de gestión para:

- 📦 **Gestión de Catálogos**: Administración de catálogos de servicios y líneas de catálogo
- 🎫 **Tickets e Incidentes**: Manejo de solicitudes (requests), incidentes y tareas
- 📎 **Attachments**: Gestión de archivos adjuntos
- 🔐 **Autenticación OAuth2**: Seguridad basada en tokens JWT
- 📊 **Auditoría**: Sistema de registro de auditoría para trazabilidad
- 🔗 **Integración ServiceNow**: Conectividad con plataforma ServiceNow
- 📧 **Notificaciones por Email**: Sistema de envío de correos
- 📄 **Generación de PDFs**: Creación de documentos PDF con iText

## 🛠️ Tecnologías

- **Java 8**
- **Spring Boot 2.3.4**
- **Spring Security OAuth2** - Autenticación y autorización
- **Spring Data JPA** - Capa de persistencia
- **PostgreSQL** - Base de datos
- **Swagger/OpenAPI** - Documentación de API
- **Maven** - Gestión de dependencias
- **iText PDF** - Generación de documentos
- **SSHJ** - Conexiones SSH
- **Jsoup** - Parseo HTML

## 📁 Estructura del Proyecto

```
src/main/java/com/logicalis/apisolver/
├── auth/                    # Configuración de seguridad y OAuth2
├── configuration/           # Configuraciones de Spring y Swagger
├── controller/             # Controllers REST
│   └── servicenow/         # Controllers específicos de ServiceNow
├── dao/                    # Data Access Objects
│   └── servicenow/         # DAOs de ServiceNow
├── model/                  # Entidades y modelos
│   ├── enums/              # Enumeraciones
│   ├── servicenow/         # Modelos de ServiceNow
│   └── utilities/          # Utilidades de modelo
├── services/               # Lógica de negocio
│   ├── impl/               # Implementaciones de servicios
│   └── servicenow/         # Servicios de ServiceNow
├── util/                   # Clases utilitarias
└── view/                   # DTOs y objetos de vista
```

## 🚀 Configuración

### Prerequisitos

- Java 8 o superior
- PostgreSQL 9+
- Maven 3.6+
- 25-30GB de RAM recomendada (configurado para alto rendimiento)

### Instalación

1. Clonar el repositorio:
```bash
git clone <repository-url>
cd Solver-Java
```

2. Configurar la base de datos PostgreSQL:
```bash
createdb solver
```

3. Configurar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/solver
spring.datasource.username=<tu-usuario>
spring.datasource.password=<tu-contraseña>
```

4. Compilar el proyecto:
```bash
./mvnw clean install
```

5. Ejecutar la aplicación:
```bash
./mvnw spring-boot:run
```

La API estará disponible en: `https://localhost:6050`

## 📚 Documentación API

Una vez iniciada la aplicación, la documentación Swagger estará disponible en:

```
https://localhost:6050/swagger-ui.html
```

## 🔑 Autenticación

La API utiliza OAuth2 con tokens JWT. Para obtener un token:

```bash
POST /oauth/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&username=<usuario>&password=<contraseña>
```

Usar el token en las peticiones:
```bash
Authorization: Bearer <tu-token-jwt>
```

## 🏗️ Características Principales

### Controladores Disponibles

- **AttachmentController**: Gestión de archivos adjuntos
- **BusinessRuleController**: Reglas de negocio
- **CatalogController**: Catálogos de servicios
- **CatalogLineController**: Líneas de catálogo
- **ChoiceController**: Opciones y selecciones
- **IncidentController**: Gestión de incidentes
- **RequestController**: Solicitudes de servicio
- **TaskController**: Tareas y seguimiento
- **UserController**: Gestión de usuarios

### Integración ServiceNow

Conectividad completa con ServiceNow para sincronización de:
- Incidentes
- Solicitudes
- Tareas
- Catálogos
- Usuarios

## ⚙️ Configuración Avanzada

### JVM Options

El proyecto está configurado con parámetros de alto rendimiento:
- Xms: 25GB
- Xmx: 30GB

Ajustar según los recursos disponibles en `pom.xml`.

### SSL/TLS

La aplicación está configurada para usar HTTPS con certificados SSL. Configurar en `application.properties`:
```properties
security.require-ssl=true
server.ssl.key-store=<ruta-al-keystore>
server.ssl.key-store-password=<contraseña>
```

## 🧪 Testing

Ejecutar tests:
```bash
./mvnw test
```

## 📦 Build para Producción

```bash
./mvnw clean package -DskipTests
```

El JAR ejecutable se generará en `target/apisolver-0.0.1-SNAPSHOT.jar`

## 📝 Notas de Desarrollo

- La aplicación usa JPA con DDL auto-update
- Soporte para archivos de hasta 50MB
- CORS configurado para dominios específicos
- Sistema de jobs programados deshabilitado por defecto

## 🤝 Contribuciones

Para contribuir al proyecto:

1. Fork el repositorio
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit de cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto es propiedad de Logicalis.

## 👤 Autor

**Ivan Hills**

---

*Desarrollado con ☕ y Spring Boot*
