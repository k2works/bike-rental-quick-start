package io.axoniq.demo.bikerental.payment.ui;

import io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus;
import io.axoniq.demo.bikerental.coreapi.payment.PreparePaymentCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class PaymentController {

    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;

    public PaymentController(QueryGateway queryGateway, CommandGateway commandGateway) {
        this.queryGateway = queryGateway;
        this.commandGateway = commandGateway;
    }

    @PostMapping("/preparePayment")
    public CompletableFuture<String> preparePayment(@RequestParam("amount") int amount, @RequestParam("rentalReference") String rentalReference) {
        return commandGateway.send(new PreparePaymentCommand(null, amount, rentalReference));
    }

    @GetMapping("/status/{paymentId}")
    public CompletableFuture<PaymentStatus> getStatus(@PathVariable("paymentId") String paymentId) {
        return queryGateway.query("getStatus", paymentId, PaymentStatus.class);
    }

    @GetMapping("/findPayment")
    public CompletableFuture<String> findPaymentId(@RequestParam("reference") String paymentReference) {
        return queryGateway.query("getPaymentId", paymentReference, String.class);
    }
}
