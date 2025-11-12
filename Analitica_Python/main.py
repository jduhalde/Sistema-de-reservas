from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import requests
from database.connection import init_db
from services.sync_service import SyncService
from services.ml_service import MLService

app = FastAPI()

# Configuración de CORS para permitir peticiones desde React
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.on_event("startup")
async def startup_event():
    """
    Inicializa la base de datos al arrancar la aplicación
    """
    print("🚀 Iniciando servicio de analítica...")
    init_db()
    print("✅ Base de datos PostgreSQL inicializada")


@app.get("/")
def read_root():
    return {"message": "Servicio de Analítica funcionando correctamente"}


@app.get("/api/analytics/summary")
def get_analytics_summary():
    """
    ✅ ENDPOINT ORIGINAL - MANTENER INTACTO
    Endpoint que consulta las reservas del servicio Java
    y devuelve estadísticas procesadas
    """
    try:
        # URL del servicio Java (desde contenedor Docker)
        java_api_url = "http://host.docker.internal:8080/api/reservas"

        print(f"Consultando la API de Java en: {java_api_url}")

        # Hacer petición GET al servicio Java
        response = requests.get(java_api_url)
        response.raise_for_status()

        reservas = response.json()

        print(f">>> Datos recibidos de la API de Java:")
        print(reservas)

        # Procesar estadísticas
        total_reservas = len(reservas)

        # Contar reservas por sala
        reservas_por_sala = {}
        for reserva in reservas:
            if reserva.get('sala'):  # Verificar que la sala no sea None
                nombre_sala = reserva['sala']['nombre']
                reservas_por_sala[nombre_sala] = reservas_por_sala.get(
                    nombre_sala, 0) + 1

        # Contar reservas por artículo
        reservas_por_articulo = {}
        for reserva in reservas:
            if reserva.get('articulo'):  # Solo contar si tiene artículo (puede ser None)
                nombre_articulo = reserva['articulo']['nombre']
                reservas_por_articulo[nombre_articulo] = reservas_por_articulo.get(
                    nombre_articulo, 0) + 1

        # Devolver resultado en camelCase (como espera React)
        return {
            "totalReservas": total_reservas,
            "reservasPorSala": reservas_por_sala,
            "reservasPorArticulo": reservas_por_articulo
        }

    except requests.exceptions.RequestException as e:
        print(f"Error al conectar con la API de Java: {e}")
        return {
            "error": "No se pudo conectar con el servicio de reservas",
            "details": str(e)
        }
    except Exception as e:
        print(f"Error inesperado: {e}")
        return {
            "error": "Error al procesar estadísticas",
            "details": str(e)
        }


@app.post("/api/analytics/sync")
def sync_reservas(sync_request: dict):
    """
    🆕 NUEVO ENDPOINT
    Recibe datos de reservas desde Java y los almacena en PostgreSQL
    """
    try:
        reservas_data = sync_request.get('reservas', [])

        if not reservas_data:
            return {
                "success": False,
                "message": "No se recibieron datos de reservas"
            }

        result = SyncService.sync_reservas(reservas_data)
        return result

    except Exception as e:
        print(f"❌ Error en endpoint /sync: {str(e)}")
        return {
            "success": False,
            "message": f"Error al sincronizar: {str(e)}"
        }


@app.post("/api/analytics/train")
def train_model():
    """
    🆕 NUEVO ENDPOINT
    Entrena el modelo Prophet y genera predicciones
    """
    try:
        result = MLService.train_and_predict(days_to_predict=30)
        return result

    except Exception as e:
        print(f"❌ Error en endpoint /train: {str(e)}")
        return {
            "success": False,
            "message": f"Error al entrenar modelo: {str(e)}"
        }


@app.get("/api/analytics/predictions")
def get_predictions():
    """
    🆕 NUEVO ENDPOINT
    Obtiene las predicciones almacenadas en la base de datos
    """
    try:
        result = MLService.get_predictions()
        return result

    except Exception as e:
        print(f"❌ Error en endpoint /predictions: {str(e)}")
        return {
            "success": False,
            "message": f"Error al obtener predicciones: {str(e)}",
            "predicciones": []
        }
