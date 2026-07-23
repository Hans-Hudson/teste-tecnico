# Calculadora — Teste Técnico

## Contexto do problema

A especificação do teste técnico pedia uma aplicação simples (ou parte de uma
solução mais complexa, porém funcional).

Optei por focar na **calculadora com histórico persistente**, priorizando
profundidade (arquitetura, testes, acessibilidade, tratamento de erros).

## Solução proposta

- **Kotlin + Jetpack Compose**, com arquitetura **MVI**.
- Lógica de cálculo isolada em uma [`CalculatorEngine`](app/src/main/java/com/hansbraga/testetecnico/calculator/domain/CalculatorEngine.kt)
  pura (sem dependência de Android), e um [`CalculatorReducer`](app/src/main/java/com/hansbraga/testetecnico/calculator/presentation/mvi/CalculatorReducer.kt)
  também puro, desacoplado do `ViewModel` — ambos testáveis isoladamente e
  fáceis de estender com novas tratativas de erro.
- **Histórico persistente com Room**, exposto como `Flow` reativo através de
  um repositório (`CalculatorHistoryRepository`), mantendo o `ViewModel`
  desacoplado de detalhes do Room.
- **Injeção de dependência com Koin.**
- **Dependências e constantes de build centralizadas em `build-source/`**:
  um version catalog (`libs.versions.toml`, com versões compartilhadas via
  `version.ref`) e um `build-config.properties` com minSdk/targetSdk/compileSdk
  e `applicationId`, lidos pelo `build.gradle.kts` raiz e do módulo `app`.
- **Acessibilidade (TalkBack)**: content descriptions para botões sem rótulo
  textual claro, display como live region (anuncia resultados/erros
  automaticamente), e elementos do histórico ocultados de forma consistente
  (visual + acessibilidade) quando vazios, sem deslocar o layout.
- Suporte a light/dark theme (Material 3) e a insets de tela (notch/câmera e
  barra de navegação por gestos) via `enableEdgeToEdge()` + `safeDrawingPadding()`.
  Os papéis de cor (`primary`/`secondary`/`surfaceVariant`/... e seus pares
  `on*`) são definidos explicitamente para os dois temas, e cada botão passa
  `contentColor` pareado com seu `containerColor` — evita depender do padrão
  genérico do Material3, que gerava baixo contraste em alguns botões no modo
  escuro.
- **Layout responsivo do grid de botões**: histórico, display e as linhas de
  botões dividem o espaço vertical por peso (`Modifier.weight`) em vez de
  alturas fixas, para não deixar o display sem espaço (altura zero) em telas
  mais compactas.

### Comportamento da calculadora

Segue o padrão de calculadoras nativas: um operando por vez, sem parênteses ou
precedência de operadores. Divisão por zero mostra "Erro" e trava a entrada
até "C". Tocar em um item do histórico reutiliza o resultado no display; cada
item pode ser apagado individualmente, e há um botão para limpar todo o
histórico. Novos cálculos são adicionados ao final da lista (mais recente
embaixo), com rolagem automática para o último item.

## Resolver expressão por foto (OpenAI)

Um botão "Resolver por foto" na tela da calculadora leva a uma segunda tela
onde o usuário pode fotografar ou selecionar da galeria uma imagem contendo
uma expressão matemática. A imagem é enviada para a API da OpenAI
(modelo `gpt-4o-mini`, o mais barato com suporte a visão, via `/v1/responses`
com Structured Outputs), que resolve a expressão e retorna um JSON
`{ hasExpression, expression, result }` — todo o processamento acontece na
API; o app é só a ponte. Se a imagem não contiver uma expressão matemática
(`hasExpression: false`), um erro é exibido ao usuário.

Decisões técnicas:

- **Captura de imagem** via `ActivityResultContracts.TakePicture()` (delega
  para o app de câmera do sistema, usando um `FileProvider`) e
  `ActivityResultContracts.PickVisualMedia()` (Android Photo Picker) — sem
  Camera2/preview customizado e sem permissões de câmera/armazenamento em
  tempo de execução, já que ambos os contratos delegam para componentes do
  sistema.
- **Retrofit + kotlinx.serialization** para a chamada HTTP; a imagem é
  redimensionada/comprimida no cliente antes do envio (base64) para controlar
  tamanho de payload e custo.
- **MVI com um `suspend fun` no repositório** (não `Flow`) para a chamada de
  rede, já que é uma operação única de request/resposta; o `ViewModel`
  expõe um `PhotoSolverState` (`Idle` / `Loading` / `Success` / `Error`) via
  `StateFlow` para a UI.
- **Chave da API OpenAI**: lida de `local.properties` (arquivo local,
  ignorado pelo Git) e exposta como `BuildConfig.OPENAI_API_KEY`, usada
  apenas por um interceptor OkHttp que adiciona o header `Authorization`.
  Para rodar essa funcionalidade localmente, adicione ao seu
  `local.properties`:
  ```
  OPENAI_API_KEY=sk-sua-chave-aqui
  ```
  Sem essa entrada, a tela de resolver por foto compila e roda normalmente,
  mas qualquer requisição à API falha com erro de autenticação.

## Estrutura do projeto

```
build-source/                  # version catalog + config de build/SDK
app/src/main/java/.../
├── MainActivity.kt
├── TesteTecnicoApp.kt          # Application, inicializa o Koin
├── core/theme/                 # tema Material3 (light/dark)
├── calculator/
│   ├── domain/                 # regras de negócio puras (engine, modelos, repositório como interface)
│   ├── data/                   # Room (Entity, Dao, Database) e implementação do repositório
│   ├── presentation/mvi/       # State, Intent, Reducer (puro) e ViewModel
│   ├── presentation/ui/        # Composables (tela, seção de histórico, testTags)
│   └── di/                     # módulo Koin
└── mathsolver/                 # resolver expressão por foto (OpenAI)
    ├── domain/                 # MathSolverResult, MathSolverRepository (interface)
    ├── data/                   # DTOs, Retrofit API, implementação do repositório
    ├── presentation/mvi/       # State, Intent e ViewModel
    ├── presentation/ui/        # Composables (tela, testTags, captura/seleção de imagem)
    └── di/                     # módulo Koin (Retrofit/OkHttp/Json)
```

## Como rodar

```
./gradlew assembleDebug        # gera o APK debug
./gradlew testDebugUnitTest    # roda os testes unitários
```

Testes unitários cobrem a engine de cálculo, o reducer MVI (com MockK
isolando a engine), o `ViewModel` (com Turbine + um fake repository) e o DAO
do Room (com Robolectric + banco in-memory, sem precisar de emulador).

Testes de UI Compose (`androidx.compose.ui.test`) cobrem a `CalculatorScreenContent`
e a `PhotoSolverScreenContent` (disparo de intents por botão, renderização de
estados normal/erro/carregando, e interações do histórico), também rodando
sobre Robolectric via `testDebugUnitTest` em vez de instrumentados
(`androidTest`), pelo mesmo motivo de não haver emulador configurado no
ambiente de desenvolvimento.

O repositório que fala com a API da OpenAI (`MathSolverRepositoryImpl`) é
testado isolando a interface Retrofit com MockK, sem chamadas de rede reais.

Dois testes travam a altura mínima do display da calculadora (com histórico
cheio e vazio) sobre uma tela compacta (`w360dp-h640dp`) — reproduziram um
bug real de layout (display colapsando para 0dp de altura) antes da correção.
