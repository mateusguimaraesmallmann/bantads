#!/bin/bash

echo "🚀 Iniciando build completo da aplicação BANTADS..."

# Parar e remover containers
echo "🧹 Parando containers e removendo volumes antigos..."

# Se quiser remover:
docker-compose -f bantads-compose.yaml down

# Build do front-end
#echo "🎨 Realizando build do front-end..."
#cd ./front-end
#npm install
#npm run build
#cd ..

# Subir containers com build forçado
echo "🐳 Subindo os containers com Docker Compose..."
docker-compose -f bantads-compose.yaml up --build -d

echo "✅ Aplicação BANTADS iniciada com sucesso!"