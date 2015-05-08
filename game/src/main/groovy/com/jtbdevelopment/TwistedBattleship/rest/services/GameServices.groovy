package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.rest.GameActionInfo
import com.jtbdevelopment.TwistedBattleship.rest.handlers.PlayerMoveHandler
import com.jtbdevelopment.TwistedBattleship.rest.handlers.SetupShipsHandler
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
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
    PlayerMoveHandler playerMoveHandler

    @PUT
    @Path("setup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Object setupShips(final Map<Ship, List<GridCoordinate>> ships) {
        Map<Ship, ShipState> shipStateMap = (Map<Ship, ShipState>) ships.collectEntries {
            Ship ship, List<GridCoordinate> coordinates ->
                [(ship): new ShipState(ship, new TreeSet<GridCoordinate>(coordinates))]
        }
        setupShipsHandler.handleAction((ObjectId) playerID.get(), (ObjectId) gameID.get(), shipStateMap)
    }

    @PUT
    @Path("play")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Object play(final GameActionInfo action) {
        playerMoveHandler.handleAction((ObjectId) playerID.get(), (ObjectId) gameID.get(), action)
    }
}
