package com.avg.kreditantrag.jobWorker;

import com.avg.kreditantrag.service.ZinssatzService;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RisikoAnalyseWorker {

    private final ZinssatzService zinssatzService;
    private static final Logger logger = LoggerFactory.getLogger(RisikoAnalyseWorker.class);

    public RisikoAnalyseWorker(ZinssatzService zinssatzService) {
        this.zinssatzService = zinssatzService;
    }

    @JobWorker(type = "risiko-analysieren")
    public Map<String, Object> handle(
            @Variable int score,
            @Variable double kreditbetrag,
            @Variable double monatliches_nettoeinkommen,
            @Variable double verbindlichkeiten,
            @Variable int laufzeit // in Monaten
    ) {
        Map<String, Object> result = new HashMap<>();

        // Risikoanalyse
        double risikoFaktor = (verbindlichkeiten / (monatliches_nettoeinkommen + 1)) * 100;
        risikoFaktor = Math.round(risikoFaktor * 100) / 100.0;
        double basisZinssatz = zinssatzService.findeBasiszinssatzNachScore(score);
        double risikoZuschlag = Math.min(risikoFaktor * 0.1, 5.0);
        double effektiverZinssatz = basisZinssatz + (score < 6 ? risikoZuschlag + 2.0 : risikoZuschlag);
        effektiverZinssatz = Math.round(effektiverZinssatz * 100.0) / 100.0;

        // Laufzeit in Jahren
        double laufzeitJahre = laufzeit / 12.0;

        // Zinsbetrag berechnen
        double zinsbetrag = kreditbetrag * effektiverZinssatz / 100.0 * laufzeitJahre;
        zinsbetrag = Math.round(zinsbetrag * 100.0) / 100.0;

        // Gesamtsumme + Monatsrate
        double gesamtsumme = kreditbetrag + zinsbetrag;
        gesamtsumme = Math.round(gesamtsumme * 100.0) / 100.0;
        double monatsrate = gesamtsumme / laufzeit;
        monatsrate = Math.round(monatsrate * 100.0) / 100.0;

        // Kreditvertrag-ID erzeugen
        String kreditvertragId = UUID.randomUUID().toString();

        // Ergebnis-Map füllen
        result.put("effektiverZinssatz", effektiverZinssatz);
        result.put("risikoFaktor", risikoFaktor);
        result.put("zinsbetrag", zinsbetrag);
        result.put("monatsrate", monatsrate);
        result.put("gesamtsumme", gesamtsumme);
        result.put("laufzeit", laufzeit);
        result.put("kreditvertragId", kreditvertragId);

        logger.info("Risikoanalyse abgeschlossen: score={}, risikoFaktor={}, zinssatz={}%, zinsen={}, rate={}, id={}",
                score, risikoFaktor, effektiverZinssatz, zinsbetrag, monatsrate, kreditvertragId);

        return result;
    }
}
