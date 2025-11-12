import api from './api';

export const getArticulos = async () => {
    const response = await api.get('/api/articulos');
    return response.data;
};

export const getArticuloById = async (id) => {
    const response = await api.get(`/api/articulos/${id}`);
    return response.data;
};

export const createArticulo = async (articulo) => {
    const response = await api.post('/api/articulos', articulo);
    return response.data;
};

export const updateArticulo = async (id, articulo) => {
    const response = await api.put(`/api/articulos/${id}`, articulo);
    return response.data;
};

export const deleteArticulo = async (id) => {
    await api.delete(`/api/articulos/${id}`);
};