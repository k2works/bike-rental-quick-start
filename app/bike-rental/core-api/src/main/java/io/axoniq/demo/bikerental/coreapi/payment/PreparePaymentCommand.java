package io.axoniq.demo.bikerental.coreapi.payment;

import org.axonframework.commandhandling.RoutingKey;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PreparePaymentCommand(
        @TargetAggregateIdentifier String paymentId,
        int amount, @RoutingKey String paymentReference) {
}


