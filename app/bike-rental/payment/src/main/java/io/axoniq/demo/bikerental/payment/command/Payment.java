package io.axoniq.demo.bikerental.payment.command;

import io.axoniq.demo.bikerental.coreapi.payment.*;
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
        String paymentId = UUID.randomUUID().toString();
        apply(new PaymentPreparedEvent(paymentId, command.amount(), command.paymentReference()));
    }

    @CommandHandler
    public void handle(ConfirmPaymentCommand command) {
        if (!closed) {
            apply(new PaymentConfirmedEvent(command.paymentId(), paymentReference));
        }
    }

    @CommandHandler
    public void handle(RejectPaymentCommand command) {
        if (!closed) {
            apply(new PaymentRejectedEvent(command.paymentId(), paymentReference));
        }
    }

    @EventSourcingHandler
    protected void on(PaymentPreparedEvent event) {
        this.id = event.paymentId();
        this.paymentReference = event.paymentReference();
    }

    @EventSourcingHandler
    protected void on(PaymentConfirmedEvent event) {
        this.closed = true;
    }

    @EventSourcingHandler
    protected void on(PaymentRejectedEvent event) {
        this.closed = true;
    }
}
