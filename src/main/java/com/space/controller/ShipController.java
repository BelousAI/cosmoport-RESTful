package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

@Controller
@RequestMapping("/rest")
public class ShipController {

    private ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    @ResponseBody
    public ResponseEntity<?> getAllShips(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder shipOrder,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        //Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(shipOrder.getFieldName()));
        Page<Ship> page = shipService.getAllShips(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, shipOrder, pageNumber, pageSize);

        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @GetMapping(value = "/ships/count")
    @ResponseBody
    public ResponseEntity<?> getShipsCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating) {

        Integer shipsCount = shipService.getShipsCount(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return new ResponseEntity<>(shipsCount, HttpStatus.OK);
    }

    @GetMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<?> getShip(@PathVariable Long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship foundShip = shipService.getShip(id);
        return (foundShip == null)
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(foundShip, HttpStatus.OK);
    }

    @DeleteMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteShip(@PathVariable(value = "id") Long id) {
        if (id <= 0 ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship foundShip = shipService.getShip(id);
        if (foundShip == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            shipService.deleteShip(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping(value = "/ships")
    @ResponseBody
    public ResponseEntity<?> createShip(@RequestBody Map<String, String> body) {
        if (body == null || body.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String name = body.get("name");
        if (name == null || name.isEmpty() || name.length() > 50) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String planet = body.get("planet");
        if (planet == null || planet.isEmpty() || planet.length() > 50) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String shipTypeString = body.get("shipType");
        if (shipTypeString == null || shipTypeString.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ShipType shipType = ShipType.valueOf(shipTypeString);

        String prodDateString = body.get("prodDate");
        if (prodDateString == null || prodDateString.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Long prodDateLong = Long.parseLong(prodDateString);
        if (prodDateLong < 0 ||
                prodDateLong < new GregorianCalendar(2800, 0, 1).getTimeInMillis() ||
                prodDateLong >= new GregorianCalendar(3020, 0, 1).getTimeInMillis()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String isUsedString = body.get("isUsed");
        Boolean isUsed = false;
        if (isUsedString != null && !isUsedString.isEmpty()) {
            isUsed = Boolean.parseBoolean(isUsedString);
        }

        String speedString = body.get("speed");
        if (speedString == null || speedString.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Double speed = Double.parseDouble(speedString);
        speed = Math.round(speed * 100) / 100.0;
        if (speed < 0.01d || speed > 0.99d) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String crewSizeString = body.get("crewSize");
        if (crewSizeString == null || crewSizeString.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Integer crewSize = Integer.parseInt(crewSizeString);
        if (crewSize < 1 || crewSize > 9999) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.createShip(name, planet, shipType, prodDateLong, isUsed, speed, crewSize);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @PostMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<?> updateShip(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.getShip(id);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (body != null && !body.isEmpty()) {

            String name = body.get("name");
            if (name != null) {
                if (name.isEmpty() || name.length() > 50) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    ship.setName(name);
                }
            }

            String planet = body.get("planet");
            if (planet != null) {
                if (planet.isEmpty() || planet.length() > 50) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    ship.setPlanet(planet);
                }
            }

            String shipTypeString = body.get("shipType");
            if (shipTypeString != null) {
                if (shipTypeString.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    ship.setShipType(ShipType.valueOf(shipTypeString));
                }
            }

            String prodDateLongString = body.get("prodDate");
            if (prodDateLongString != null) {
                if (prodDateLongString.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    Long prodDateLong = Long.parseLong(prodDateLongString);
                    if (prodDateLong < 0 ||
                            prodDateLong < new GregorianCalendar(2800, 0, 1).getTimeInMillis() ||
                            prodDateLong >= new GregorianCalendar(3020, 0, 1).getTimeInMillis()) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    } else {
                        ship.setProdDate(new Date(prodDateLong));
                    }
                }
            }

            String isUsedString = body.get("isUsed");
            if (isUsedString != null && !isUsedString.isEmpty()) {
                Boolean isUsed = Boolean.parseBoolean(isUsedString);
                ship.setUsed(isUsed);
            }

            String speedString = body.get("speed");
            if (speedString !=  null) {
                if (speedString.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    Double speed = Double.parseDouble(speedString);
                    speed = Math.round(speed * 100) / 100.0;
                    if (speed < 0.01d || speed > 0.99d) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    } else {
                        ship.setSpeed(speed);
                    }
                }
            }

            String crewSizeString = body.get("crewSize");
            if (crewSizeString != null) {
                if (crewSizeString.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    Integer crewSize = Integer.parseInt(crewSizeString);
                    if (crewSize < 1 || crewSize > 9999) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    } else {
                        ship.setCrewSize(crewSize);
                    }
                }
            }
            ship = shipService.updateShip(ship);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }
}
