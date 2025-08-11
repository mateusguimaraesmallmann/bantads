FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN rm -f .env .env.* || true
EXPOSE 3000
CMD ["node", "api-gateway.js"]