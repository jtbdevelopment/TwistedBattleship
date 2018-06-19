package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.rest.handlers.*
import com.jtbdevelopment.TwistedBattleship.rest.services.messages.ShipAndCoordinates
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import groovy.transform.TypeChecked
import org.bson.types.ObjectId
import org.junit.Test

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Date: 5/5/15
 * Time: 6:38 PM
 */
class GameServicesTest extends MongoGameCoreTestCase {
    GameServices services = new GameServices()

    @Test
    void testActionAnnotations() {
        Map<String, List<Object>> stuff = [
                //  method: [name, params, path, path param values, consumes
                "setupShips"   : ["setup", [List.class], [], [MediaType.APPLICATION_JSON]],
                "fire"         : ["fire", [Target.class], [], [MediaType.APPLICATION_JSON]],
                "cruiseMissile": ["missile", [Target.class], [], [MediaType.APPLICATION_JSON]],
                "spy"          : ["spy", [Target.class], [], [MediaType.APPLICATION_JSON]],
                "repair"       : ["repair", [Target.class], [], [MediaType.APPLICATION_JSON]],
                "ecm"          : ["ecm", [Target.class], [], [MediaType.APPLICATION_JSON]],
                "move"         : ["move", [Target.class], [], [MediaType.APPLICATION_JSON]],
        ]
        stuff.each {
            String method, List<Object> details ->
                def m = GameServices.getMethod(method, details[1] as Class[])
                int expectedA = 3 + details[3].size
                assert (m.annotations.size() == expectedA ||
                        (m.annotations.size() == (expectedA + 1) && m.isAnnotationPresent(TypeChecked.TypeCheckingInfo.class))
                )
                assert m.isAnnotationPresent(PUT.class)
                assert m.isAnnotationPresent(Produces.class)
                assert m.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
                assert m.isAnnotationPresent(Path.class)
                assert m.getAnnotation(Path.class).value() == details[0]
                if (details[3].size > 0) {
                    assert m.isAnnotationPresent(Consumes.class)
                    assert m.getAnnotation(Consumes.class).value() == details[3]
                }
                if (details[2].size > 0) {
                    int count = 0
                    details[2].each {
                        String pp ->
                            ((PathParam) m.parameterAnnotations[count][0]).value() == pp
                            ++count
                    }
                }
        }
    }

    @Test
    void testSetupShips() {
        List<ShipAndCoordinates> input = [
                new ShipAndCoordinates(
                        ship: Ship.Cruiser,
                        coordinates: [
                                new GridCoordinate(0, 10),
                                new GridCoordinate(0, 9),
                                new GridCoordinate(0, 11)
                        ]
                ),
                new ShipAndCoordinates(
                        ship: Ship.Destroyer,
                        coordinates: [
                                new GridCoordinate(1, 10),
                                new GridCoordinate(1, 9),
                                new GridCoordinate(1, 10)
                        ]
                )
        ]
        TBMaskedGame maskedGame = new TBMaskedGame()
        ObjectId gameId = new ObjectId()
        services.playerID.set(PONE.id)
        services.gameID.set(gameId)
        services.setupShipsHandler = [
                handleAction: {
                    Serializable p, Serializable g, List<ShipState> ss ->
                        assert PONE.id.is(p)
                        assert gameId.is(g)
                        assert 2 == ss.size()
                        def cruiser = ss.find { it.ship == Ship.Cruiser }
                        assertNotNull cruiser
                        def destroyer = ss.find { it.ship == Ship.Destroyer }
                        assertNotNull destroyer
                        assert cruiser.healthRemaining == 3
                        assert cruiser.ship == Ship.Cruiser
                        assert cruiser.shipSegmentHit == [false, false, false]
                        assert cruiser.shipGridCells.toList() == [new GridCoordinate(0, 9), new GridCoordinate(0, 10), new GridCoordinate(0, 11)]
                        assert destroyer.healthRemaining == 2
                        assert destroyer.ship == Ship.Destroyer
                        assert destroyer.shipSegmentHit == [false, false]
                        assert destroyer.shipGridCells.toList() == [new GridCoordinate(1, 9), new GridCoordinate(1, 10)]
                        return maskedGame
                }
        ] as SetupShipsHandler
        assert maskedGame.is(services.setupShips(input))
    }

    @Test
    void testFire() {
        TBMaskedGame maskedGame = new TBMaskedGame()
        Target target = new Target(player: PONE.md5, coordinate: new GridCoordinate(10, 5))
        ObjectId gameId = new ObjectId()
        services.playerID.set(PONE.id)
        services.gameID.set(gameId)
        services.fireAtCoordinateHandler = [
                handleAction: {
                    Serializable p, Serializable g, Target t ->
                        assert PONE.id.is(p)
                        assert gameId.is(g)
                        assert target.is(t)
                        maskedGame
                }
        ] as FireAtCoordinateHandler
        assert maskedGame.is(services.fire(target))
    }

    @Test
    void testMissile() {
        TBMaskedGame maskedGame = new TBMaskedGame()
        Target target = new Target(player: PONE.md5, coordinate: new GridCoordinate(10, 5))
        ObjectId gameId = new ObjectId()
        services.playerID.set(PONE.id)
        services.gameID.set(gameId)
        services.cruiseMissileHandler = [
                handleAction: {
                    Serializable p, Serializable g, Target t ->
                        assert PONE.id.is(p)
                        assert gameId.is(g)
                        assert target.is(t)
                        maskedGame
                }
        ] as CruiseMissileHandler
        assert maskedGame.is(services.cruiseMissile(target))
    }

    @Test
    void testSpy() {
        TBMaskedGame maskedGame = new TBMaskedGame()
        Target target = new Target(player: PONE.md5, coordinate: new GridCoordinate(10, 5))
        ObjectId gameId = new ObjectId()
        services.playerID.set(PONE.id)
        services.gameID.set(gameId)
        services.spyHandler = [
                handleAction: {
                    Serializable p, Serializable g, Target t ->
                        assert PONE.id.is(p)
                        assert gameId.is(g)
                        assert target.is(t)
                        maskedGame
                }
        ] as SpyHandler
        assert maskedGame.is(services.spy(target))
    }

    @Test
    void testRepair() {
        TBMaskedGame maskedGame = new TBMaskedGame()
        Target target = new Target(player: PONE.md5, coordinate: new GridCoordinate(10, 5))
        ObjectId gameId = new ObjectId()
        services.playerID.set(PONE.id)
        services.gameID.set(gameId)
        services.repairShipHandler = [
                handleAction: {
                    Serializable p, Serializable g, Target t ->
                        assert PONE.id.is(p)
                        assert gameId.is(g)
                        assert target.is(t)
                        maskedGame
                }
        ] as RepairShipHandler
        assert maskedGame.is(services.repair(target))
    }

    @Test
    void testECM() {
        TBMaskedGame maskedGame = new TBMaskedGame()
        Target target = new Target(player: PONE.md5, coordinate: new GridCoordinate(10, 5))
        ObjectId gameId = new ObjectId()
        services.playerID.set(PONE.id)
        services.gameID.set(gameId)
        services.ecmHandler = [
                handleAction: {
                    Serializable p, Serializable g, Target t ->
                        assert PONE.id.is(p)
                        assert gameId.is(g)
                        assert target.is(t)
                        maskedGame
                }
        ] as ECMHandler
        assert maskedGame.is(services.ecm(target))
    }

    @Test
    void testMove() {
        TBMaskedGame maskedGame = new TBMaskedGame()
        Target target = new Target(player: PONE.md5, coordinate: new GridCoordinate(10, 5))
        ObjectId gameId = new ObjectId()
        services.playerID.set(PONE.id)
        services.gameID.set(gameId)
        services.evasiveManeuverHandler = [
                handleAction: {
                    Serializable p, Serializable g, Target t ->
                        assert PONE.id.is(p)
                        assert gameId.is(g)
                        assert target.is(t)
                        maskedGame
                }
        ] as EvasiveManeuverHandler
        assert maskedGame.is(services.move(target))
    }
}