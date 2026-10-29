# FoodLog — Trabalho 3 (Room)

Evolução do **FoodLog — Trabalho 2**: aplicativo Android em **Kotlin + Jetpack Compose**, agora com banco local SQLite via **Room**. Foram preservadas as **sete telas**, o `NavHost`, o menu inferior e a identidade visual.

## Como abrir e rodar

1. Descompacte o ZIP (ou clone este **novo** repositório do Trabalho 3).
2. No **Android Studio**, clique em **Open** e abra a pasta que contém `settings.gradle.kts` (não apenas `app/`).
3. Use o **Gradle JDK (Embedded JDK/JBR 17 ou mais recente)**, instale o **Android SDK 36** e aguarde a sincronização do Gradle; a primeira execução exige internet. A versão anterior trazia uma configuração local que exigia JDK 25; ela foi removida nesta entrega para não exigir essa versão na escola.
4. Selecione um emulador/celular com Android 7.0 (API 24) ou superior e clique em **Run app**.
5. O app inicia na Home. Os restaurantes e reviews agora vêm **do banco de dados**; numa instalação limpa, as listas começam vazias até o primeiro cadastro.

**Configuração pensada para a escola:** Android Gradle Plugin **9.0.0**, `compileSdk/targetSdk 36`, Kotlin Compose **2.2.10**, KSP **2.2.10-2.0.2** e Room **2.8.5**. O projeto enviado do Trabalho 2 estava em AGP 9.3.2/SDK 37, que não abria na versão do Android Studio da escola. A compilação em um Android Studio real ainda precisa ser confirmada.

## As sete telas

1. **Home:** exibe reviews e restaurantes vindos do Room.
2. **Busca:** pesquisa e **adiciona/remove** restaurantes persistentes (`LazyColumn + Card`).
3. **Novo Log:** formulário de avaliação que grava no Room.
4. **Perfil:** estatísticas derivadas das listas consultadas no banco.
5. **Histórico:** mostra e remove reviews persistentes (`LazyColumn + Card`).
6. **Detalhe Restaurante:** consulta pelo **ID direto no DAO**, exibe média de notas e permite **editar** os dados do restaurante.
7. **Detalhe Review:** consulta pelo **ID direto no DAO**, mostra os campos e permite **editar** prato, nota, preço e comentário.

A barra inferior permite navegar entre Home, Busca, Novo Log e Perfil. As demais telas são abertas por itens e botões; o botão de voltar funciona pelo `NavController`.

## Arquivos Room e decisões

| Arquivo | Responsabilidade |
|---|---|
| `model/Restaurante.kt` | `@Entity(tableName="restaurantes")`, chave `id` gerada automaticamente, nome, categoria, cidade, descrição |
| `model/Review.kt` | `@Entity(tableName="reviews")`, ID gerado, ID do restaurante, prato, nota, preço e comentário |
| `data/RestauranteDao.kt` | `suspend @Insert`, `buscarTodos`, `buscarPorId`, `@Update`, `@Delete` |
| `data/ReviewDao.kt` | `suspend @Insert`, `buscarTodos`, `buscarPorId`, `buscarPorRestaurante`, `@Update`, `@Delete` |
| `data/AppDatabase.kt` | Banco Room, `@Database(version=1)`, DAOs e Singleton seguro |
| `data/FoodLogViewModel.kt` | Coroutines de IO, try/catch + Log.e, consultas e atualização do estado Compose |
| `screens/TelaBusca.kt` e `screens/TelaHistorico.kt` | `LaunchedEffect(Unit)` solicita nova leitura do Room ao abrir |
| `screens/TelaDetalheRestaurante.kt` / `TelaDetalheReview.kt` | Busca direta por ID e formulários de edição |

O campo `Review.restauranteId` é uma **chave estrangeira**. Quando um restaurante é excluído, suas avaliações associadas também são excluídas pelo Room (`onDelete = CASCADE`), evitando dados órfãos. O `mutableStateListOf` continua apenas como **estado de interface alimentado pelas consultas**, não como fonte permanente de dados.

## Teste obrigatório de persistência

1. **Busca → Adicionar restaurante:** informe nome, categoria, cidade, descrição e salve.
2. Abra o restaurante e toque em **Editar restaurante (Room)**; modifique o nome e salve.
3. **Novo Log:** publique uma review para esse restaurante.
4. **Perfil → Histórico:** abra a review, toque em **Editar avaliação (Room)**, altere a nota e salve.
5. Feche completamente o aplicativo sem limpar os dados (remova-o de Recentes ou use **Forçar parada** nas configurações).
6. Reabra o FoodLog: **restaurante e review, incluindo suas edições, precisam continuar lá**.
7. Remova a review e reabra o aplicativo para verificar a exclusão; em seguida, teste a remoção de restaurante.

**Não desinstale o aplicativo nem limpe seu armazenamento durante o teste:** isso apaga o banco local e não corresponde ao requisito de simplesmente fechar e reabrir.

## Documentação e entrega

- `docs/trabalho3/PROCESSO.md`: respostas-base às perguntas da **seção 4**, com campos para inserir suas evidências e dificuldades reais.
- `docs/trabalho3/ROTEIRO_EVIDENCIAS.md`: sequência de prints e vídeos curtos para fazer durante o teste (especialmente a persistência).
- `docs/trabalho3/CONFIGURACAO_KSP.md`: sequência de configuração do plugin KSP exigida pelo professor.
- `docs/trabalho2/`: histórico e material da entrega anterior, preservados para comparação.

**Antes de entregar:** validar a compilação e todas as telas no Android Studio, gravar o vídeo de fechamento/reabertura, incluir prints reais na documentação, criar um **novo repositório GitHub** para o Trabalho 3 e apresentá-lo em sala. Não faça commits depois do prazo do Blackboard.

## Limitações de validação desta entrega

O código e os requisitos do professor foram verificados estaticamente. **Não foi possível executar o Gradle nem o emulador neste ambiente**, por ausência do Android SDK e das dependências locais. Por isso, sincronize e compile no Android Studio antes de publicar. Se surgir um erro, preserve a mensagem completa do **Build Output** ou **Logcat** para correção. A persistência deverá ser demonstrada com vídeo real, não apenas com os arquivos de código.
