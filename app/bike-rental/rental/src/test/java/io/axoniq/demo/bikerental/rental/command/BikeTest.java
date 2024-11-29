package io.axoniq.demo.bikerental.rental.command;

import io.axoniq.demo.bikerental.coreapi.rental.BikeRegisteredEvent;
import io.axoniq.demo.bikerental.coreapi.rental.BikeRequestedEvent;
import io.axoniq.demo.bikerental.coreapi.rental.RegisterBikeCommand;
import io.axoniq.demo.bikerental.coreapi.rental.RequestBikeCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.axonframework.test.matchers.Matchers.*;

public class BikeTest {
    private AggregateTestFixture<Bike> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(Bike.class);
    }

    @Test
    void canRegisterBike() {
        fixture.givenNoPriorActivity()
                .when(new RegisterBikeCommand("bikeId-1234", "city-bike", "Amsterdam"))
                .expectEvents(new BikeRegisteredEvent("bikeId-1234", "city-bike", "Amsterdam"));
    }

    @Test
    void canRequestAvailableBike() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"))
                .when(new RequestBikeCommand("bikeId", "rider"))
                .expectResultMessagePayloadMatching(matches(String.class::isInstance))
                .expectEventsMatching(exactSequenceOf(
                        messageWithPayload(matches((BikeRequestedEvent e) ->
                                e.bikeId().equals("bikeId")
                                        && e.renter().equals("rider"))),
                        andNoMore()));
    }

    @Test
    void cannotRequestAlreadyRequestedBike() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeRequestedEvent("bikeId", "rider", "rentalId"))
                .when(new RequestBikeCommand("bikeId", "rider"))
                .expectNoEvents()
                .expectException(IllegalStateException.class);

    }

}