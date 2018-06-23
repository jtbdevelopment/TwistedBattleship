package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.scoring.GameScorer
import org.bson.types.ObjectId
import org.junit.Test
import org.mockito.Mockito

/**
 * Date: 4/22/15
 * Time: 9:12 PM
 */
class GamePhaseTransitionEngineTest extends MongoGameCoreTestCase {
    private GameScorer<TBGame> gameScorer = Mockito.mock(GameScorer.class);
    private GamePhaseTransitionEngine engine = new GamePhaseTransitionEngine(gameScorer)

    @Test
    void testEvaluateSetupPhaseWithAllPlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.startingShips = Ship.values().toList()
        game.playerDetails = [
                (PONE.id) : new TBPlayerState(setup: true),
                (PFOUR.id): new TBPlayerState(setup: true),
                (PTWO.id) : new TBPlayerState(setup: true)
        ]
        game.playerDetails.values().each {
            it.startingShips = game.startingShips
        }
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Playing

        game.playerDetails.each {
            assert TBActionLogEntry.TBActionType.Begin == it.value.actionLog[-1].actionType
            assert "Game ready to play." == it.value.actionLog[-1].description
        }
    }

    @Test
    void testEvaluateSetupPhaseWithSomePlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.playerDetails = [
                (PONE.id)  : new TBPlayerState(setup: true),
                (PFOUR.id) : new TBPlayerState(setup: false),
                (PTHREE.id): new TBPlayerState(setup: true),
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Setup
        game.playerDetails.each { assert 0 == it.value.actionLog.size() }
    }

    @Test
    void testEvaluateSetupPhaseWithNoPlayersSetup() {
        TBGame game = new TBGame()
        game.gamePhase = GamePhase.RoundOver.Setup
        game.playerDetails = [
                (PONE.id) : new TBPlayerState(setup: false),
                (PFOUR.id): new TBPlayerState(setup: false),
                (PTWO.id) : new TBPlayerState(setup: false)
        ]
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.Setup
        game.playerDetails.each { assert 0 == it.value.actionLog.size() }
    }

    @Test
    void testEvaluatePlayingPhaseOnePlayerAlive() {
        TBGame game = new TBGame()
        game.setId(new ObjectId())
        boolean scorerCalled = false
        game.gamePhase = GamePhase.RoundOver.Playing
        game.playerDetails = [
                (PONE.id) : new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, 0, [], [])]),
                (PFOUR.id): new TBPlayerState(shipStates: []),
                (PTWO.id) : new TBPlayerState(shipStates: [new ShipState(Ship.Submarine, 1, [new GridCoordinate(0, 0), new GridCoordinate(0, 1), new GridCoordinate(0, 2)], [false, true, true])])
        ]
        game.playerDetails[PONE.id].opponentGrids[PTWO.id] = new Grid(10)
        game.playerDetails[PONE.id].opponentGrids[PTWO.id].set(0, 2, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentViews[PONE.id] = new Grid(10)
        game.playerDetails[PTWO.id].opponentViews[PONE.id].set(0, 2, GridCellState.KnownByHit)
        game.players = [PONE, PFOUR, PTWO]
        Mockito.when(gameScorer.scoreGame(game)).thenReturn(game)
        TBGame result = engine.evaluateGame(game)
        assert result.gamePhase == GamePhase.RoundOver
        game.playerDetails.each {
            assert "200000000000000000000000 defeated all challengers!" == it.value.actionLog[-1].description
            assert TBActionLogEntry.TBActionType.Victory == it.value.actionLog[-1].actionType
        }
        assert GridCellState.RevealedShip == game.playerDetails[PONE.id].opponentGrids[PTWO.id].get(0, 0)
        assert GridCellState.RevealedShip == game.playerDetails[PTWO.id].opponentViews[PONE.id].get(0, 0)
        assert GridCellState.HiddenHit == game.playerDetails[PONE.id].opponentGrids[PTWO.id].get(0, 1)
        assert GridCellState.HiddenHit == game.playerDetails[PTWO.id].opponentViews[PONE.id].get(0, 1)
        assert GridCellState.KnownByHit == game.playerDetails[PONE.id].opponentGrids[PTWO.id].get(0, 2)
        assert GridCellState.KnownByHit == game.playerDetails[PTWO.id].opponentViews[PONE.id].get(0, 2)
        Mockito.verify(gameScorer).scoreGame(game)
    }

    @Test
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
        game.playerDetails.each { assert 0 == it.value.actionLog.size() }
    }
}
