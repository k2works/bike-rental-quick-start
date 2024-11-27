package io.axoniq.demo.bikerental.rental.command;

import io.axoniq.demo.bikerental.coreapi.rental.BikeRegisteredEvent;
import io.axoniq.demo.bikerental.coreapi.rental.RegisterBikeCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}