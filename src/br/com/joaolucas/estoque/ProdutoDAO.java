package br.com.joaolucas.estoque;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProdutoDAO {

    public int cadastrar(Produto produto) throws SQLException {
        if (produto == null) {
            throw new IllegalArgumentException(
                    "O produto não pode ser nulo."
            );
        }

        String sql = """
                INSERT INTO produtos
                    (nome, preco, quantidade_estoque, estoque_minimo)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, produto.getNome());
            comando.setBigDecimal(2, produto.getPreco());
            comando.setInt(3, produto.getQuantidadeEstoque());
            comando.setInt(4, produto.getEstoqueMinimo());

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("id");
                }

                throw new SQLException(
                        "O banco não retornou o ID do produto."
                );
            }
        }
    }

    public ArrayList<Produto> listar() throws SQLException {
        String sql = """
                SELECT id, nome, preco, quantidade_estoque, estoque_minimo
                FROM produtos
                ORDER BY id
                """;

        ArrayList<Produto> produtos = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                produtos.add(converterParaProduto(resultado));
            }
        }

        return produtos;
    }

    public Produto buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT id, nome, preco, quantidade_estoque, estoque_minimo
                FROM produtos
                WHERE id = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return converterParaProduto(resultado);
                }

                return null;
            }
        }
    }

    public void atualizar(Produto produto) throws SQLException {
        if (produto == null) {
            throw new IllegalArgumentException(
                    "O produto não pode ser nulo."
            );
        }

        String sql = """
                UPDATE produtos
                SET nome = ?, preco = ?, estoque_minimo = ?
                WHERE id = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, produto.getNome());
            comando.setBigDecimal(2, produto.getPreco());
            comando.setInt(3, produto.getEstoqueMinimo());
            comando.setInt(4, produto.getId());

            if (comando.executeUpdate() == 0) {
                throw new IllegalArgumentException(
                        "Produto não encontrado."
                );
            }
        }
    }

    public void reporEstoque(int id, int quantidade) throws SQLException {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        String sql = """
                UPDATE produtos
                SET quantidade_estoque = quantidade_estoque + ?
                WHERE id = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, quantidade);
            comando.setInt(2, id);

            if (comando.executeUpdate() == 0) {
                throw new IllegalArgumentException(
                        "Produto não encontrado."
                );
            }
        }
    }

    public ArrayList<Produto> listarEstoqueBaixo() throws SQLException {
        String sql = """
                SELECT id, nome, preco, quantidade_estoque, estoque_minimo
                FROM produtos
                WHERE quantidade_estoque <= estoque_minimo
                ORDER BY id
                """;

        ArrayList<Produto> produtos = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                produtos.add(converterParaProduto(resultado));
            }
        }

        return produtos;
    }

    private Produto converterParaProduto(ResultSet resultado)
            throws SQLException {

        return new Produto(
                resultado.getInt("id"),
                resultado.getString("nome"),
                resultado.getBigDecimal("preco"),
                resultado.getInt("quantidade_estoque"),
                resultado.getInt("estoque_minimo")
        );
    }
}