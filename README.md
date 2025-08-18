# GeoReminder

Aplicativo Android de lembretes por localização (geofencing) desenvolvido em Kotlin, com UI em Jetpack Compose, persistência local com Room e injeção de dependências via Koin. Integra Google Maps e Places API para seleção de local, e usa notificações locais quando o usuário entra na área do lembrete.

---

## Sumário
- [Visão geral](#visão-geral)
- [Funcionalidades já implementadas](#funcionalidades-já-implementadas)
- [Arquitetura e camadas](#arquitetura-e-camadas)
- [Modelos e persistência (Room)](#modelos-e-persistência-room)
- [Geofencing e notificações](#geofencing-e-notificações)
- [UI (Jetpack Compose)](#ui-jetpack-compose)
- [DI (Koin)](#di-koin)
- [Configuração (Maps/Places API Key)](#configuração-mapsplaces-api-key)
- [Permissões Android](#permissões-android)
- [Como executar](#como-executar)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Roadmap / Próximos passos](#roadmap--próximos-passos)
- [Licença](#licença)

---

## Visão geral
O GeoReminder permite criar lembretes associados a um local no mapa. Você busca um lugar (Places API), ajusta o raio, e salva. Os dados ficam salvos em Room. Há infraestrutura pronta para geofencing (Manager + BroadcastReceiver) que dispara uma notificação quando o usuário entra na região configurada.

---

## Funcionalidades já implementadas
- Criação de lembrete com:
  - Título e descrição
  - Busca de local (Google Places Search by Text)
  - Seleção do ponto no mapa com marker arrastável
  - Ajuste do raio de ativação (em metros)
- Persistência local com Room (Entity/DAO/Database), repositório e mapeadores para domínio
- Use cases de domínio (criar, consultar, atualizar, etc.)
- Injeção de dependências com Koin
- Inicialização do Google Places na Application
- Infra de geofencing (GeofenceManager, BroadcastReceiver)
- Notificações locais (NotificationUtils)

Status: a integração "registrar geofence ao salvar" ainda não está conectada no fluxo do formulário. Ver Roadmap.

---

## Arquitetura e camadas
- Domain
  - Models: `Reminder`, `Location`
  - Repository (interface): `ReminderRepository`
  - Use cases: `CreateReminderUseCase`, `GetRemindersUseCase`, `GetReminderByIdUseCase`, `GetRemindersForDateUseCase`, `GetActiveRemindersUseCase`, `UpdateReminderUseCase`, `DeleteReminderUseCase`, `ActivateReminderUseCase`, `CompleteReminderUseCase`, `CheckProximityUseCase`
- Data
  - Room: `ReminderEntity`, `ReminderDao`, `ReminderDatabase`
  - Repositório (implementação): `ReminderRepositoryImpl`
  - Mapeadores: `ReminderMappers` (`toDomain`/`toEntity`)
- UI
  - ViewModel: `ReminderFormViewModel`
  - Compose: `ReminderFormScreen`, `LocationSearchBar`, `LocationPickerMap`, `RadiusSlider`
- Infra
  - Geofencing: `GeofenceManager`, `GeofenceBroadcastReceiver`
  - Notificações: `NotificationUtils`
  - App/Bootstrap: `GeoReminderApp`, `MainActivity`
  - Serviço (stub): `LocationForegroundService`

---

## Modelos e persistência (Room)
Modelos de domínio:
- `Reminder(id, title, description?, location?, dateTime?, isActive, isCompleted)`
- `Location(latitude, longitude, name?, radius)`

Camada de dados (Room):
- `ReminderEntity` mapeia os campos para a tabela `reminders` (latitude/longitude/radius opcionais, `triggerTime` em epoch millis)
- `ReminderDao` com CRUD e queries observáveis (Flow) e snapshot
- `ReminderDatabase` versão 1; exemplo de `MIGRATION_1_2` incluído
- Mapeadores `ReminderMappers.kt` convertem entre Entity e Domain (inclui conversão para `LocalDateTime` quando disponível)

Repositório:
- `ReminderRepository` (domínio) e `ReminderRepositoryImpl` (dados) encapsulam Room e regras simples

---

## Geofencing e notificações
- `GeofenceManager`
  - Cria e registra/remover geofences via `GeofencingClient`
  - Usa `PendingIntent` para acionar `GeofenceBroadcastReceiver`
- `GeofenceBroadcastReceiver`
  - Recebe eventos de transição (ENTER) e dispara notificação
  - Atualmente exibe uma notificação com "Lembrete ID: <requestId>"
- `NotificationUtils`
  - Criação de canal (Android O+) e exibição de notificações locais

Integração recomendada (pendente): após salvar um lembrete, registrar o geofence usando o ID gerado e os dados de localização/raio. Opcionalmente, no `BroadcastReceiver`, buscar o lembrete no Room pelo ID para mostrar título/descrição na notificação.

---

## UI (Jetpack Compose)
- `ReminderFormScreen`
  - Campos de título/descrição
  - `LocationSearchBar`: busca lugares (debounced, até 5 resultados) via Places
  - `LocationPickerMap`: Google Map com `Marker` arrastável e `Circle` do raio
  - `RadiusSlider`: controle do raio (padrão 50–1000m)
  - Botão de salvar habilita quando há título e localização
- `ReminderFormViewModel`
  - Obtém última localização (quando permitido) para inicializar o mapa
  - Mantém `ReminderFormUiState` (title, description, LatLng, radius, permission flag)
  - Usa `CreateReminderUseCase` para persistir

---

## DI (Koin)
- `AppModule.kt`
  - Singleton do `ReminderDatabase` e `ReminderDao`
  - `ReminderRepositoryImpl` como implementação de `ReminderRepository`
  - `CreateReminderUseCase`
  - `ReminderFormViewModel`
- `GeoReminderApp`
  - Inicializa Places e Koin no `onCreate`

---

## Configuração (Maps/Places API Key)
O projeto usa Google Maps e Places. É necessário definir a API Key uma única vez e propagá-la para Manifest e BuildConfig.

Passos sugeridos (Gradle Kotlin DSL):
1. Crie/edite `local.properties` (não commitar) adicionando:
```properties
MAPS_API_KEY=YOUR_REAL_KEY
```
2. No `app/build.gradle.kts`, adicione:
```kotlin
android {
    defaultConfig {
        val mapsKey = project.findProperty("MAPS_API_KEY") as String? ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsKey
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsKey\"")
    }
}
```
3. O Manifest já referencia `${MAPS_API_KEY}` e a `Application` inicializa o Places com `BuildConfig.MAPS_API_KEY`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```

---

## Permissões Android
Declaradas no Manifest:
- `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `ACCESS_BACKGROUND_LOCATION`
- `POST_NOTIFICATIONS` (Android 13+)
- `INTERNET`

Em tempo de execução:
- `MainActivity` solicita `ACCESS_FINE_LOCATION`
- Recomendações:
  - Solicitar `POST_NOTIFICATIONS` no Android 13+
  - Explicar/solicitar `ACCESS_BACKGROUND_LOCATION` quando habilitar geofencing efetivo em background

---

## Como executar
1) Clonar o repositório:
```bash
git clone https://github.com/lucas-santana-dev/GeoReminder.git
cd GeoReminder
```
2) Configure a API Key conforme seção acima e faça o sync do Gradle no Android Studio.
3) Build e execução:
```bash
./gradlew clean assembleDebug
```
4) Instale/rode no emulador ou dispositivo.

---

## Estrutura do projeto
```
app/
  src/main/java/br/com/plussapps/georeminder/
    data/
      ReminderEntity.kt
      ReminderDao.kt
      ReminderDatabase.kt
      ReminderRepositoryImpl.kt
      mapper/
        ReminderMappers.kt
    domain/
      model/
        Reminder.kt
        Location.kt
      repository/
        ReminderRepository.kt
      usecase/
        *.kt (Create, Get, Update, Delete, Activate, Complete, CheckProximity)
    di/
      AppModule.kt
    geofencing/
      GeofenceManager.kt
      GeofenceBroadcastReceiver.kt
    notifications/
      NotificationUtils.kt
    service/
      LocationForegroundService.kt
    ui/
      screens/ReminderFormScreen.kt
      viewmodels/ReminderFormViewModel.kt
      composables/
        LocationSearchBar.kt
        LocationPickerMap.kt
        RadiusSlider.kt
    GeoReminderApp.kt
    MainActivity.kt
```

---

## Roadmap / Próximos passos
- [ ] Registrar geofence automaticamente após salvar um lembrete (usar ID gerado + Lat/Lng/raio)
- [ ] No `GeofenceBroadcastReceiver`, buscar o lembrete por ID e exibir título/descrição na notificação
- [ ] Tratar permissões: `POST_NOTIFICATIONS` (API 33+), `ACCESS_BACKGROUND_LOCATION` (Android 10+)
- [ ] Definir ícone de notificação adequado (substituir `ic_launcher_background`)
- [ ] Implementar tela de listagem/edição dos lembretes
- [ ] Testes unitários/instrumentados (DAO, repositório, use cases, ViewModel)
- [ ] Foreground service (se necessário) ou remoção caso geofencing seja suficiente

---

## Licença
Defina aqui a licença do projeto (ex.: MIT, Apache-2.0). Caso ainda não tenha, considere adicionar um arquivo LICENSE na raiz do repositório.

---

Dúvidas ou sugestões? Abra uma issue.