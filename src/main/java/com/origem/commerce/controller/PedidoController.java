package com.origem.commerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.origem.commerce.service.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor 
@RequestMapping("/produtos")
public class PedidoController {

    private final ProductService productService;

    @PostMapping("/{id}/comprar")
    public ResponseEntity<String> comprar(@PathVariable Long id) {
        try {
            productService.comprar(id);
            return ResponseEntity.ok("Compra realizada!");
        } catch (ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(409).body("Erro de concorrencia: Item sendo comprado por outro cliente.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}