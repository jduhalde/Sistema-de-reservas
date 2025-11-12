import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor de Request: Agrega el token JWT automáticamente
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 🔄 INTERCEPTOR DE RESPONSE MEJORADO
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response) {
            const status = error.response.status;
            const backendMessage = error.response.data?.message || error.response.data;

            // 401 Unauthorized: Token expirado o inválido
            if (status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
                return Promise.reject(error);
            }

            // 🆕 400 Bad Request: Puede incluir conflictos de horarios
            if (status === 400) {
                // Verificar si el mensaje contiene palabras clave de conflicto
                const messageStr = typeof backendMessage === 'string' ? backendMessage : JSON.stringify(backendMessage);
                const isConflict = messageStr.toLowerCase().includes('reserva') ||
                    messageStr.toLowerCase().includes('horario') ||
                    messageStr.toLowerCase().includes('sala') ||
                    messageStr.toLowerCase().includes('conflicto');

                if (isConflict) {
                    return Promise.reject({
                        status: 400,
                        message: backendMessage
                    });
                }

                // Para otros errores 400
                return Promise.reject({
                    status: 400,
                    message: backendMessage || 'Error en la solicitud'
                });
            }

            // 403 Forbidden: Sin permisos
            if (status === 403) {
                const message = backendMessage || 'No tienes permisos para realizar esta acción';
                alert(message);
                console.error('Acceso denegado:', message);
                return Promise.reject({
                    status: 403,
                    message: message
                });
            }

            // 🆕 409 Conflict: Conflictos de horarios
            if (status === 409) {
                return Promise.reject({
                    status: 409,
                    message: backendMessage || 'Ya existe una reserva en ese horario'
                });
            }
        }

        return Promise.reject(error);
    }
);

export default api;