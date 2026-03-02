# EquiTrust Marketplace 🐎
> **Verified Horse Trading Platform**

Este proyecto nace para resolver la falta de transparencia en la industria equina, transformando los tradicionales "avisos clasificados" en un ecosistema de alta confianza basado en verificaciones técnicas y médicas.

---

## 📌 El Problema
La compra de un caballo es una transacción de **alto riesgo y alto costo**. Actualmente, los compradores se enfrentan a:
* **Falta de transparencia:** Historiales médicos incompletos o alterados.
* **Incertidumbre de rendimiento:** Videos desactualizados o que no reflejan la realidad del animal.
* **Riesgos de estafa:** Vendedores no verificados y falta de canales seguros.

## 📖 DOCUMENTACIÓN COMPLETA - MÓDULO USER
RESUMEN DE ENDPOINTS

Públicos (No requieren token):
```
POST /api/v1/auth/register  - Registrar usuario
POST /api/v1/auth/login     - Iniciar sesión
GET  /api/v1/auth/test      - Test de autenticación
```

Usuario Autenticado:
```
GET  /api/v1/users/me          - Ver mi perfil
PUT  /api/v1/users/me          - Actualizar mi perfil
PUT  /api/v1/users/me/password - Cambiar mi contraseña
```
Solo ADMIN:
```
GET    /api/v1/users                  - Listar todos los usuarios
GET    /api/v1/users/{id}             - Obtener usuario por ID
GET    /api/v1/users/email/{email}    - Buscar por email
GET    /api/v1/users/dni/{dni}        - Buscar por DNI
PUT    /api/v1/users/{id}/toggle-status - Activar/desactivar usuario
DELETE /api/v1/users/{id}             - Eliminar usuario (soft delete)
```


## 📋 DETALLE DE ENDPOINTS

### 1️⃣ ```GET /api/v1/users/me - Ver mi perfil```
Permisos: Cualquier usuario autenticado

Headers:
```Authorization: Bearer {token}```

Response 200:
```
{
  "id": 1,
  "dni": "12345678",
  "name": "Juan",
  "last_name": "Pérez",
  "email": "juan@example.com",
  "number": "+54911234567",
  "address": "CABA, Argentina",
  "rol": "BUYER",
  "created_at": "2026-02-16T10:00:00"
}
```

### 2️⃣ ```PUT /api/v1/users/me - Actualizar mi perfil```
Permisos: Cualquier usuario autenticado

Headers:

```
Authorization: Bearer {token}
Content-Type: application/json
```

Request Body:
```
{
  "name": "Juan Carlos",
  "lastName": "Pérez García",
  "number": "+54911111111",
  "address": "Nueva Dirección 456"
}
```
```
Notas:

Todos los campos son opcionales
Solo se actualizan los campos enviados
* No se puede cambiar: email, dni, rol, password
```

Response 200:
```
{
  "id": 1,
  "dni": "12345678",
  "name": "Juan Carlos",
  "last_name": "Pérez García",
  "email": "juan@example.com",
  "number": "+54911111111",
  "address": "Nueva Dirección 456",
  "rol": "BUYER",
  "created_at": "2026-02-16T10:00:00"
}
```

### 3️⃣ ```PUT /api/v1/users/me/password - Cambiar contraseña```

Permisos: Cualquier usuario autenticado

Headers:
```
Authorization: Bearer {token}
Content-Type: application/json
```
Request Body:
```
{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword456!",
  "confirmPassword": "NewPassword456!"
}
```

Validaciones:
```
currentPassword debe ser correcta
newPassword debe cumplir política de seguridad
newPassword y confirmPassword deben coincidir
newPassword debe ser diferente de currentPassword
```

Response 200:
```
{
  "message": "Password changed successfully"
}
```

Errores posibles:

400 - Contraseña actual incorrecta:
```
{
  "timestamp": "2026-02-16T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Current password is incorrect"
}
```
400 - Contraseñas no coinciden:
```
{
  "timestamp": "2026-02-16T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "New password and confirmation do not match"
}
```

### 4️⃣ ```GET /api/v1/users - Listar todos los usuarios```

Permisos: Solo ADMIN

Headers:
```
Authorization: Bearer {token}
```
Response 200:
```
[
  {
    "id": 1,
    "dni": "12345678",
    "name": "Juan",
    "last_name": "Pérez",
    "email": "juan@example.com",
    "rol": "BUYER",
    "created_at": "2026-02-16T10:00:00"
  },
  {
    "id": 2,
    "dni": "87654321",
    "name": "María",
    "last_name": "González",
    "email": "maria@example.com",
    "rol": "SELLER",
    "created_at": "2026-02-16T11:00:00"
  }
]
```

### 5️⃣ ```GET /api/v1/users/{id} - Obtener usuario por ID```

Permisos: Solo ADMIN

Headers:
```
Authorization: Bearer {token}
```

Ejemplo:

```
GET /api/v1/users/1
```

Response 200:
```
{
  "id": 1,
  "dni": "12345678",
  "name": "Juan",
  "last_name": "Pérez",
  "email": "juan@example.com",
  "number": "+54911234567",
  "address": "CABA, Argentina",
  "rol": "BUYER",
  "created_at": "2026-02-16T10:00:00"
}
```
Error 404:
```
{
  "timestamp": "2026-02-16T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 1"
}
```

### 6️⃣ ```GET /api/v1/users/email/{email} - Buscar por email```

Permisos: Solo ADMIN

Headers:
```
Authorization: Bearer {token}
```
Ejemplo:
```
GET /api/v1/users/email/juan@example.com
```
Response: Igual que GET by ID

### 7️⃣ ```GET /api/v1/users/dni/{dni} - Buscar por DNI```

Permisos: Solo ADMIN

Headers:
```
Authorization: Bearer {token}
```

Ejemplo:
```
GET /api/v1/users/dni/12345678
```
Response: Igual que GET by ID


### 8️⃣ ```PUT /api/v1/users/{id}/toggle-status - Activar/Desactivar```

Permisos: Solo ADMIN

Headers:
```
Authorization: Bearer {token}
```
Ejemplo:
```
PUT /api/v1/users/1/toggle-status
```
Funcionalidad:
```
- Si el usuario está enabled=true → cambia a enabled=false
- Si el usuario está enabled=false → cambia a enabled=true
```
Response 200:
```
{
  "id": 1,
  "dni": "12345678",
  "name": "Juan",
  "last_name": "Pérez",
  "email": "juan@example.com",
  "rol": "BUYER",
  "created_at": "2026-02-16T10:00:00"
}
```

- Nota: Los usuarios desactivados (enabled=false) no pueden hacer login.

### 9️⃣ ```DELETE /api/v1/users/{id} - Eliminar usuario```

Permisos: Solo ADMIN

Headers:
```
Authorization: Bearer {token}
```

Ejemplo:

```
DELETE /api/v1/users/1
```
Funcionalidad:
```
- Soft delete: No elimina físicamente el usuario
- Marca deleted=true en la base de datos
- El usuario ya no aparece en consultas
- Se puede recuperar cambiando deleted=false manualmente en BD
```

Response 200:
```
{
  "message": "User deleted successfully"
}
```

## 🧪 GUÍA DE TESTING

### ```Escenario 1: Usuario Normal (BUYER)```

```
1 - Registrar usuario
POST /api/v1/auth/register
{
  "dni": "12345678",
  "name": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "password": "Password123!",
  "rol": "BUYER"
}
--- Guardar el token recibido

2 - Ver mi perfil
GET /api/v1/users/me
Authorization: Bearer {token}
--- Debe funcionar

3 - Actualizar mi perfil
PUT /api/v1/users/me
Authorization: Bearer {token}
{
  "name": "Juan Carlos",
  "number": "+54911111111"
}
---- Debe funcionar

4 - Cambiar mi contraseña
PUT /api/v1/users/me/password
Authorization: Bearer {token}
{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword456!",
  "confirmPassword": "NewPassword456!"
}
--- Debe funcionar

5 - Intentar ver todos los usuarios
GET /api/v1/users
Authorization: Bearer {token}

--- Debe fallar con 403 Forbidden
```

### ```Escenario 2: Administrador (ADMIN)```

```
1 - Registrar admin
POST /api/v1/auth/register
{
  "dni": "99999999",
  "name": "Admin",
  "lastName": "System",
  "email": "admin@equitrust.com",
  "password": "Admin123!",
  "rol": "ADMIN"
}

2 - Ver todos los usuarios
GET /api/v1/users
Authorization: Bearer {admin_token}
# ✅ Debe funcionar

3 - Buscar usuario por email
GET /api/v1/users/email/juan@example.com
Authorization: Bearer {admin_token}
---- Debe funcionar

4 - Desactivar usuario
PUT /api/v1/users/1/toggle-status
Authorization: Bearer {admin_token}
---- Debe funcionar

5 - Eliminar usuario
DELETE /api/v1/users/1
Authorization: Bearer {admin_token}
---- Debe funcionar
```