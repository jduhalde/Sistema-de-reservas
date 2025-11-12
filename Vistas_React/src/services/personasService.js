import api from './api';

export const getPersonas = async () => {
    const response = await api.get('/api/personas');
    return response.data;
};

export const getPersonaById = async (id) => {
    const response = await api.get(`/api/personas/${id}`);
    return response.data;
};

export const createPersona = async (persona) => {
    const response = await api.post('/api/personas', persona);
    return response.data;
};

export const updatePersona = async (id, persona) => {
    const response = await api.put(`/api/personas/${id}`, persona);
    return response.data;
};

export const deletePersona = async (id) => {
    await api.delete(`/api/personas/${id}`);
};