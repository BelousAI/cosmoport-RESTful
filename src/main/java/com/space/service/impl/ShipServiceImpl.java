package com.space.service.impl;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import com.space.service.ShipService;
import com.space.service.ShipSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;


@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship createShip(String name, String planet, ShipType shipType,
                           Long prodDate, Boolean isUsed, Double speed, Integer crewSize) {

        Double rating = calculateRating(speed, isUsed, prodDate);
        Ship createdShip = new Ship(name, planet, shipType, new Date(prodDate), isUsed, speed, crewSize, rating);
        return shipRepository.save(createdShip);
    }

    @Override
    public Ship getShip(Long id) {
        return shipRepository.getShipById(id);
    }

    @Override
    public Ship updateShip(Ship ship) {
        Double speed = ship.getSpeed();
        Boolean isUsed = ship.getUsed();
        Long prodDateLong = ship.getProdDate().getTime();
        ship.setRating(calculateRating(speed, isUsed, prodDateLong));
        return shipRepository.save(ship);
    }

    @Override
    public void deleteShip(Long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public Integer getShipsCount(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating) {

        Specification<Ship> specification = createSpecification(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipRepository.findAll(specification).size();
    }

    @Override
    public Page<Ship> getAllShips(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating,
            ShipOrder order,
            Integer pageNumber,
            Integer pageSize) {

        Specification<Ship> specification = createSpecification(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        return shipRepository.findAll(specification, pageable);
    }

    private Specification<Ship> createSpecification(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating) {

        ShipSpecificationsBuilder builder = new ShipSpecificationsBuilder();

        if (name != null) {
            builder.with("name", ":", name);
        }

        if (planet != null) {
            builder.with("planet", ":", planet);
        }

        if (shipType != null) {
            builder.with("shipType", ":", shipType);
        }

        if (after != null) {
            builder.with("prodDate", ">", new Date(after));
        }

        if (before != null) {
            builder.with("prodDate", "<", new Date(before));
        }

        if (isUsed != null) {
            builder.with("isUsed", ":", isUsed);
        }

        if (minSpeed != null) {
            builder.with("speed", ">", minSpeed);
        }

        if (maxSpeed != null) {
            builder.with("speed", "<", maxSpeed);
        }

        if (minCrewSize != null) {
            builder.with("crewSize", ">", minCrewSize);
        }

        if (maxCrewSize != null) {
            builder.with("crewSize", "<", maxCrewSize);
        }

        if (minRating != null) {
            builder.with("rating", ">", minRating);
        }

        if (maxRating != null) {
            builder.with("rating", "<", maxRating);
        }

        return builder.build();
    }

    private Double calculateRating(Double speed, Boolean isUsed, Long prodDateLong) {
        double k = isUsed ? 0.5 : 1;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(prodDateLong);

        Double rating = 80 * speed * k / (3019 - c.get(Calendar.YEAR) + 1);
        rating = Math.round(rating * 100)/100.0;

        return rating;
    }

}
