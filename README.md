# 🔐 Microservicio de Autenticación

Este proyecto es un **microservicio de autenticación** desarrollado bajo la **arquitectura Scaffold de Bancolombia**, orientado a un diseño limpio, modular y mantenible.  
El servicio gestiona el registro, login, logout y validación de usuarios, implementando buenas prácticas en **reactividad**, **seguridad** y **despliegue en la nube (AWS)**.

---

## 🚀 Características principales

- Arquitectura **Scaffold de Bancolombia**.
- API reactiva basada en **Spring WebFlux**.
- Autenticación con **JWT**.
- Documentación automática de endpoints con **OpenAPI (Swagger)**.
- Conexión a **PostgreSQL** usando **R2DBC** (reactiva).
- Pruebas unitarias con **JUnit** y **Mockito**.
- Despliegue en AWS usando contenedores y servicios administrados.

---

## 🛠️ Tecnologías utilizadas

### Lenguajes
- **Java 21**

### Frameworks y Librerías
- **Spring Boot**  
- **Spring WebFlux**  
- **JUnit** y **Mockito** (testing)  
- **OpenAPI (Swagger)**  

### Base de Datos
- **PostgreSQL**  
- **R2DBC** 

### Infraestructura y Cloud (AWS)
- ** ECR **
- ** ECS **
- ** RDS ** 
- ** Secrets Manager** 
- ** Elastic Load Balancer** 
- ** API Gateway**

---

## 📂 Estructura del proyecto

El proyecto sigue la **arquitectura hexagonal** propuesta por el **Scaffold de Bancolombia**, separando responsabilidades en capas bien definidas

