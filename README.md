# BANTADS 

Este projeto foi construído utilizando uma arquitetura de microsserviços distribuídos gerenciados por contêineres com Docker Compose. 
O sistema implementa o padrão SAGA orquestrada para coordenação de transações distribuídas e utiliza CQRS (Command Query Responsibility Segregation) para separação de responsabilidades de leitura e escrita.


## Como executar o projeto

Para executar este projeto, certifique-se de que o Docker e o Docker Compose estejam instalados e configurados. Em seguida, abra um terminal na raiz do repositório e execute o comando abaixo:

```bash
docker-compose up --build
```


## Microsserviços

A seguir, a lista dos microsserviços que compõe o sistema, e suas portas alocadas:

- **API Gateway**: exposto na porta **3000**; é o único serviço que se comunica com o frontend, atuando como porta de entrada para os demais.
- **Auth Service**: exposto na porta **8080**; acessado para autenticação e autorização de usuários.
- **Service de Clientes**: exposto na porta **8081**; gerencia dados de clientes.
- **Service de Conta**: exposto na porta **8082**; gerencia dados de conta. (atualizar para CQRS)
- **Service de Gerentes**: exposto na porta **8083**; gerencia dados de gerentes.
- **Orchestrator Service (SAGA)**: exposto na porta **8084**; orquestra transações distribuídas utilizando o padrão SAGA.

## Bancos de Dados

O sistema utiliza uma arquitetura de **Schema-per-Service** para garantir o isolamento de dados entre microsserviços, mantendo a independência e autonomia de cada serviço.

### PostgreSQL (Schema-per-Service)
- **postgres-db**: Porta **5432** - Instância única do PostgreSQL que hospeda múltiplos schemas:
  - **Schema `cliente`**: Dados do serviço de clientes 
  - **Schema `contacomando`**: Dados de comandos de contas (CQRS - Write side)
  - **Schema `contaleitura`**: Dados de consultas de contas (CQRS - Read side)
  - **Schema `gerente`**: Dados de voos e aeroportos
  - **Schema `saga`**: Auxiliar ao padrão SAGA

<!-- ### MongoDB
- **auth-db**: Porta **27017** - Banco de dados do serviço de autenticação (credenciais e tokens) -->

<!-- ## Message Broker

- **RabbitMQ**: Porta **5672** (AMQP) e **15672** (Management UI)
  - Interface de gerenciamento disponível em: `http://localhost:15672`
  - Credenciais padrão: usuário `guest`, senha `guest` -->

## Arquitetura

### Padrões Implementados

5. **Schema-per-Service**: Isolamento de dados dos serviços com schemas dedicados para garantir baixo acoplamento
1. **API Gateway**: Ponto de acesso único para os serviços
3. **CQRS**: Separa operações de leitura e escrita para contas
2. **SAGA Orquestrada**: Coordenação das transações que abrangem múltiplos serviços
4. **Event-Driven Architecture**: Comunicação assíncrona via RabbitMQ

### Autenticação e Autorização

O sistema utiliza JWT (JSON Web Tokens) para autenticação. Os tokens são gerados pelo Auth Service e validados pelo API Gateway, que também gerencia autorização baseada em roles (CLIENTE/GERENTE).

### Principais Endpoints

Todos os endpoints devem ser acessados através do API Gateway na porta **3000**:

- `POST /login` - Autenticação de usuários
- `POST /clientes` - Cadastro de clientes (via SAGA)
- `POST /logout` - Logout de usuários

<!-- TODO: completar com futuros endpoints -->