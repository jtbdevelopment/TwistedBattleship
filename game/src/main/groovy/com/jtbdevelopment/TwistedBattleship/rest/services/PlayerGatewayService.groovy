package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo
import com.jtbdevelopment.TwistedBattleship.rest.ShipInfo
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.rest.services.AbstractPlayerGatewayService
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 11/14/14
 * Time: 6:36 AM
 */
@Path("/")
@Component
@CompileStatic
class PlayerGatewayService extends AbstractPlayerGatewayService<ObjectId> {
    @GET
    @Path("features")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("GrMethodMayBeStatic")
    List<GameFeatureInfo> featuresAndDescriptions() {
        GameFeature.groupedFeatures.keySet().sort {
            GameFeature a, GameFeature b ->
                return a.order - b.order
        }.collect {
            GameFeature group ->
                new GameFeatureInfo(group, GameFeature.groupedFeatures[group].collect {
                    GameFeature option ->
                        new GameFeatureInfo.Detail(option)
                })
        }
    }

    @GET
    @Path("circles")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("GrMethodMayBeStatic")
    Map<Integer, Set<GridCoordinate>> circleSizes() {
        return GridCircleUtil.CIRCLE_OFFSETS
    }

    @GET
    @Path("ships")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("GrMethodMayBeStatic")
    List<ShipInfo> ships() {
        return Ship.values().collect { Ship it -> new ShipInfo(it) }
    }
}
