package br.com.joaolucas.estoque;
import java.util.Scanner;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        VendaDAO vendaDAO = new VendaDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        int opcao = -1;

        do {
            System.out.println("\n=== ESTOQUE E VENDAS ===\n" + 1 + " - Cadastrar produto\n" + 2 + " - Listar produtos\n"+ 3 + " - Repor estoque\n" +  4 + " - Consultar estoque baixo\n" + 5 + " - Atualizar produto\n" + 6 + " - Registrar venda\n" + 7 + " - Histórico de vendas\n" + 0 + " - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());

                switch (opcao) {
                    case 1:
                        System.out.print("Nome do produto: ");
                        String nome = sc.nextLine();

                        System.out.print("Preço (ex.: 59,90): ");BigDecimal preco = new BigDecimal(sc.nextLine().replace(',', '.'));

                        System.out.print("Quantidade inicial: ");
                        int quantidade = Integer.parseInt(sc.nextLine());

                        System.out.print("Estoque mínimo: ");
                        int minimo = Integer.parseInt(sc.nextLine());

                        Produto novoProduto = new Produto(0, nome, preco, quantidade, minimo);

                        int idGerado = produtoDAO.cadastrar(novoProduto);

                        System.out.println("Produto salvo no banco! ID: " + idGerado);
                        break;
                    case 2:
                        ArrayList<Produto> produtosDoBanco = produtoDAO.listar();

                        if (produtosDoBanco.isEmpty()){
                            System.out.println("Nenhum produto cadastrado.");
                        } else{
                            for (Produto produto : produtosDoBanco){
                                System.out.printf("ID: %d | Nome: %s | Preço: R$ %s | Estoque: %d | Mínimo: %d%n", produto.getId(), produto.getNome(), produto.getPreco().toPlainString(), produto.getQuantidadeEstoque(), produto.getEstoqueMinimo());
                            }
                        }
                        break;
                    case 3:
                        System.out.print("ID do produto: ");
                        int idReposicao = Integer.parseInt(sc.nextLine());

                        Produto produtoReposicao = produtoDAO.buscarPorId(idReposicao);

                        if (produtoReposicao == null){
                            System.out.println("Produto não encontrado.");
                        } else{
                            System.out.print("Quantidade para reposição: ");
                            int quantidadeReposicao = Integer.parseInt(sc.nextLine());

                            produtoDAO.reporEstoque(idReposicao, quantidadeReposicao);

                            System.out.println("Estoque reposto no banco!");
                        }
                        break;
                    case 4:
                        ArrayList<Produto> produtosBaixos = produtoDAO.listarEstoqueBaixo();

                        if (produtosBaixos.isEmpty()){
                            System.out.println("Nenhum produto com estoque baixo.");
                        } else{
                            for (Produto produto : produtosBaixos){
                                System.out.printf("ID: %d | Nome: %s | Estoque: %d | Mínimo: %d%n", produto.getId(), produto.getNome(), produto.getQuantidadeEstoque(), produto.getEstoqueMinimo()
                                );
                            }
                        }
                        break;
                    case 5:
                        System.out.print("ID do produto: ");
                        int idAtualizar = Integer.parseInt(sc.nextLine());

                        Produto produtoAtualizar = produtoDAO.buscarPorId(idAtualizar);

                        if (produtoAtualizar == null){
                            System.out.println("Produto não encontrado.");
                        } else{
                            System.out.print("Novo nome: ");
                            String novoNome = sc.nextLine();

                            System.out.print("Novo preço: ");
                            BigDecimal novoPreco = new BigDecimal(sc.nextLine().replace(',', '.'));

                            System.out.print("Novo estoque mínimo: ");
                            int novoMinimo = Integer.parseInt(sc.nextLine());

                            Produto dadosAtualizados = new Produto(produtoAtualizar.getId(), novoNome, novoPreco, produtoAtualizar.getQuantidadeEstoque(), novoMinimo
                            );

                            produtoDAO.atualizar(dadosAtualizados);

                            System.out.println("Produto atualizado no banco!");
                        }
                        break;
                    case 6:
                        Venda novaVenda = new Venda(0);
                        while (true){
                            System.out.print("ID do produto (0 para finalizar, -1 para cancelar): ");
                            int idProduto = Integer.parseInt(sc.nextLine());

                            if (idProduto == -1){
                                System.out.println("Venda cancelada.");
                                break;
                            }

                            if (idProduto == 0) {
                                if (novaVenda.getItens().isEmpty()){
                                    System.out.println("A venda precisa ter itens.");
                                    continue;
                                }
                                System.out.println("Total: R$ " + novaVenda.calcularTotal().toPlainString());
                                System.out.print("Confirmar venda? (s/n): ");
                                String resposta = sc.nextLine();

                                if (resposta.equalsIgnoreCase("s")){
                                    int idVenda = vendaDAO.registrar(novaVenda);
                                    System.out.println("Venda salva no banco! ID: " + idVenda);
                                } else{
                                    System.out.println("Venda cancelada.");
                                }
                                break;
                            }

                            Produto produtoVenda = produtoDAO.buscarPorId(idProduto);

                            if (produtoVenda == null){
                                System.out.println("Produto não encontrado.");
                            } else{
                                System.out.println("Produto: " + produtoVenda.getNome() + " | Preço: R$ " + produtoVenda.getPreco().toPlainString() + " | Estoque: " + produtoVenda.getQuantidadeEstoque());
                                System.out.print("Quantidade: ");
                                int quantidadeVenda = Integer.parseInt(sc.nextLine());

                                ItemVenda item = new ItemVenda(produtoVenda, quantidadeVenda);
                                novaVenda.adicionarItem(item);
                                System.out.println("Item adicionado.");
                            }
                        }
                        break;
                    case 7:
                    ArrayList<String> historico = vendaDAO.listarHistorico();

                    if (historico.isEmpty()){
                        System.out.println("Nenhuma venda registrada.");
                    } else{
                        for (String linha : historico){
                            System.out.println(linha);
                        }
                    }
                    break;
                    case 0:
                        System.out.println("Encerrando...");
                        break;
                    default:
                        System.out.println("Opção Inválida");
                }
            } catch(IllegalArgumentException e){
                System.out.println("Não foi possível concluir: " + e.getMessage());
            } catch(SQLException e){
                System.out.println("Erro no banco: "  +  e.getMessage());
            }
        } while (opcao != 0);

        sc.close();
    }
}