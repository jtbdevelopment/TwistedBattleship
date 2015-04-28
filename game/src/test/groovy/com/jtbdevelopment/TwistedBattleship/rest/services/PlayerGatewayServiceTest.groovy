package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import groovy.transform.TypeChecked

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 4/28/15
 * Time: 6:36 AM
 */
class PlayerGatewayServiceTest extends GroovyTestCase {
    PlayerGatewayService playerGatewayService = new PlayerGatewayService()

    void testGetFeatures() {

        assert playerGatewayService.featuresAndDescriptions() == [
                (GameFeature.Critical)        :
                        new PlayerGatewayService.GameFeatureGroupDetails(
                                description: GameFeature.Critical.description,
                                options: [GameFeature.CriticalEnabled, GameFeature.CriticalDisabled],
                                optionDescriptions: [GameFeature.CriticalEnabled.description, GameFeature.CriticalDisabled.description]
                        ),
                (GameFeature.ActionsPerTurn)  :
                        new PlayerGatewayService.GameFeatureGroupDetails(
                                description: GameFeature.ActionsPerTurn.description,
                                options: [GameFeature.PerShip, GameFeature.Single],
                                optionDescriptions: [GameFeature.PerShip.description, GameFeature.Single.description]
                        ),
                (GameFeature.EmergencyRepairs):
                        new PlayerGatewayService.GameFeatureGroupDetails(
                                description: GameFeature.EmergencyRepairs.description,
                                options: [GameFeature.EREnabled, GameFeature.ERDisabled],
                                optionDescriptions: [GameFeature.EREnabled.description, GameFeature.ERDisabled.description]
                        ),
                (GameFeature.EvasiveManeuvers):
                        new PlayerGatewayService.GameFeatureGroupDetails(
                                description: GameFeature.EvasiveManeuvers.description,
                                options: [GameFeature.EMEnabled, GameFeature.EMDisabled],
                                optionDescriptions: [GameFeature.EMEnabled.description, GameFeature.EMDisabled.description]
                        ),
                (GameFeature.Spy)             :
                        new PlayerGatewayService.GameFeatureGroupDetails(
                                description: GameFeature.Spy.description,
                                options: [GameFeature.SpyEnabled, GameFeature.SpyDisabled],
                                optionDescriptions: [GameFeature.SpyEnabled.description, GameFeature.SpyDisabled.description]
                        ),
                (GameFeature.ECM)             :
                        new PlayerGatewayService.GameFeatureGroupDetails(
                                description: GameFeature.ECM.description,
                                options: [GameFeature.ECMEnabled, GameFeature.ECMDisabled],
                                optionDescriptions: [GameFeature.ECMEnabled.description, GameFeature.ECMDisabled.description]
                        ),
                (GameFeature.FogOfWar)        :
                        new PlayerGatewayService.GameFeatureGroupDetails(
                                description: GameFeature.FogOfWar.description,
                                options: [GameFeature.SharedIntel, GameFeature.IsolatedIntel],
                                optionDescriptions: [GameFeature.SharedIntel.description, GameFeature.IsolatedIntel.description]
                        ),
                (GameFeature.GridSize)        :
                        new PlayerGatewayService.GameFeatureGroupDetails(
                                description: GameFeature.GridSize.description,
                                options: [GameFeature.Grid10x10, GameFeature.Grid15x15, GameFeature.Grid20x20],
                                optionDescriptions: [GameFeature.Grid10x10.description, GameFeature.Grid15x15.description, GameFeature.Grid20x20.description]
                        ),
        ]
    }

    void testGetFeaturesAnnotations() {
        def gameServices = PlayerGatewayService.getMethod("featuresAndDescriptions", [] as Class[])
        assert (gameServices.annotations.size() == 3 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 4)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "features"
        assert gameServices.isAnnotationPresent(GET.class)
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }
}
