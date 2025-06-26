spring-rag-ai
=============

Poc for using Spring AI with local and external LLMs by using RAG

### Prerequisites

* Docker >= 28.2.2
* Gradle >= 8.14.2
* JDK 21

### Environment

Add your own `setenv-dev.sh` 

```bash
export SRA_OPENAI_API_KEY=<Create one>
```

### Run

```bash
./gradlew bootRun
```

### Database

Start the project one time this will download the docker container for pgvector:16 then the application will fail :-)
Connect with a db client, choose the sbdocs and execute the schema.sql on it. 

The next time the application will not fail

> Yeah, that needs some improvements



