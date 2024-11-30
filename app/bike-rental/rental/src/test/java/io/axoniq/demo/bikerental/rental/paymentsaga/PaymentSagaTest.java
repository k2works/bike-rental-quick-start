package io.axoniq.demo.bikerental.rental.paymentsaga;

import io.axoniq.demo.bikerental.coreapi.payment.PaymentConfirmedEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PreparePaymentCommand;
import io.axoniq.demo.bikerental.coreapi.rental.ApproveRequestCommand;
import io.axoniq.demo.bikerental.coreapi.rental.BikeRequestedEvent;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaymentSagaTest {
    private SagaTestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new SagaTestFixture(PaymentSaga.class);
    }

    @Test
    void shouldStartSagaOnBikeRequested() {
        fixture.givenNoPriorActivity()
                .whenPublishingA(new BikeRequestedEvent("bikeId", "renter", "payRef"))
                .expectDispatchedCommands(new PreparePaymentCommand(10, "payRef"))
                .expectActiveSagas(1);
    }

    @Test
    void shouldAcceptRequestOnPaymentConfirmed() {
        fixture.givenAPublished(new BikeRequestedEvent("bikeId", "renter", "rentalRef"))
                .whenPublishingA(new PaymentConfirmedEvent("paymentId", "rentalRef"))
                .expectDispatchedCommands(new ApproveRequestCommand("bikeId", "renter"))
                .expectActiveSagas(0);
    }
}
