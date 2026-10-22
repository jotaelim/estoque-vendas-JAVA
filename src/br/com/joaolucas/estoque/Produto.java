package br.com.joaolucas.estoque;
import java.math.BigDecimal;

public class Produto {
    private int id;
    private String  nome;
    private BigDecimal preco;
    private int quantidadeEstoque;
    private int estoqueMinimo;

    public Produto(int id, String nome, BigDecimal preco, int quantidadeEstoque, int estoqueMinimo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        }
        if (quantidadeEstoque < 0 || estoqueMinimo < 0) {
            throw new IllegalArgumentException("O estoque inicial e o estoque mínimo não podem ser negativos.");
        }
            if (preco == null || preco.signum() < 0) {
                throw new IllegalArgumentException("O preço não pode ser nulo ou negativo.");
        }
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
    }
    public void setNome(String nome){
        if (nome == null || nome.isBlank()){
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        }

        this.nome = nome;
    }
    public void setPreco(BigDecimal preco){
        if (preco == null || preco.signum() < 0){
            throw new IllegalArgumentException(
                    "O preço não pode ser nulo ou negativo."
            );
        }

        this.preco = preco;
    }
    public void setEstoqueMinimo(int estoqueMinimo){
        if (estoqueMinimo < 0){
            throw new IllegalArgumentException(
                    "O estoque mínimo não pode ser negativo."
            );
        }

        this.estoqueMinimo = estoqueMinimo;
    }

    public int getId(){
        return this.id;
    }
    public String getNome(){
        return this.nome;
    }
    public BigDecimal getPreco(){
        return this.preco;
    }
    public int getQuantidadeEstoque(){
        return this.quantidadeEstoque;
    }
    public int getEstoqueMinimo(){
        return this.estoqueMinimo;
    }

    public void reporEstoque(int quantidade){
        if(quantidade <= 0){
            System.out.println("A quantidade deve ser maior que zero");
        } else{
            this.quantidadeEstoque += quantidade;
        }
    }

    public void baixarEstoque(int quantidade) {
        if (quantidade <= 0) {
            System.out.println("A quantidade deve ser maior que zero");
        } else if (quantidade > this.quantidadeEstoque) {
            System.out.println("Estoque insuficiente");
        } else {
            this.quantidadeEstoque -= quantidade;
        }
    }

    public boolean estaComEstoqueBaixo() {
        return this.quantidadeEstoque <= this.estoqueMinimo;
    }
}