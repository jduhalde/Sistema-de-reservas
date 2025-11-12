import { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const AdminRoute = ({ children }) => {
    const { user, loading, isAdmin } = useContext(AuthContext);

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="text-xl">Cargando...</div>
            </div>
        );
    }

    if (!user) {
        return <Navigate to="/login" />;
    }

    if (!isAdmin()) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-100">
                <div className="bg-white p-8 rounded-lg shadow-md max-w-md">
                    <h2 className="text-2xl font-bold text-red-600 mb-4">Acceso Denegado</h2>
                    <p className="text-gray-700 mb-4">
                        No tenés permisos para acceder a esta página. Esta función es exclusiva para administradores.
                    </p>
                    <Link
                        to="/reservas"
                        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 inline-block"
                    >
                        Volver a Reservas
                    </Link>
                </div>
            </div>
        );
    }

    return children;
};

export default AdminRoute;