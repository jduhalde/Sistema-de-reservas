export const getSummary = async () => {
    // Llamar directamente a Python en puerto 8000
    const response = await fetch('http://localhost:8000/api/analytics/summary');

    if (!response.ok) {
        throw new Error('Error al obtener estadísticas');
    }

    return response.json();
};