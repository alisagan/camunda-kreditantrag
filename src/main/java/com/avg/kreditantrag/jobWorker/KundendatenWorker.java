package com.avg.kreditantrag.jobWorker;

import com.avg.kreditantrag.service.KundeService;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KundendatenWorker {

    private final KundeService kundenService;
    private static final Logger logger = LoggerFactory.getLogger(KundendatenWorker.class);

    public KundendatenWorker(KundeService kundenService) {
        this.kundenService = kundenService;
    }

    @JobWorker(type = "kreditwuerdigkeit-abfragen")
    public Map<String, Object> handle(
            @Variable String vorname,
            @Variable String nachname,
            @Variable double monatliches_nettoeinkommen,
            @Variable double kreditbetrag
    ) {
        String name = vorname + " " + nachname;
        Map<String, Object> kunde = kundenService.findeKundeNachName(name);

        if (kunde == null || kunde.isEmpty()) {
            throw new RuntimeException("Keine Kundendaten gefunden für: " + name);
        }

        double vermoegen = Double.parseDouble(kunde.get("vermoegen").toString());
        double verbindlichkeiten = Double.parseDouble(kunde.get("verbindlichkeiten").toString());

        int score = 0;

        if (monatliches_nettoeinkommen > 4000) score += 3;
        else if (monatliches_nettoeinkommen > 2500) score += 2;
        else if (monatliches_nettoeinkommen > 1500) score += 1;

        if (verbindlichkeiten < 500) score += 2;
        else if (verbindlichkeiten < 1000) score += 1;

        if (vermoegen > 10000) score += 2;
        else if (vermoegen > 5000) score += 1;

        if (kreditbetrag < 25000) score += 2;
        else if (kreditbetrag < 50000) score += 1;

        boolean kreditwuerdig = score >= 6;

        Map<String, Object> result = new HashMap<>();
        result.put("vermoegen", vermoegen);
        result.put("verbindlichkeiten", verbindlichkeiten);
        result.put("score", score);
        result.put("kreditwuerdig", kreditwuerdig);
        return result;
    }
}
