package com.artun.demo2FlinkAPI.service;

import com.artun.demo2FlinkAPI.model.VoiceStream;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

/**
 *
 * Created by I.C.ARTUN
 */

@Slf4j
@Service
public class FlinkService {

    public void processTopicAndCassandraService() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
        properties.setProperty("group.id", "voice-group");
        properties.setProperty("auto.offset.reset", "latest");

        DataStream<String> streamVoice = env
                .addSource(new FlinkKafkaConsumer<>("voice-topic", new SimpleStringSchema(), properties));

        DataStream<Tuple5<String, String, Long, Date, Date>> result = streamVoice
                .map(new MapToVoiceStream())
                .keyBy(new KeySelector<VoiceStream, String>() {
                    @Override
                    public String getKey(VoiceStream voiceStream) throws Exception {
                        return voiceStream.getPhoneNumber();
                    }
                })
                .timeWindow(Time.seconds(3))
                .apply(new WindowFunction<VoiceStream, Tuple5<String, String, Long, Date, Date>, String, TimeWindow>() {
                    @Override
                    public void apply(String phoneNumber, TimeWindow timeWindow, Iterable<VoiceStream> iterable, Collector<Tuple5<String, String, Long, Date, Date>> collector) throws Exception {

                        for (VoiceStream voiceStream : iterable) {

                            if (voiceStream.getActive() && voiceStream.getDuration() >= 50L) {
                                collector.collect(new Tuple5<>(UUID.randomUUID().toString(), voiceStream.getPhoneNumber(), voiceStream.getDuration(), voiceStream.getCallStartDate(), new Date()));
                            }
                        }
                    }
                })
                .setParallelism(5);

        CassandraSink.addSink(result)
                .setQuery("INSERT INTO sample.voice_stream_result(id, phone_number, duration, call_start_date, created_at) values (?, ?, ?, ?, ?);")
                .setHost("127.0.0.1")
                .build();

        env.execute();
    }

    public static class MapToVoiceStream implements MapFunction<String, VoiceStream> {

        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public VoiceStream map(String s) throws Exception {

            JsonNode voiceStreamDataJson = mapper.readTree(s);
            JsonNode phoneNumber = voiceStreamDataJson.get("phoneNumber");
            JsonNode duration = voiceStreamDataJson.get("duration");
            JsonNode callStartDate = voiceStreamDataJson.get("callStartDate");
            JsonNode active = voiceStreamDataJson.get("active");

            return new VoiceStream(phoneNumber.textValue(), duration.asLong(), new Date(callStartDate.longValue() * 1000), active.asBoolean());
        }
    }

}
