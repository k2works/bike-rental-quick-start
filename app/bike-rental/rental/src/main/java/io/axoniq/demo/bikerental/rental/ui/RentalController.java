package io.axoniq.demo.bikerental.rental.ui;

import io.axoniq.demo.bikerental.coreapi.rental.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/")
public class RentalController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    private final BikeRentalDataGenerator bikeRentalDataGenerator;

    public RentalController(CommandGateway commandGateway, QueryGateway queryGateway, BikeRentalDataGenerator bikeRentalDataGenerator) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.bikeRentalDataGenerator = bikeRentalDataGenerator;
    }

    @PostMapping("/bikes")
    public CompletableFuture<String> registerBike(
            @RequestParam("bikeType") String bikeType,
    @RequestParam("location") String location) {

        RegisterBikeCommand registerBikeCommand =
                new RegisterBikeCommand(
                        UUID.randomUUID().toString(),
                        bikeType,
                        location);

        CompletableFuture<String> commandResult =
                commandGateway.send(registerBikeCommand);

        return commandResult;
    }

    @GetMapping("/bikes")
    public CompletableFuture<List<BikeStatus>> findAll() {
        return queryGateway.query(
                BikeStatusNamedQueries.FIND_ALL,
                null,
                ResponseTypes.multipleInstancesOf(BikeStatus.class)
        );
    }

    @GetMapping("/bikes/{bikeId}")
    public CompletableFuture<BikeStatus> findStatus(@PathVariable("bikeId") String bikeId) {
        return queryGateway.query(BikeStatusNamedQueries.FIND_ONE, bikeId, BikeStatus.class);
    }

    @PostMapping("/requestBike")
    public CompletableFuture<String> requestBike(@RequestParam("bikeId") String bikeId, @RequestParam(value = "renter", required = false) String renter) {
        return commandGateway.send(new RequestBikeCommand(bikeId, renter != null ? renter : this.bikeRentalDataGenerator.randomRenter()));
    }

    @PostMapping("/approveRequest")
    public CompletableFuture<String> approveRequest(@RequestParam("bikeId") String bikeId, @RequestParam(value = "renter", required = false) String renter) {
        return commandGateway.send(new ApproveRequestCommand(bikeId, renter != null ? renter : this.bikeRentalDataGenerator.randomRenter()));
    }
}