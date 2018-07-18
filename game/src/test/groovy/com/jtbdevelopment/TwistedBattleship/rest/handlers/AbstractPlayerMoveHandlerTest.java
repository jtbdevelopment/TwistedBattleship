package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.CannotTargetInactivePlayerException;
import com.jtbdevelopment.TwistedBattleship.exceptions.CoordinateOutOfBoundsException;
import com.jtbdevelopment.TwistedBattleship.exceptions.InvalidTargetPlayerException;
import com.jtbdevelopment.TwistedBattleship.exceptions.NotEnoughActionsForSpecialException;
import com.jtbdevelopment.TwistedBattleship.rest.Target;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.GameIsNotInPlayModeException;
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException;
import com.jtbdevelopment.games.exceptions.input.PlayerOutOfTurnException;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Date: 5/8/15
 * Time: 7:00 AM
 */
public class AbstractPlayerMoveHandlerTest extends MongoGameCoreTestCase {
    private static final int MOVES_REQUIRED = 2;
    private boolean targetSelf = false;
    private AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository = Mockito.mock(AbstractPlayerRepository.class);
    private AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository = Mockito.mock(AbstractGameRepository.class);
    private GameTransitionEngine<TBGame> transitionEngine = Mockito.mock(GameTransitionEngine.class);
    private GamePublisher<TBGame, MongoPlayer> gamePublisher = Mockito.mock(GamePublisher.class);
    private GameEligibilityTracker gameEligibilityTracker = Mockito.mock(GameEligibilityTracker.class);
    private GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker = Mockito.mock(GameMasker.class);
    private AbstractPlayerMoveHandler handler = new TestHandler(playerRepository, gameRepository, transitionEngine, gamePublisher, gameEligibilityTracker, gameMasker);

    @Before
    public void setUp() {
        Mockito.when(playerRepository.findByMd5(PONE.getMd5())).thenReturn(PONE);
        Mockito.when(playerRepository.findByMd5(PTWO.getMd5())).thenReturn(PTWO);
        Mockito.when(playerRepository.findByMd5(PTHREE.getMd5())).thenReturn(PTHREE);
        Mockito.when(playerRepository.findByMd5(PFOUR.getMd5())).thenReturn(PFOUR);
        Mockito.when(playerRepository.findByMd5(PFIVE.getMd5())).thenReturn(PFIVE);
        Mockito.when(playerRepository.findByMd5(PINACTIVE1.getMd5())).thenReturn(PINACTIVE1);
        Mockito.when(playerRepository.findByMd5(PINACTIVE2.getMd5())).thenReturn(PINACTIVE2);
        targetSelf = false;
    }

    @Test(expected = PlayerOutOfTurnException.class)
    public void testExceptionForPlayerOutOfTurn() {
        TBGame game = new TBGame();
        game.setCurrentPlayer(PONE.getId());
        Target target = new Target();
        target.setPlayer(PTHREE.getMd5());
        target.setCoordinate(null);
        handler.handleActionInternal(PTHREE, game, target);
    }

    @Test(expected = PlayerNotPartOfGameException.class)
    public void testExceptionForTargetPlayerNotPartOfGame() {
        TBGame game = new TBGame();
        game.setCurrentPlayer(PONE.getId());
        game.setPlayers(Arrays.asList(PONE, PTWO));
        Target target = new Target();
        target.setPlayer(PTHREE.getMd5());
        handler.handleActionInternal(PONE, game, target);
    }

    @Test(expected = InvalidTargetPlayerException.class)
    public void testExceptionForTargetingSelfForNonSelfMove() {
        TBGame game = new TBGame();
        game.setCurrentPlayer(PONE.getId());
        game.setPlayers(Arrays.asList(PONE, PTWO));
        Target target = new Target();
        target.setPlayer(PONE.getMd5());
        handler.handleActionInternal(PONE, game, target);
    }

    @Test(expected = InvalidTargetPlayerException.class)
    public void testExceptionForTargetingOtherForSelfMove() {
        targetSelf = true;
        TBGame game = new TBGame();
        game.setCurrentPlayer(PONE.getId());
        game.setPlayers(Arrays.asList(PONE, PTWO));
        Target target = new Target();
        target.setPlayer(PTWO.getMd5());
        handler.handleActionInternal(PONE, game, target);
    }

    @Test
    public void testExceptionForPlayerOutBoundsCoordinate() {
        Stream.of(GameFeature.Grid20x20, GameFeature.Grid10x10, GameFeature.Grid15x15).forEach(size -> {
            TBGame game = new TBGame();
            game.setCurrentPlayer(PONE.getId());
            game.setFeatures(new HashSet<>(Collections.singletonList(size)));
            game.setRemainingMoves(5);
            game.setGamePhase(GamePhase.Playing);
            game.setPlayers(Arrays.asList(PONE, PTWO));
            GridCoordinate coord = new GridCoordinate(0, 0);
            try {
                Target target = new Target();
                target.setPlayer(PTWO.getMd5());
                target.setCoordinate(coord);
                handler.handleActionInternal(PONE, game, target);
                Assert.fail();
            } catch (CoordinateOutOfBoundsException e) {
                //
            }
        });
    }

    @Test(expected = NotEnoughActionsForSpecialException.class)
    public void testExceptionForNotEnoughMovesRemainingForSpecialMoveOnPerShipGame() {
        TBGame game = new TBGame();
        game.setCurrentPlayer(PONE.getId());
        game.setRemainingMoves(MOVES_REQUIRED - 1);
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.PerShip)));
        game.setGamePhase(GamePhase.Playing);
        game.setPlayers(Arrays.asList(PONE, PTHREE));
        Target target = new Target();
        target.setPlayer(PTHREE.getMd5());
        target.setCoordinate(new GridCoordinate(0, 0));
        handler.handleActionInternal(PONE, game, target);
    }

    @Test
    public void testExceptionForGameNotInPlayingPhase() {
        Arrays.stream(GamePhase.values()).filter(p -> !GamePhase.Playing.equals(p)).forEach(phase -> {
            TBGame game = new TBGame();
            game.setCurrentPlayer(PONE.getId());
            game.setRemainingMoves(MOVES_REQUIRED);
            game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.PerShip, GameFeature.Grid10x10)));
            game.setGamePhase(phase);
            game.setPlayers(Arrays.asList(PONE, PFOUR));
            game.setGridSize(10);
            try {
                Target target = new Target();
                target.setPlayer(PFOUR.getMd5());
                target.setCoordinate(new GridCoordinate(0, 0));
                handler.handleActionInternal(PONE, game, target);
                Assert.fail();
            } catch (GameIsNotInPlayModeException e) {
                //
            }
        });
    }

    @Test(expected = CannotTargetInactivePlayerException.class)
    public void testExceptionForTargetingInactivePlayer() {
        TBGame game = new TBGame();
        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        map.put(PFOUR.getId(), Mockito.mock(TBPlayerState.class));
        Mockito.when(map.get(PFOUR.getId()).isAlive()).thenReturn(false);
        game.setCurrentPlayer(PONE.getId());
        game.setRemainingMoves(MOVES_REQUIRED);
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.PerShip, GameFeature.Grid10x10)));
        game.setGamePhase(GamePhase.Playing);
        game.setGridSize(10);
        game.setPlayers(Arrays.asList(PONE, PFOUR));
        game.setPlayerDetails(map);
        Target target = new Target();
        target.setPlayer(PFOUR.getMd5());
        target.setCoordinate(new GridCoordinate(0, 0));
        handler.handleActionInternal(PONE, game, target);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionForMoveSpecificValidation() {
        TBGame game = new TBGame();


        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        map.put(PFOUR.getId(), Mockito.mock(TBPlayerState.class));
        Mockito.when(map.get(PFOUR.getId()).isAlive()).thenReturn(true);

        game.setCurrentPlayer(PONE.getId());
        game.setRemainingMoves(MOVES_REQUIRED);
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.PerShip, GameFeature.Grid10x10)));
        game.setGamePhase(GamePhase.Playing);
        game.setPlayers(Arrays.asList(PONE, PFOUR));
        game.setGridSize(10);
        game.setPlayerDetails(map);
        Target target = new Target();
        target.setPlayer(PFOUR.getMd5());
        target.setCoordinate(new GridCoordinate(0, 0));
        handler.handleActionInternal(PONE, game, target);
    }

    @Test
    public void testDoesNotRotateIfMovesRemain() {
        TBGame initialGame = new TBGame();


        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        TBPlayerState state = new TBPlayerState();
        state.setShipStates(new ArrayList<>());
        map.put(PONE.getId(), state);
        TBPlayerState state1 = new TBPlayerState();
        state1.setShipStates(Collections.singletonList(new ShipState(Ship.Battleship, new HashSet<>())));
        map.put(PTWO.getId(), state1);
        TBPlayerState state2 = new TBPlayerState();
        state2.setShipStates(Collections.singletonList(new ShipState(Ship.Battleship, new HashSet<>())));
        map.put(PTHREE.getId(), state2);


        initialGame.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.Grid10x10)));
        initialGame.setRemainingMoves(MOVES_REQUIRED + 1);
        initialGame.setCurrentPlayer(PTHREE.getId());
        initialGame.setPlayerDetails(map);
        initialGame.setGamePhase(GamePhase.Playing);

        assertSame(initialGame, handler.rotateTurnBasedGame(initialGame));
        assertEquals(1, initialGame.getRemainingMoves());
        assertEquals(PTHREE.getId(), initialGame.getCurrentPlayer());
    }

    @Test
    public void testRotatesToNextAlivePlayerIfMovesZeroAndNotPerShip() {
        TBGame initialGame = new TBGame();


        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        TBPlayerState state = new TBPlayerState();
        state.setShipStates(new ArrayList<>());
        map.put(PONE.getId(), state);
        TBPlayerState state1 = new TBPlayerState();
        state1.setShipStates(Arrays.asList(new ShipState(Ship.Battleship, new HashSet<>()), new ShipState(Ship.Cruiser, new HashSet<>())));
        map.put(PTWO.getId(), state1);
        TBPlayerState state2 = new TBPlayerState();
        state2.setShipStates(Collections.singletonList(new ShipState(Ship.Battleship, new HashSet<>())));
        map.put(PTHREE.getId(), state2);


        initialGame.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.Grid10x10)));
        initialGame.setRemainingMoves(MOVES_REQUIRED);
        initialGame.setCurrentPlayer(PTHREE.getId());
        initialGame.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));
        initialGame.setPlayerDetails(map);
        initialGame.setGamePhase(GamePhase.Playing);

        assertSame(initialGame, handler.rotateTurnBasedGame(initialGame));
        assertEquals(1, initialGame.getRemainingMoves());
        assertEquals(PTWO.getId(), initialGame.getCurrentPlayer());
    }

    @Test
    public void testRotatesToNextAlivePlayerIfMovesZeroAndPerShip() {
        TBGame initialGame = new TBGame();
        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        TBPlayerState state = new TBPlayerState();
        state.setShipStates(new ArrayList<>());
        map.put(PONE.getId(), state);
        TBPlayerState state1 = new TBPlayerState();
        state1.setShipStates(Arrays.asList(new ShipState(Ship.Destroyer, 0, new ArrayList<>(), new ArrayList<>()), new ShipState(Ship.Battleship, new HashSet<>()), new ShipState(Ship.Carrier, new HashSet<>()), new ShipState(Ship.Cruiser, new HashSet<>())));
        map.put(PTWO.getId(), state1);
        TBPlayerState state2 = new TBPlayerState();
        state2.setShipStates(Collections.singletonList(new ShipState(Ship.Battleship, new HashSet<>())));
        map.put(PTHREE.getId(), state2);


        initialGame.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid10x10, GameFeature.PerShip)));
        initialGame.setRemainingMoves(MOVES_REQUIRED);
        initialGame.setCurrentPlayer(PTHREE.getId());
        initialGame.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));
        initialGame.setPlayerDetails(map);
        initialGame.setGamePhase(GamePhase.Playing);

        assertSame(initialGame, handler.rotateTurnBasedGame(initialGame));
        assertEquals(3, initialGame.getRemainingMoves());
        assertEquals(PTWO.getId(), initialGame.getCurrentPlayer());
    }

    @Test
    public void testDoesNotRotatesToNextAlivePlayerIfMovesZeroButNoOtherAlivePlayers() {
        TBGame initialGame = new TBGame();


        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        TBPlayerState state = new TBPlayerState();
        state.setShipStates(new ArrayList<>());
        map.put(PONE.getId(), state);
        TBPlayerState state1 = new TBPlayerState();
        state1.setShipStates(new ArrayList<>());
        map.put(PTWO.getId(), state1);
        TBPlayerState state2 = new TBPlayerState();
        state2.setShipStates(Collections.singletonList(new ShipState(Ship.Battleship, new HashSet<>())));
        map.put(PTHREE.getId(), state2);


        initialGame.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.Grid10x10)));
        initialGame.setRemainingMoves(MOVES_REQUIRED);
        initialGame.setCurrentPlayer(PTHREE.getId());
        initialGame.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));
        initialGame.setPlayerDetails(map);
        initialGame.setGamePhase(GamePhase.Playing);

        assertSame(initialGame, handler.rotateTurnBasedGame(initialGame));
        assertEquals(0, initialGame.getRemainingMoves());
        assertEquals(PTHREE.getId(), initialGame.getCurrentPlayer());
    }

    private class TestHandler extends AbstractPlayerMoveHandler {
        TestHandler(final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository, final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository, final GameTransitionEngine<TBGame> transitionEngine, final GamePublisher<TBGame, MongoPlayer> gamePublisher, final GameEligibilityTracker gameTracker, final GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
            super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
        }

        @Override
        public void validateMoveSpecific(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
            throw new IllegalArgumentException();
        }

        @Override
        public int movesRequired(final TBGame game) {
            return MOVES_REQUIRED;
        }

        @Override
        public boolean targetSelf() {
            return targetSelf;
        }

        @Override
        public TBGame playMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
//            if (DefaultGroovyMethods.asBoolean(game.getPlayerDetails().get(PTHREE.getId()))) {
//                game.getPlayerDetails().get(PTHREE.getId()).lastActionMessage = "3";
//            }
//
            return game;
        }

    }
}
