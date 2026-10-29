# FoodLog — Documentação do Trabalho 2 (Seção 4)

> **Rascunho para a dupla revisar e completar com prints verdadeiros antes da entrega.** A descrição técnica abaixo representa as decisões adotadas no código. Não afirmar que os testes foram aprovados até executá-los no Android Studio.

## 1. Como estava o Trabalho 1 e o que mudou?

No Trabalho 1, tínhamos três telas feitas em Jetpack Compose: Home, Busca e Novo Log. Elas tinham cards, campos, botões e identidade visual laranja. Porém, para mostrar cada tela era preciso mudar manualmente a chamada dentro da `MainActivity`; os botões de navegação não trocavam de tela e ações como publicar uma review apenas exibiam mensagens.

Para o Trabalho 2, reorganizamos o projeto em arquivos separados, planejamos sete telas com um `NavHost`, dois tipos de dados (Restaurante e Review) e listas compartilhadas em memória. Agora o fluxo previsto é cadastrar restaurantes, registrar avaliações, consultar detalhes e remover itens. **Inserir imagens comparando o Trabalho 1 e o Trabalho 2 após testar.**

**Evidências:** [PRINT 01: Home antiga] [PRINT 02: navegação nova] [PRINT 03: tela após cadastro]

## 2. Por que essas telas novas?

- **Perfil:** resume a atividade do usuário e dá acesso ao histórico.
- **Histórico de Reviews:** torna possível consultar e remover as avaliações cadastradas; é uma das duas listas obrigatórias.
- **Detalhe Restaurante:** exibe o restaurante selecionado, suas avaliações e a nota média calculada.
- **Detalhe Review:** exibe os dados da avaliação selecionada e permite abrir o restaurante relacionado.

Mantivemos e evoluímos **Home**, **Busca** e **Novo Log** porque já eram essenciais para a proposta do FoodLog. Busca se tornou a lista de restaurantes; Novo Log passou a inserir avaliações reais em memória.

**Evidências:** [PRINT 04: Perfil] [PRINT 05: Histórico] [PRINT 06: dois detalhes diferentes]

## 3. Qual decisão de organização do código foi tomada e por quê?

Separamos responsabilidades para evitar colocar tudo em uma única `MainActivity`:

- `Rotas.kt` nomeia as sete rotas e define a passagem de IDs;
- `AppNavigation.kt` centraliza a navegação e a barra inferior;
- `Restaurante.kt` e `Review.kt` definem as informações de cada tipo de item;
- `FoodLogViewModel.kt` concentra as duas `mutableStateListOf`, usadas em telas diferentes;
- cada tela ganhou um arquivo Kotlin próprio, com cards compartilhados em `components`.

Isso facilita entender qual parte do código cuida da interface, da navegação e dos dados, além de manter a lista atualizada em mais de uma tela.

**Evidência:** [PRINT 07: árvore de pacotes e arquivo AppNavigation.kt]

## 4. Qual foi a dificuldade extra da tela de Detalhes?

Escolhemos **calcular a média de notas de cada restaurante a partir das avaliações realmente cadastradas**. O Detalhe Restaurante não exibe apenas campos fixos: ele consulta as reviews associadas ao ID do restaurante, conta quantas existem e calcula a média. Se adicionarmos ou removermos uma review, o valor muda. A tela também permite abrir o detalhe de uma review relacionada e iniciar um cadastro para aquele restaurante.

Escolhemos essa função porque o FoodLog existe para registrar e consultar experiências gastronômicas; a média resume a opinião registrada sobre cada estabelecimento.

**Evidências:** [PRINT 08: média antes] [PRINT 09: média após cadastrar/remover review]

## 5. Quais dificuldades encontramos e como resolvemos?

**PREENCHER APÓS O DESENVOLVIMENTO E OS TESTES REAIS. NÃO INVENTAR ERROS.**

- Dificuldade real encontrada: _________________________________
- Onde apareceu (arquivo/tela): _________________________________
- O que fizemos para resolver: __________________________________
- Como verificamos a solução: ___________________________________
- Print do problema ou da solução: [PRINT 10, se houver]

## Quadro de evidências e pessoas

- Integrante 1: ____________________
- Integrante 2: ____________________
- Situação da autorização para trabalhar em **dupla** (enunciado sugere trio): ____________________
- Data dos testes no emulador: ____________________
- Repositório NOVO do Trabalho 2: ____________________

**Antes de entregar:** substituir todos os marcadores de prints por imagens verdadeiras, completar a questão 5 e revisar o texto para que descreva a experiência da dupla em suas próprias palavras.
