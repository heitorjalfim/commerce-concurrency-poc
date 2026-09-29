package com.origem.commerce;

import com.origem.commerce.model.Product;
import com.origem.commerce.repository.ProductRepository;
import com.origem.commerce.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ProductConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testConsistenciaAcessoSimultaneo() throws InterruptedException {
        Product produto = new Product();
        produto.setNome("Vaso de Argila Exclusivo");
        produto.setEstoque(10);
        Product produtoSalvo = productRepository.save(produto);

        int numeroDeThreads = 15;
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);
        CountDownLatch latch = new CountDownLatch(1);

        AtomicInteger comprasComSucesso = new AtomicInteger(0);
        AtomicInteger falhasConcorrencia = new AtomicInteger(0);

        for (int i = 0; i < numeroDeThreads; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); 
                    productService.comprar(produtoSalvo.getId());
                    comprasComSucesso.incrementAndGet();
                } catch (Exception e) {
                    falhasConcorrencia.incrementAndGet();
                }
            });
        }

        latch.countDown(); 
        executor.shutdown();

        while (!executor.isTerminated()) {
            Thread.sleep(100);
        }

        Product produtoFinal = productRepository.findById(produtoSalvo.getId()).orElseThrow();

        Assertions.assertTrue(produtoFinal.getEstoque() >= 0, "O estoque jamais pode ser negativo!");
        
        Assertions.assertEquals(10, produtoFinal.getEstoque() + comprasComSucesso.get(), 
                "A soma das compras bem-sucedidas com o estoque final deve ser igual ao estoque inicial.");

        System.out.println("=== TESTE CONCLUIDO COM SUCESSO ===");
        System.out.println("Compras aprovadas: " + comprasComSucesso.get());
        System.out.println("Conflitos de concorrencia bloqueados: " + falhasConcorrencia.get());
        System.out.println("Estoque restante no banco: " + produtoFinal.getEstoque());
    }
}