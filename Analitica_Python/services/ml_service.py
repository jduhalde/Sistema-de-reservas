from prophet import Prophet
import pandas as pd
from database.models import ReservaSnapshot, Prediction
from database.connection import SessionLocal
from datetime import datetime, timedelta


class MLService:
    """
    Servicio de Machine Learning para predicciones con Prophet
    """

    @staticmethod
    def train_and_predict(days_to_predict=30):
        """
        Entrena modelo Prophet y genera predicciones

        Args:
            days_to_predict: Número de días a predecir (default: 30)

        Returns:
            dict: Resultado del entrenamiento y predicciones
        """
        db = SessionLocal()
        try:
            # Obtener datos históricos de reservas
            reservas = db.query(ReservaSnapshot).all()

            if len(reservas) == 0:
                return {
                    "success": False,
                    "message": "No hay datos para entrenar el modelo. Sincroniza primero."
                }

            # Preparar datos para Prophet
            data = []
            for reserva in reservas:
                data.append({
                    # Convertir a datetime64
                    'ds': pd.to_datetime(reserva.fecha_hora_inicio.date()),
                    'y': 1  # Cada reserva cuenta como 1 evento
                })

            # Convertir a DataFrame y agrupar por fecha (contar reservas por día)
            df = pd.DataFrame(data)
            df['ds'] = pd.to_datetime(df['ds'])  # Asegurar que sea datetime64
            df = df.groupby('ds').sum().reset_index()

            print(f"📊 Datos históricos: {len(df)} días con reservas")
            print(f"📈 Rango de fechas: {df['ds'].min()} a {df['ds'].max()}")

            # Crear y entrenar modelo Prophet
            model = Prophet(
                daily_seasonality=False,
                weekly_seasonality=True,
                yearly_seasonality=True,
                changepoint_prior_scale=0.05
            )

            model.fit(df)

            # Crear DataFrame de fechas futuras
            future = model.make_future_dataframe(periods=days_to_predict)

            # Generar predicciones
            forecast = model.predict(future)

            # Filtrar solo predicciones futuras - CORREGIDO
            ultimo_dato = df['ds'].max()
            forecast['ds'] = pd.to_datetime(forecast['ds'])
            forecast_future = forecast[forecast['ds'] > ultimo_dato]

            # Limpiar predicciones anteriores
            db.query(Prediction).delete()

            # Guardar predicciones en base de datos
            predicciones_guardadas = 0
            for _, row in forecast_future.iterrows():
                prediction = Prediction(
                    fecha=pd.to_datetime(row['ds']).to_pydatetime(),
                    prediccion=max(0, row['yhat']),
                    limite_inferior=max(0, row['yhat_lower']),
                    limite_superior=max(0, row['yhat_upper']),
                    modelo_version='prophet_v1'
                )
                db.add(prediction)
                predicciones_guardadas += 1

            db.commit()

            # Preparar datos para respuesta
            predictions_list = [
                {
                    "fecha": pd.to_datetime(row['ds']).strftime('%Y-%m-%d'),
                    "prediccion": round(max(0, row['yhat']), 2),
                    "limiteInferior": round(max(0, row['yhat_lower']), 2),
                    "limiteSuperior": round(max(0, row['yhat_upper']), 2)
                }
                for _, row in forecast_future.iterrows()
            ]

            return {
                "success": True,
                "message": f"Modelo entrenado exitosamente. {predicciones_guardadas} predicciones generadas.",
                "datos_entrenamiento": len(df),
                "predicciones": predictions_list
            }

        except Exception as e:
            db.rollback()
            print(f"❌ Error en entrenamiento ML: {str(e)}")
            import traceback
            traceback.print_exc()
            return {
                "success": False,
                "message": f"Error al entrenar modelo: {str(e)}"
            }
        finally:
            db.close()

    @staticmethod
    def get_predictions():
        """
        Obtiene predicciones guardadas en la base de datos

        Returns:
            dict: Predicciones almacenadas
        """
        db = SessionLocal()
        try:
            predictions = db.query(Prediction).order_by(Prediction.fecha).all()

            if len(predictions) == 0:
                return {
                    "success": False,
                    "message": "No hay predicciones disponibles. Entrena el modelo primero.",
                    "predicciones": []
                }

            predictions_list = [
                {
                    "fecha": pred.fecha.strftime('%Y-%m-%d'),
                    "prediccion": round(pred.prediccion, 2),
                    "limiteInferior": round(pred.limite_inferior, 2),
                    "limiteSuperior": round(pred.limite_superior, 2)
                }
                for pred in predictions
            ]

            return {
                "success": True,
                "predicciones": predictions_list
            }

        except Exception as e:
            print(f"❌ Error al obtener predicciones: {str(e)}")
            return {
                "success": False,
                "message": f"Error al obtener predicciones: {str(e)}",
                "predicciones": []
            }
        finally:
            db.close()
