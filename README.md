# StudyFlow

Aplicativo Android pessoal para organizar estudos, registrar conhecimento e acompanhar a evolução na aprendizagem.

O StudyFlow reúne disciplinas, assuntos, conteúdos, dicas, materiais, questões e anotações em um único lugar. Todo o funcionamento é **offline**, com persistência local via **Room Database**, interface moderna em **Jetpack Compose** e identidade visual baseada no **Material Design 3**.

---

## Sumário

- [Visão geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Banco de dados](#banco-de-dados)
- [Como executar](#como-executar)
- [Problemas comuns](#problemas-comuns)
- [Roadmap](#roadmap)
- [Status do projeto](#status-do-projeto)

---

## Visão geral

O aplicativo foi pensado para o estudante que precisa de uma plataforma pessoal e completa de aprendizagem, sem depender de internet ou serviços externos.

Fluxo principal:

1. Cadastre uma **disciplina**.
2. Divida a disciplina em **assuntos**.
3. Registre **conteúdos**, **dicas** e **materiais** em cada assunto.
4. Cadastre **questões** vinculadas a disciplinas e assuntos.
5. Resolva questões e registre o motivo dos erros.
6. Acompanhe o progresso no **Início** e na **Evolução**.

A navegação inferior possui cinco áreas:

| Área | Descrição |
|------|-----------|
| **Início** | Painel com saudação, estatísticas, revisões e atividades recentes |
| **Disciplinas** | Organização acadêmica e acesso aos assuntos |
| **Questões** | Banco de questões, filtros, resolução e histórico |
| **Caderno** | Anotações livres vinculadas aos assuntos |
| **Evolução** | Aproveitamento, desempenho por disciplina e motivos de erro |

---

## Funcionalidades

### Início

- Saudação ao estudante de acordo com o horário.
- Quantidade de disciplinas cadastradas.
- Quantidade de questões respondidas.
- Percentual de acertos.
- Assuntos que precisam de revisão.
- Atividades recentes.
- Acesso rápido para cadastrar disciplina, assunto, questão e anotação.

### Disciplinas

- Cadastrar, editar e excluir disciplinas.
- Adicionar descrição.
- Definir cor e ícone.
- Consultar a quantidade de assuntos cadastrados.

### Assuntos

- Cadastrar assuntos dentro de uma disciplina.
- Editar e excluir assuntos.
- Organizar assuntos por disciplina.
- Definir a situação do aprendizado:
  - Não iniciado
  - Estudando
  - Precisa revisar
  - Dominado
- Acompanhar o progresso de cada assunto.

### Área de conhecimento do assunto

Cada assunto funciona como uma unidade independente de aprendizagem, com:

**Conteúdos**
- Título, tópicos, explicação detalhada e exemplos práticos.
- Estrutura preparada para anexos de imagens e PDFs em versões futuras.

**Dicas**
- Conceitos fundamentais.
- Pegadinhas de prova.
- Observações que facilitem a compreensão.

**Materiais**
- Links e referências.
- Materiais complementares organizados por assunto.

### Questões

Cada questão possui:

- Enunciado.
- Alternativas A, B, C, D e E.
- Gabarito.
- Comentário/explicação.
- Disciplina relacionada.
- Assunto relacionado.
- Banca organizadora.
- Ano da prova.
- Fonte.
- Nível de dificuldade (Fácil, Médio, Difícil).

Recursos disponíveis:

- Cadastrar, editar e excluir questões.
- Filtrar por disciplina, assunto e dificuldade.
- Resolver questões.
- Exibir gabarito e explicação após a resposta.
- Registrar histórico de respostas.
- Após um erro, registrar o motivo:
  - Desconhecimento do conteúdo
  - Confusão entre conceitos
  - Interpretação incorreta
  - Desatenção
  - Dúvida entre alternativas
  - Outros

### Caderno

- Criar, editar e excluir anotações livres.
- Vincular anotações a um assunto.
- Visualizar data da última atualização.

### Evolução

- Aproveitamento geral em questões.
- Total de disciplinas, assuntos e questões.
- Desempenho por disciplina.
- Distribuição dos motivos de erro.
- Resumo de acertos, erros e questões distintas respondidas.

---

## Arquitetura

O projeto segue o padrão **MVVM** com **Repository Pattern**, separação de responsabilidades e uso de **Coroutines** e **Flow** para operações assíncronas e observação reativa dos dados.

```text
┌──────────────────────────────┐
│      Jetpack Compose UI      │
│  Screens + Components        │
└──────────────┬───────────────┘
               │ observa estado
┌──────────────▼───────────────┐
│          ViewModels          │
│  StateFlow + SavedStateHandle │
└──────────────┬───────────────┘
               │
┌──────────────▼───────────────┐
│        Repositories          │
│  Regras de acesso aos dados  │
└──────────────┬───────────────┘
               │
┌──────────────▼───────────────┐
│         Room DAOs            │
│  Consultas SQL + Flow        │
└──────────────┬───────────────┘
               │
┌──────────────▼───────────────┐
│    SQLite local (offline)    │
└──────────────────────────────┘
```

Principais decisões:

- **Interface declarativa** com Jetpack Compose e Material Design 3.
- **Estado gerenciado por ViewModel** e exposto via `StateFlow`.
- **Repository** como única porta de acesso aos DAOs.
- **Room** com entidades relacionais, chaves estrangeiras e consultas agregadas.
- **Navigation Compose** com rotas tipadas por argumentos e navegação inferior.
- **Injeção de dependências manual** via `AppContainer`.
- **Funcionamento 100% offline** e sem permissão de internet.

---

## Tecnologias

| Tecnologia | Uso |
|------------|-----|
| Kotlin | Linguagem principal |
| Jetpack Compose | Construção das interfaces |
| Material Design 3 | Identidade visual |
| Room Database | Persistência local |
| MVVM | Organização da aplicação |
| Repository Pattern | Acesso aos dados |
| Navigation Compose | Navegação entre telas |
| Coroutines + Flow | Assincronismo e observação de dados |
| ViewModel | Gerenciamento de estado |
| KSP | Processamento do Room |

Versões principais:

- Kotlin `2.0.21`
- Android Gradle Plugin `8.7.3`
- Compose BOM `2024.12.01`
- Room `2.6.1`
- Navigation Compose `2.8.5`
- Lifecycle `2.8.7`
- `minSdk 26` / `targetSdk 35` / `compileSdk 35`

---

## Estrutura do projeto

```text
app-sistema-questoes/
├── app/
│   ├── build.gradle.kts
│   ├── schemas/                         # Schemas exportados pelo Room
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/                         # Ícones, temas e recursos
│       └── java/com/studyflow/app/
│           ├── MainActivity.kt
│           ├── StudyFlowApplication.kt
│           ├── data/
│           │   ├── local/
│           │   │   ├── StudyFlowDatabase.kt
│           │   │   ├── Converters.kt
│           │   │   ├── dao/             # DAOs do Room
│           │   │   ├── entity/          # Entidades do banco
│           │   │   └── relation/        # Consultas agregadas
│           │   ├── model/StudyEnums.kt  # Enumerações do domínio
│           │   └── repository/          # Repositórios
│           ├── di/AppContainer.kt       # Container de dependências
│           └── ui/
│               ├── AppViewModelProvider.kt
│               ├── StudyFlowApp.kt      # NavHost + Scaffold
│               ├── components/          # Componentes reutilizáveis
│               ├── navigation/          # Rotas e barra inferior
│               ├── screens/
│               │   ├── home/
│               │   ├── disciplines/
│               │   ├── subjects/
│               │   ├── knowledge/
│               │   ├── questions/
│               │   ├── notebook/
│               │   └── evolution/
│               ├── theme/               # Cores, tipografia e tema
│               └── util/                # Formatação e ícones
├── gradle/
│   ├── libs.versions.toml               # Catálogo de versões
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── local.properties                     # Específico da máquina (não versionado)
```

---

## Banco de dados

Banco local: `studyflow.db`

Entidades principais:

| Entidade | Responsabilidade |
|----------|------------------|
| `DisciplineEntity` | Disciplinas, cor e ícone |
| `SubjectEntity` | Assuntos, situação e progresso |
| `ContentEntity` | Conteúdos do assunto |
| `TipEntity` | Dicas do assunto |
| `MaterialEntity` | Links e materiais complementares |
| `QuestionEntity` | Questões e metadados |
| `AnswerRecordEntity` | Histórico de respostas e motivos de erro |
| `NoteEntity` | Anotações do caderno |

Relações:

- Uma disciplina possui vários assuntos.
- Um assunto possui vários conteúdos, dicas, materiais e anotações.
- Uma questão pode estar vinculada a uma disciplina e a um assunto.
- Cada resposta pertence a uma questão e pode registrar um motivo de erro.

Os schemas exportados ficam em `app/schemas/` para apoiar futuras migrações.

---

## Como executar

### Requisitos

- Linux, macOS ou Windows.
- JDK 17.
- Android SDK com:
  - Android 15 (API 35)
  - Build Tools 35.0.0
  - Platform Tools
- Android Studio (recomendado) ou SDK via linha de comando.
- Emulador ou dispositivo físico com Android 8.0 (API 26) ou superior.

### Android Studio

1. Abra o Android Studio.
2. Selecione **Open** e escolha a pasta do projeto.
3. Em **Settings → Languages & Frameworks → Android SDK**, instale:
   - Android 15 (API 35)
   - Build Tools 35.0.0
4. Aguarde a sincronização do Gradle.
5. Execute o app com o botão **Run**.

O Android Studio cria automaticamente o arquivo `local.properties` com o caminho do SDK.

### Linha de comando

Crie o `local.properties` apontando para o SDK da sua máquina:

```bash
cd ~/Desktop/workspace/app-sistema-questoes
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

Ou defina a variável de ambiente:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
```

Compile o APK de debug:

```bash
./gradlew :app:assembleDebug
```

APK gerado em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Instale em um dispositivo/emulador conectado:

```bash
./gradlew :app:installDebug
```

Execute as verificações de lint:

```bash
./gradlew :app:lintDebug
```

### Instalação do SDK via linha de comando

Caso não utilize o Android Studio:

```bash
SDK="$HOME/Android/Sdk"
mkdir -p "$SDK/cmdline-tools"
cd /tmp
curl -fLO https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip -q commandlinetools-linux-11076708_latest.zip
mv cmdline-tools "$SDK/cmdline-tools/latest"

export ANDROID_HOME="$SDK"
yes | "$SDK/cmdline-tools/latest/bin/sdkmanager" --licenses
"$SDK/cmdline-tools/latest/bin/sdkmanager" \
  "platform-tools" "platforms;android-35" "build-tools;35.0.0"
```

Se o `sdkmanager` falhar por problemas de rede, baixe manualmente:

```bash
cd /tmp
curl -fLO https://dl.google.com/android/repository/platform-35_r02.zip
curl -fLO https://dl.google.com/android/repository/build-tools_r35_linux.zip
curl -fLO https://dl.google.com/android/repository/platform-tools-latest-linux.zip

unzip -q platform-35_r02.zip -d "$SDK/platforms"
unzip -q build-tools_r35_linux.zip -d /tmp/bt
mkdir -p "$SDK/build-tools/35.0.0"
cp -r /tmp/bt/android-15/* "$SDK/build-tools/35.0.0/"
unzip -q platform-tools-latest-linux.zip -d "$SDK"
```

---

## Problemas comuns

### `SDK location not found`

O `local.properties` está ausente ou aponta para um caminho inexistente.

```bash
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

Confirme que o diretório existe:

```bash
ls "$HOME/Android/Sdk/platforms/android-35"
ls "$HOME/Android/Sdk/build-tools/35.0.0"
```

### `Unable to create debug keystore in ~/.android`

O projeto gera automaticamente um `debug.keystore` na raiz e usa essa assinatura no build de debug. Se o arquivo for removido, ele será recriado no próximo build.

### `Failed to find Build Tools revision`

Instale a versão exigida:

```bash
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "build-tools;35.0.0"
```

### `local.properties` não aparece no Git

Isso é esperado. O arquivo contém caminhos específicos da máquina e está no `.gitignore`.

---

## Roadmap

- Anexos de imagens e PDFs nos conteúdos.
- Exportação e importação de dados/backup.
- Notificações e lembretes de revisão.
- Estatísticas por período e gráficos históricos.
- Metas de estudo e sequência de dias ativos.
- Testes unitários e testes de interface.
- Migrações versionadas do Room.

---

## Status do projeto

- Build de debug validado.
- Lint sem erros.
- APK funcional gerado em `app/build/outputs/apk/debug/app-debug.apk`.
- Aplicativo totalmente offline, com armazenamento local.
