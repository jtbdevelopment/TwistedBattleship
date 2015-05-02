package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase

/**
 * Date: 4/22/15
 * Time: 9:12 PM
 */
class GamePhaseTransitionEngineTest extends MongoGameCoreTestCase {
    GamePhaseTransitionEngine engine = new GamePhaseTransitionEngine()

    void testEvaluateSetupPhaseWithAllPlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.playerDetails = [
                (PONE.id): new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser): new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier): new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
                (PFOUR.id): new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser): new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier): new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
                (PTWO.id): new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser): new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier): new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ])
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Playing
    }

    void testEvaluateSetupPhaseWithSomePlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.playerDetails = [
                (PONE.id): new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser): new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier): new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
                (PFOUR.id): new TBPlayerState(shipStates: [:]),
                (PTHREE.id): new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser): new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier): new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Setup
    }

    void testEvaluateSetupPhaseWithNoPlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.playerDetails = [
                (PONE.id): new TBPlayerState(shipStates: [:]),
                (PFOUR.id): new TBPlayerState(shipStates: [:]),
                (PTWO.id): new TBPlayerState(shipStates: [:])
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Setup
    }

    void testEvaluatePlayingPhaseOnePlayerAlive() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Playing
        game.playerDetails = [
                (PONE.id): new TBPlayerState(shipStates: [(Ship.Battleship): new ShipState(Ship.Battleship, 0, new TreeSet<GridCoordinate>(), []) ]),
                (PFOUR.id): new TBPlayerState(shipStates: [:]),
                (PTWO.id): new TBPlayerState(shipStates: [(Ship.Submarine): new ShipState(Ship.Submarine, 1, new TreeSet<GridCoordinate>(), []) ])
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.RoundOver
    }

    void testEvaluatePlayingPhaseMultiplePlayerAlive() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Playing
        game.playerDetails = [
                (PONE.id): new TBPlayerState(shipStates: [(Ship.Battleship): new ShipState(Ship.Battleship, 2, new TreeSet<GridCoordinate>(), []) ]),
                (PFOUR.id): new TBPlayerState(shipStates: [:]),
                (PTWO.id): new TBPlayerState(shipStates: [(Ship.Submarine): new ShipState(Ship.Submarine, 1, new TreeSet<GridCoordinate>(), []) ])
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Playing
    }
}
