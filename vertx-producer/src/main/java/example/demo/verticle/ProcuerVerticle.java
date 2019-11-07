package example.demo.verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.demo.config.ApplicationConfiguration;
import example.demo.data.Payload;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Slf4j
@Component
public class ProcuerVerticle extends AbstractVerticle{

    private final String topic = "raw-data";

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    private KafkaProducer producer;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start() {

        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfiguration.bootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "1");

        producer = KafkaProducer.create(vertx, config);

        vertx.eventBus().consumer("events", msg -> {
            try {
                String data = objectMapper.writeValueAsString(new Payload(msg.body().toString()));
                KafkaProducerRecord<String, String> record = KafkaProducerRecord.create("raw-data", data);
                producer.write(record, res -> {
                    if (((AsyncResult<RecordMetadata>) res).succeeded())
                        log.info("ddd");
                    else
                        log.error("coludnt publish record " + data);
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

    }


    @Override
    public void stop() {
        if (producer != null) {
            producer.close(voidAsyncResult -> {
                AsyncResult res = (AsyncResult) voidAsyncResult;
                if (res.succeeded())
                    log.info("Producer is now closed");
                else
                    log.error("error closing producer");
            });
        }
    }

}
