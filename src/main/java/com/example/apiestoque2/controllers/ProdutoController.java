package com.example.apiestoque2.controllers;

import com.example.apiestoque2.models.Produto;
import com.example.apiestoque2.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;
    
    public ProdutoController(ProdutoService produtoService){
        this.produtoService = produtoService;
    }
    
    @GetMapping("/selecionar")
    @Operation(summary = "Lista todos os produtos",
            description = "Retorna uma lista de todos od produtos disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de produtos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            ),

            @ApiResponse(responseCode = "400",
                    description = "Requisição com algum erro nos dados fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "502",
                    description = "Erro no servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            )
    })
    public List<Produto> listarProdutos(){
        return produtoService.buscarTodos();
    }

    @PostMapping("/inserir")
    @Operation(summary = "Insere produto", description = "Insere um produto no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Produto inserido com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "400",
                    description = "Requisição com algum erro nos dados fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "502",
                    description = "Erro no servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<Map<String, String>> inserirProduto(@Valid @RequestBody Produto produto, BindingResult result){
        try{
            if(result.hasErrors()) return generateResponseError(result);

            // inserindo
            Produto prod = produtoService.salvarProduto(produto);
            if(isNotIdFine(prod.getId())) throw new RuntimeException();

            return ResponseEntity.ok(null);
        }catch(ClassCastException cce){
            return generateResponseClassCast();
        }catch (RuntimeException re){
            return generateResponseRuntime();
        }
    }

    @DeleteMapping("/excluir/{id}")
    @Operation(summary = "Exclui produto por ID", description = "Remove um produto do sistema pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Produto excluído com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            ),

            @ApiResponse(responseCode = "400",
                    description = "Requisição com algum erro nos dados fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "502",
                    description = "Erro no servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<String> excluirProduto(@PathVariable Long id){
        try{
            // verificacao id
            if(isNotIdFine(id)) ResponseEntity.badRequest().body("ID não pode ser zero ou negativo.");

            // excluindo e verificando se encontrou id
            if(produtoService.excluirProduto(id) == null) throw new RuntimeException();
            return ResponseEntity.ok("Produto excluido com sucesso");
        }catch (RuntimeException re){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto com ID " + id + " não encontrado.");
        }
    }

    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Atualiza produto por ID", description = "Atualiza um produto do sistema pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            ),

            @ApiResponse(responseCode = "400",
                    description = "Requisição com algum erro nos dados fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "502",
                    description = "Erro no servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<Map<String, String>> atualizarProduto(@PathVariable Long id, @Valid @RequestBody Produto produtoAtualizado, BindingResult result){
        try{
            // verificacao id
            if(isNotIdFine(id)) ResponseEntity.badRequest().body("ID não pode ser zero ou negativo.");
            Produto produto = produtoService.buscarProduto(id);

            // setando e salvando
            produto.setNome(produtoAtualizado.getNome());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());

            if(result.hasErrors()) return generateResponseError(result);

            Produto prodInserido = produtoService.salvarProduto(produto);

            // verificando se deu certo a inserção 2
            if(produto.getId() != prodInserido.getId()){
                Map<String, String> retorno = new HashMap<>();
                retorno.put("produto", "não encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(retorno);
            }
            return ResponseEntity.ok(null);
        }catch(ClassCastException cce){
            return generateResponseClassCast();
        }catch (RuntimeException re){
            return generateResponseRuntime();
        }
    }

    @PatchMapping("/atualizarParcial/{id}")
    @Operation(summary = "Atualiza produto parcialmente por ID", description = "Atualiza um produto parcialmente do sistema pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            ),

            @ApiResponse(responseCode = "400",
                    description = "Requisição com algum erro nos dados fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "502",
                    description = "Erro no servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<Map<String, String>> atualizarProdutoParcial(@PathVariable Long id, @RequestBody Map<String, Object> updates){
        try{
            // verificacao id
            if(isNotIdFine(id)) ResponseEntity.badRequest().body("ID não pode ser zero ou negativo.");

            Produto produto = produtoService.buscarProduto(id);
            List<String> campos = new ArrayList<>();

            if(updates.containsKey("nome")){
                produto.setNome((String)(updates.get("nome")));
                campos.add("nome");
            }
            if(updates.containsKey("descricao")){
                produto.setDescricao((String)(updates.get("descricao")));
                campos.add("descricao");
            }
            if(updates.containsKey("preco")){
                try{
                    produto.setPreco((Double)(updates.get("preco")));
                }catch(ClassCastException cce){
                    produto.setPreco((Integer)(updates.get("preco")));
                }
                campos.add("preco");
            }
            if(updates.containsKey("quantidadeEstoque")){
                produto.setQuantidadeEstoque((Integer)(updates.get("quantidadeEstoque")));
                campos.add("quantidadeEstoque");
            }

            ResponseEntity<Map<String, String>> erros = generateResponseError(produto, campos);
            if (erros != null) return erros;

            produtoService.salvarProduto(produto);
            return ResponseEntity.ok(null);
        }catch(ClassCastException cce){
            return generateResponseClassCast();
        }catch(RuntimeException re){
            return generateResponseRuntime();
        }
    }

    @GetMapping("/buscarPorNomeEPrecoMenorQue")
    @Operation(summary = "Busca produto com base no nome e preço menor que",
            description = "Busca produto com base no nome e preço menor que")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Produtos encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            ),

            @ApiResponse(responseCode = "400",
                    description = "Requisição com algum erro nos dados fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "502",
                    description = "Erro no servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class)
                    )
            ),

            @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public List<Produto> buscarPorNomeEPrecoMenorQue(@RequestParam String nome, @RequestParam double preco){
        return produtoService.buscarProdutoPorNomeEPrecoMenorQue(nome, preco);
    }

    public static boolean isNotIdFine(Long id){
        return id <= 0;
    }

    public static ResponseEntity<Map<String, String>> generateResponseError(Produto produto, List<String> campos){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        boolean haveError = false;
        Map<String, String> retorno = new HashMap<>();
        for(String campo : campos){
            Set<ConstraintViolation<Produto>> violations = validator.validateProperty(produto, campo);
            if (!violations.isEmpty()) {
                retorno.put(campo, violations.iterator().next().getMessage());
                haveError = true;
            }
        }
        return haveError ? ResponseEntity.badRequest().body(retorno) : null;
    }

    public static ResponseEntity<Map<String, String>> generateResponseError(BindingResult result){
        Map<String, String> retorno = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            retorno.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(retorno);
    }

    public static ResponseEntity<Map<String, String>> generateResponseClassCast(){
        Map<String, String> retorno = new HashMap<>();
        retorno.put("preco", "deve ser numérico");
        retorno.put("quantidadeEstoque", "deve ser numérico");
        return ResponseEntity.badRequest().body(retorno);
    }

    public static ResponseEntity<Map<String, String>> generateResponseRuntime(){
        Map<String, String> retorno = new HashMap<>();
        retorno.put("geral", "erro na requisição");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(retorno);
    }
}
