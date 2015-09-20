package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.rest.handlers.*
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.ShipAndCoordinates
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.rest.AbstractMultiPlayerGameServices
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 11/11/14
 * Time: 9:42 PM
 */
@Component
@CompileStatic
class GameServices extends AbstractMultiPlayerGameServices<ObjectId> {
    @Autowired
    SetupShipsHandler setupShipsHandler

    @Autowired
    FireAtCoordinateHandler fireAtCoordinateHandler

    @Autowired
    SpyHandler spyHandler

    @Autowired
    RepairShipHandler repairShipHandler

    @Autowired
    ECMHandler ecmHandler

    @Autowired
    EvasiveManeuverHandler evasiveManeuverHandler

    @PUT
    @Path("setup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Object setupShips(final List<ShipAndCoordinates> ships) {
        List<ShipState> shipStates = (List<ShipState>) ships.collect {
            ShipAndCoordinates shipAndCoordinates ->
                new ShipState(shipAndCoordinates.ship, new TreeSet<GridCoordinate>(shipAndCoordinates.coordinates))
        }
        setupShipsHandler.handleAction((ObjectId) playerID.get(), (ObjectId) gameID.get(), shipStates)
    }

    @PUT
    @Path("fire")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Object fire(final Target target) {
        fireAtCoordinateHandler.handleAction((ObjectId) playerID.get(), (ObjectId) gameID.get(), target)
    }

    @PUT
    @Path("spy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Object spy(final Target target) {
        spyHandler.handleAction((ObjectId) playerID.get(), (ObjectId) gameID.get(), target)
    }

    @PUT
    @Path("repair")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Object repair(final Target target) {
        repairShipHandler.handleAction((ObjectId) playerID.get(), (ObjectId) gameID.get(), target)
    }

    @PUT
    @Path("ecm")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Object ecm(final Target target) {
        ecmHandler.handleAction((ObjectId) playerID.get(), (ObjectId) gameID.get(), target)
    }

    @PUT
    @Path("move")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Object move(final Target target) {
        evasiveManeuverHandler.handleAction((ObjectId) playerID.get(), (ObjectId) gameID.get(), target)
    }
}
