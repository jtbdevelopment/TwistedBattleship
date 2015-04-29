package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
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
    public List<GameFeatureInfo> featuresAndDescriptions() {
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
}
