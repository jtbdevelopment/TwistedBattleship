package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.rest.AbstractMultiPlayerGameServices
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

import javax.ws.rs.Consumes
import javax.ws.rs.FormParam
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
    @PUT
    @Path("setup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    TBMaskedGame setupShips(@FormParam("details") final Map<Ship, List<GridCoordinate>> ships) {

    }
}
