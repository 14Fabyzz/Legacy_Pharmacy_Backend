package com.legacy.pharmacy.reportes.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {
    private List<Candidate> candidates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;
    }

    // Helper method to easily get the generated text
    public String getGeneratedText() {
        if (candidates != null && !candidates.isEmpty()
                && candidates.get(0).getContent() != null
                && candidates.get(0).getContent().getParts() != null
                && !candidates.get(0).getContent().getParts().isEmpty()) {

            return candidates.get(0).getContent().getParts().get(0).getText();
        }
        return "Resumen no disponible o respuesta incompleta por parte de la IA.";
    }
}
