import { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout, isAdmin } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="bg-blue-600 text-white p-4">
            <div className="container mx-auto flex justify-between items-center">
                <Link to="/" className="text-xl font-bold">
                    Sistema de Reservas
                </Link>

                <div className="flex items-center space-x-4">
                    {user ? (
                        <>
                            <span className="px-3 py-1 bg-blue-800 rounded-full text-sm">
                                {user.email}
                                {isAdmin() && (
                                    <span className="ml-2 px-2 py-1 bg-yellow-500 text-black rounded-full text-xs font-bold">
                                        ADMIN
                                    </span>
                                )}
                            </span>

                            <Link to="/reservas" className="hover:underline">
                                Reservas
                            </Link>

                            <Link to="/analytics" className="hover:underline">
                                Estadísticas
                            </Link>

                            {isAdmin() && (
                                <Link to="/prediccion" className="hover:underline bg-yellow-500 text-black px-3 py-1 rounded">
                                    Análisis Predictivo
                                </Link>
                            )}

                            <button
                                onClick={handleLogout}
                                className="bg-red-500 hover:bg-red-600 px-4 py-2 rounded transition"
                            >
                                Cerrar Sesión
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="hover:underline">
                                Iniciar Sesión
                            </Link>
                            <Link to="/register" className="hover:underline">
                                Registrarse
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;