# Usa una imagen oficial de Python ligera
FROM python:3.11-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de requerimientos primero (para aprovechar caché de Docker)
COPY requirements.txt .

# Instala las dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Copia el resto del código del agente
COPY . .

# Expone el puerto que usa FastAPI internamente
EXPOSE 8000

# Comando para iniciar el servidor cuando el contenedor arranque
CMD ["uvicorn", "app.api:app", "--host", "0.0.0.0", "--port", "8000"]
