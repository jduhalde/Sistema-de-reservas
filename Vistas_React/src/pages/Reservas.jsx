import { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import {
    getReservas,
    createReserva,
    updateReserva,
    deleteReserva,
} from '../services/reservasService';
import { getPersonas } from '../services/personasService';
import { getSalas } from '../services/salasService';
import { getArticulos } from '../services/articulosService';

const Reservas = () => {
    const { isAdmin } = useContext(AuthContext);
    const [reservas, setReservas] = useState([]);
    const [personas, setPersonas] = useState([]);
    const [salas, setSalas] = useState([]);
    const [articulos, setArticulos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editingReserva, setEditingReserva] = useState(null);

    const [formData, setFormData] = useState({
        personaId: '',
        salaId: '',
        articuloId: '',
        fechaHoraInicio: '',
        fechaHoraFin: '',
    });

    useEffect(() => {
        cargarDatos();
    }, []);

    const cargarDatos = async () => {
        try {
            const [reservasData, personasData, salasData, articulosData] = await Promise.all([
                getReservas(),
                getPersonas(),
                getSalas(),
                getArticulos(),
            ]);

            setReservas(reservasData);
            setPersonas(personasData);
            setSalas(salasData);
            setArticulos(articulosData);
        } catch (err) {
            setError('Error al cargar los datos');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const reservaData = {
                persona: { id: parseInt(formData.personaId) },
                sala: formData.salaId ? { id: parseInt(formData.salaId) } : null,
                articulo: formData.articuloId ? { id: parseInt(formData.articuloId) } : null,
                fechaHoraInicio: formData.fechaHoraInicio,
                fechaHoraFin: formData.fechaHoraFin,
            };

            if (editingReserva) {
                await updateReserva(editingReserva.id, reservaData);
            } else {
                await createReserva(reservaData);
            }

            await cargarDatos();
            cerrarModal();
        } catch (err) {
            console.error(err);

            // ✅ Capturar errores 400 y 409 del interceptor de api.js
            if (err.status === 400 || err.status === 409) {
                setError(err.message);
            }
            // ✅ Capturar error 403 (sin permisos)
            else if (err.response?.status === 403) {
                setError('No tenés permisos para realizar esta acción');
            }
            // ✅ Fallback: intentar extraer mensaje del backend o mostrar genérico
            else {
                const backendMessage = err.response?.data?.message || err.response?.data;
                setError(backendMessage || 'Error al guardar la reserva');
            }
        }
    };

    const handleDelete = async (id) => {
        if (!isAdmin()) {
            alert('Solo los administradores pueden eliminar reservas');
            return;
        }

        if (window.confirm('¿Estás seguro de que querés eliminar esta reserva?')) {
            try {
                await deleteReserva(id);
                await cargarDatos();
            } catch (err) {
                if (err.response?.status === 403) {
                    alert('No tenés permisos para eliminar reservas');
                } else {
                    alert('Error al eliminar la reserva');
                }
                console.error(err);
            }
        }
    };

    const abrirModalNuevo = () => {
        setEditingReserva(null);
        setFormData({
            personaId: '',
            salaId: '',
            articuloId: '',
            fechaHoraInicio: '',
            fechaHoraFin: '',
        });
        setError('');
        setShowModal(true);
    };

    const abrirModalEditar = (reserva) => {
        if (!isAdmin()) {
            alert('Solo los administradores pueden editar reservas');
            return;
        }

        setEditingReserva(reserva);
        setFormData({
            personaId: reserva.persona?.id || '',
            salaId: reserva.sala?.id || '',
            articuloId: reserva.articulo?.id || '',
            fechaHoraInicio: reserva.fechaHoraInicio,
            fechaHoraFin: reserva.fechaHoraFin,
        });
        setError('');
        setShowModal(true);
    };

    const cerrarModal = () => {
        setShowModal(false);
        setEditingReserva(null);
        setFormData({
            personaId: '',
            salaId: '',
            articuloId: '',
            fechaHoraInicio: '',
            fechaHoraFin: '',
        });
        setError('');
    };

    if (loading) {
        return <div className="container mx-auto p-8">Cargando...</div>;
    }

    return (
        <div className="container mx-auto p-8">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold">Gestión de Reservas</h1>
                <button
                    onClick={abrirModalNuevo}
                    className="bg-green-500 text-white px-6 py-2 rounded hover:bg-green-600 transition"
                >
                    Nueva Reserva
                </button>
            </div>

            <div className="bg-white shadow-md rounded-lg overflow-hidden">
                <table className="min-w-full">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="px-6 py-3 text-left">ID</th>
                            <th className="px-6 py-3 text-left">Persona</th>
                            <th className="px-6 py-3 text-left">Sala</th>
                            <th className="px-6 py-3 text-left">Artículo</th>
                            <th className="px-6 py-3 text-left">Fecha Inicio</th>
                            <th className="px-6 py-3 text-left">Fecha Fin</th>
                            <th className="px-6 py-3 text-left">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {reservas.map((reserva) => (
                            <tr key={reserva.id} className="border-t hover:bg-gray-50">
                                <td className="px-6 py-4">{reserva.id}</td>
                                <td className="px-6 py-4">{reserva.persona?.nombre || 'N/A'}</td>
                                <td className="px-6 py-4">{reserva.sala?.nombre || 'N/A'}</td>
                                <td className="px-6 py-4">{reserva.articulo?.nombre || 'N/A'}</td>
                                <td className="px-6 py-4">
                                    {new Date(reserva.fechaHoraInicio).toLocaleString('es-AR')}
                                </td>
                                <td className="px-6 py-4">
                                    {new Date(reserva.fechaHoraFin).toLocaleString('es-AR')}
                                </td>
                                <td className="px-6 py-4">
                                    <div className="flex space-x-2">
                                        {isAdmin() && (
                                            <button
                                                onClick={() => abrirModalEditar(reserva)}
                                                className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 text-sm"
                                            >
                                                Editar
                                            </button>
                                        )}

                                        {isAdmin() && (
                                            <button
                                                onClick={() => handleDelete(reserva.id)}
                                                className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm"
                                            >
                                                Eliminar
                                            </button>
                                        )}

                                        {!isAdmin() && (
                                            <span className="text-gray-400 text-sm italic">
                                                Solo lectura
                                            </span>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-8 max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4">
                            {editingReserva ? 'Editar Reserva' : 'Nueva Reserva'}
                        </h2>

                        {error && (
                            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                                {error}
                            </div>
                        )}

                        <form onSubmit={handleSubmit}>
                            <div className="mb-4">
                                <label className="block text-gray-700 mb-2">Persona *</label>
                                <select
                                    value={formData.personaId}
                                    onChange={(e) => setFormData({ ...formData, personaId: e.target.value })}
                                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                >
                                    <option value="">Seleccionar persona</option>
                                    {personas.map((p) => (
                                        <option key={p.id} value={p.id}>
                                            {p.nombre}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="mb-4">
                                <label className="block text-gray-700 mb-2">Sala</label>
                                <select
                                    value={formData.salaId}
                                    onChange={(e) => setFormData({ ...formData, salaId: e.target.value })}
                                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    <option value="">Sin sala</option>
                                    {salas.map((s) => (
                                        <option key={s.id} value={s.id}>
                                            {s.nombre}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="mb-4">
                                <label className="block text-gray-700 mb-2">Artículo</label>
                                <select
                                    value={formData.articuloId}
                                    onChange={(e) => setFormData({ ...formData, articuloId: e.target.value })}
                                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    <option value="">Sin artículo</option>
                                    {articulos.map((a) => (
                                        <option key={a.id} value={a.id}>
                                            {a.nombre}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="mb-4">
                                <label className="block text-gray-700 mb-2">Fecha y Hora Inicio *</label>
                                <input
                                    type="datetime-local"
                                    value={formData.fechaHoraInicio}
                                    onChange={(e) => setFormData({ ...formData, fechaHoraInicio: e.target.value })}
                                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>

                            <div className="mb-6">
                                <label className="block text-gray-700 mb-2">Fecha y Hora Fin *</label>
                                <input
                                    type="datetime-local"
                                    value={formData.fechaHoraFin}
                                    onChange={(e) => setFormData({ ...formData, fechaHoraFin: e.target.value })}
                                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>

                            <div className="flex space-x-4">
                                <button
                                    type="submit"
                                    className="flex-1 bg-blue-500 text-white py-2 rounded hover:bg-blue-600 transition"
                                >
                                    {editingReserva ? 'Actualizar' : 'Crear'}
                                </button>
                                <button
                                    type="button"
                                    onClick={cerrarModal}
                                    className="flex-1 bg-gray-500 text-white py-2 rounded hover:bg-gray-600 transition"
                                >
                                    Cancelar
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Reservas;