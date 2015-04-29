package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo
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
                new GameFeatureInfo(GameFeature.GridSize,
                        [
                                new GameFeatureInfo.Detail(GameFeature.Grid10x10),
                                new GameFeatureInfo.Detail(GameFeature.Grid15x15),
                                new GameFeatureInfo.Detail(GameFeature.Grid20x20),
                        ]),
                new GameFeatureInfo(GameFeature.FogOfWar,
                        [
                                new GameFeatureInfo.Detail(GameFeature.SharedIntel),
                                new GameFeatureInfo.Detail(GameFeature.IsolatedIntel),
                        ]),
                new GameFeatureInfo(GameFeature.ECM,
                        [
                                new GameFeatureInfo.Detail(GameFeature.ECMEnabled),
                                new GameFeatureInfo.Detail(GameFeature.ECMDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.Spy,
                        [
                                new GameFeatureInfo.Detail(GameFeature.SpyEnabled),
                                new GameFeatureInfo.Detail(GameFeature.SpyDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.EvasiveManeuvers,
                        [
                                new GameFeatureInfo.Detail(GameFeature.EMEnabled),
                                new GameFeatureInfo.Detail(GameFeature.EMDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.EmergencyRepairs,
                        [
                                new GameFeatureInfo.Detail(GameFeature.EREnabled),
                                new GameFeatureInfo.Detail(GameFeature.ERDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.ActionsPerTurn,
                        [
                                new GameFeatureInfo.Detail(GameFeature.PerShip),
                                new GameFeatureInfo.Detail(GameFeature.Single),
                        ]),
                new GameFeatureInfo(GameFeature.Critical,
                        [
                                new GameFeatureInfo.Detail(GameFeature.CriticalEnabled),
                                new GameFeatureInfo.Detail(GameFeature.CriticalDisabled),
                        ]),
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
