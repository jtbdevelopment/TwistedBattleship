package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.rest.GameAction
import com.jtbdevelopment.TwistedBattleship.rest.GameActionInfo
import com.jtbdevelopment.TwistedBattleship.rest.handlers.PlayerMoveHandler
import com.jtbdevelopment.TwistedBattleship.rest.handlers.SetupShipsHandler
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import groovy.transform.TypeChecked
import org.bson.types.ObjectId

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Date: 5/5/15
 * Time: 6:38 PM
 */
class GameServicesTest extends MongoGameCoreTestCase {
    GameServices services = new GameServices()

    void testActionAnnotations() {
        Map<String, List<Object>> stuff = [
                //  method: [name, params, path, path param values, consumes
                "setupShips": ["setup", [Map.class], [], [MediaType.APPLICATION_JSON]],
                "play": ["play", [GameActionInfo.class], [], [MediaType.APPLICATION_JSON]],
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

    void testSetupShips() {
        Map<Ship, List<GridCoordinate>> input = [
                (Ship.Cruiser)  : [
                        new GridCoordinate(0, 10),
                        new GridCoordinate(0, 9),
                        new GridCoordinate(0, 11)
                ],
                (Ship.Destroyer): [
                        new GridCoordinate(1, 10),
                        new GridCoordinate(1, 9),
                        new GridCoordinate(1, 10)
                ]
        ]
        TBMaskedGame maskedGame = new TBMaskedGame()
        ObjectId gameId = new ObjectId()
        services.playerID.set(PONE.id)
        services.gameID.set(gameId)
        services.setupShipsHandler = [
                handleAction: {
                    Serializable p, Serializable g, Map<Ship, ShipState> ss ->
                        assert PONE.id.is(p)
                        assert gameId.is(g)
                        assert 2 == ss.size()
                        assert ss[Ship.Cruiser].healthRemaining == 3
                        assert ss[Ship.Cruiser].ship == Ship.Cruiser
                        assert ss[Ship.Cruiser].shipSegmentHit == [false, false, false]
                        assert ss[Ship.Cruiser].shipGridCells.toList() == [new GridCoordinate(0, 9), new GridCoordinate(0, 10), new GridCoordinate(0, 11)]
                        assert ss[Ship.Destroyer].healthRemaining == 2
                        assert ss[Ship.Destroyer].ship == Ship.Destroyer
                        assert ss[Ship.Destroyer].shipSegmentHit == [false, false]
                        assert ss[Ship.Destroyer].shipGridCells.toList() == [new GridCoordinate(1, 9), new GridCoordinate(1, 10)]
                        return maskedGame
                }
        ] as SetupShipsHandler
        assert maskedGame.is(services.setupShips(input))
    }

    void testPlayTurn() {
        ObjectId gameId = new ObjectId()
        services.playerID.set(PTHREE.id)
        services.gameID.set(gameId)
        GameActionInfo info = new GameActionInfo(action: GameAction.ECM, coordinate: new GridCoordinate(20, 10))
        TBMaskedGame maskedGame = new TBMaskedGame()

        services.playerMoveHandler = [
                handleAction: {
                    Serializable p, Serializable g, GameActionInfo action ->
                        assert PTHREE.id.is(p)
                        assert gameId.is(g)
                        assert action.action == GameAction.ECM
                        assert action.coordinate == new GridCoordinate(20, 10)
                        maskedGame
                }
        ] as PlayerMoveHandler
        assert maskedGame.is(services.play(info))
    }
}
