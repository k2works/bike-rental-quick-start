package io.axoniq.demo.bikerental.payment.query;

import io.axoniq.demo.bikerental.coreapi.payment.PaymentPreparedEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusProjection {

    private final PaymentStatusRepository paymentStatusRepository;
    private final QueryUpdateEmitter updateEmitter;

    public PaymentStatusProjection(PaymentStatusRepository paymentStatusRepository,
                                   QueryUpdateEmitter updateEmitter) {
        this.paymentStatusRepository = paymentStatusRepository;
        this.updateEmitter = updateEmitter;
    }

    @QueryHandler(queryName = "getStatus")
    public PaymentStatus getStatus(String paymentId) {
        return paymentStatusRepository.findById(paymentId).orElse(null);
    }

    @EventHandler
    public void handle(PaymentPreparedEvent event) {
        paymentStatusRepository.save(new PaymentStatus(event.paymentId(), event.amount(), event.paymentReference()));
        updateEmitter.emit(String.class, event.paymentReference()::equals, event.paymentId());
    }
}
