import Card from './Card'

function ProductCard({ producto, onClick }) {
    const handleClick = () => {
        if (onClick && producto.id) {
            onClick(producto.id);
        }
    };

    return (
        <Card 
            className="hover:shadow-lg transition-shadow cursor-pointer transform hover:scale-105" 
            onClick={handleClick}
        >
            <div className="p-6">
                <div className="text-6xl text-center mb-4">{producto.imagen}</div>
                <h3 className="text-xl font-semibold mb-2 text-gray-800">{producto.nombre}</h3>
                <p className="text-gray-600 mb-3 text-sm">{producto.descripcion}</p>
                <div className="flex justify-between items-center">
                    <span className="text-2xl font-bold text-orange-500">
                        ${producto.precio}
                    </span>
                    <span className={`px-2 py-1 rounded text-sm font-medium ${
                        producto.disponible 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                    }`}>
                        {producto.disponible ? 'Disponible' : 'Agotado'}
                    </span>
                </div>
            </div>
        </Card>
    );
}

export default ProductCard;