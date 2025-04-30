package com.example.kakofa_backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.example.kakofa_backend.Model.AnalysisResult;
import com.example.kakofa_backend.Model.User;
import com.example.kakofa_backend.Repository.AnalysisResultRepository;
import com.example.kakofa_backend.Repository.UserRepository;

@Service
public class AnalysisResultService {

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate; // RestTemplate kullanarak HTTP isteği göndereceğiz

    // Kullanıcı ID'si ile analiz sonucu kaydeden metot
    public void saveAnalysisResult(Long userId, String filePath, String analysisResult) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        AnalysisResult result = new AnalysisResult();
        result.setUser(user);
        result.setFilePath(filePath);
        result.setAnalysisResult(analysisResult);
        result.setCreatedAt(java.time.LocalDateTime.now().toString());

        // Analiz sonucunu veritabanına kaydet
        analysisResultRepository.save(result);
    }

    // Flask API'ye metin gönderip analiz sonucu döndürme
    public String sendTextForAnalysis(String pdfText) {
        String url = "http://localhost:5000/analyze"; // Flask API'nin URL'si

        // JSON payload'ı oluştur
        String jsonPayload = "{ \"text\": \"" + pdfText + "\" }";

        // Başlıkları ayarla
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // HTTP isteği için entity oluştur
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        try {
            // POST isteği gönder ve sonuçları al
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Eğer başarılıysa, gelen sonuç döndürülür
                return response.getBody();
            } else {
                return "Analiz sırasında bir hata oluştu.";
            }
        } catch (HttpClientErrorException e) {
            // Hata durumunda uygun mesajı döndür
            return "Flask API'sine bağlanırken bir hata oluştu: " + e.getMessage();
        }
    }
}
