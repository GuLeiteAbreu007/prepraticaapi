package com.example.apiestoque2.service;

import com.example.apiestoque2.models.Produto;
import com.example.apiestoque2.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository){
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> buscarTodos(){
        return produtoRepository.findAll();
    }

    public Produto buscarProduto(Long id){
        return produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Produto excluirProduto(Long id){
        Optional<Produto> prod = produtoRepository.findById(id);
        if(prod.isPresent()){
            produtoRepository.deleteById(id);
            return prod.get();
        }
        return null;
    }

    public List<Produto> buscarProdutoPorNomeEPrecoMenorQue(String nome, double preco){
        return produtoRepository.findByNomeLikeIgnoreCaseAndPrecoLessThan(nome, preco);
    }

    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto);
    }
}
