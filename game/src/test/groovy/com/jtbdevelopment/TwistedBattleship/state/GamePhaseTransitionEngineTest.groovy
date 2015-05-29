package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.scoring.GameScorer

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
                (PONE.id) : new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser)  : new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier)  : new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
                (PFOUR.id): new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser)  : new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier)  : new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
                (PTWO.id) : new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser)  : new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier)  : new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ])
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Playing
        game.playerDetails.each { assert "Begin!" == it.value.lastActionMessage }
    }

    void testEvaluateSetupPhaseWithSomePlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.playerDetails = [
                (PONE.id)  : new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser)  : new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier)  : new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
                (PFOUR.id) : new TBPlayerState(shipStates: [:]),
                (PTHREE.id): new TBPlayerState(shipStates: [
                        (Ship.Battleship): new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        (Ship.Cruiser)  : new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        (Ship.Carrier)  : new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        (Ship.Submarine): new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        (Ship.Destroyer): new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Setup
        game.playerDetails.each { assert "" == it.value.lastActionMessage }
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
        game.playerDetails.each { assert "" == it.value.lastActionMessage }
    }

    void testEvaluatePlayingPhaseOnePlayerAlive() {
        TBGame game = new TBGame()
        boolean scorerCalled = false
        game.gamePhase = GamePhase.RoundOver.Playing
        game.playerDetails = [
                (PONE.id): new TBPlayerState(shipStates: [(Ship.Battleship): new ShipState(Ship.Battleship, 0, [], [])]),
                (PFOUR.id): new TBPlayerState(shipStates: [:]),
                (PTWO.id): new TBPlayerState(shipStates: [(Ship.Submarine): new ShipState(Ship.Submarine, 1, [], [])])
        ]
        game.players = [PONE, PFOUR, PTWO]
        engine.gameScorer = [
                scoreGame: {
                    TBGame g ->
                        assert game.is(g)
                        scorerCalled = true
                        return game
                }
        ] as GameScorer
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.RoundOver
        assert scorerCalled
        game.playerDetails.each { assert "2 defeated all challengers!" == it.value.lastActionMessage }
    }

    void testEvaluatePlayingPhaseMultiplePlayerAlive() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Playing
        game.playerDetails = [
                (PONE.id): new TBPlayerState(shipStates: [(Ship.Battleship): new ShipState(Ship.Battleship, 2, [], [])]),
                (PFOUR.id): new TBPlayerState(shipStates: [:]),
                (PTWO.id): new TBPlayerState(shipStates: [(Ship.Submarine): new ShipState(Ship.Submarine, 1, [], [])])
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Playing
        game.playerDetails.each { assert "" == it.value.lastActionMessage }
    }
}
