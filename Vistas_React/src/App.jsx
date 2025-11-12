import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import PrivateRoute from './components/PrivateRoute';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Reservas from './pages/Reservas';
import Analytics from './pages/Analytics';
import PredictiveAnalytics from './pages/PredictiveAnalytics';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navbar />
        <main className="min-h-screen bg-gray-50 py-8">
          <div className="container mx-auto px-4">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route
                path="/reservas"
                element={
                  <PrivateRoute>
                    <Reservas />
                  </PrivateRoute>
                }
              />
              <Route
                path="/analytics"
                element={
                  <PrivateRoute>
                    <Analytics />
                  </PrivateRoute>
                }
              />
              <Route
                path="/prediccion"
                element={
                  <PrivateRoute>
                    <PredictiveAnalytics />
                  </PrivateRoute>
                }
              />
              <Route path="*" element={<Navigate to="/" />} />
            </Routes>
          </div>
        </main>
        <Footer />
      </Router>
    </AuthProvider>
  );
}

export default App;