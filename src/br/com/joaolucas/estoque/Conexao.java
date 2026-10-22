package br.com.joaolucas.estoque;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    public static Connection conectar() throws SQLException{
        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PASSWORD");

        if (url == null || usuario == null || senha == null){
            throw new IllegalArgumentException(
                    "Configure DB_URL, DB_USER e DB_PASSWORD no IntelliJ.");
        }

        return DriverManager.getConnection(url, usuario, senha);
    }

    public static void main(String[] args){
        try (Connection conexao = conectar()){
            System.out.println(
                    "Conectado ao banco: " + conexao.getCatalog());
        } catch (SQLException | IllegalArgumentException e){
            System.out.println("Falha na conexão: " + e.getMessage());
        }
    }
}
