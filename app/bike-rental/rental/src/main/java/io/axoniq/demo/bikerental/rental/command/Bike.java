package io.axoniq.demo.bikerental.rental.command;

import io.axoniq.demo.bikerental.coreapi.rental.BikeRegisteredEvent;
import io.axoniq.demo.bikerental.coreapi.rental.BikeRequestedEvent;
import io.axoniq.demo.bikerental.coreapi.rental.RegisterBikeCommand;
import io.axoniq.demo.bikerental.coreapi.rental.RequestBikeCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class Bike {
    @AggregateIdentifier
    private String bikeId;

    private boolean isAvailable;
    private String reservedBy;
    private boolean reservationConfirmed;

    public Bike() {
    }

    @CommandHandler
    public Bike(RegisterBikeCommand command) {
        var seconds = Instant.now().getEpochSecond();
        if (seconds % 10 ==0) {
            throw new IllegalStateException("Can't accept new bikes right now");
        }

        apply(new BikeRegisteredEvent(command.bikeId(), command.bikeType(), command.location()));
    }

    @CommandHandler
    public String handle(RequestBikeCommand command) {
        if (!this.isAvailable) {
            throw new IllegalStateException("Bike is already rented");
        }
        String rentalReference = UUID.randomUUID().toString();
        apply(new BikeRequestedEvent(command.bikeId(), command.renter(), rentalReference));

        return rentalReference;
    }

    @EventSourcingHandler
    protected void handle(BikeRegisteredEvent event) {
        this.bikeId = event.bikeId();
        this.isAvailable = true;
    }

    @EventSourcingHandler
    protected void handle(BikeRequestedEvent event) {
        this.reservedBy = event.renter();
        this.reservationConfirmed = false;
        this.isAvailable = false;
    }

}
