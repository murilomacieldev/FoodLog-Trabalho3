

## 1. Como o projeto estava no Trabalho 2 e o que mudou?

No Trabalho 2, o FoodLog já tinha sete telas, navegação funcional e dois tipos de dados: restaurantes e reviews. Os cadastros eram mantidos em listas na memória (`mutableStateListOf`) e podiam ser perdidos quando o aplicativo era encerrado.

No Trabalho 3, preservamos as telas, o fluxo de navegação e os formulários, mas substituímos o armazenamento temporário pelo **Room**, que salva restaurantes e reviews em um banco SQLite local. As telas passaram a carregar os dados do banco e os botões de cadastrar, editar e remover passaram a realizar operações SQL pelos DAOs.

## 2. Quais Entity, DAO e Database criamos? Por que esses campos?

- `Restaurante` (`@Entity(tableName="restaurantes")`): `id` auto-gerado para identificar cada item, `nome` para apresentação, `categoria` para filtros, `cidade` para localização e `descricao` para detalhes.
- `Review` (`@Entity(tableName="reviews")`): `id` auto-gerado, `restauranteId` para associar a avaliação ao restaurante, `prato`, `nota`, `preco` e `comentario` para registrar a experiência.
- `RestauranteDao`: faz o CRUD de restaurantes; `ReviewDao`: faz o CRUD de avaliações.
- `AppDatabase`: reúne as duas Entities e os dois DAOs em uma única instância Singleton.

Criamos uma relação de chave estrangeira para impedir reviews de apontarem para restaurantes inexistentes. Ao excluir um restaurante, suas reviews são removidas automaticamente, preservando a consistência dos dados.


## 3. Como decidimos as consultas dos DAOs?

Os dois DAOs têm os quatro métodos obrigatórios, todos `suspend`: **inserir, buscarTodos, atualizar e deletar**. Além desses, criamos `buscarPorId(id)` para que as telas de detalhes recuperem do Room o item selecionado na navegação. O `ReviewDao` também possui `buscarPorRestaurante(restauranteId)` para consultar somente as avaliações de um estabelecimento.

No ViewModel, usamos um escopo de coroutines em `Dispatchers.IO` para executar as consultas fora da thread principal. Os resultados alimentam listas reativas exibidas pelo Compose. Nas telas Busca e Histórico, `LaunchedEffect(Unit)` solicita a atualização quando a tela é aberta.

## 4. Qual foi a dificuldade ao migrar de mutableStateListOf para Room?
antes, bastava adicionar o item diretamente na lista. Com Room, é necessário esperar a operação do banco terminar e só então atualizar a interface. Por isso ajustamos as ações de cadastro, edição e remoção para trabalhar com coroutines e atualizar as listas após uma operação bem-sucedida.


## 5. Teste de persistência — evidência obrigatória

o que foi feito no video:
1. Abrimos Busca e cadastramos um restaurante.
2. Abrimos o detalhe e editamos o restaurante.
3. Criamos uma review, editamos sua nota e confirmamos no histórico.
4. Fechamos o aplicativo completamente, **sem desinstalar nem limpar dados**.
5. Abrimos novamente e mostramos que o restaurante e a review, incluindo suas edições, permanecem salvos.

link:
https://drive.google.com/file/d/1WkCx4V0HAbFMDuj6CN835XkW_QF1HKM6/view?usp=sharing


