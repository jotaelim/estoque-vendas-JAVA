package br.com.joaolucas.estoque;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Venda {
    private int id;
    private LocalDateTime data;
    private ArrayList<ItemVenda> itens;
    private boolean confirmada = false;

    public Venda(int id){
        this.id = id;
        this.data = LocalDateTime.now();
        this.itens = new ArrayList<>();
    }

    public int getId(){
        return this.id;
    }
    public LocalDateTime getData(){
        return this.data;
    }
    public ArrayList<ItemVenda> getItens(){
        return new ArrayList<>(this.itens);
    }

    public void adicionarItem(ItemVenda item){
        if(this.confirmada){
            throw new IllegalArgumentException("Não é possível adicionar  itens a uma venda confirmada.");
        }
        if(item == null){
            throw new IllegalArgumentException("O item não pode ser vazio");
        } else{
            for (ItemVenda existente : this.itens){
                if (existente.getProduto().getId() == item.getProduto().getId()) {
                    throw new IllegalArgumentException("Produto já adicionado. Informe a quantidade em um único item.");
                }
            }
            this.itens.add(item);
        }
    }

    public BigDecimal calcularTotal(){
        BigDecimal total = BigDecimal.ZERO;
        for(ItemVenda item0  : itens){
            total = total.add(item0.calcularSubtotal());
        }
        return total;
    }

    public void validarEstoque(){
        if (this.itens.isEmpty()){
            throw new IllegalArgumentException("A venda precisa ter itens.");
        }

        for (ItemVenda item : this.itens){
            if(item.getQuantidade() > item.getProduto().getQuantidadeEstoque()){
                throw  new IllegalArgumentException("Estoque insuficiente");
            }
        }
    }

    public void confirmar(){
        if (this.confirmada){
            throw new IllegalArgumentException("A venda já foi confirmada.");
        }
        validarEstoque();
        for (ItemVenda item : this.itens){
            item.getProduto().baixarEstoque(item.getQuantidade());
        }
        this.confirmada = true;
    }
}
