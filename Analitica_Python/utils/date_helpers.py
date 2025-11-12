from datetime import datetime


def parse_iso_date(date_string):
    """
    Parsea una fecha en formato ISO a datetime

    Args:
        date_string: String con fecha en formato ISO

    Returns:
        datetime: Objeto datetime parseado
    """
    try:
        # Manejar formato con 'Z' (UTC)
        if date_string.endswith('Z'):
            date_string = date_string.replace('Z', '+00:00')

        return datetime.fromisoformat(date_string)
    except Exception as e:
        print(f"Error parseando fecha {date_string}: {e}")
        return None


def format_date_for_response(date_obj):
    """
    Formatea un objeto datetime para respuesta JSON

    Args:
        date_obj: Objeto datetime

    Returns:
        str: Fecha formateada en string
    """
    if isinstance(date_obj, datetime):
        return date_obj.strftime('%Y-%m-%d')
    return str(date_obj)
