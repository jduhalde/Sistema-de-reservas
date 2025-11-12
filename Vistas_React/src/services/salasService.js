import api from './api';

export const getSalas = async () => {
    const response = await api.get('/api/salas');
    return response.data;
};

export const getSalaById = async (id) => {
    const response = await api.get(`/api/salas/${id}`);
    return response.data;
};

export const createSala = async (sala) => {
    const response = await api.post('/api/salas', sala);
    return response.data;
};

export const updateSala = async (id, sala) => {
    const response = await api.put(`/api/salas/${id}`, sala);
    return response.data;
};

export const deleteSala = async (id) => {
    await api.delete(`/api/salas/${id}`);
};