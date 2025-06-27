# 📄 Repo-trackontract-DWI
Repositorio del Proyecto de **Desarrollo Web Integral (DWI)**: Sistema de Gestión de Contratos **Trackontract**

Este proyecto tiene como objetivo implementar una solución web robusta para la gestión de contratos, clientes, categorías y usuarios. Está orientado a cubrir las necesidades académicas de la materia de DWI y a aplicar principios modernos de desarrollo fullstack con tecnologías de producción.

## 🧩 Módulos Funcionales

### 1. Gestión de Usuarios
- Registro, edición y listado de usuarios
- Inicio y cierre de sesión
- Habilitar / deshabilitar usuario
- Perfil personal y recuperación de contraseña *(prioridad baja)*
- Seguridad:
  - Control de acceso basado en roles (Administrador / Usuario)
  - Bloqueo temporal tras 3 intentos fallidos (30 min)
  - Cifrado seguro de contraseñas

### 2. Gestión de Categorías de Contratos
- CRUD completo de categorías
- Filtro por categorías activas
- Habilitar / deshabilitar

### 3. Gestión de Clientes
- Registro, edición y activación / desactivación de clientes
- Consulta general y filtrada por estado

### 4. Gestión de Contratos
- Registro de contratos vinculados a clientes y categorías
- Actualización de información
- Activación / desactivación
- Consulta por estado y asignación

## 🔐 Reglas de Negocio
- Cada contrato se relaciona con un cliente y una categoría
- Solo administradores pueden modificar registros
- Usuarios comunes solo pueden visualizar contratos asignados a clientes específicos
- Se lleva historial de acciones críticas (registro, edición, cambios de estado)

## ⚙️ Stack Tecnológico

| Componente    | Tecnología          |
|---------------|---------------------|
| **Frontend**  | React.js + Vite     |
| **Backend**   | Spring Boot (Java)  |
| **Base de datos** | PostgreSQL     |
| **ORM (opcional)** | JPA / Hibernate |
| **Autenticación** | JWT             |
| **Estilos**   | Tailwind CSS o Bootstrap |



## 🧱 Arquitectura

El sistema sigue una arquitectura **MVC (Modelo - Vista - Controlador)** en el backend con **Spring Boot**, y un frontend desacoplado desarrollado con **React.js**.

## CARPETA BACKEND
```plaintext
src/
└── main/
    └── java/
        └── com/
            └── trackontract/
                ├── user/
                │   ├── controller/
                │   ├── service/
                │   ├── service/impl/
                │   ├── model/
                │   ├── dto/
                │   └── repository/
                │
                ├── contract/
                │   ├── controller/
                │   ├── service/
                │   ├── service/impl/
                │   ├── model/
                │   ├── dto/
                │   └── repository/
                │
                ├── client/
                │   ├── ...
                │
                ├── category/
                │   ├── ...
                │
                ├── security/      ← JWT, filtros, configuraciones
                ├── config/        ← Beans globales, CORS, Swagger
                └── TrackontractApplication.java

```

## CARPETA FRONTEND
```plaintext
frontend/
└── src/
    ├── views/
    │   ├── cliente/
    │   │   ├── components/
    │   │   ├── pages/
    │   │   ├── hooks/
    │   │   └── services/
    │   │
    │   ├── abogado/
    │   │   ├── components/
    │   │   ├── pages/
    │   │   ├── hooks/
    │   │   └── services/
    │   │
    │   ├── administrador/
    │       ├── components/
    │       ├── pages/
    │       ├── hooks/
    │       └── services/
    │
    ├── common/
    ├── assets/
    ├── router/
    ├── App.jsx
    └── index.jsx

```
