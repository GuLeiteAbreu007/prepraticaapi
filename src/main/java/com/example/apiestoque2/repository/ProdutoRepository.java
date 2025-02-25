package com.example.apiestoque2.repository;
import com.example.apiestoque2.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @Modifying
    @Query("DELETE FROM Produto e where e.id = ?1")
    void deleteById(Long id);

    List<Produto> findByNomeLikeIgnoreCaseAndPrecoLessThan(String nome, double preco);
}
