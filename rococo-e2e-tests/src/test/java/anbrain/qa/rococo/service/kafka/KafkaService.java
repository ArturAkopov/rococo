package anbrain.qa.rococo.service.kafka;

import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.utils.MapWithWait;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaService implements Runnable {

    private static final AtomicBoolean isRun = new AtomicBoolean(false);
    private static final Config CFG = Config.getInstance();
    private static final Properties properties = new Properties();
    private static final ObjectMapper om = new ObjectMapper();
    private static final MapWithWait<String, UserJson> store = new MapWithWait<>();

    static {
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    }

    private final List<String> topics;
    private final Consumer consumer;

    public KafkaService(List<String> topics) {
        this.topics = topics;
        this.consumer = new KafkaConsumer(properties);
    }

    public KafkaService() {
        this(CFG.kafkaTopics());
    }

    public static UserJson getUserJson(String username) throws InterruptedException {
        return store.get(username, 5000L);
    }

    @Override
    public void run() {
        try {
            isRun.set(true);
            consumer.subscribe(topics);
            while (isRun.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
                for (ConsumerRecord<String, String> record : records) {
                    String userAsString = record.value();
                    UserJson userJson = om.readValue(userAsString, UserJson.class);
                    store.put(userJson.username(), userJson);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            consumer.close();
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        isRun.set(false);
    }
}
