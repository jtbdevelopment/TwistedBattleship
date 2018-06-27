package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.rest.GameFeatureInfo
import com.jtbdevelopment.TwistedBattleship.rest.ShipInfo
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship

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
                new GameFeatureInfo(GameFeature.ActionsPerTurn,
                        [
                                new GameFeatureInfo.Detail(GameFeature.PerShip),
                                new GameFeatureInfo.Detail(GameFeature.Single),
                        ]),
                new GameFeatureInfo(GameFeature.FogOfWar,
                        [
                                new GameFeatureInfo.Detail(GameFeature.SharedIntel),
                                new GameFeatureInfo.Detail(GameFeature.IsolatedIntel),
                        ]),
                new GameFeatureInfo(GameFeature.StartingShips,
                        [
                                new GameFeatureInfo.Detail(GameFeature.StandardShips),
                                new GameFeatureInfo.Detail(GameFeature.AllCarriers),
                                new GameFeatureInfo.Detail(GameFeature.AllDestroyers),
                                new GameFeatureInfo.Detail(GameFeature.AllSubmarines),
                                new GameFeatureInfo.Detail(GameFeature.AllCruisers),
                                new GameFeatureInfo.Detail(GameFeature.AllBattleships),
                        ]),
                new GameFeatureInfo(GameFeature.ECM,
                        [
                                new GameFeatureInfo.Detail(GameFeature.ECMEnabled),
                                new GameFeatureInfo.Detail(GameFeature.ECMDisabled),
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
                new GameFeatureInfo(GameFeature.Spy,
                        [
                                new GameFeatureInfo.Detail(GameFeature.SpyEnabled),
                                new GameFeatureInfo.Detail(GameFeature.SpyDisabled),
                        ]),
                new GameFeatureInfo(GameFeature.CruiseMissile,
                        [
                                new GameFeatureInfo.Detail(GameFeature.CruiseMissileEnabled),
                                new GameFeatureInfo.Detail(GameFeature.CruiseMissileDisabled),
                        ]),
        ]
    }

    void testGetFeaturesAnnotations() {
        def gameServices = PlayerGatewayService.getMethod("featuresAndDescriptions", [] as Class[])
        assert gameServices.annotations.size() == 3
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "features"
        assert gameServices.isAnnotationPresent(GET.class)
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testGetCircles() {
        assert GridCircleUtil.CIRCLE_OFFSETS.is(playerGatewayService.circleSizes())
    }

    void testGetCirclesAnnotations() {
        def gameServices = PlayerGatewayService.getMethod("circleSizes", [] as Class[])
        assert gameServices.annotations.size() == 3
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "circles"
        assert gameServices.isAnnotationPresent(GET.class)
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testGetShips() {
        def ships = [
                new ShipInfo(Ship.Battleship),
                new ShipInfo(Ship.Carrier),
                new ShipInfo(Ship.Cruiser),
                new ShipInfo(Ship.Destroyer),
                new ShipInfo(Ship.Submarine)
        ]
        assert ships.containsAll(playerGatewayService.ships())
        assert playerGatewayService.ships().containsAll(ships)
    }

    void testGetShipsAnnotations() {
        def gameServices = PlayerGatewayService.getMethod("ships", [] as Class[])
        assert gameServices.annotations.size() == 3
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "ships"
        assert gameServices.isAnnotationPresent(GET.class)
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testGetCellStates() {
        assert GridCellState.values() == playerGatewayService.states()
    }

    void testGetStatesAnnotations() {
        def gameServices = PlayerGatewayService.getMethod("states", [] as Class[])
        assert gameServices.annotations.size() == 34
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "states"
        assert gameServices.isAnnotationPresent(GET.class)
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }
}
