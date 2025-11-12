import api from './api';

export const register = async (email, password, nombre, telefono) => {
    const response = await api.post('/api/auth/register', {
        email,
        password,
        nombre,      // 🆕 Nuevo campo
        telefono     // 🆕 Nuevo campo
    });
    return response.data;
};

export const login = async (email, password) => {
    const response = await api.post('/api/auth/login', { email, password });
    return response.data;
};

export const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
};