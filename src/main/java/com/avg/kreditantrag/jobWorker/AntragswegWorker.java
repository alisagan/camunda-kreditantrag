package com.avg.kreditantrag.jobWorker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class AntragswegWorker {
    private final Random random = new Random();
    private static final Logger logger = LoggerFactory.getLogger(AntragswegWorker.class);

    @JobWorker(type = "antragsweg-auswahl")
    public Map<String, Object> wähleAntragsweg(){
        String[] möglichkeiten = {"online", "schalter"};
        String zufälligerWeg = möglichkeiten[random.nextInt(möglichkeiten.length)];

        logger.info("Antragsweg zufällig gewählt: {}", zufälligerWeg);

        return Map.of("antragsweg", zufälligerWeg);
    }
}
