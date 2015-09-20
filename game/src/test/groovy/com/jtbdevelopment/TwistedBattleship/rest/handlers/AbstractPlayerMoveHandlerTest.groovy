package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.CannotTargetInactivePlayerException
import com.jtbdevelopment.TwistedBattleship.exceptions.CoordinateOutOfBoundsException
import com.jtbdevelopment.TwistedBattleship.exceptions.InvalidTargetPlayerException
import com.jtbdevelopment.TwistedBattleship.exceptions.NotEnoughActionsForSpecialException
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.exceptions.input.GameIsNotInPlayModeException
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException
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
    boolean targetSelf = false
    AbstractPlayerMoveHandler handler = new AbstractPlayerMoveHandler() {
        @Override
        void validateMoveSpecific(
                final Player player, final TBGame game, final Player targetPlayer, final GridCoordinate coordinate) {
            throw new IllegalArgumentException()
        }

        @Override
        int movesRequired(final TBGame game) {
            return MOVES_REQUIRED
        }

        @Override
        boolean targetSelf() {
            return targetSelf
        }

        @Override
        TBGame playMove(
                final Player player, final TBGame game, final Player targetedPlayer, final GridCoordinate coordinate) {
            return game
        }
    }

    @Override
    protected void setUp() throws Exception {
        handler.playerRepository = [
                findByMd5: {
                    String md5 ->
                        [PONE, PTWO, PTHREE, PFOUR, PINACTIVE1, PINACTIVE2].find { md5 == it.md5 }
                }
        ] as AbstractPlayerRepository
        targetSelf = false
    }

    void testExceptionForPlayerOutOfTurn() {
        shouldFail(PlayerOutOfTurnException.class, {
            TBGame game = new TBGame(currentPlayer: PONE.id)
            handler.handleActionInternal(PTHREE, game, new Target(player: PTHREE.md5, coordinate: null))
        })
    }

    void testExceptionForTargetPlayerNotPartOfGame() {
        shouldFail(PlayerNotPartOfGameException.class, {
            TBGame game = new TBGame(currentPlayer: PONE.id, players: [PONE, PTWO])
            handler.handleActionInternal(PONE, game, new Target(player: PTHREE.md5))
        })
    }

    void testExceptionForTargetingSelfForNonSelfMove() {
        shouldFail(InvalidTargetPlayerException.class, {
            TBGame game = new TBGame(currentPlayer: PONE.id, players: [PONE, PTWO])
            handler.handleActionInternal(PONE, game, new Target(player: PONE.md5))
        })
    }

    void testExceptionForTargetingOtherForSelfMove() {
        shouldFail(InvalidTargetPlayerException.class, {
            targetSelf = true
            TBGame game = new TBGame(currentPlayer: PONE.id, players: [PONE, PTWO])
            handler.handleActionInternal(PONE, game, new Target(player: PTWO.md5))
        })
    }

    void testExceptionForPlayerOutBoundsCoordinate() {
        [GameFeature.Grid20x20, GameFeature.Grid10x10, GameFeature.Grid15x15].each {
            GameFeature size ->
                TBGame game = new TBGame(currentPlayer: PONE.id, features: [size], remainingMoves: 5, gamePhase: GamePhase.Playing, players: [PONE, PTWO])
                GridCoordinate coord = new GridCoordinate(0, 0)
                shouldFail(CoordinateOutOfBoundsException.class, {
                    handler.handleActionInternal(PONE, game, new Target(player: PTWO.md5, coordinate: coord))
                })
        }
    }

    void testExceptionForNotEnoughMovesRemainingForSpecialMoveOnPerShipGame() {
        shouldFail(NotEnoughActionsForSpecialException.class, {
            TBGame game = new TBGame(currentPlayer: PONE.id, remainingMoves: MOVES_REQUIRED - 1, features: [GameFeature.PerShip], gamePhase: GamePhase.Playing, players: [PONE, PTHREE])
            handler.handleActionInternal(PONE, game, new Target(player: PTHREE.md5, coordinate: new GridCoordinate(0, 0)))
        })
    }

    void testExceptionForGameNotInPlayingPhase() {
        GamePhase.values().findAll { it != GamePhase.Playing }.each {
            GamePhase it ->
                TBGame game = new TBGame(currentPlayer: PONE.id, remainingMoves: MOVES_REQUIRED, features: [GameFeature.PerShip, GameFeature.Grid10x10], gamePhase: it, players: [PONE, PFOUR], gridSize: 10)
                shouldFail(GameIsNotInPlayModeException.class, {
                    handler.handleActionInternal(PONE, game, new Target(player: PFOUR.md5, coordinate: new GridCoordinate(0, 0)))
                })

        }
    }

    void testExceptionForTargetingInactivePlayer() {
        TBGame game = new TBGame(
                currentPlayer: PONE.id,
                remainingMoves: MOVES_REQUIRED,
                features: [GameFeature.PerShip, GameFeature.Grid10x10],
                gamePhase: GamePhase.Playing,
                gridSize: 10,
                players: [PONE, PFOUR],
                playerDetails: [
                        (PFOUR.id): [
                                isAlive: {
                                    false
                                }
                        ] as TBPlayerState
                ]
        )
        shouldFail(CannotTargetInactivePlayerException.class, {
            handler.handleActionInternal(PONE, game, new Target(player: PFOUR.md5, coordinate: new GridCoordinate(0, 0)))
        })
    }

    void testExceptionForMoveSpecificValidation() {
        TBGame game = new TBGame(
                currentPlayer: PONE.id,
                remainingMoves: MOVES_REQUIRED,
                features: [GameFeature.PerShip, GameFeature.Grid10x10],
                gamePhase: GamePhase.Playing,
                players: [PONE, PFOUR],
                gridSize: 10,
                playerDetails: [
                        (PFOUR.id): [
                                isAlive: {
                                    true
                                }
                        ] as TBPlayerState
                ]
        )
        shouldFail(IllegalArgumentException.class, {
            handler.handleActionInternal(PONE, game, new Target(player: PFOUR.md5, coordinate: new GridCoordinate(0, 0)))
        })
    }

    void testDoesNotRotateIfMovesRemain() {
        TBGame initialGame = new TBGame(
                features: [GameFeature.Grid10x10],
                remainingMoves: MOVES_REQUIRED + 1,
                currentPlayer: PTHREE.id,
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(shipStates: []),
                        (PTWO.id)  : new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                        (PTHREE.id): new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                ],
                gamePhase: GamePhase.Playing)

        assert initialGame.is(handler.rotateTurnBasedGame(initialGame))
        assert 1 == initialGame.remainingMoves
        assert PTHREE.id == initialGame.currentPlayer
    }

    void testRotatesToNextAlivePlayerIfMovesZeroAndNotPerShip() {
        TBGame initialGame = new TBGame(
                features: [GameFeature.Grid10x10],
                remainingMoves: MOVES_REQUIRED,
                currentPlayer: PTHREE.id,
                players: [PONE, PTWO, PTHREE],
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(shipStates: []),
                        (PTWO.id)  : new TBPlayerState(shipStates: [
                                new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>),
                                new ShipState(Ship.Cruiser, [] as SortedSet<GridCoordinate>)
                        ]),
                        (PTHREE.id): new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                ],
                gamePhase: GamePhase.Playing)

        assert initialGame.is(handler.rotateTurnBasedGame(initialGame))
        assert 1 == initialGame.remainingMoves
        assert PTWO.id == initialGame.currentPlayer
    }

    void testRotatesToNextAlivePlayerIfMovesZeroAndPerShip() {
        TBGame initialGame = new TBGame(
                features: [GameFeature.Grid10x10, GameFeature.PerShip],
                remainingMoves: MOVES_REQUIRED,
                currentPlayer: PTHREE.id,
                players: [PONE, PTWO, PTHREE],
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(shipStates: []),
                        (PTWO.id)  : new TBPlayerState(shipStates: [
                                new ShipState(Ship.Destroyer, 0, [], []),
                                new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>),
                                new ShipState(Ship.Carrier, [] as SortedSet<GridCoordinate>),
                                new ShipState(Ship.Cruiser, [] as SortedSet<GridCoordinate>)
                        ]),
                        (PTHREE.id): new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                ],
                gamePhase: GamePhase.Playing)

        assert initialGame.is(handler.rotateTurnBasedGame(initialGame))
        assert 3 == initialGame.remainingMoves
        assert PTWO.id == initialGame.currentPlayer
    }

    void testDoesNotRotatesToNextAlivePlayerIfMovesZeroButNoOtherAlivePlayers() {
        TBGame initialGame = new TBGame(
                features: [GameFeature.Grid10x10],
                remainingMoves: MOVES_REQUIRED,
                currentPlayer: PTHREE.id,
                players: [PONE, PTWO, PTHREE],
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(shipStates: []),
                        (PTWO.id)  : new TBPlayerState(shipStates: []),
                        (PTHREE.id): new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)]),
                ],
                gamePhase: GamePhase.Playing)

        assert initialGame.is(handler.rotateTurnBasedGame(initialGame))
        assert 0 == initialGame.remainingMoves
        assert PTHREE.id == initialGame.currentPlayer
    }
}
