package com.example.rsocketjson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class ClientConfiguration {

    @Bean
    public RSocketRequester rSocketRequester(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") RSocketRequester.Builder builder) {
        return builder
                .rsocketConnector(rSocketConnector -> rSocketConnector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2))))
                .dataMimeType(MediaType.APPLICATION_CBOR)
                .connectTcp("localhost", 7000)
                .block();
    }
}
