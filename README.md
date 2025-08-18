# GeoReminder

Aplicativo Android para gerenciamento de lembretes, desenvolvido em Kotlin, utilizando Jetpack Compose para UI, Room para persistência local e Koin para injeção de dependências.

Atualize este README conforme o projeto evoluir (ex.: adicionar capturas de tela, links de release e instruções específicas do seu fluxo).

---

## Sumário
- [Visão geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Stack técnica](#stack-técnica)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Como executar](#como-executar)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Persistência com Room](#persistência-com-room)
- [Injeção de dependência (Koin)](#injeção-de-dependência-koin)
- [Qualidade e build](#qualidade-e-build)
- [Testes](#testes)
- [Troubleshooting (Room/KSP)](#troubleshooting-roomksp)
- [Roadmap](#roadmap)
- [Contribuição](#contribuição)
- [Licença](#licença)

---

## Visão geral
O GeoReminder é um app de lembretes com interface moderna construída em Jetpack Compose e dados persistidos localmente com Room. O foco é oferecer uma experiência simples para criar, listar, editar e excluir lembretes, mantendo uma base sólida e escalável.

Se você está chegando agora, os pontos principais do código:
- Camada de dados com Room (Entity/DAO/Database).
- Injeção de dependências com Koin.
- Fluxos reativos com Kotlin Coroutines/Flow.
- UI declarativa com Jetpack Compose.

---

## Funcionalidades
- Criar, editar e excluir lembretes.
- Listagem reativa dos lembretes armazenados localmente.
- Persistência offline usando Room.
- DI com Koin para modularidade e testabilidade.

Itens que você pode adicionar aqui quando disponíveis:
- Filtros/pesquisa de lembretes.
- Notificações locais.
- Suporte a localização/geofencing.
- Backup/sincronização.

---

## Stack técnica
- Linguagem: Kotlin
- UI: Jetpack Compose
- Persistência: Room (KSP para geração de código)
- Injeção de dependências: Koin
- Concurrency: Kotlin Coroutines + Flow
- Build: Gradle (KTS)
- Min/Target SDK: verifique no arquivo Gradle do módulo app
- JDK: 11 (ou conforme configurado no projeto)

---

## Arquitetura
- Data: entidades, DAOs e database do Room, além de repositórios que expõem APIs reativas (Flow).
- DI: módulos do Koin (AppModule) que disponibilizam Database, DAO e Repository.
- UI: camadas Compose consumindo os fluxos do repositório/ViewModel.

Sugerido (se já não estiver implementado):
- MVVM com ViewModel + State/Events.
- Separação clara entre domínio e dados (caso existam use-cases).

---

## Pré-requisitos
- Android Studio Giraffe/Koala ou superior.
- JDK 11.
- Dispositivo/emulador Android com API compatível com o minSdk do projeto.
- Conexão com a internet apenas para dependências (o app funciona offline após instalado).

---

## Como executar
1) Clonar o repositório:
```bash
git clone https://github.com/<owner>/<repo>.git
cd <repo>
```

2) Abrir no Android Studio e aguardar o sync do Gradle.

3) Rodar a build:
```bash
./gradlew clean assembleDebug
```

4) Executar no emulador ou dispositivo:
- Pelo Android Studio (botão Run) ou
- Via CLI (instalar o apk de debug gerado em app/build/outputs/apk/debug/).

5) Testes instrumentados (se aplicável):
```bash
./gradlew connectedAndroidTest
```

---

## Estrutura do projeto
Caminhos relevantes (ajuste se necessário):

```
app/
  src/main/java/br/com/plussapps/georeminder/
    data/
      ReminderEntity.kt
      ReminderDao.kt
      ReminderDatabase.kt
      ReminderRepository.kt   <- caso exista
    di/
      AppModule.kt
    ui/
      ...                     <- telas/Componentes Compose
```

- ReminderEntity: modelo anotado com @Entity(tableName = "reminders") e @PrimaryKey.
- ReminderDao: consultas e operações (CRUD) com suporte a Flow.
- ReminderDatabase: classe @Database com a lista de entities e versão do schema.
- AppModule: configuração do Room e Koin (database, dao, repository).
- UI: telas/estados/temas usando Compose.

---

## Persistência com Room
- Entity com tipos suportados (String, Long, Double, Float, Boolean, etc.).
- DAO expondo operações e retornos reativos com Flow.
- Database configurada com @Database(entities = [...], version = X, exportSchema = false).

Migração:
- Ao alterar schema, incremente a `version` em ReminderDatabase e adicione uma MIGRATION correspondente.
- Em desenvolvimento, você pode usar `fallbackToDestructiveMigration()` no builder para simplificar (atenção: apaga dados).

---

## Injeção de dependência (Koin)
- Módulos definidos em `di/AppModule.kt`.
- Exponha:
    - Singleton do `ReminderDatabase`.
    - Singleton/Factory do `ReminderDao`.
    - Repositórios.
    - ViewModels (quando existirem).

Inicialização:
- Configure o start do Koin na Application (se aplicável), carregando os módulos do app.

---

## Qualidade e build
- KSP habilitado para Room.
- Compose ativado no Gradle do módulo app.
- Certifique-se de manter todas as dependências do Room na mesma versão (runtime, ktx e compiler).

Comandos úteis:
```bash
# Build Debug
./gradlew assembleDebug

# Lint (se configurado)
./gradlew lint

# Testes unitários
./gradlew testDebugUnitTest
```

---

## Testes
- Unitários: para repositórios e regras de negócio.
- Instrumentados: para DAO/Database (Room) e UI (Compose Testing) se disponível.

Dicas:
- Para testar DAOs, use `Room.inMemoryDatabaseBuilder` em testes instrumentados ou Robolectric para testes locais.
- Use coroutines test e Turbine (ou similar) para testar flows.

---

## Troubleshooting (Room/KSP)
- “Entity class must be annotated…”: verifique @Entity na sua classe e se o módulo do KSP está aplicado.
- “An entity must have at least 1 field annotated with @PrimaryKey”: adicione @PrimaryKey(autoGenerate = true) quando aplicável.
- “Cannot figure out how to save this field…”: use apenas tipos suportados ou forneça TypeConverters.
- “no such table: reminders”: geralmente ocorre quando o schema não foi gerado; confira a Entity, a versão do DB, a lista de entities no @Database e se o KSP rodou sem erros.

Versões:
- Mantenha as dependências do Room alinhadas (runtime/ktx e compiler via KSP na mesma versão).
- Caso use tipos java.time em minSdk baixo, habilite desugaring.

---

## Roadmap
- [ ] Notificações de lembrete.
- [ ] Filtros/pesquisa.
- [ ] Migrações de banco de dados documentadas.
- [ ] Telas/Fluxos adicionais (ex.: detalhes do lembrete).
- [ ] Internacionalização (i18n) e acessibilidade.

---

## Contribuição
Contribuições são bem-vindas!
1) Abra uma issue descrevendo a proposta/bug.
2) Crie um branch a partir da main.
3) Faça commits pequenos e objetivos.
4) Abra um Pull Request adicionando contexto, prints/gifs quando aplicável.

Padrões:
- Siga o estilo do Kotlin e Convenções do Compose.
- Adicione/atualize testes quando alterar comportamento.

---

## Licença
Defina aqui a licença do projeto (ex.: MIT, Apache-2.0). Caso ainda não tenha, considere adicionar um arquivo LICENSE na raiz do repositório.

---

Dúvidas ou sugestões? Abra uma issue ou entre em contato com os mantenedores.