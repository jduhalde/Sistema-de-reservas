from sqlalchemy import Column, Integer, String, DateTime, Float, Text
from database.connection import Base
from datetime import datetime


class ReservaSnapshot(Base):
    """
    Modelo para almacenar snapshots de reservas sincronizadas desde Java
    """
    __tablename__ = "reservas_snapshot"

    id = Column(Integer, primary_key=True, index=True)
    reserva_id = Column(Integer, nullable=False, index=True)
    fecha_hora_inicio = Column(DateTime, nullable=False, index=True)
    fecha_hora_fin = Column(DateTime, nullable=False)
    sala_id = Column(Integer, nullable=True)
    sala_nombre = Column(String(255), nullable=True)
    articulo_id = Column(Integer, nullable=True)
    articulo_nombre = Column(String(255), nullable=True)
    persona_id = Column(Integer, nullable=False)
    synced_at = Column(DateTime, default=datetime.utcnow)

    def __repr__(self):
        return f"<ReservaSnapshot(id={self.id}, reserva_id={self.reserva_id}, fecha={self.fecha_hora_inicio})>"


class Prediction(Base):
    """
    Modelo para almacenar predicciones generadas por Prophet
    """
    __tablename__ = "predictions"

    id = Column(Integer, primary_key=True, index=True)
    fecha = Column(DateTime, nullable=False, index=True)
    prediccion = Column(Float, nullable=False)
    limite_inferior = Column(Float, nullable=False)
    limite_superior = Column(Float, nullable=False)
    modelo_version = Column(String(50), nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    def __repr__(self):
        return f"<Prediction(fecha={self.fecha}, prediccion={self.prediccion})>"
