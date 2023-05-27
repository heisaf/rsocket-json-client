package com.example.rsocketjson;

import com.example.rsocketjson.model.StockFilter;
import com.example.rsocketjson.model.StockTicker;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@Slf4j
public class RsocketJsonApplication {

	private final RSocketRequester rSocketRequester;

	public RsocketJsonApplication(RSocketRequester rSocketRequester) {
		this.rSocketRequester = rSocketRequester;
	}

	public static void main(String[] args) {
		SpringApplication.run(RsocketJsonApplication.class, args);
	}

	@PostConstruct
	public  void run() {
		final var latch = new CountDownLatch(1);
		final var filter = StockFilter.builder().matchSymbol("AD.*").build();

		this.rSocketRequester
				.route("stocks")
				.data(filter)
				.retrieveFlux(StockTicker.class)
				.map(StockTicker::getSymbol)
				.take(25)
				.doOnComplete(latch::countDown)
				.subscribe(log::info);
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
