package anbrain.qa.rococo.jupiter.extension;

import anbrain.qa.rococo.service.kafka.KafkaService;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaExtension implements SuiteExtension {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final KafkaService kafkaService = new KafkaService();

    @Override
    public void beforeSuite(ExtensionContext context) {
        executor.submit(kafkaService);
        executor.shutdown();
    }

    @Override
    public void afterSuite() {
        kafkaService.shutdown();
    }
}
