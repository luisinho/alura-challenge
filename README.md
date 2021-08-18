# alura-flix

[![NPM](https://img.shields.io/npm/l/react)](https://github.com/luisinho/alura-challenge/blob/main/LICENSE)

# Sobre o projeto

AluraFlix é uma aplicação back-end construída durante o evento **Alura-challenge** evento organizado pela Alura.

A aplicação consiste em um banco de vídeos, categorias e usuários, os quais podem ser criados, alterados, editados e listados pelos usuários que estejam logados na aplicação.Na aplicação existe uma api que os usuários podem consultar uma lista de vídeos com categoria livre do qual não há necessidade de estarem logados.

# Link Heroku
https://luisinho-alura-challenge.herokuapp.com/

# Consumindo API no Postman

## Criar usuário
![Criar usuario](https://github.com/luisinho/assets-projects/blob/main/movieflix/criar-usuario.png?raw=true)

## Basic auth da aplicação
![Basic auth](https://github.com/luisinho/assets-projects/blob/main/movieflix/basic_auth_app.png?raw=true)

## Login do usuário
![Login usuario](https://github.com/luisinho/assets-projects/blob/main/movieflix/login-usuario.png?raw=true)

# Tecnologias utilizadas
- Java
- Spring Boot
- Spring security JWT
- Spring security Oauth2
- JPA / Hibernate
- Maven

## Implantação em produção
- Back end: Heroku
- Banco de dados: Postgresql

## Back end
Pré-requisitos: Java 11

```bash
# clonar repositório
git clone https://github.com/luisinho/alura-challenge.git

# entrar na pasta do projeto back end
cd backend

# executar o projeto
./mvnw spring-boot:run
```
# Autor

Luis Antonio Batista dos Santos

https://www.linkedin.com/in/luis-antonio-batista-dos-santos-5a37b781

aluraflix
