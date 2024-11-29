package io.axoniq.demo.bikerental.rental.command;

import io.axoniq.demo.bikerental.coreapi.rental.*;
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

    @Test
    void canApproveRequestedBike() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeRequestedEvent("bikeId", "rider", "rentalId"))
                .when(new ApproveRequestCommand("bikeId", "rider"))
                .expectEvents(new BikeInUseEvent("bikeId", "rider"));
    }

    @Test
    void canRejectRequestedBike() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeRequestedEvent("bikeId", "rider", "rentalId"))
                .when(new RejectRequestCommand("bikeId", "rider"))
                .expectEvents(new RequestRejectedEvent("bikeId"));
    }

    @Test
    void canNotRejectRequestedForWrongRequester() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeRequestedEvent("bikeId", "rider", "rentalId"))
                .when(new RejectRequestCommand("bikeId", "otherRider"))
                .expectSuccessfulHandlerExecution()
                .expectNoEvents();
    }

    @Test
    void cannotApproveRequestedForAnotherRider() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeRequestedEvent("bikeId", "rider", "rentalId"))
                .when(new ApproveRequestCommand("bikeId", "otherRider"))
                .expectNoEvents()
                .expectSuccessfulHandlerExecution();
    }

    @Test
    void canReturnedBikeInUse() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeRequestedEvent("bikeId", "rider", "rentalId"),
                        new BikeInUseEvent("bikeId", "rider"))
                .when(new ReturnBikeCommand("bikeId", "NewLocation"))
                .expectEvents(new BikeReturnedEvent("bikeId", "NewLocation"));
    }

    @Test
    void canRequestRejectedBike() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeRequestedEvent("bikeId", "rider", "rentalId"),
                        new RequestRejectedEvent("bikeId"))
                .when(new RequestBikeCommand("bikeId", "newRider"))
                .expectEventsMatching(exactSequenceOf(
                        messageWithPayload(matches((BikeRequestedEvent e) ->
                                e.bikeId().equals("bikeId")
                                        && e.renter().equals("newRider"))),
                        andNoMore()));
    }
}