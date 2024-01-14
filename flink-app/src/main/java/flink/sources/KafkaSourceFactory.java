package flink.sources;

import flink.utilities.KafkaUtilities;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

public class KafkaSourceFactory {
    public static KafkaSource<String> getTestKafkaSource() {
        return KafkaSource.<String>builder()
                .setTopics("test-topic")
                .setProperties(KafkaUtilities.getTestKafkaProperties())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.earliest())
                .build();
    }
}
