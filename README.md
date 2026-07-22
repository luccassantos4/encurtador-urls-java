# 🔗 URL Shortener API

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-green)
![Java](https://img.shields.io/badge/Java-21-orange)
![MongoDB](https://img.shields.io/badge/MongoDB-Latest-green)
![License](https://img.shields.io/badge/License-MIT-blue)

**Uma API REST de encurtamento de URLs desenvolvida com Spring Boot e MongoDB**

[Pré-requisitos](#-pré-requisitos) • [Instalação](#-instalação) • [Endpoints](#-endpoints) • [Arquitetura](#-arquitetura) • [Tecnologias](#-tecnologias)

</div>

---

## 🎯 Objetivo do Projeto

Este projeto foi desenvolvido como parte de um desafio prático de backend com o objetivo de demonstrar habilidades em:

- **Desenvolvimento de APIs REST** com Spring Boot
- **Integração com banco de dados NoSQL** (MongoDB)
- **Arquitetura em camadas** seguindo boas práticas de software
- **Tratamento de falhas e resiliência** com fallback para operações sem persistência
- **Docker** para containerização do banco de dados

A aplicação permite encurtar URLs longas em links curtos e redirecioná-los para a URL original, com expiração automática após 1 minuto para demonstrar o uso de TTL (Time-To-Live) no MongoDB.

---

## 🚀 Funcionalidades

- ✅ **Encurtamento de URLs**: Gera um ID único alfanumérico para cada URL
- ✅ **Redirecionamento**: Redireciona URLs curtas para a URL original
- ✅ **Expiração Automática**: URLs expiram automaticamente após 1 minuto
- ✅ **Resiliência**: Funciona mesmo sem acesso ao MongoDB (modo degradado)
- ✅ **Validação de Colisões**: Garante IDs únicos através de verificação no banco
- ✅ **Arquitetura Limpa**: Separação clara entre Controller, Service e Repository
- ✅ **Validação de URLs**: Verifica se a URL fornecida é válida antes de processar
- ✅ **Testes Automatizados**: Cobertura com testes unitários e de integração

---

## 📁 Estrutura do Projeto

```
encurtador-urls-java/
├── src/main/java/tech/luccassantos4/urlshortener/
│   ├── config/              # Configurações do MongoDB
│   ├── controller/          # Camada de apresentação (REST)
│   │   └── dto/            # Data Transfer Objects
│   ├── service/            # Camada de negócio
│   ├── entities/           # Entidades do banco de dados
│   ├── repository/         # Camada de acesso a dados
│   └── UrlshortenerApplication.java
├── docker/                 # Configurações Docker
│   └── docker-compose.yml
├── pom.xml                # Dependências Maven
└── README.md
```

---

## 🏗️ Arquitetura

O projeto segue o padrão **arquitetura em camadas** com separação clara de responsabilidades:

### **Controller Layer** (`UrlController`)
- Responsável por receber requisições HTTP
- Validação de entrada
- Construção de respostas
- **Não contém lógica de negócio**

### **Service Layer** (`UrlService`)
- Contém toda a lógica de negócio
- Geração de IDs únicos
- Tratamento de falhas do banco de dados
- Gerenciamento de expiração
- **Ponto central da lógica da aplicação**

### **Repository Layer** (`UrlRepository`)
- Interface Spring Data MongoDB
- Abstração de acesso ao banco
- Operações CRUD

### **Entity Layer** (`UrlEntity`)
- Representação dos dados no MongoDB
- Configuração de índices e TTL

---

## 🔌 Endpoints

### 1. Encurtar URL

**POST** `/shorten-url`

Encurta uma URL longa e retorna a URL curta gerada.

**Request Body:**
```json
{
  "url": "https://www.exemplo.com/url/muito/longa/que/precisa/ser/encurtada"
}
```

**Response (200 OK):**
```json
{
  "url": "http://localhost:8080/abc123xyz"
}
```

**Comportamento:**
- Gera um ID único de 5-10 caracteres alfanuméricos
- Se o MongoDB não estiver acessível, gera um ID de 8 caracteres sem verificação
- Salva a URL no MongoDB com expiração de 1 minuto
- Retorna a URL completa para redirecionamento
- Valida se a URL fornecida é válida (retorna 400 se inválida)

---

### 2. Redirecionar para URL Original

**GET** `/{id}`

Redireciona para a URL original correspondente ao ID fornecido.

**Parâmetros:**
- `id` (path): ID da URL curta

**Response:**
- **302 Found**: Redireciona para a URL original
- **404 Not Found**: Se o ID não existir ou estiver expirado

**Comportamento:**
- Busca a URL no MongoDB pelo ID
- Se encontrada, redireciona para a URL original
- Se não encontrada ou expirada, retorna 404

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 4.1.0 | Framework para aplicações Java |
| **Spring Data MongoDB** | - | Integração com MongoDB |
| **Spring Web MVC** | - | Suporte a APIs REST |
| **MongoDB** | Latest | Banco de dados NoSQL |
| **Apache Commons Lang3** | 3.19.0 | Utilitários para geração de strings aleatórias |
| **Maven** | - | Gerenciamento de dependências |
| **Docker** | - | Containerização do MongoDB |

---

## 📋 Pré-requisitos

- **Java 21** ou superior instalado
- **Maven** para gerenciamento de dependências
- **Docker** e **Docker Compose** para executar o MongoDB
- **Git** para clonar o repositório

---

## 🚀 Instalação e Execução

### 1. Clone o repositório

```bash
git clone <seu-repositorio>
cd encurtador-urls-java
```

### 2. Inicie o MongoDB com Docker

```bash
cd docker
docker-docker compose up -d
```

Isso iniciará o MongoDB na porta `27017` com as credenciais:
- **Usuário**: `admin`
- **Senha**: `123`
- **Database**: `shortenerdb`

### 3. Execute a aplicação

```bash
# Usando Maven
mvn spring-boot:run

# Ou usando o wrapper Maven
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

### 4. Teste os endpoints

```bash
# Encurtar uma URL
curl -X POST http://localhost:8080/shorten-url \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.google.com"}'

# Redirecionar usando a URL curta
curl -i http://localhost:8080/{id-gerado}
```

---

## 🔧 Configuração

As configurações do MongoDB estão em `src/main/resources/application.properties`:

```properties
spring.application.name=urlshortener
spring.mongodb.auto-index-creation=true
spring.mongodb.uri=mongodb://admin:123@localhost:27017/shortenerdb?authSource=admin
```

---

## 🧪 Testes

O projeto possui uma suite completa de testes automatizados:

### **Testes Unitários** (`UrlServiceTest`)
- Testes de geração de URLs curtas
- Validação de URLs (nulas, vazias, malformadas)
- Tratamento de exceções do banco de dados
- Recuperação de URLs originais

### **Testes de Integração** (`UrlControllerTest`)
- Testes dos endpoints REST
- Validação de respostas HTTP
- Tratamento de erros na camada de controller
- Integração completa com Spring Boot

### **Executar os Testes**

```bash
# Executar todos os testes
mvn test

# Executar apenas testes unitários
mvn test -Dtest=UrlServiceTest

# Executar apenas testes de integração
mvn test -Dtest=UrlControllerTest
```