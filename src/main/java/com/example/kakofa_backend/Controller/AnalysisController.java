package com.example.kakofa_backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private RestTemplate restTemplate; // RestTemplate ile HTTP isteklerini kolayca yönetiyoruz

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileAndAnalyze(@RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            // PDF dosyasını metne dönüştürme işlemi
            String pdfText = extractTextFromPdf(file);

            // Python API'ye metni gönderme işlemi
            String analysisResult = sendTextToPython(pdfText);

            // Başarılı bir yanıt döndür
            return new ResponseEntity<>(analysisResult, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Dosya işlenirken bir hata oluştu!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PDF dosyasını metne dönüştüren metot
    private String extractTextFromPdf(MultipartFile file) {
        // Burada PDF metnine dönüştürme işlemini yapmalısınız.
        return "Metne dönüştürülmüş içerik"; // Örnek bir metin
    }

    // Python API'ye metni gönderen metot
    private String sendTextToPython(String text) {
        String url = "http://localhost:5000/analyze"; // Flask API URL'si

        // JSON formatında metin
        String jsonInputString = "{\"text\": \"" + text + "\"}";

        // RestTemplate ile HTTP POST isteği gönderiyoruz
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonInputString, headers);

        // RestTemplate ile POST isteği gönderiyoruz
        String response = restTemplate.postForObject(url, entity, String.class);

        return response; // Python'dan gelen yanıtı geri döndürüyoruz
    }
}
