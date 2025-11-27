package com.kafkastreams.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkastreams.models.PaymentsEvent;
import com.kafkastreams.models.ProcessedPayments;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PaymentsStreamConfig {
    private final ObjectMapper mapper = new ObjectMapper();

    @Bean
    public KStream<String, String> paymentStream(StreamsBuilder builder) {

        KStream<String, String> inputStream = builder.stream("paymentstopic");

        KStream<String, String> processed = inputStream.mapValues(value -> {
            try {
                // 1. Convert JSON → Payment object
                PaymentsEvent payment = mapper.readValue(value, PaymentsEvent.class);

                // 2. Convert INR → USD
                double usd = payment.amountInInr / 83.0;

                // 3. Create output object
                ProcessedPayments output = new ProcessedPayments(payment.userId, usd);

                // 4. Convert output → JSON string
                return mapper.writeValueAsString(output);

            } catch (Exception e) {
                log.error(e.getMessage());
                return "{\"error\":\"INVALID_JSON\"}";
            }
        });

        // Write to output topic
        processed.to("processed-payments");

        return inputStream;
    }
}
