import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ProductCard from '../components/ProductCard';
import Button from '../components/Button';
import { productosData } from '../data/productos';

function Products() {
    const [filtro, setFiltro] = useState('todos');
    const [busqueda, setBusqueda] = useState('');
    const [ordenamiento, setOrdenamiento] = useState('nombre');
    const navigate = useNavigate();

    useEffect(() => {
        document.title = "Comida al Paso - Productos";
        console.log("Página de productos cargada");
    }, []);

    // useEffect para log cuando cambian los filtros
    useEffect(() => {
        console.log(`Filtros aplicados - Categoría: ${filtro}, Búsqueda: "${busqueda}", Orden: ${ordenamiento}`);
    }, [filtro, busqueda, ordenamiento]);

    const categorias = ['todos', ...new Set(productosData.map(p => p.categoria))];
    
    // Filtrar productos
    let productosFiltrados = productosData.filter(producto => {
        const coincideFiltro = filtro === 'todos' || producto.categoria === filtro;
        const coincideBusqueda = producto.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
                                producto.descripcion.toLowerCase().includes(busqueda.toLowerCase());
        return coincideFiltro && coincideBusqueda;
    });

    // Ordenar productos
    productosFiltrados.sort((a, b) => {
        switch (ordenamiento) {
            case 'precio-asc':
                return a.precio - b.precio;
            case 'precio-desc':
                return b.precio - a.precio;
            case 'nombre':
            default:
                return a.nombre.localeCompare(b.nombre);
        }
    });

    const navegarAProducto = (id) => {
        navigate(`/productos/${id}`);
    };

    const limpiarFiltros = () => {
        setFiltro('todos');
        setBusqueda('');
        setOrdenamiento('nombre');
    };

    const manejarCambioBusqueda = (e) => {
        setBusqueda(e.target.value);
    };

    const manejarCambioFiltro = (e) => {
        setFiltro(e.target.value);
    };

    const manejarCambioOrden = (e) => {
        setOrdenamiento(e.target.value);
    };

    return (
        <div>
            <h1 className="text-3xl font-bold text-center mb-8 text-gray-800">
                Nuestros Productos
            </h1>
            
            {/* Controles de filtrado */}
            <div className="bg-white p-6 rounded-lg shadow-md mb-8">
                <div className="grid md:grid-cols-4 gap-4 items-end">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Buscar:
                        </label>
                        <input
                            type="text"
                            placeholder="Buscar productos..."
                            value={busqueda}
                            onChange={manejarCambioBusqueda}
                            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-300"
                        />
                    </div>
                    
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Categoría:
                        </label>
                        <select 
                            value={filtro} 
                            onChange={manejarCambioFiltro}
                            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-300"
                        >
                            {categorias.map(categoria => (
                                <option key={categoria} value={categoria}>
                                    {categoria === 'todos' ? 'Todas las categorías' : 
                                     categoria.charAt(0).toUpperCase() + categoria.slice(1)}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Ordenar por:
                        </label>
                        <select 
                            value={ordenamiento} 
                            onChange={manejarCambioOrden}
                            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-300"
                        >
                            <option value="nombre">Nombre (A-Z)</option>
                            <option value="precio-asc">Precio (menor a mayor)</option>
                            <option value="precio-desc">Precio (mayor a menor)</option>
                        </select>
                    </div>

                    <div>
                        <Button 
                            variant="secondary" 
                            onClick={limpiarFiltros}
                            className="w-full"
                        >
                            Limpiar filtros
                        </Button>
                    </div>
                </div>
            </div>

            {/* Resultados */}
            <div className="mb-4">
                <p className="text-gray-600">
                    Mostrando {productosFiltrados.length} de {productosData.length} productos
                </p>
            </div>

            {/* Grid de productos */}
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                {productosFiltrados.map(producto => (
                    <ProductCard 
                        key={producto.id} 
                        producto={producto}
                        onClick={navegarAProducto}
                    />
                ))}
            </div>

            {productosFiltrados.length === 0 && (
                <div className="text-center py-12">
                    <div className="text-6xl mb-4">🔍</div>
                    <p className="text-gray-500 text-xl mb-4">No se encontraron productos</p>
                    <Button onClick={limpiarFiltros}>
                        Limpiar filtros
                    </Button>
                </div>
            )}
        </div>
    );
}

export default Products;