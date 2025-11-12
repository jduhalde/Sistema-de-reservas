import { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');

        if (storedToken && storedUser) {
            setToken(storedToken);
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);

    const login = (token, userData) => {
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(userData));
        setToken(token);
        setUser(userData);
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
        // NO usar navigate acá - lo maneja el componente que llama logout
    };

    const isAdmin = () => {
        return user?.role === 'ROLE_ADMIN';
    };

    const hasRole = (role) => {
        return user?.role === role;
    };

    return (
        <AuthContext.Provider value={{
            user,
            token,
            login,
            logout,
            loading,
            isAdmin,
            hasRole
        }}>
            {children}
        </AuthContext.Provider>
    );
};