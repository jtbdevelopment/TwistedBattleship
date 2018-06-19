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
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.exceptions.input.GameIsNotInPlayModeException
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException
import com.jtbdevelopment.games.exceptions.input.PlayerOutOfTurnException
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.masking.GameMasker
import com.jtbdevelopment.games.state.transition.GameTransitionEngine
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import org.bson.types.ObjectId
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Date: 5/8/15
 * Time: 7:00 AM
 */
class AbstractPlayerMoveHandlerTest extends MongoGameCoreTestCase {
    private static final int MOVES_REQUIRED = 2
    boolean targetSelf = false
    private AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository = Mockito.mock(AbstractPlayerRepository.class)
    private AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository = Mockito.mock(AbstractGameRepository.class)
    private GameTransitionEngine<TBGame> transitionEngine = Mockito.mock(GameTransitionEngine.class)
    private GamePublisher<TBGame, MongoPlayer> gamePublisher = Mockito.mock(GamePublisher.class)
    private GameEligibilityTracker gameEligibilityTracker = Mockito.mock(GameEligibilityTracker.class)
    private GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker = Mockito.mock(GameMasker.class)

    private class TestHandler extends AbstractPlayerMoveHandler {
        TestHandler(
                final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
                final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository,
                final GameTransitionEngine<TBGame> transitionEngine,
                final GamePublisher<TBGame, MongoPlayer> gamePublisher,
                final GameEligibilityTracker gameTracker,
                final GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
            super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker)
        }

        @Override
        void validateMoveSpecific(
                final MongoPlayer player,
                final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
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
                final MongoPlayer player,
                final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
            if (game.playerDetails[PTHREE.id]) {
                game.playerDetails[PTHREE.id].lastActionMessage = "3"
            }
            return game
        }
    }
    private AbstractPlayerMoveHandler handler = new TestHandler(playerRepository, gameRepository, transitionEngine, gamePublisher, gameEligibilityTracker, gameMasker)

    @Before
    void setUp() throws Exception {
        Mockito.when(playerRepository.findByMd5(PONE.md5)).thenReturn(PONE)
        Mockito.when(playerRepository.findByMd5(PTWO.md5)).thenReturn(PTWO)
        Mockito.when(playerRepository.findByMd5(PTHREE.md5)).thenReturn(PTHREE)
        Mockito.when(playerRepository.findByMd5(PFOUR.md5)).thenReturn(PFOUR)
        Mockito.when(playerRepository.findByMd5(PFIVE.md5)).thenReturn(PFIVE)
        Mockito.when(playerRepository.findByMd5(PINACTIVE1.md5)).thenReturn(PINACTIVE1)
        Mockito.when(playerRepository.findByMd5(PINACTIVE2.md5)).thenReturn(PINACTIVE2)
        targetSelf = false
    }

    @Test(expected = PlayerOutOfTurnException.class)
    void testExceptionForPlayerOutOfTurn() {
        TBGame game = new TBGame(currentPlayer: PONE.id)
        handler.handleActionInternal(PTHREE, game, new Target(player: PTHREE.md5, coordinate: null))
    }

    @Test(expected = PlayerNotPartOfGameException.class)
    void testExceptionForTargetPlayerNotPartOfGame() {
        TBGame game = new TBGame(currentPlayer: PONE.id, players: [PONE, PTWO])
        handler.handleActionInternal(PONE, game, new Target(player: PTHREE.md5))
    }

    @Test(expected = InvalidTargetPlayerException.class)
    void testExceptionForTargetingSelfForNonSelfMove() {
        TBGame game = new TBGame(currentPlayer: PONE.id, players: [PONE, PTWO])
        handler.handleActionInternal(PONE, game, new Target(player: PONE.md5))
    }

    @Test(expected = InvalidTargetPlayerException.class)
    void testExceptionForTargetingOtherForSelfMove() {
        targetSelf = true
        TBGame game = new TBGame(currentPlayer: PONE.id, players: [PONE, PTWO])
        handler.handleActionInternal(PONE, game, new Target(player: PTWO.md5))
    }

    @Test
    void testExceptionForPlayerOutBoundsCoordinate() {
        [GameFeature.Grid20x20, GameFeature.Grid10x10, GameFeature.Grid15x15].each {
            GameFeature size ->
                TBGame game = new TBGame(currentPlayer: PONE.id, features: [size], remainingMoves: 5, gamePhase: GamePhase.Playing, players: [PONE, PTWO])
                GridCoordinate coord = new GridCoordinate(0, 0)
                try {
                    handler.handleActionInternal(PONE, game, new Target(player: PTWO.md5, coordinate: coord))
                    Assert.fail()
                } catch (CoordinateOutOfBoundsException e) {
                    //
                }
        }

    }


    @Test(expected = NotEnoughActionsForSpecialException.class)
    void testExceptionForNotEnoughMovesRemainingForSpecialMoveOnPerShipGame() {
        TBGame game = new TBGame(currentPlayer: PONE.id, remainingMoves: MOVES_REQUIRED - 1, features: [GameFeature.PerShip], gamePhase: GamePhase.Playing, players: [PONE, PTHREE])
        handler.handleActionInternal(PONE, game, new Target(player: PTHREE.md5, coordinate: new GridCoordinate(0, 0)))
    }

    @Test
    void testExceptionForGameNotInPlayingPhase() {
        GamePhase.values().findAll { it != GamePhase.Playing }.each {
            GamePhase it ->
                TBGame game = new TBGame(currentPlayer: PONE.id, remainingMoves: MOVES_REQUIRED, features: [GameFeature.PerShip, GameFeature.Grid10x10], gamePhase: it, players: [PONE, PFOUR], gridSize: 10)
                try {
                    handler.handleActionInternal(PONE, game, new Target(player: PFOUR.md5, coordinate: new GridCoordinate(0, 0)))
                    Assert.fail();
                } catch (GameIsNotInPlayModeException e) {

                }
        }
    }

    @Test(expected = CannotTargetInactivePlayerException.class)
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
        handler.handleActionInternal(PONE, game, new Target(player: PFOUR.md5, coordinate: new GridCoordinate(0, 0)))
    }

    @Test(expected = IllegalArgumentException.class)
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
        handler.handleActionInternal(PONE, game, new Target(player: PFOUR.md5, coordinate: new GridCoordinate(0, 0)))
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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
