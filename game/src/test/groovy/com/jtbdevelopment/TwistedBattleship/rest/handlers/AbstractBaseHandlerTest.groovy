package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.factory.gameinitializers.*
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipPlacementValidator
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase
import org.bson.types.ObjectId
import org.junit.Before

/**
 * Date: 5/18/15
 * Time: 6:47 AM
 */
abstract class AbstractBaseHandlerTest extends MongoGameCoreTestCase {
    protected TBGame game

    @Before
    public void setUp() throws Exception {
        game = new TBGame(
                id: new ObjectId(),
                features: [GameFeature.Grid15x15, GameFeature.ActionsPerTurn, GameFeature.CruiseMissileEnabled, GameFeature.ECMEnabled, GameFeature.EMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled],
                gridSize: 15,
                players: [PONE, PTWO, PTHREE, PFOUR])
        new BackwardsCompatibilityInitializer().initializeGame(game)
        new GridSizeInitializer().initializeGame(game)
        new StartingShipsInitializer().initializeGame(game)
        new CurrentPlayerInitializer().initializeGame(game)
        new MovesInitializer().initializeGame(game)
        new PlayerGameStateInitializer().initializeGame(game)
        game.gamePhase = GamePhase.Setup
        new SetupShipsHandler(new ShipPlacementValidator(), null, null, null, null, null, null).handleActionInternal(
                PONE,
                game,
                [
                        new ShipState(Ship.Carrier, [
                                new GridCoordinate(0, 0),
                                new GridCoordinate(1, 0),
                                new GridCoordinate(2, 0),
                                new GridCoordinate(3, 0),
                                new GridCoordinate(4, 0),
                        ] as SortedSet),
                        new ShipState(Ship.Battleship, [
                                new GridCoordinate(0, 14),
                                new GridCoordinate(1, 14),
                                new GridCoordinate(2, 14),
                                new GridCoordinate(3, 14),
                        ] as SortedSet),
                        new ShipState(Ship.Cruiser, [
                                new GridCoordinate(14, 14),
                                new GridCoordinate(13, 14),
                                new GridCoordinate(12, 14),
                        ] as SortedSet),
                        new ShipState(Ship.Submarine, [
                                new GridCoordinate(14, 0),
                                new GridCoordinate(13, 0),
                                new GridCoordinate(12, 0),
                        ] as SortedSet),
                        new ShipState(Ship.Destroyer, [
                                new GridCoordinate(7, 7),
                                new GridCoordinate(7, 8),
                        ] as SortedSet),
                ]
        )
    }

    protected TBActionLogEntry getLastEntry(final List<TBActionLogEntry> entries) {
        return entries.get(entries.size() - 1);
    }
}
