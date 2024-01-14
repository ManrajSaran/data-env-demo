package flink.jobs;

import flink.sources.KafkaSourceFactory;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.pinot.common.utils.http.HttpClient;
import org.apache.pinot.connector.flink.common.FlinkRowGenericRowConverter;
import org.apache.pinot.connector.flink.http.PinotConnectionUtils;
import org.apache.pinot.connector.flink.sink.PinotSinkFunction;
import org.apache.pinot.controller.helix.ControllerRequestClient;
import org.apache.pinot.spi.config.table.TableConfig;
import org.apache.pinot.spi.data.Schema;
import org.apache.pinot.spi.utils.builder.ControllerRequestURLBuilder;

public class SinglePinotJob {
    public static final RowTypeInfo TEST_TYPE_INFO =
            new RowTypeInfo(new TypeInformation[]{Types.STRING}, new String[]{"text"});

    public void run() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSourceFactory.getTestKafkaSource();

        DataStream<Row> stream = env.fromSource(source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source").flatMap(new FlatMapFunction<String, Row>() {
            @Override
            public void flatMap(String s, Collector<Row> collector) throws Exception {
                Row row = new Row(1);
                row.setField(0, s);
                collector.collect(row);
            }
        });

        stream.print();

        HttpClient httpClient = HttpClient.getInstance();
        ControllerRequestClient client = new ControllerRequestClient(
                ControllerRequestURLBuilder.baseUrl("http://localhost:9000"), httpClient
        );
        Schema schema = PinotConnectionUtils.getSchema(client, "test");
        TableConfig tableConfig = PinotConnectionUtils.getTableConfig(client, "test", "OFFLINE");

        stream.addSink(new PinotSinkFunction<Row>(
                new FlinkRowGenericRowConverter(TEST_TYPE_INFO),
        tableConfig, schema));

        env.execute();
    }
}
