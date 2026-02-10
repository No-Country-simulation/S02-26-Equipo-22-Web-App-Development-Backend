# 🐴 HorseTrust - Frontend

> Plataforma de compraventa de caballos con chat en tiempo real

Este es el frontend de HorseTrust, una aplicación Next.js que incluye un sistema de chat en tiempo real mediante WebSocket para facilitar la comunicación entre compradores y vendedores de caballos.

---

## 📋 Tabla de Contenidos

- [Inicio Rápido](#-inicio-rápido)
- [Chat en Tiempo Real](#-chat-en-tiempo-real)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Tecnologías](#-tecnologías)
- [Configuración](#-configuración)
- [Deployment](#-deployment)

---

## 🚀 Inicio Rápido

### Prerrequisitos

- Node.js 18+ 
- npm o yarn
- Backend corriendo en `http://localhost:8080`

### Instalación

```bash
# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev
```

La aplicación estará disponible en: **http://localhost:3000**

### URLs Importantes

- 🏠 **Home**: `http://localhost:3000`
- 💬 **Chat Demo**: `http://localhost:3000/chat-demo`

---

## 💬 Chat en Tiempo Real

### Características

✅ **Mensajes instantáneos** - Comunicación en tiempo real vía WebSocket  
✅ **Diferenciación visual** - Mensajes propios vs. otros usuarios  
✅ **Notificaciones de sistema** - JOIN/LEAVE de usuarios  
✅ **Reconexión automática** - Manejo robusto de desconexiones  
✅ **Validación de mensajes** - Límite de 1000 caracteres  
✅ **Timestamps** - Hora de envío de cada mensaje  
✅ **Estado de conexión** - Indicador visual del estado  

### Arquitectura del Chat

```
src/
├── types/
│   └── ChatMessage.ts          # Interfaces TypeScript
├── service/
│   └── hooks/
│       └── useChat.ts          # Hook personalizado WebSocket
└── _component/
    └── chat/
        └── ChatPanel.tsx       # Componente UI del chat
```

---

## 📦 Componentes del Chat

### 1. `ChatMessage.ts` - Tipos TypeScript

Define las interfaces y enums para los mensajes del chat.

```typescript
// src/types/ChatMessage.ts

export enum MessageType {
  CHAT = 'CHAT',    // Mensaje normal
  JOIN = 'JOIN',    // Usuario se une
  LEAVE = 'LEAVE'   // Usuario sale
}

export interface ChatMessage {
  sender: string;      // Nombre del usuario
  content: string;     // Contenido del mensaje
  horseId: string;     // ID del caballo
  type: MessageType;   // Tipo de mensaje
  timestamp: string;   // Timestamp ISO
}
```

**Funciones helper:**

```typescript
// Crear un mensaje
createChatMessage(sender, content, horseId, type?)

// Parsear timestamp
parseMessageTimestamp(timestamp)
```

---

### 2. `useChat.ts` - Hook Personalizado

Hook de React que maneja toda la lógica de WebSocket.

```typescript
// src/service/hooks/useChat.ts

const {
  messages,           // Array de mensajes
  connectionState,    // Estado: 'disconnected' | 'connecting' | 'connected' | 'error'
  error,             // Mensaje de error (si existe)
  sendMessage,       // Función para enviar mensaje
  joinChat,          // Función para unirse al chat
  leaveChat,         // Función para salir del chat
  clearMessages      // Función para limpiar mensajes
} = useChat(horseId, backendUrl);
```

**Ejemplo de uso:**

```tsx
import { useChat } from '@/service/hooks/useChat';

function MiComponente() {
  const { messages, connectionState, sendMessage, joinChat } = useChat(
    'horse-123',
    'http://localhost:8080'
  );

  useEffect(() => {
    if (connectionState === 'connected') {
      joinChat('Juan Pérez');
    }
  }, [connectionState]);

  const handleSend = () => {
    sendMessage('Hola, me interesa este caballo');
  };

  return (
    <div>
      <p>Estado: {connectionState}</p>
      {messages.map((msg, i) => (
        <div key={i}>{msg.sender}: {msg.content}</div>
      ))}
      <button onClick={handleSend}>Enviar</button>
    </div>
  );
}
```

**Configuración del Hook:**

- **Reconexión automática**: Cada 5 segundos
- **Heartbeat**: 4 segundos (entrada/salida)
- **Protocolo**: STOMP over SockJS
- **Endpoints**:
  - Conexión: `/ws-horse`
  - Enviar: `/app/chat.sendMessage`
  - Unirse: `/app/chat.addUser`
  - Suscripción: `/topic/public`

---

### 3. `ChatPanel.tsx` - Componente UI

Componente React completo con interfaz premium para el chat.

```tsx
import { ChatPanel } from '@/_component/chat/ChatPanel';

<ChatPanel
  horseId="horse-123"
  currentUsername="Juan Pérez"
  backendUrl="http://localhost:8080"  // Opcional
/>
```

**Props:**

| Prop | Tipo | Requerido | Default | Descripción |
|------|------|-----------|---------|-------------|
| `horseId` | `string` | ✅ | - | ID del caballo |
| `currentUsername` | `string` | ✅ | - | Nombre del usuario actual |
| `backendUrl` | `string` | ❌ | `http://localhost:8080` | URL del backend |

**Características del UI:**

- 🎨 **Diseño premium** con Tailwind CSS
- 📱 **Responsive** - Funciona en móvil y desktop
- 🔵 **Mensajes propios** - Azul, alineados a la derecha
- ⚪ **Mensajes de otros** - Blanco, alineados a la izquierda
- 📍 **Auto-scroll** - Scroll automático a nuevos mensajes
- ⌨️ **Atajos de teclado** - Enter para enviar
- 📊 **Contador de caracteres** - Muestra 0/1000
- 🔴 **Indicador de estado** - Conectado/Desconectado/Error

---

## 🎨 Diferenciación Visual de Mensajes

### Mensajes Propios
```
                  ┌───────────────────┐
                  │ Hola, me interesa │  ← Azul
                  │ este caballo      │
                  └───────────────────┘
                  14:30                  ← Derecha
```

### Mensajes de Otros
```
María                                    ← Nombre
┌───────────────────┐
│ ¿Cuál es el       │                   ← Blanco
│ precio?           │
└───────────────────┘
14:31                                    ← Izquierda
```

### Mensajes del Sistema
```
        ─────────────────────────
       │ Juan se unió al chat │          ← Centrado, azul claro
        ─────────────────────────
```

---

## 🏗️ Estructura del Proyecto

```
S02-26-Equipo-22-Web-App-Front/
├── src/
│   ├── app/
│   │   ├── chat-demo/
│   │   │   └── page.tsx              # Página de demo del chat
│   │   └── page.tsx                  # Página principal
│   ├── _component/
│   │   └── chat/
│   │       └── ChatPanel.tsx         # Componente del chat
│   ├── service/
│   │   └── hooks/
│   │       └── useChat.ts            # Hook de WebSocket
│   └── types/
│       └── ChatMessage.ts            # Tipos TypeScript
├── public/                           # Archivos estáticos
├── package.json                      # Dependencias
├── tsconfig.json                     # Configuración TypeScript
└── tailwind.config.ts                # Configuración Tailwind
```

---

## 🛠️ Tecnologías

### Core
- **Next.js 16** - Framework React
- **React 19** - Librería UI
- **TypeScript 5** - Tipado estático
- **Tailwind CSS 4** - Estilos

### Chat
- **@stomp/stompjs** - Cliente STOMP
- **sockjs-client** - Cliente SockJS
- **@types/sockjs-client** - Tipos TypeScript

### Otras
- **@tanstack/react-query** - Manejo de estado servidor
- **axios** - Cliente HTTP

---

## ⚙️ Configuración

### Variables de Entorno

Crea un archivo `.env.local`:

```bash
# Backend URL
NEXT_PUBLIC_BACKEND_URL=http://localhost:8080

# Otras configuraciones
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Uso en el código

```typescript
const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

<ChatPanel
  horseId={horseId}
  currentUsername={username}
  backendUrl={backendUrl}
/>
```

---

## 🧪 Testing del Chat

### Opción 1: Múltiples Pestañas

1. Abre `http://localhost:3000/chat-demo`
2. Abre la misma URL en otra pestaña
3. Envía mensajes desde ambas pestañas
4. Observa la sincronización en tiempo real

### Opción 2: Modo Incógnito

1. Abre `http://localhost:3000/chat-demo` en tu navegador normal
2. Abre una ventana de incógnito
3. Navega a la misma URL
4. Chatea entre ambas ventanas

### Opción 3: Navegadores Diferentes

1. Abre Chrome → `http://localhost:3000/chat-demo`
2. Abre Firefox → `http://localhost:3000/chat-demo`
3. Chatea entre navegadores

---

## 📡 Integración con Backend

### Endpoints WebSocket

El frontend se conecta a estos endpoints del backend:

```javascript
// Conexión WebSocket
ws://localhost:8080/ws-horse

// Enviar mensaje
/app/chat.sendMessage

// Unirse al chat
/app/chat.addUser

// Recibir mensajes
/topic/public
```

### Formato de Mensajes

**Enviar mensaje:**
```json
{
  "sender": "Juan Pérez",
  "content": "Hola, me interesa este caballo",
  "horseId": "horse-123",
  "type": "CHAT"
}
```

**Recibir mensaje:**
```json
{
  "sender": "María García",
  "content": "¿Cuál es el precio?",
  "horseId": "horse-123",
  "type": "CHAT",
  "timestamp": "2026-02-10T17:30:00"
}
```

---

## 🚀 Deployment

### Build de Producción

```bash
# Crear build optimizado
npm run build

# Ejecutar en producción
npm start
```

### Variables de Entorno (Producción)

```bash
# .env.production
NEXT_PUBLIC_BACKEND_URL=https://api.horsetrust.com
NEXT_PUBLIC_API_URL=https://api.horsetrust.com/api
```

### Vercel Deployment

1. Conecta tu repositorio a Vercel
2. Configura las variables de entorno
3. Deploy automático en cada push

```bash
# O deploy manual
vercel --prod
```

---

## 🐛 Troubleshooting

### El chat no se conecta

**Problema:** Estado permanece en "Conectando..."

**Solución:**
1. Verifica que el backend esté corriendo en el puerto 8080
2. Revisa la consola del navegador para errores
3. Verifica la configuración de CORS en el backend

```bash
# Verificar backend
curl http://localhost:8080/actuator/health
```

### Mensajes no se envían

**Problema:** Los mensajes no aparecen en otras pestañas

**Solución:**
1. Verifica que `horseId` sea el mismo en todas las instancias
2. Revisa que el backend esté procesando los mensajes
3. Verifica los logs del backend

### Error de CORS

**Problema:** Error de CORS en la consola

**Solución:**
Verifica la configuración en el backend (`WebSocketConfig.java`):

```java
registry.addEndpoint("/ws-horse")
    .setAllowedOriginPatterns("*")
    .withSockJS();
```

---

## 📚 Recursos Adicionales

### Documentación
- [Next.js Docs](https://nextjs.org/docs)
- [STOMP Protocol](https://stomp.github.io/)
- [SockJS](https://github.com/sockjs/sockjs-client)
- [Tailwind CSS](https://tailwindcss.com/docs)

### Ejemplos
- Ver `src/app/chat-demo/page.tsx` para ejemplo completo
- Ver `src/service/hooks/useChat.ts` para lógica WebSocket
- Ver `src/_component/chat/ChatPanel.tsx` para UI

---

## 👥 Equipo

**No Country - Simulación S02-26 Equipo 22**

---

## 📄 Licencia

Ver archivo [LICENSE](./LICENSE)

---

## 🎯 Próximos Pasos

- [ ] Integrar chat con autenticación real
- [ ] Agregar indicadores de "escribiendo..."
- [ ] Implementar notificaciones push
- [ ] Agregar historial de mensajes persistente
- [ ] Implementar avatares de usuario
- [ ] Agregar soporte para imágenes en chat
- [ ] Implementar confirmaciones de lectura

---

**¿Necesitas ayuda?** Revisa la [documentación del backend](../S02-26-Equipo-22-Web-App-Development-Backend/README.txt) o abre un issue en el repositorio.
