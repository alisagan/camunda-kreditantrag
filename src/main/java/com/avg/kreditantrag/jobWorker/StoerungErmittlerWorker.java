package com.avg.kreditantrag.jobWorker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class StoerungErmittlerWorker {
    private final Random random = new Random();
    private static final Logger logger = LoggerFactory.getLogger(StoerungErmittlerWorker.class);

    @JobWorker(type = "stoerung-entscheiden")
    public Map<String, Object> entscheideObStoerung() {
        String[] moeglichkeiten = {"ja", "nein"};
        String entscheidung = moeglichkeiten[random.nextInt(moeglichkeiten.length)];

        logger.info("Zufällige Entscheidung über Störung: {}", entscheidung);

        return Map.of("stoerung", entscheidung);
    }
}
