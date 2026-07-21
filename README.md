# Calculadora — Teste Técnico

## Contexto do problema

A especificação do teste técnico pedia uma aplicação simples (ou parte de uma
solução mais complexa, porém funcional), sugerindo alguns temas — entre eles,
uma calculadora com histórico persistente local e uma listagem de filmes
consumindo API.

Optei por focar exclusivamente na **calculadora com histórico persistente**,
priorizando profundidade (arquitetura, testes, acessibilidade, tratamento de
erros) em vez de dividir o tempo disponível entre duas funcionalidades
diferentes. A listagem de filmes **não foi implementada**.

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

### Comportamento da calculadora

Segue o padrão de calculadoras nativas: um operando por vez, sem parênteses ou
precedência de operadores. Divisão por zero mostra "Erro" e trava a entrada
até "C". Tocar em um item do histórico reutiliza o resultado no display; cada
item pode ser apagado individualmente, e há um botão para limpar todo o
histórico. Novos cálculos são adicionados ao final da lista (mais recente
embaixo), com rolagem automática para o último item.

## Estrutura do projeto

```
build-source/                  # version catalog + config de build/SDK
app/src/main/java/.../
├── MainActivity.kt
├── TesteTecnicoApp.kt          # Application, inicializa o Koin
├── core/theme/                 # tema Material3 (light/dark)
└── calculator/
    ├── domain/                 # regras de negócio puras (engine, modelos, repositório como interface)
    ├── data/                   # Room (Entity, Dao, Database) e implementação do repositório
    ├── presentation/mvi/       # State, Intent, Reducer (puro) e ViewModel
    ├── presentation/ui/        # Composables (tela, seção de histórico, testTags)
    └── di/                     # módulo Koin
```

## Como rodar

```
./gradlew assembleDebug        # gera o APK debug
./gradlew testDebugUnitTest    # roda os testes unitários
```

Testes unitários cobrem a engine de cálculo, o reducer MVI (com MockK
isolando a engine), o `ViewModel` (com Turbine + um fake repository) e o DAO
do Room (com Robolectric + banco in-memory, sem precisar de emulador).

## O que ficou incompleto / o que faria diferente com mais tempo

- **Listagem de filmes**: não implementada — o tempo disponível foi
  direcionado inteiramente para a calculadora, conforme priorização definida
  no início do desenvolvimento.
- **Testes de UI Compose** (`androidx.compose.ui.test`): a tela já foi
  construída com `testTag` em todos os elementos interativos pensando nisso,
  mas os testes em si não foram escritos — ficaram como prioridade secundária
  frente aos testes unitários.
- **Validação em dispositivo/emulador real**: o ambiente de desenvolvimento
  usado não tinha um emulador Android configurado; a verificação foi feita via
  testes automatizados e build bem-sucedido (`assembleDebug`), não por
  inspeção visual direta do app rodando.
- Com mais tempo, também revisitaria o versionamento de schema do Room
  (atualmente `exportSchema = false`, aceitável para este escopo sem
  migrations) e adicionaria testes de acessibilidade automatizados.
