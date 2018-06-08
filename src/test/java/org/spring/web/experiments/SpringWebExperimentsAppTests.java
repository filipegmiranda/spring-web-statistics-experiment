package org.spring.web.experiments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spring.web.experiments.model.Statistic;
import org.spring.web.experiments.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;



/**
 * Demonstrating here how to test the HTTP Layer using the SprintTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringWebExperimentsAppTests {


	@Autowired
	private WebTestClient webTestClient;

	private static String PATH_STATISTICS = "statistics";

	private static String PATH_TRANSACTION = "transactions";

	@Test
	public void postingTransactionsWithin60SecsShouldWork() {
		long currentSec = currentMillis();
		Transaction transaction = new Transaction(12.3,  currentSec);
		webTestClient
				.post()
				.uri(PATH_TRANSACTION)
				.accept(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromObject(transaction))
				.exchange()
				.expectStatus()
				.isEqualTo(HttpStatus.CREATED);
	}

	@Test
	public void postingTransactionsOlderThanLast60SecsShouldReply204() {
		Transaction transaction = new Transaction(12.3, 1478192204000L);
		webTestClient
				.post()
				.uri(PATH_TRANSACTION)
				.accept(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromObject(transaction))
				.exchange()
				.expectStatus()
				.isEqualTo(HttpStatus.NO_CONTENT);
	}


	@Test
	public void requestingStatisticsForLast60SecsShouldWork() {
		Statistic expectedStatistic = new Statistic(60, 15, 30, 10, 4);
		//this should be created in a loop as well
		Transaction transactions[] = {
				//first one outside of window should not be there in the final statistics
				new Transaction(10, currentMillis() - 61000),
				new Transaction(10, currentMillis() - 5000),
				new Transaction(30, currentMillis() - 6000),
		 	    new Transaction(10, currentMillis() - 1000),
		        new Transaction(10, currentMillis() - 10000)
		};

		for (Transaction transaction : transactions) {
			webTestClient
					.post()
					.uri(PATH_TRANSACTION)
					.accept(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromObject(transaction))
					.exchange();
		}
		webTestClient
				.get()
				.uri(PATH_STATISTICS)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectBody(Statistic.class)
		.isEqualTo(expectedStatistic);
	}


	public long currentMillis() {
		return java.time.Clock.systemUTC().millis();
	}


}
