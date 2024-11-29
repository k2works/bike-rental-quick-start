package io.axoniq.demo.bikerental.rental.paymentsaga;

import io.axoniq.demo.bikerental.coreapi.payment.PreparePaymentCommand;
import io.axoniq.demo.bikerental.coreapi.rental.BikeRequestedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.Scope;
import org.axonframework.messaging.ScopeDescriptor;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

@Saga
public class PaymentSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    private String bikeId;
    private String renter;

    public PaymentSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "bikeId")
    public void on(BikeRequestedEvent event) {
        this.bikeId = event.bikeId();
        this.renter = event.renter();
        SagaLifecycle.associateWith("paymentReference", event.rentalReference());
        preparePayment(event.rentalReference());
    }

    @DeadlineHandler(deadlineName = "retryPayment")
    public void preparePayment(String rentalReference) {
        ScopeDescriptor scope = Scope.describeCurrentScope();
        commandGateway.send(new PreparePaymentCommand(10, rentalReference))
                .whenComplete((r, e) -> {
                    if (e != null) {
                        deadlineManager.schedule(Duration.ofSeconds(5), "retryPayment", rentalReference, scope);
                    }
                });
    }
}
