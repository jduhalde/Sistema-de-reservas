import { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Home = () => {
    const { user } = useContext(AuthContext);

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100">
            <div className="container mx-auto px-4 py-16">
                <div className="text-center mb-12">
                    <h1 className="text-5xl font-bold text-gray-800 mb-4">
                        Sistema de Gestión de Reservas
                    </h1>
                    <p className="text-xl text-gray-600 mb-8">
                        Gestiona tus reservas de salas y artículos de forma eficiente
                    </p>

                    {user ? (
                        <Link
                            to="/reservas"
                            className="bg-blue-600 text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-blue-700 transition inline-block"
                        >
                            Ir a Reservas
                        </Link>
                    ) : (
                        <div className="space-x-4">
                            <Link
                                to="/login"
                                className="bg-blue-600 text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-blue-700 transition inline-block"
                            >
                                Iniciar Sesión
                            </Link>
                            <Link
                                to="/register"
                                className="bg-green-600 text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-green-700 transition inline-block"
                            >
                                Registrarse
                            </Link>
                        </div>
                    )}
                </div>

                <div className="grid md:grid-cols-3 gap-8 mt-16">
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <h3 className="text-xl font-bold mb-3 text-blue-600">📅 Reservas</h3>
                        <p className="text-gray-600">
                            Crea y gestiona reservas de salas y artículos de manera sencilla
                        </p>
                    </div>

                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <h3 className="text-xl font-bold mb-3 text-blue-600">📊 Estadísticas</h3>
                        <p className="text-gray-600">
                            Visualiza estadísticas de uso de recursos en tiempo real
                        </p>
                    </div>

                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <h3 className="text-xl font-bold mb-3 text-blue-600">🔐 Seguridad</h3>
                        <p className="text-gray-600">
                            Sistema seguro con autenticación JWT y roles de usuario
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Home;
