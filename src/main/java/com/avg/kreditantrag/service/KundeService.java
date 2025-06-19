package com.avg.kreditantrag.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class KundeService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> findeKundeNachName(String name) {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String url = "http://localhost:3000/kunden?name=" + encodedName;

        Map[] kunden = restTemplate.getForObject(url, Map[].class);

        if (kunden != null && kunden.length > 0) {
            return kunden[0];
        } else {
            throw new RuntimeException("Kunde '" + name + "' nicht gefunden.");
        }
    }
}
