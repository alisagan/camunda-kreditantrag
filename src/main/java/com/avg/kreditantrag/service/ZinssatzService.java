package com.avg.kreditantrag.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ZinssatzService {

    private final RestTemplate restTemplate = new RestTemplate();

    public double findeBasiszinssatzNachScore(int score) {
        String url = "http://localhost:3000/zinssaetze?score=" + score;

        Map[] daten = restTemplate.getForObject(url, Map[].class);

        if (daten != null && daten.length > 0 && daten[0].get("basiszinssatz") != null) {
            return Double.parseDouble(daten[0].get("basiszinssatz").toString());
        } else {
            throw new RuntimeException("Kein Basiszinssatz gefunden für Score: " + score);
        }
    }
}
