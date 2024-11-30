package io.axoniq.demo.bikerental.payment.command;

import io.axoniq.demo.bikerental.coreapi.payment.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaymentTest {
    private AggregateTestFixture<Payment> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(Payment.class);
    }

    @Test
    void canConfirmPayment() {
        fixture.given(new PaymentPreparedEvent("paymentId", 100, "payment-1234"))
                .when(new ConfirmPaymentCommand("paymentId"))
                .expectEvents(new PaymentConfirmedEvent("paymentId", "payment-1234"));
    }

    @Test
    void cannotConfirmPaymentTwice() {
        fixture.given(new PaymentPreparedEvent("paymentId", 100, "payment-1234"),
                      new PaymentConfirmedEvent("paymentId", "payment-1234"))
                .when(new ConfirmPaymentCommand("paymentId"))
                .expectNoEvents();
    }

    @Test
    void canRejectPayment() {
        fixture.given(new PaymentPreparedEvent("paymentId", 100, "payment-1234"))
                .when(new RejectPaymentCommand("paymentId"))
                .expectEvents(new PaymentRejectedEvent("paymentId", "payment-1234"));
    }

    @Test
    void cannotRejectPaymentTwice() {
        fixture.given(new PaymentPreparedEvent("paymentId", 100, "payment-1234"),
                      new PaymentRejectedEvent("paymentId", "payment-1234"))
                .when(new RejectPaymentCommand("paymentId"))
                .expectNoEvents();
    }
}
