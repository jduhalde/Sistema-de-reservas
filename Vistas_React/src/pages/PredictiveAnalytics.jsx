import { useState, useEffect } from 'react';
import { syncData, trainModel, getPredictions } from '../services/predictionService';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Area, AreaChart } from 'recharts';

const PredictiveAnalytics = () => {
    const [predictions, setPredictions] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [syncing, setSyncing] = useState(false);
    const [training, setTraining] = useState(false);

    useEffect(() => {
        loadPredictions();
    }, []);

    const loadPredictions = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await getPredictions();

            if (response.success) {
                setPredictions(response.predicciones);
            } else {
                setError(response.message || 'No hay predicciones disponibles');
            }
        } catch (err) {
            setError(err.message || 'Error al cargar predicciones');
        } finally {
            setLoading(false);
        }
    };

    const handleSync = async () => {
        try {
            setSyncing(true);
            setError('');
            setSuccessMessage('');

            const response = await syncData();
            setSuccessMessage('✅ Datos sincronizados correctamente');

            console.log('Sincronización exitosa:', response);
        } catch (err) {
            setError(err.message || 'Error al sincronizar datos');
        } finally {
            setSyncing(false);
        }
    };

    const handleTrain = async () => {
        try {
            setTraining(true);
            setError('');
            setSuccessMessage('');

            const response = await trainModel();

            if (response.success) {
                setSuccessMessage(`✅ Modelo entrenado exitosamente. ${response.predicciones.length} predicciones generadas.`);
                setPredictions(response.predicciones);
            } else {
                setError(response.message || 'Error al entrenar modelo');
            }
        } catch (err) {
            setError(err.message || 'Error al entrenar modelo');
        } finally {
            setTraining(false);
        }
    };

    const handleSyncAndTrain = async () => {
        await handleSync();
        setTimeout(async () => {
            await handleTrain();
        }, 1000);
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">
                🔮 Análisis Predictivo con Machine Learning
            </h1>

            {/* Mensajes de estado */}
            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
                    {error}
                </div>
            )}

            {successMessage && (
                <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-6">
                    {successMessage}
                </div>
            )}

            {/* Botones de acción */}
            <div className="bg-white rounded-lg shadow-md p-6 mb-6">
                <h2 className="text-xl font-semibold text-gray-700 mb-4">
                    Acciones
                </h2>
                <div className="flex flex-wrap gap-4">
                    <button
                        onClick={handleSyncAndTrain}
                        disabled={syncing || training}
                        className="bg-blue-600 text-white px-6 py-3 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors font-semibold"
                    >
                        {syncing || training ? '⏳ Procesando...' : '🚀 Sincronizar y Entrenar Modelo'}
                    </button>

                    <button
                        onClick={loadPredictions}
                        disabled={loading}
                        className="bg-green-600 text-white px-6 py-3 rounded-md hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
                    >
                        {loading ? '⏳ Cargando...' : '🔄 Actualizar Predicciones'}
                    </button>
                </div>

                <div className="mt-4 p-4 bg-blue-50 rounded-md">
                    <p className="text-sm text-gray-700">
                        <strong>💡 Cómo usar:</strong>
                    </p>
                    <ol className="text-sm text-gray-600 mt-2 ml-4 list-decimal">
                        <li>Presiona "Sincronizar y Entrenar Modelo" para obtener predicciones</li>
                        <li>El sistema sincronizará las reservas y entrenará el modelo Prophet</li>
                        <li>Las predicciones se mostrarán en el gráfico automáticamente</li>
                    </ol>
                </div>
            </div>

            {/* Gráfico de predicciones */}
            {loading ? (
                <div className="text-center py-8">
                    <p className="text-gray-600">Cargando predicciones...</p>
                </div>
            ) : predictions.length > 0 ? (
                <div className="bg-white rounded-lg shadow-md p-6">
                    <h2 className="text-xl font-semibold text-gray-700 mb-4">
                        📈 Predicción de Ocupación (Próximos 30 días)
                    </h2>

                    <ResponsiveContainer width="100%" height={400}>
                        <AreaChart data={predictions}>
                            <defs>
                                <linearGradient id="colorPrediccion" x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.8} />
                                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0.1} />
                                </linearGradient>
                            </defs>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis
                                dataKey="fecha"
                                tick={{ fontSize: 12 }}
                                angle={-45}
                                textAnchor="end"
                                height={80}
                            />
                            <YAxis
                                label={{ value: 'Número de Reservas', angle: -90, position: 'insideLeft' }}
                            />
                            <Tooltip
                                contentStyle={{ backgroundColor: '#fff', border: '1px solid #ccc' }}
                                labelStyle={{ fontWeight: 'bold' }}
                            />
                            <Legend />

                            <Area
                                type="monotone"
                                dataKey="limiteSuperior"
                                stroke="#93c5fd"
                                fill="#dbeafe"
                                fillOpacity={0.3}
                                name="Límite Superior"
                            />
                            <Area
                                type="monotone"
                                dataKey="limiteInferior"
                                stroke="#93c5fd"
                                fill="#dbeafe"
                                fillOpacity={0.3}
                                name="Límite Inferior"
                            />
                            <Line
                                type="monotone"
                                dataKey="prediccion"
                                stroke="#3b82f6"
                                strokeWidth={3}
                                dot={{ r: 4 }}
                                activeDot={{ r: 6 }}
                                name="Predicción"
                            />
                        </AreaChart>
                    </ResponsiveContainer>

                    <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div className="bg-blue-50 p-4 rounded-md">
                            <p className="text-sm text-gray-600">Predicciones generadas</p>
                            <p className="text-2xl font-bold text-blue-600">{predictions.length}</p>
                        </div>
                        <div className="bg-green-50 p-4 rounded-md">
                            <p className="text-sm text-gray-600">Ocupación promedio estimada</p>
                            <p className="text-2xl font-bold text-green-600">
                                {(predictions.reduce((acc, p) => acc + p.prediccion, 0) / predictions.length).toFixed(1)}
                            </p>
                        </div>
                        <div className="bg-purple-50 p-4 rounded-md">
                            <p className="text-sm text-gray-600">Modelo utilizado</p>
                            <p className="text-lg font-bold text-purple-600">Prophet ML</p>
                        </div>
                    </div>
                </div>
            ) : (
                <div className="bg-white rounded-lg shadow-md p-8 text-center">
                    <p className="text-gray-600 mb-4">
                        No hay predicciones disponibles. Sincroniza y entrena el modelo para comenzar.
                    </p>
                    <button
                        onClick={handleSyncAndTrain}
                        className="bg-blue-600 text-white px-6 py-3 rounded-md hover:bg-blue-700 transition-colors"
                    >
                        🚀 Generar Predicciones
                    </button>
                </div>
            )}
        </div>
    );
};

export default PredictiveAnalytics;