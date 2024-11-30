package io.axoniq.demo.bikerental.payment.command;

import io.axoniq.demo.bikerental.coreapi.payment.PaymentPreparedEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PreparePaymentCommand;
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
    void canPreparePayment() {
        fixture.givenNoPriorActivity()
                .when(new PreparePaymentCommand("paymentId", 100, "payment-1234"))
                .expectEvents(new PaymentPreparedEvent("paymentId", 100, "payment-1234"));
    }
}
