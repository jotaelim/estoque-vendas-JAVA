package br.com.joaolucas.estoque;
import java.math.BigDecimal;

public class ItemVenda {
    private Produto produto;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemVenda(Produto produto, int quantidade){
        if(produto == null){
            throw new IllegalArgumentException("O produto não pode estar vazio.");
        } else if(quantidade <=  0){
            throw new IllegalArgumentException("A quantidade não pode estar vazia.");
        }
        this.produto  = produto;
        this.quantidade  = quantidade;
        this.precoUnitario  = produto.getPreco();
    }

    public Produto getProduto(){
        return this.produto;
    }
    public int getQuantidade(){
        return this.quantidade;
    }
    public BigDecimal getPrecoUnitario(){
        return this.precoUnitario;
    }

    public BigDecimal calcularSubtotal(){
        return this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }
}
