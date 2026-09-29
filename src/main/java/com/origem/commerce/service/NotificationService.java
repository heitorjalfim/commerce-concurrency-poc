package com.origem.commerce.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Async 
    public void enviarNotificacaoVenda(Long produtoId) {
        try {
            System.out.println("[FILA ASSINCRONA] Iniciando envio de e-mail ao artesão para o produto ID: " + produtoId);
            Thread.sleep(3000); 
            System.out.println("[FILA ASSINCRONA] E-mail enviado com SUCESSO sem travar a API do cliente!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}