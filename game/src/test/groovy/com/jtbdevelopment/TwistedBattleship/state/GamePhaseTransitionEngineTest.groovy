package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
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
        game.startingShips = Ship.values().toList()
        game.playerDetails = [
                (PONE.id) : new TBPlayerState(shipStates: [
                        new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ], lastActionMessage: "X"),
                (PFOUR.id): new TBPlayerState(shipStates: [
                        new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ]),
                (PTWO.id) : new TBPlayerState(shipStates: [
                        new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ])
        ]
        game.playerDetails.values().each {
            it.startingShips = game.startingShips
        }
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Playing
        assert "Begin!" == game.generalMessage
        game.playerDetails.each { assert "" == it.value.lastActionMessage }
    }

    void testEvaluateSetupPhaseWithSomePlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.generalMessage = "Y"
        game.playerDetails = [
                (PONE.id)  : new TBPlayerState(shipStates: [
                        new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ], lastActionMessage: "X"),
                (PFOUR.id) : new TBPlayerState(shipStates: [], lastActionMessage: "X"),
                (PTHREE.id): new TBPlayerState(shipStates: [
                        new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Cruiser, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Carrier, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Submarine, new TreeSet<GridCoordinate>()),
                        new ShipState(Ship.Destroyer, new TreeSet<GridCoordinate>())
                ], lastActionMessage: "X"),
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Setup
        game.playerDetails.each { assert "X" == it.value.lastActionMessage }
        assert "Y" == game.generalMessage
    }

    void testEvaluateSetupPhaseWithNoPlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.generalMessage = "Y"
        game.playerDetails = [
                (PONE.id) : new TBPlayerState(shipStates: [], lastActionMessage: "X"),
                (PFOUR.id): new TBPlayerState(shipStates: [], lastActionMessage: "X"),
                (PTWO.id) : new TBPlayerState(shipStates: [], lastActionMessage: "X")
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Setup
        game.playerDetails.each { assert "X" == it.value.lastActionMessage }
        assert "Y" == game.generalMessage
    }

    void testEvaluatePlayingPhaseOnePlayerAlive() {
        TBGame game = new TBGame()
        boolean scorerCalled = false
        game.gamePhase = GamePhase.RoundOver.Playing
        game.playerDetails = [
                (PONE.id): new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, 0, [], [])], lastActionMessage: "X"),
                (PFOUR.id): new TBPlayerState(shipStates: []),
                (PTWO.id): new TBPlayerState(shipStates: [new ShipState(Ship.Submarine, 1, [new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2)], [false, true, true])])
        ]
        game.playerDetails[PONE.id].opponentGrids[PTWO.id] = new Grid(10)
        game.playerDetails[PONE.id].opponentGrids[PTWO.id].set(0, 2, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentViews[PONE.id] = new Grid(10)
        game.playerDetails[PTWO.id].opponentViews[PONE.id].set(0, 2, GridCellState.KnownByHit)
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
        game.playerDetails.each { assert "" == it.value.lastActionMessage }
        assert "2 defeated all challengers!" == game.generalMessage
        assert GridCellState.RevealedShip == game.playerDetails[PONE.id].opponentGrids[PTWO.id].get(0, 0)
        assert GridCellState.RevealedShip == game.playerDetails[PTWO.id].opponentViews[PONE.id].get(0, 0)
        assert GridCellState.HiddenHit == game.playerDetails[PONE.id].opponentGrids[PTWO.id].get(0, 1)
        assert GridCellState.HiddenHit == game.playerDetails[PTWO.id].opponentViews[PONE.id].get(0, 1)
        assert GridCellState.KnownByHit == game.playerDetails[PONE.id].opponentGrids[PTWO.id].get(0, 2)
        assert GridCellState.KnownByHit == game.playerDetails[PTWO.id].opponentViews[PONE.id].get(0, 2)
    }

    void testEvaluatePlayingPhaseMultiplePlayerAlive() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Playing
        game.playerDetails = [
                (PONE.id) : new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, 2, [], [])]),
                (PFOUR.id): new TBPlayerState(shipStates: []),
                (PTWO.id) : new TBPlayerState(shipStates: [new ShipState(Ship.Submarine, 1, [], [])])
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Playing
        game.playerDetails.each { assert "" == it.value.lastActionMessage }
    }
}
