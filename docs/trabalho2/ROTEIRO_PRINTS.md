# Roteiro de prints e testes — FoodLog MAF

Não é necessário tirar prints de cada linha de código. Faça capturas **reais** do emulador e do Android Studio em momentos úteis. Sugestão de organização: `docs/trabalho2/prints/`.

## Etapa A — comparação com Trabalho 1

- [ ] `01_home_antiga.png`: tela Home da primeira entrega (pode vir do projeto original).
- [ ] `02_estrutura_nova.png`: árvore de pacotes (`screens`, `navigation`, `model`, `data`) no Android Studio.
- [ ] `03_navigation.png`: `AppNavigation.kt` com `NavHost` e navegação inferior.

## Etapa B — listas e formulários

- [ ] `04_busca_antes.png`: lista de restaurantes antes de cadastrar um novo.
- [ ] `05_form_restaurante.png`: formulário de restaurante preenchido.
- [ ] `06_busca_depois.png`: novo restaurante aparecendo na lista.
- [ ] `07_form_review.png`: formulário Novo Log preenchido, nota selecionada.
- [ ] `08_historico_depois.png`: review nova aparecendo no histórico.

## Etapa C — detalhes e regra extra

- [ ] `09_detalhe_restaurante.png`: restaurante selecionado com média de notas.
- [ ] `10_detalhe_review.png`: detalhe da review cadastrada, mostrando o item correto.
- [ ] `11_media_alterada.png`: média após criar/remover uma segunda avaliação do mesmo restaurante.
- [ ] `12_perfil.png`: perfil com contadores atualizados.

## Testes obrigatórios antes do GitHub

- [ ] Home abre, filtros alteram o feed e botão Buscar funciona.
- [ ] BottomNavigation abre Home, Busca, Novo Log e Perfil; repita mais de uma vez.
- [ ] Busca filtra resultados conforme o texto; filtros de categoria funcionam.
- [ ] Adicionar restaurante com campos preenchidos faz o item aparecer.
- [ ] Restaurante novo abre os **próprios** detalhes, não os de outro.
- [ ] Remover restaurante funciona com diálogo de confirmação.
- [ ] Formulário Novo Log rejeita campos vazios, nota ausente e preço inválido.
- [ ] Criar review insere novo item no Histórico.
- [ ] Clicar em duas reviews diferentes abre os detalhes corretos.
- [ ] Remover review atualiza Histórico, Home, Perfil e a média do restaurante.
- [ ] Detalhe Restaurante mostra média calculada e abre reviews relacionadas.
- [ ] Botão Voltar da TopAppBar retorna à tela anterior.
- [ ] Remover um restaurante apaga suas reviews associadas, com confirmação.
- [ ] Aplicativo navega sem travar durante todo o roteiro.

**Se algum teste falhar:** anote o erro e tire um print. Corrigiremos antes de considerar a entrega pronta. Isso também ajuda a responder à pergunta 5 da documentação.
