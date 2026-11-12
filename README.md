# Fórum API

API REST de um fórum de dúvidas, feita com **Spring Boot** e **Kotlin** na disciplina de
Desenvolvimento Web 2. Usuários autenticados criam tópicos ligados a um curso; a listagem é pública,
filtrável, paginada e cacheada; o acesso de escrita depende do papel do usuário.

## Stack

- Kotlin 2.3 sobre Java 25
- Spring Boot 4.1 (Spring MVC, Spring Data JPA, Bean Validation, Spring Security, Spring Cache)
- MySQL + Flyway (schema versionado em `src/main/resources/db/migration`)
- Autenticação com JWT ([jjwt](https://github.com/jwtk/jjwt)) e senhas com BCrypt

## Funcionalidades

- CRUD de tópicos, com validação de entrada e erros no formato `ProblemDetail` (RFC 9457)
- Login com e-mail e senha que devolve um token JWT válido por 1 hora
- Autorização por papel (`ADMIN`, `PROFESSOR`, `ALUNO`), lido do banco a cada requisição
- Listagem com filtro opcional por nome do curso, paginação e ordenação
- Cache da listagem, limpo automaticamente a cada criação, edição ou remoção

## Como rodar

### Pré-requisitos

- **JDK 25.** O Maven não precisa estar instalado: o projeto usa o Maven Wrapper (`./mvnw`), que
  precisa da variável `JAVA_HOME` apontando para o JDK.
- **MySQL** rodando em `localhost:3306`.

### 1. Crie o banco

```bash
mysql -u root -p -e "CREATE DATABASE forum;"
```

Só o banco vazio. As tabelas e os dados iniciais são criados pelo Flyway na primeira inicialização.

### 2. Configure os segredos

A senha do MySQL e a chave do JWT não ficam no repositório. Copie o modelo e preencha:

```bash
cp application-local.properties.example application-local.properties
```

- `spring.datasource.password`: a senha do usuário `root` do seu MySQL (o usuário fica em
  `src/main/resources/application.properties`).
- `jwt.secret`: uma chave aleatória com pelo menos 32 caracteres. Para gerar uma:
  `openssl rand -base64 48`.

O arquivo fica na **raiz do projeto**, ao lado do `pom.xml`, e só é lido com o perfil `local` ativo.

### 3. Suba a aplicação

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

A API fica em `http://localhost:8080`. No Windows, use `mvnw.cmd`. No IntelliJ, defina
*Run → Edit Configurations → Active profiles* como `local`.

Sem o perfil `local`, a aplicação não sobe (`Could not resolve placeholder 'jwt.secret'`). Isso é
proposital: ela se recusa a rodar sem uma chave configurada.

### Testes

```bash
./mvnw test -Dspring.profiles.active=local
```

O teste sobe o contexto completo da aplicação, então também precisa do MySQL rodando.

## Usuários de teste

Criados pelas migrations, todos com a senha `123456`:

| E-mail | Papéis |
|---|---|
| `alan@email.com` | ADMIN, PROFESSOR |
| `maria@email.com` | PROFESSOR, ALUNO |
| `juliana@email.com` | ALUNO |

## Endpoints

| Método | Rota | Acesso | Resposta |
|---|---|---|---|
| `POST` | `/login` | público | `200` com `{"token": "..."}` |
| `GET` | `/topicos` | público | `200` com uma página de tópicos |
| `GET` | `/topicos/{id}` | público | `200`, ou `404` |
| `POST` | `/topicos` | ALUNO, PROFESSOR ou ADMIN | `201` + cabeçalho `Location` |
| `PUT` | `/topicos/{id}` | PROFESSOR ou ADMIN | `200` |
| `DELETE` | `/topicos/{id}` | ADMIN | `204` |
| `GET` | `/hello` | público | texto |
| `GET` | `/hello/saudacao/{nome}` | autenticado | texto |

Rotas protegidas exigem o cabeçalho `Authorization: Bearer <token>`. Sem token, com token inválido
ou expirado, ou com papel insuficiente, a resposta é `403`.

### Parâmetros de `GET /topicos`

| Parâmetro | Padrão | Exemplo |
|---|---|---|
| `nomeCurso` | (sem filtro) | `?nomeCurso=android`: cursos cujo nome contém o texto, sem diferenciar maiúsculas |
| `page` | `0` | `?page=1`: a numeração começa em 0 |
| `size` | `5` | `?size=10` |
| `sort` | `dataCriacao,desc` | `?sort=titulo,asc` |

### Corpo das requisições

```jsonc
// POST /login
{ "email": "alan@email.com", "senha": "123456" }

// POST /topicos  (curso e autor são ids; título com 5 a 100 caracteres, mensagem com 5 a 600)
{ "titulo": "Dúvida sobre Kotlin", "mensagem": "Como funciona o let?", "curso": 1, "autor": 1 }

// PUT /topicos/{id}
{ "titulo": "Título novo", "mensagem": "Mensagem atualizada" }
```

### Exemplo com curl

```bash
# login: guarda o token numa variável
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alan@email.com","senha":"123456"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')

# criar um tópico
curl -i -X POST http://localhost:8080/topicos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Dúvida sobre Kotlin","mensagem":"Como funciona o let?","curso":1,"autor":1}'

# listar com filtro e paginação
curl -i -G http://localhost:8080/topicos --data-urlencode "nomeCurso=android" -d size=10
```

## Estrutura

```
src/main/kotlin/br/com/forum/forumapi/
├── controller/    endpoints HTTP
├── dto/           formatos de entrada e saída
├── service/       regras de negócio, transações e cache
├── repositories/  acesso a dados (Spring Data JPA)
├── model/         entidades JPA
├── exception/     tratamento centralizado de erros
├── config/        configuração do Spring Security
└── security/      geração e validação do token JWT
```

## Limitações conhecidas

- Algumas exceções ainda não têm tratamento próprio e chegam ao cliente como `403` sem corpo: JSON
  malformado ou com campo obrigatório ausente, ordenação por propriedade inexistente
  (`?sort=xyz`) e remoção de um tópico que já tem respostas.
- O cache fica na memória da aplicação, sem limite de tamanho nem expiração.
- O autor de um tópico vem do corpo da requisição, não do usuário autenticado.
