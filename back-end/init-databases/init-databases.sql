CREATE DATABASE cliente;
CREATE DATABASE conta;
CREATE DATABASE gerente;
CREATE DATABASE saga;

CREATE DATABASE bantads;
\connect bantads;

-- SCHEMA PER SERVICE - Criar apenas um banco mas separar por schemas 
-- Um schema para cada serviço
CREATE SCHEMA IF NOT EXISTS cliente;
CREATE SCHEMA IF NOT EXISTS contacomando;
CREATE SCHEMA IF NOT EXISTS contaleitura;
CREATE SCHEMA IF NOT EXISTS gerente;
CREATE SCHEMA IF NOT EXISTS saga;

-- tipos já definidos
CREATE TYPE enum_tipo_movimentacao AS ENUM ('depósito', 'saque', 'transferência');

-- tabela de endereços
CREATE TABLE IF NOT EXISTS "bantads"."cliente"."endereco" (
  "id" SERIAL PRIMARY KEY,
  "cep" CHAR(8),
  "estado" CHAR(2),
  "cidade" VARCHAR(255),
  "bairro" VARCHAR(255),
  "logradouro" VARCHAR(255),
  "numero" VARCHAR(10),
  "complement" VARCHAR(255)
);

-- tabela de clientes
CREATE TABLE IF NOT EXISTS "bantads"."cliente"."cliente" (
  "id" SERIAL PRIMARY KEY,
  "cpf" CHAR(11) UNIQUE NOT NULL,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "nome" VARCHAR(255) NOT NULL,
  "salario" DOUBLE PRECISION DEFAULT 0,
  "telefone" VARCHAR(11),
  "endereco_id" INTEGER REFERENCES "bantads"."cliente"."endereco" ("id")
);

-- população inicial de endereços dos clientes
INSERT INTO "bantads"."cliente"."endereco" (cep, estado, cidade, bairro, logradouro, numero) VALUES
('81520260', 'PR', 'Curitiba', 'Jardim das Américas', 'R. Dr. Alcides Vieira Arcoverde', '1225'),
('80060000', 'PR', 'Curitiba', 'Centro', 'R. XV de Novembro', '1299');

-- população inicial de clientes
INSERT INTO "bantads"."cliente"."cliente" (cpf, nome, email, salario, endereco_id) VALUES
('12912861012', 'Catharyna', 'cli1@bantads.com.br', 10000, 1),
('09506382000', 'Cleuddônio', 'cli2@bantads.com.br', 20000, 1),
('85733854057', 'Catianna', 'cli3@bantads.com.br', 3000, 2),
('58872160006', 'Cutardo', 'cli4@bantads.com.br', 500, 2),
('76179646090', 'Coândrya', 'cli5@bantads.com.br', 1500, 1)
ON CONFLICT (cpf) DO NOTHING;

-- tabela de conta, no lado de comandos
CREATE TABLE IF NOT EXISTS "bantads"."contacomando"."conta" (
  "id" SERIAL PRIMARY KEY,
  "cliente_id" integer UNIQUE NOT NULL,
  "gerente_id" integer NOT NULL,
  "data_criacao" timestamptz NOT NULL DEFAULT NOW(),
  "saldo" DOUBLE PRECISION DEFAULT 0,
  "limite" integer
);

-- tabela de movimentações, no lado de comandos
CREATE TABLE IF NOT EXISTS "bantads"."contacomando"."movimentacoes" (
  "id" SERIAL PRIMARY KEY,
  "data_movimentacao" timestamptz NOT NULL DEFAULT NOW(),
  "tipo" enum_tipo_movimentacao,
  "conta_id" integer NOT NULL REFERENCES "bantads"."contacomando"."conta" ("id"), 
  "conta_destino" integer REFERENCES "bantads"."contacomando"."conta" ("id"),
  "valor" DOUBLE PRECISION
);

-- tabela de conta e movimentações, no lado de leitura - DESNORMALIZADA
CREATE TABLE IF NOT EXISTS "bantads"."contaleitura"."conta" (
  "id" integer PRIMARY KEY,
  "cliente_id" integer NOT NULL,
  "gerente_id" integer NOT NULL,
  "data_criacao" timestamptz,
  "saldo" DOUBLE PRECISION DEFAULT 0,
  "limite" integer,
  "data_movimentacao" timestamptz,
  "tipo" varchar(10),
  "conta_destino" integer,
  "valor" DOUBLE PRECISION
);

-- tabela de gerentes
CREATE TABLE IF NOT EXISTS "bantads"."gerente"."gerente" (
  "id" SERIAL PRIMARY KEY,
  "cpf" CHAR(11) UNIQUE NOT NULL,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "nome" VARCHAR(255),
  "telefone" VARCHAR(11)
);

-- população inicial de gerentes
INSERT INTO "bantads"."gerente"."gerente" (cpf, nome, email) VALUES
('98574307084', 'Geniéve', 'ger1@bantads.com.br'),
('64065268052', 'Godophredo', 'ger2@bantads.com.br'),
('23862179060', 'Gyândula', 'ger3@bantads.com.br'),
('40501740066', 'Adamântio', 'adm1@bantads.com.br')
ON CONFLICT (cpf) DO NOTHING;

-- tabela para salvar o estado do SAGA
CREATE TABLE IF NOT EXISTS "bantads"."saga"."saga_instance" (
    "id" UUID PRIMARY KEY,
    "correlation_id" UUID NOT NULL UNIQUE,
    "saga_type" VARCHAR(255) NOT NULL,
    "current_state" VARCHAR(50) NOT NULL,
    "payload" TEXT,
    "created_at" TIMESTAMPTZ NOT NULL,
    "updated_at" TIMESTAMPTZ NOT NULL
);
