from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import os
from dotenv import load_dotenv

# Cargar variables de entorno
load_dotenv()

# Obtener URL de base de datos
DATABASE_URL = os.getenv("DATABASE_URL")

# Crear engine de SQLAlchemy
engine = create_engine(DATABASE_URL, echo=True)

# Crear sesión
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Base para modelos
Base = declarative_base()


def get_db():
    """
    Generador de sesión de base de datos
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def init_db():
    """
    Inicializa las tablas en la base de datos
    """
    from database.models import ReservaSnapshot, Prediction
    Base.metadata.create_all(bind=engine)
    print("✅ Tablas creadas exitosamente en PostgreSQL")
