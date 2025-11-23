CREATE DATABASE bantads;
\connect bantads;

DROP TABLE IF EXISTS "saga"."saga_instance" CASCADE;
DROP TABLE IF EXISTS "contaleitura"."conta" CASCADE;
DROP TABLE IF EXISTS "contacomando"."movimentacoes" CASCADE;
DROP TABLE IF EXISTS "contacomando"."conta" CASCADE;
DROP TABLE IF EXISTS "cliente"."cliente" CASCADE;
DROP TABLE IF EXISTS "cliente"."endereco" CASCADE;
DROP TABLE IF EXISTS "gerente"."gerente" CASCADE;

CREATE SCHEMA IF NOT EXISTS cliente;
CREATE SCHEMA IF NOT EXISTS contacomando;
CREATE SCHEMA IF NOT EXISTS contaleitura;
CREATE SCHEMA IF NOT EXISTS gerente;
CREATE SCHEMA IF NOT EXISTS saga;

DROP TYPE IF EXISTS enum_tipo_movimentacao;
CREATE TYPE enum_tipo_movimentacao AS ENUM ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA'); 


-- GERENTE
CREATE TABLE IF NOT EXISTS "gerente"."gerente" (
  "id" SERIAL PRIMARY KEY,
  "cpf" CHAR(11) UNIQUE NOT NULL,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "nome" VARCHAR(255),
  "telefone" VARCHAR(20)
);

-- Dados Iniciais 
INSERT INTO "gerente"."gerente" (id, cpf, nome, email, telefone) VALUES
(1, '98574307084', 'Geniéve', 'ger1@bantads.com.br', '41999990001'),
(2, '64065268052', 'Godophredo', 'ger2@bantads.com.br', '41999990002'),
(3, '23862179060', 'Gyândula', 'ger3@bantads.com.br', '41999990003'),
(4, '40501740066', 'Adamântio', 'adm1@bantads.com.br', '41999999999');

-- Ajusta a sequência
SELECT setval('gerente.gerente_id_seq', (SELECT MAX(id) FROM gerente.gerente));


--  CLIENTE
CREATE TABLE IF NOT EXISTS "cliente"."endereco" (
  "id" SERIAL PRIMARY KEY,
  "cep" CHAR(8),
  "estado" CHAR(2),
  "cidade" VARCHAR(255),
  "bairro" VARCHAR(255),
  "logradouro" VARCHAR(255),
  "numero" VARCHAR(10),
  "complemento" VARCHAR(255),
);

CREATE TABLE IF NOT EXISTS "cliente"."cliente" (
  "id" SERIAL PRIMARY KEY,
  "cpf" CHAR(11) UNIQUE NOT NULL,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "nome" VARCHAR(255) NOT NULL,
  "salario" DOUBLE PRECISION DEFAULT 0,
  "telefone" VARCHAR(11),
  "endereco_id" INTEGER REFERENCES "cliente"."endereco" ("id")
);

-- Endereços
INSERT INTO "cliente"."endereco" (id, cep, estado, cidade, bairro, logradouro, numero, tipo_logradouro) VALUES
(1, '81520260', 'PR', 'Curitiba', 'Jardim das Américas', 'Dr. Alcides Vieira Arcoverde', '1225', 'Rua'),
(2, '80060000', 'PR', 'Curitiba', 'Centro', 'XV de Novembro', '1299', 'Rua'),
(3, '01310100', 'SP', 'São Paulo', 'Bela Vista', 'Paulista', '1000', 'Avenida'),
(4, '50020000', 'PE', 'Recife', 'Boa Viagem', 'Boa Viagem', '500', 'Avenida'),
(5, '30140071', 'MG', 'Belo Horizonte', 'Savassi', 'Getúlio Vargas', '300', 'Avenida');

-- Dados Iniciais 
INSERT INTO "cliente"."cliente" (id, cpf, nome, email, salario, limite_sugerido, endereco_id, status) VALUES
(1, '12912861012', 'Catharyna', 'cli1@bantads.com.br', 10000.00, 5000.00, 1, 'APROVADO'),
(2, '09506382000', 'Cleuddônio', 'cli2@bantads.com.br', 20000.00, 10000.00, 2, 'APROVADO'),
(3, '85733854057', 'Catianna', 'cli3@bantads.com.br', 3000.00, 1500.00, 3, 'APROVADO'),
(4, '58872160006', 'Cutardo', 'cli4@bantads.com.br', 500.00, 0.00, 4, 'APROVADO'),
(5, '76179646090', 'Coândrya', 'cli5@bantads.com.br', 1500.00, 0.00, 5, 'APROVADO');

-- Ajusta sequências
SELECT setval('cliente.endereco_id_seq', (SELECT MAX(id) FROM cliente.endereco));
SELECT setval('cliente.cliente_id_seq', (SELECT MAX(id) FROM cliente.cliente));


-- CONTA (Comando - Escrita)

CREATE TABLE IF NOT EXISTS "contacomando"."conta" (
  "id" SERIAL PRIMARY KEY,
  "cliente_id" integer UNIQUE NOT NULL,
  "gerente_id" integer NOT NULL,
  "data_criacao" timestamptz NOT NULL DEFAULT NOW(),
  "saldo" NUMERIC(15, 2) DEFAULT 0,
  "limite" NUMERIC(15, 2),
  "numero" VARCHAR(20) UNIQUE,
  "motivo_reprovacao" VARCHAR(255),
  "status" VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS "contacomando"."movimentacoes" (
  "id" SERIAL PRIMARY KEY,
  "data_movimentacao" timestamptz NOT NULL DEFAULT NOW(),
  "tipo" enum_tipo_movimentacao,
  "conta_id" integer NOT NULL REFERENCES "contacomando"."conta" ("id"), 
  "conta_destino" integer REFERENCES "contacomando"."conta" ("id"),
  "valor" DOUBLE PRECISION
);

-- Dados Iniciais 
INSERT INTO "contacomando"."conta" (id, cliente_id, gerente_id, numero, saldo, limite, data_criacao, status) VALUES
(1, 1, 1, '1291', 800.00, 5000.00, '2000-01-01', 'ATIVA'),
(2, 2, 2, '0950', -10000.00, 10000.00, '1990-10-10', 'ATIVA'),
(3, 3, 3, '8573', -1000.00, 1500.00, '2012-12-12', 'ATIVA'), 
(4, 4, 1, '5887', 150000.00, 0.00, '2022-02-22', 'ATIVA'), 
(5, 5, 2, '7617', 1500.00, 0.00, '2025-01-01', 'ATIVA');

-- Dados Iniciais 
INSERT INTO "contacomando"."movimentacoes" (data_movimentacao, tipo, conta_id, conta_destino, valor) VALUES
('2020-01-01 10:00:00', 'DEPOSITO', 1, NULL, 1000.00),
('2020-01-01 11:00:00', 'DEPOSITO', 1, NULL, 900.00),
('2020-01-01 12:00:00', 'SAQUE', 1, NULL, 550.00),
('2020-01-01 13:00:00', 'SAQUE', 1, NULL, 350.00),
('2020-01-10 15:00:00', 'DEPOSITO', 1, NULL, 2000.00),
('2020-01-15 08:00:00', 'SAQUE', 1, NULL, 500.00),
('2020-01-20 12:00:00', 'TRANSFERENCIA', 1, 2, 1700.00), 
('2025-01-01 12:00:00', 'DEPOSITO', 2, NULL, 1000.00),
('2025-01-02 10:00:00', 'DEPOSITO', 2, NULL, 5000.00),
('2025-01-10 10:00:00', 'SAQUE', 2, NULL, 200.00),
('2025-02-05 10:00:00', 'DEPOSITO', 2, NULL, 7000.00),
('2025-05-05 10:00:00', 'DEPOSITO', 3, NULL, 1000.00),
('2025-05-06 10:00:00', 'SAQUE', 3, NULL, 2000.00),
('2025-06-01 10:00:00', 'DEPOSITO', 4, NULL, 150000.00),
('2025-07-01 10:00:00', 'DEPOSITO', 5, NULL, 1500.00);

-- Ajusta sequências
SELECT setval('contacomando.conta_id_seq', (SELECT MAX(id) FROM contacomando.conta));
SELECT setval('contacomando.movimentacoes_id_seq', (SELECT MAX(id) FROM contacomando.movimentacoes));


-- CONTA (Leitura - Read Model do CQRS)

CREATE TABLE IF NOT EXISTS "contaleitura"."conta" (
  "id" SERIAL PRIMARY KEY,
  "id_conta_comando" BIGINT UNIQUE, 
  "cliente_id" integer,
  "gerente_id" integer,
  "numero" VARCHAR(20),
  "data_criacao" timestamptz,
  "saldo" NUMERIC(15, 2),
  "limite" NUMERIC(15, 2),
  "status" VARCHAR(50),
  "motivo_reprovacao" TEXT
);

INSERT INTO "contaleitura"."conta" (id, id_conta_comando, cliente_id, gerente_id, numero, data_criacao, saldo, limite, status)
SELECT id, id, cliente_id, gerente_id, numero, data_criacao, saldo, limite, status
FROM contacomando.conta;

SELECT setval('contaleitura.conta_id_seq', (SELECT MAX(id) FROM contaleitura.conta));


--SAGA (Orquestração)

CREATE TABLE IF NOT EXISTS "saga"."saga_instance" (
    "id" UUID PRIMARY KEY,
    "correlation_id" UUID NOT NULL UNIQUE,
    "saga_type" VARCHAR(255) NOT NULL,
    "current_state" VARCHAR(50) NOT NULL,
    "payload" TEXT,
    "created_at" TIMESTAMPTZ NOT NULL,
    "updated_at" TIMESTAMPTZ NOT NULL
);