# Sistema de Estoque e Vendas

Aplicação de terminal desenvolvida em Java com PostgreSQL para cadastrar
produtos, controlar estoque e registrar vendas com vários itens.

Projeto pessoal de João Lucas Mota, estudante de Ciência da Computação,
desenvolvido para praticar orientação a objetos, SQL e persistência com JDBC.

## Funcionalidades

- Cadastro e atualização de produtos.
- Listagem de produtos.
- Reposição de estoque.
- Consulta de produtos com estoque baixo.
- Registro de vendas com vários produtos.
- Bloqueio de vendas sem estoque suficiente.
- Histórico de vendas com itens, quantidades e preços.
- Persistência dos dados após encerrar o programa.

## Tecnologias

- Java 25
- PostgreSQL
- JDBC
- Maven

## Organização

- `Main`: menu interativo e leitura dos dados.
- `Produto`: dados e regras do produto.
- `ItemVenda`: produto, quantidade e preço unitário da compra.
- `Venda`: organização dos itens e cálculo do total.
- `Conexao`: conexão com PostgreSQL.
- `ProdutoDAO`: operações de produtos no banco.
- `VendaDAO`: registro das vendas e consulta do histórico.
- `schema.sql`: criação das tabelas.

## Como executar

### 1. Pré-requisitos

- JDK 25.
- PostgreSQL instalado e em execução.
- IntelliJ IDEA com suporte a Maven.

### 2. Criar o banco

No pgAdmin, crie um banco chamado `estoque_vendas`.

Abra o Query Tool nesse banco e execute o conteúdo de `schema.sql`
uma única vez para criar as tabelas.

### 3. Abrir o projeto

Baixe e extraia o projeto. No IntelliJ, abra o arquivo `pom.xml`
como projeto e aguarde o carregamento das dependências Maven.

### 4. Configurar a conexão

Crie uma configuração de execução do tipo Application:

- Classe principal: `br.com.joaolucas.estoque.Main`
- JDK: 25
- Módulo: o módulo do projeto

Em Environment variables, configure:

| Variável | Exemplo |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/estoque_vendas` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | Sua senha local |

Ajuste o endereço, a porta e o usuário conforme sua instalação.
Não publique senhas no repositório.

### 5. Executar

Execute a classe `Main` e use o menu:

```text
1 - Cadastrar produto
2 - Listar produtos
3 - Repor estoque
4 - Consultar estoque baixo
5 - Atualizar produto
6 - Registrar venda
7 - Histórico de vendas
0 - Sair
```

Nos preços, use ponto ou vírgula como separador decimal, sem separador
de milhar. Exemplo: `59,90`.

## Como as vendas funcionam

Cada produto pode aparecer uma vez no carrinho, com a quantidade desejada.

Ao confirmar, o sistema grava a venda, seus itens e a baixa do estoque
na mesma transação.

Se algum produto não existir ou não tiver estoque suficiente, a operação
é desfeita com rollback. Caso todas as operações sejam concluídas,
o commit confirma a venda.

O nome e o preço unitário são armazenados nos itens da venda,
preservando o histórico mesmo após alterações no cadastro.

Os cálculos monetários utilizam BigDecimal.

## Roteiro de testes manuais

- Cadastrar produtos e consultar a listagem.
- Atualizar os dados e verificar se permanecem após reiniciar.
- Repor estoque e conferir a nova quantidade.
- Consultar produtos no limite do estoque mínimo.
- Registrar uma venda com vários produtos e conferir o total.
- Tentar vender acima do estoque disponível.
- Verificar se uma venda recusada preserva todos os estoques
  e não aparece no histórico.
- Reiniciar e consultar as vendas salvas.

## Próximas melhorias

- Interface gráfica.
- Testes automatizados.
- Relatórios por período.