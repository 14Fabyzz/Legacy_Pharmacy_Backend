package com.legacy.pharmacy.reportes.service;

import com.legacy.pharmacy.reportes.dto.gemini.GeminiRequest;
import com.legacy.pharmacy.reportes.dto.gemini.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiClientService {

    private final RestClient restClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * Envía un prompt a Google Gemini y retorna la historia/análisis generado en
     * String.
     * 
     * @param prompt El texto de entrada para la IA
     * @return El resumen ejecutivo en texto plano.
     */
    public String generateContentSync(String prompt) {

        // 1. Construir el Request Body en la estructura que espera la API de Gemini
        GeminiRequest.Part part = GeminiRequest.Part.builder().text(prompt).build();
        GeminiRequest.Content content = GeminiRequest.Content.builder().parts(List.of(part)).build();
        GeminiRequest requestBody = GeminiRequest.builder().contents(List.of(content)).build();

        String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;

        log.info("Enviando petición a Gemini API via RestClient...");

        try {
            GeminiResponse response = restClient.post()
                    .uri(urlWithKey)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiResponse.class);
            return response != null && response.getGeneratedText() != null
                    ? response.getGeneratedText()
                    : "Respuesta vacía de Gemini.";
        } catch (Exception e) {
            log.error("Error al comunicarse con Gemini API: {}", e.getMessage());
            return "No se pudo generar el resumen ejecutivo debido a un error de comunicación con la IA.";
        }
    }
}
