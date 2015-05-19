package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.factory.gameinitializers.PlayerGameStateInitializer
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipPlacementValidator
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase

/**
 * Date: 5/18/15
 * Time: 6:47 AM
 */
abstract class AbstractBaseHandlerTest extends MongoGameCoreTestCase {
    protected TBGame game

    @Override
    protected void setUp() throws Exception {
        game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.ActionsPerTurn, GameFeature.CriticalDisabled, GameFeature.ECMEnabled, GameFeature.EMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled],
                players: [PONE, PTWO, PTHREE, PFOUR])
        new PlayerGameStateInitializer(util: new GridSizeUtil()).initializeGame(game)
        game.gamePhase = GamePhase.Setup
        new SetupShipsHandler(shipPlacementValidator: new ShipPlacementValidator(gridSizeUtil: new GridSizeUtil())).handleActionInternal(
                PONE,
                game,
                [
                        (Ship.Carrier)   : new ShipState(Ship.Carrier, [
                                new GridCoordinate(0, 0),
                                new GridCoordinate(1, 0),
                                new GridCoordinate(2, 0),
                                new GridCoordinate(3, 0),
                                new GridCoordinate(4, 0),
                        ] as SortedSet),
                        (Ship.Battleship): new ShipState(Ship.Battleship, [
                                new GridCoordinate(0, 14),
                                new GridCoordinate(1, 14),
                                new GridCoordinate(2, 14),
                                new GridCoordinate(3, 14),
                        ] as SortedSet),
                        (Ship.Cruiser)   : new ShipState(Ship.Cruiser, [
                                new GridCoordinate(14, 14),
                                new GridCoordinate(13, 14),
                                new GridCoordinate(12, 14),
                        ] as SortedSet),
                        (Ship.Submarine) : new ShipState(Ship.Submarine, [
                                new GridCoordinate(14, 0),
                                new GridCoordinate(13, 0),
                                new GridCoordinate(12, 0),
                        ] as SortedSet),
                        (Ship.Destroyer) : new ShipState(Ship.Destroyer, [
                                new GridCoordinate(7, 7),
                                new GridCoordinate(7, 8),
                        ] as SortedSet),
                ]
        )
    }
}
