package com.example.apiestoque2.models;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Representa um produto no sistema")
public class Produto {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Schema(description = "ID único do produto", example = "1234")
    private long id;
    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, message = "O nome deve ter pelo menos 2 caracteres")
    @Schema(description = "Nome do produto", example = "Hamburguer de frango")
    private String nome;
    @Schema(description = "Descrição detalhada do produto",
            example = "Hamburguer de frango congelado de 500g")
    private String descricao;
    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser pelo menos 0")
    @Schema(description = "Preço do produto", example = "1999.99")
    private double preco;
    @NotNull(message = "A quantidade não pode ser nula")
    @Min(value = 0, message = "A quantidade deve ser pelo menos 0")
    @Column(name="quantidadeestoque")
    @Schema(description = "Quantidade disponível em estoque", example = "50")
    private int quantidadeEstoque;

    public Produto(){}

    public Produto(long id, String nome, String descricao, double preco, int quantidadeEstoque){
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Produto(String nome, String descricao, double preco, int quantidadeEstoque){
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getNome() {
        return nome;
    }

    public long getId() {
        return id;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                '}';
    }
}
