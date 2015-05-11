package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.CoordinateOutOfBoundsException
import com.jtbdevelopment.TwistedBattleship.exceptions.NotEnoughActionsForSpecialException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.exceptions.input.GameIsNotInPlayModeException
import com.jtbdevelopment.games.exceptions.input.PlayerOutOfTurnException
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase

/**
 * Date: 5/8/15
 * Time: 7:00 AM
 */
class AbstractPlayerMoveHandlerTest extends MongoGameCoreTestCase {
    private static final int MOVES_REQUIRED = 2
    AbstractPlayerMoveHandler handler = new AbstractPlayerMoveHandler() {
        @Override
        int movesRequired() {
            return MOVES_REQUIRED
        }

        @Override
        TBGame playMove(final Player player, final TBGame game, final GridCoordinate coordinate) {
            return game
        }
    }

    void testExceptionForPlayerOutOfTurn() {
        shouldFail(PlayerOutOfTurnException.class, {
            TBGame game = new TBGame(currentPlayer: PONE.id)
            handler.handleActionInternal(PTHREE, game, null)
        })
    }

    void testExceptionForPlayerOutBoundsCoordinate() {

        [GameFeature.Grid20x20, GameFeature.Grid10x10, GameFeature.Grid15x15].each {
            GameFeature size ->
                TBGame game = new TBGame(currentPlayer: PONE.id, features: [size], remainingMoves: 5, gamePhase: GamePhase.Playing)
                GridCoordinate coord = new GridCoordinate(0, 0)
                handler.gridSizeUtil = [
                        isValidCoordinate: {
                            TBGame g, GridCoordinate c ->
                                assert g.is(game)
                                assert c.is(coord)
                                return false
                        }
                ] as GridSizeUtil
                shouldFail(CoordinateOutOfBoundsException.class, {
                    handler.handleActionInternal(PONE, game, coord)
                })
        }
    }

    void testExceptionForNotEnoughMovesRemainingForSpecialMoveOnPerShipGame() {
        shouldFail(NotEnoughActionsForSpecialException.class, {
            TBGame game = new TBGame(currentPlayer: PONE.id, remainingMoves: MOVES_REQUIRED - 1, features: [GameFeature.PerShip], gamePhase: GamePhase.Playing)
            handler.handleActionInternal(PONE, game, new GridCoordinate(0, 0))
        })
    }

    void testExceptionForGameNotInPlayingPhase() {
        GamePhase.values().findAll { it != GamePhase.Playing }.each {
            GamePhase it ->
                handler.gridSizeUtil = new GridSizeUtil()
                TBGame game = new TBGame(currentPlayer: PONE.id, remainingMoves: MOVES_REQUIRED, features: [GameFeature.PerShip, GameFeature.Grid10x10], gamePhase: it)
                shouldFail(GameIsNotInPlayModeException.class, {
                    handler.handleActionInternal(PONE, game, new GridCoordinate(0, 0))
                })

        }
    }

    void testDoesNotRotateIfMovesRemain() {
        TBGame initialGame = new TBGame(
                features: [GameFeature.Grid10x10],
                remainingMoves: MOVES_REQUIRED + 1,
                currentPlayer: PTHREE.id,
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(shipStates: [:]),
                        (PTWO.id)  : new TBPlayerState(shipStates: [
                                (Ship.Battleship): new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                        (PTHREE.id): new TBPlayerState(shipStates: [
                                (Ship.Battleship): new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                ],
                gamePhase: GamePhase.Playing)
        handler.gridSizeUtil = new GridSizeUtil()

        assert initialGame.is(handler.rotateTurnBasedGame(initialGame))
        assert 1 == initialGame.remainingMoves
        assert PTHREE.id == initialGame.currentPlayer
    }

    void testRotatesToNextAlivePlayerIfMovesZero() {
        TBGame initialGame = new TBGame(
                features: [GameFeature.Grid10x10],
                movesPerTurn: 5,
                remainingMoves: MOVES_REQUIRED,
                currentPlayer: PTHREE.id,
                players: [PONE, PTWO, PTHREE],
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(shipStates: [:]),
                        (PTWO.id)  : new TBPlayerState(shipStates: [
                                (Ship.Battleship): new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                        (PTHREE.id): new TBPlayerState(shipStates: [
                                (Ship.Battleship): new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                ],
                gamePhase: GamePhase.Playing)
        handler.gridSizeUtil = new GridSizeUtil()

        assert initialGame.is(handler.rotateTurnBasedGame(initialGame))
        assert 5 == initialGame.remainingMoves
        assert PTWO.id == initialGame.currentPlayer
    }

    void testDoesNotRotatesToNextAlivePlayerIfMovesZeroButNoOtherAlivePlayers() {
        TBGame initialGame = new TBGame(
                features: [GameFeature.Grid10x10],
                movesPerTurn: 5,
                remainingMoves: MOVES_REQUIRED,
                currentPlayer: PTHREE.id,
                players: [PONE, PTWO, PTHREE],
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(shipStates: [:]),
                        (PTWO.id)  : new TBPlayerState(shipStates: [:]),
                        (PTHREE.id): new TBPlayerState(shipStates: [
                                (Ship.Battleship): new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                ],
                gamePhase: GamePhase.Playing)
        handler.gridSizeUtil = new GridSizeUtil()

        assert initialGame.is(handler.rotateTurnBasedGame(initialGame))
        assert 0 == initialGame.remainingMoves
        assert PTHREE.id == initialGame.currentPlayer
    }
}
