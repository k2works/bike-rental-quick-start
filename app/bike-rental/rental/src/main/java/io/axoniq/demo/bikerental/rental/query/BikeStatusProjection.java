package io.axoniq.demo.bikerental.rental.query;

import io.axoniq.demo.bikerental.coreapi.rental.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

@Component
public class BikeStatusProjection {

    private final BikeStatusRepository bikeStatusRepository;
    private final QueryUpdateEmitter updateEmitter;

    public BikeStatusProjection(BikeStatusRepository bikeStatusRepository, QueryUpdateEmitter updateEmitter) {
        this.bikeStatusRepository = bikeStatusRepository;
        this.updateEmitter = updateEmitter;
    }

    @EventHandler
    public void on(BikeRegisteredEvent event) {
        var bikeStatus = new BikeStatus(event.bikeId(), event.bikeType(), event.location());
        bikeStatusRepository.save(bikeStatus);
    }

    @EventHandler
    public void on(BikeRequestedEvent event) {
        bikeStatusRepository.findById(event.bikeId())
                .map(bs -> {
                    bs.requestedBy(event.renter());
                    return bs;
                })
                .ifPresent(bs -> {
                    updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), bs);
                    updateEmitter.emit(String.class, event.bikeId()::equals, bs);
                });
    }

    @EventHandler
    public void on(BikeInUseEvent event) {
        bikeStatusRepository.findById(event.bikeId())
                .map(bs -> {
                    bs.rentedBy(event.renter());
                    return bs;
                })
                .ifPresent(bs -> {
                    updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), bs);
                    updateEmitter.emit(String.class, event.bikeId()::equals, bs);
                });
    }

    @QueryHandler(queryName = BikeStatusNamedQueries.FIND_ALL)
    public Iterable<BikeStatus> findAll() {
        return bikeStatusRepository.findAll();
    }

    @QueryHandler(queryName = BikeStatusNamedQueries.FIND_AVAILABLE)
    public Iterable<BikeStatus> findAvailable(String bikeType) {
        return bikeStatusRepository.findAllByBikeTypeAndStatus(bikeType, RentalStatus.AVAILABLE);
    }

    @QueryHandler(queryName = BikeStatusNamedQueries.FIND_ONE)
    public BikeStatus findOne(String bikeId) {
        return bikeStatusRepository.findById(bikeId).orElse(null);
    }

}