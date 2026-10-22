package br.com.joaolucas.estoque;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;

public class VendaDAO {

    public int registrar(Venda venda) throws SQLException {
        if (venda == null) {
            throw new IllegalArgumentException("A venda não pode ser nula.");
        }

        ArrayList<ItemVenda> itens = venda.getItens();

        if (itens.isEmpty()) {
            throw new IllegalArgumentException("A venda precisa ter itens.");
        }

        // Todas as vendas processam os produtos na mesma ordem.
        itens.sort(Comparator.comparingInt(
                item -> item.getProduto().getId()
        ));

        BigDecimal total = BigDecimal.ZERO;

        for (ItemVenda item : itens) {
            BigDecimal preco = item.getPrecoUnitario().setScale(
                    2, RoundingMode.UNNECESSARY
            );

            total = total.add(
                    preco.multiply(BigDecimal.valueOf(item.getQuantidade()))
            );
        }

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int idVenda;

                String sqlVenda = """
                        INSERT INTO vendas (data, total)
                        VALUES (?, ?)
                        RETURNING id
                        """;

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlVenda)) {

                    comando.setTimestamp(
                            1, Timestamp.valueOf(venda.getData())
                    );
                    comando.setBigDecimal(2, total);

                    try (ResultSet resultado = comando.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException(
                                    "O banco não retornou o ID da venda."
                            );
                        }

                        idVenda = resultado.getInt("id");
                    }
                }

                String sqlEstoque = """
                        UPDATE produtos
                        SET quantidade_estoque = quantidade_estoque - ?
                        WHERE id = ?
                          AND quantidade_estoque >= ?
                        RETURNING nome
                        """;

                String sqlItem = """
                        INSERT INTO itens_venda
                            (venda_id, produto_id, nome_produto,
                             quantidade, preco_unitario)
                        VALUES (?, ?, ?, ?, ?)
                        """;

                for (ItemVenda item : itens) {
                    String nomeProduto;

                    try (PreparedStatement comando =
                                 conexao.prepareStatement(sqlEstoque)) {

                        comando.setInt(1, item.getQuantidade());
                        comando.setInt(2, item.getProduto().getId());
                        comando.setInt(3, item.getQuantidade());

                        try (ResultSet resultado = comando.executeQuery()) {
                            if (!resultado.next()) {
                                throw new IllegalArgumentException(
                                        "Produto não encontrado ou estoque "
                                                + "insuficiente para o ID "
                                                + item.getProduto().getId()
                                );
                            }

                            nomeProduto = resultado.getString("nome");
                        }
                    }

                    try (PreparedStatement comando =
                                 conexao.prepareStatement(sqlItem)) {

                        comando.setInt(1, idVenda);
                        comando.setInt(2, item.getProduto().getId());
                        comando.setString(3, nomeProduto);
                        comando.setInt(4, item.getQuantidade());
                        comando.setBigDecimal(
                                5, item.getPrecoUnitario().setScale(
                                        2, RoundingMode.UNNECESSARY
                                )
                        );

                        comando.executeUpdate();
                    }
                }

                conexao.commit();
                return idVenda;

            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw e;
            }
        }
    }

    public ArrayList<String> listarHistorico() throws SQLException {
        ArrayList<String> linhas = new ArrayList<>();

        String sql = """
                SELECT v.id AS venda_id, v.data, v.total,
                       i.nome_produto, i.quantidade, i.preco_unitario
                FROM vendas v
                JOIN itens_venda i ON i.venda_id = v.id
                ORDER BY v.id DESC, i.id
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            int vendaAnterior = -1;

            while (resultado.next()) {
                int idVenda = resultado.getInt("venda_id");

                if (idVenda != vendaAnterior) {
                    linhas.add(
                            "\nVenda #" + idVenda
                                    + " | Data: " + resultado.getTimestamp("data")
                                    + " | Total: R$ "
                                    + resultado.getBigDecimal("total")
                                    .toPlainString()
                    );

                    vendaAnterior = idVenda;
                }

                int quantidade = resultado.getInt("quantidade");
                BigDecimal preco = resultado.getBigDecimal("preco_unitario");
                BigDecimal subtotal = preco.multiply(
                        BigDecimal.valueOf(quantidade)
                );

                linhas.add(
                        "  Produto: " + resultado.getString("nome_produto")
                                + " | Quantidade: " + quantidade
                                + " | Preço unitário: R$ " + preco.toPlainString()
                                + " | Subtotal: R$ " + subtotal.toPlainString()
                );
            }
        }

        return linhas;
    }
}