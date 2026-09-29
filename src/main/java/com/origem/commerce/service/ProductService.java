package com.origem.commerce.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.origem.commerce.model.Product;
import com.origem.commerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ProductService {
    private final ProductRepository productRepo;
    private final NotificationService notificationService; 

    @Transactional 
    public void comprar(Long id) {
        Product produto = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado"));
        if (produto.getEstoque() <= 0) {
            throw new IllegalStateException("Produto sem estoque");
        }
        produto.setEstoque(produto.getEstoque() - 1);
        productRepo.save(produto);
        notificationService.enviarNotificacaoVenda(id);
    }
}