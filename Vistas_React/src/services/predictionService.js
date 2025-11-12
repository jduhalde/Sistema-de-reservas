import api from './api';

export const syncData = async () => {
    const response = await api.post('/api/reservas/sync-analytics');
    return response.data;
};

export const trainModel = async () => {
    // Llamar directamente a Python en puerto 8000
    const response = await fetch('http://localhost:8000/api/analytics/train', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (!response.ok) {
        throw new Error('Error al entrenar modelo');
    }

    return response.json();
};

export const getPredictions = async () => {
    // Llamar directamente a Python en puerto 8000
    const response = await fetch('http://localhost:8000/api/analytics/predictions');

    if (!response.ok) {
        throw new Error('Error al obtener predicciones');
    }

    return response.json();
};