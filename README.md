# Annunciator

Microserviço para envio de mensagens.

## Funcionalidades

- Envio de e-mails.
- API REST para acionamento dos envios.
- Utilização de Redis para gerenciamento de filas ou cache.

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Maven
- Redis
- Docker

## Como Executar

1.  **Subir o ambiente Docker:**

    ```bash
    docker-compose up -d
    ```

2.  **Executar a aplicação Spring Boot:**

    ```bash
    ./mvnw spring-boot:run
    ```
