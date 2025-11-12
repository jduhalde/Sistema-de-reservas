from database.models import ReservaSnapshot
from database.connection import SessionLocal
from datetime import datetime


class SyncService:
    """
    Servicio para sincronizar datos de reservas desde Java a PostgreSQL
    """

    @staticmethod
    def sync_reservas(reservas_data):
        """
        Recibe lista de reservas desde Java y las almacena en PostgreSQL

        Args:
            reservas_data: Lista de diccionarios con datos de reservas

        Returns:
            dict: Resultado de la sincronización
        """
        db = SessionLocal()
        try:
            # Limpiar datos anteriores (opcional, puedes comentar esta línea si quieres histórico)
            db.query(ReservaSnapshot).delete()

            registros_insertados = 0

            for reserva in reservas_data:
                # Parsear fechas - manejar diferentes formatos
                fecha_inicio_str = reserva['fechaHoraInicio']
                fecha_fin_str = reserva['fechaHoraFin']

                # Si viene como lista (array de LocalDateTime de Java), convertir a datetime
                if isinstance(fecha_inicio_str, list):
                    # Formato: [2024, 12, 3, 9, 0, 0]
                    fecha_inicio = datetime(
                        fecha_inicio_str[0],  # año
                        fecha_inicio_str[1],  # mes
                        fecha_inicio_str[2],  # día
                        fecha_inicio_str[3] if len(
                            fecha_inicio_str) > 3 else 0,  # hora
                        fecha_inicio_str[4] if len(
                            fecha_inicio_str) > 4 else 0,  # minuto
                        fecha_inicio_str[5] if len(
                            fecha_inicio_str) > 5 else 0   # segundo
                    )
                    fecha_fin = datetime(
                        fecha_fin_str[0],
                        fecha_fin_str[1],
                        fecha_fin_str[2],
                        fecha_fin_str[3] if len(fecha_fin_str) > 3 else 0,
                        fecha_fin_str[4] if len(fecha_fin_str) > 4 else 0,
                        fecha_fin_str[5] if len(fecha_fin_str) > 5 else 0
                    )
                else:
                    # Si viene como string ISO
                    fecha_inicio = datetime.fromisoformat(
                        fecha_inicio_str.replace(
                            'Z', '+00:00') if 'Z' in fecha_inicio_str else fecha_inicio_str
                    )
                    fecha_fin = datetime.fromisoformat(
                        fecha_fin_str.replace(
                            'Z', '+00:00') if 'Z' in fecha_fin_str else fecha_fin_str
                    )

                # Crear snapshot
                snapshot = ReservaSnapshot(
                    reserva_id=reserva['id'],
                    fecha_hora_inicio=fecha_inicio,
                    fecha_hora_fin=fecha_fin,
                    sala_id=reserva.get('sala', {}).get('id'),
                    sala_nombre=reserva.get('sala', {}).get('nombre'),
                    articulo_id=reserva.get('articulo', {}).get(
                        'id') if reserva.get('articulo') else None,
                    articulo_nombre=reserva.get('articulo', {}).get(
                        'nombre') if reserva.get('articulo') else None,
                    persona_id=reserva['persona']['id']
                )

                db.add(snapshot)
                registros_insertados += 1

            db.commit()

            return {
                "success": True,
                "message": "Datos sincronizados correctamente",
                "registros_insertados": registros_insertados
            }

        except Exception as e:
            db.rollback()
            print(f"❌ Error en sincronización: {str(e)}")
            import traceback
            traceback.print_exc()
            return {
                "success": False,
                "message": f"Error al sincronizar: {str(e)}",
                "registros_insertados": 0
            }
        finally:
            db.close()
