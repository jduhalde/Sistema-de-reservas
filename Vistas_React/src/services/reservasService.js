import api from './api';

export const getReservas = async () => {
    const response = await api.get('/api/reservas');
    return response.data;
};

export const getReservaById = async (id) => {
    const response = await api.get(`/api/reservas/${id}`);
    return response.data;
};

export const createReserva = async (reserva) => {
    const response = await api.post('/api/reservas', reserva);
    return response.data;
};

export const updateReserva = async (id, reserva) => {
    const response = await api.put(`/api/reservas/${id}`, reserva);
    return response.data;
};

export const deleteReserva = async (id) => {
    await api.delete(`/api/reservas/${id}`);
};