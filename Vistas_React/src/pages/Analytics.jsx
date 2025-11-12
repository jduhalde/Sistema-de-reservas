import { useState, useEffect } from 'react';
import { getSummary } from '../services/analyticsService';

const Analytics = () => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadAnalytics();
    }, []);

    const loadAnalytics = async () => {
        try {
            setLoading(true);
            const analyticsData = await getSummary();
            setData(analyticsData);
        } catch (err) {
            setError(err.message || 'Error al cargar estadísticas');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="text-center py-8">Cargando estadísticas...</div>;
    }

    if (error) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                    {error}
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">Estadísticas de Reservas</h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div className="bg-white rounded-lg shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-700 mb-2">Total de Reservas</h3>
                    <p className="text-4xl font-bold text-blue-600">{data?.totalReservas || 0}</p>
                </div>

                {data?.reservasPorSala && (
                    <div className="bg-white rounded-lg shadow-md p-6 col-span-1 md:col-span-2">
                        <h3 className="text-lg font-semibold text-gray-700 mb-4">Reservas por Sala</h3>
                        <div className="space-y-3">
                            {Object.entries(data.reservasPorSala).map(([sala, cantidad]) => (
                                <div key={sala} className="flex justify-between items-center">
                                    <span className="text-gray-700">{sala}</span>
                                    <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full font-semibold">
                                        {cantidad}
                                    </span>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {data?.reservasPorArticulo && (
                    <div className="bg-white rounded-lg shadow-md p-6 col-span-1 md:col-span-2 lg:col-span-3">
                        <h3 className="text-lg font-semibold text-gray-700 mb-4">Reservas por Artículo</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                            {Object.entries(data.reservasPorArticulo).map(([articulo, cantidad]) => (
                                <div key={articulo} className="flex justify-between items-center bg-gray-50 p-3 rounded">
                                    <span className="text-gray-700">{articulo}</span>
                                    <span className="bg-green-100 text-green-800 px-3 py-1 rounded-full font-semibold">
                                        {cantidad}
                                    </span>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            <div className="mt-6">
                <button
                    onClick={loadAnalytics}
                    className="bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
                >
                    Actualizar Estadísticas
                </button>
            </div>
        </div>
    );
};

export default Analytics;