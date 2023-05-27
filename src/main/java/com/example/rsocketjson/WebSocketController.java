package com.example.rsocketjson;

import com.example.rsocketjson.model.StockFilter;
import com.example.rsocketjson.model.StockTicker;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Controller
public class WebSocketController {

    static ThreadLocal<NumberFormat> DECIMAL_FORMAT = new ThreadLocal<NumberFormat>() {
        @Override
        public NumberFormat initialValue() {
            return new DecimalFormat("#,###,##0.00");
        }
    };

    private final RSocketRequester rSocketRequester;

    public WebSocketController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @MessageMapping("ticker.stream")
    public Flux<String> responseStream(String symbol) {
        final var filter = StockFilter.builder().matchSymbol( symbol + ".*").build();

        return this.rSocketRequester
                .route("stocks")
                .data(filter)
                .retrieveFlux(StockTicker.class)
                .map(ticker -> String.format("%s %s", ticker.getSymbol(), DECIMAL_FORMAT.get().format(ticker.getPrice())));
    }
}
