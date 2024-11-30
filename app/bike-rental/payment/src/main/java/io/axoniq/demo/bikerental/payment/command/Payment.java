package io.axoniq.demo.bikerental.payment.command;

import io.axoniq.demo.bikerental.coreapi.payment.PaymentPreparedEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PreparePaymentCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class Payment {

    @AggregateIdentifier
    private String id;

    private boolean closed;
    private String paymentReference;

    public Payment() {
    }

    @CommandHandler
    public Payment(PreparePaymentCommand command) {
        String paymentId = (command.paymentId() == null) ? UUID.randomUUID().toString() : command.paymentId();
        apply(new PaymentPreparedEvent(paymentId, command.amount(), command.paymentReference()));
    }

    @EventSourcingHandler
    protected void on(PaymentPreparedEvent event) {
        this.id = event.paymentId();
        this.paymentReference = event.paymentReference();
    }
}
