package kz.zzhalelov.bailanysta.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    // Считываем GEMINI_API_KEY из системного окружения, либо gemini.api.key из properties
    @Value("${GEMINI_API_KEY:${gemini.api.key:}}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generatePostContent(String topic) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Ошибка: API ключ Gemini не задан в конфигурации приложения.";
        }

        // Использование актуальной модели gemini-2.0-flash
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" + apiKey;


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "Напиши короткий, увлекательный пост для социальной сети на тему: " + topic + ". Длина до 280 символов. Используй эмодзи.";

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            List candidateList = (List) response.getBody().get("candidates");
            Map candidate = (Map) candidateList.get(0);
            Map content = (Map) candidate.get("content");
            List parts = (List) content.get("parts");
            Map part = (Map) parts.get(0);
            return (String) part.get("text");
        } catch (Exception e) {
            return "Не удалось сгенерировать текст. Ошибка: " + e.getMessage();
        }
    }
}