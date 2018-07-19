package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.scoring.TBGameScorer;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Date: 5/12/15
 * Time: 11:50 AM
 */
public class FireAtCoordinateHandlerTest extends AbstractBaseHandlerTest {
    private FireAtCoordinateHandler handler = new FireAtCoordinateHandler(null, null, null, null, null, null);

    @Test
    public void testTargetSelf() {
        assertFalse(handler.targetSelf());
    }

    @Test
    public void testAlwaysCostsOne() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.PerShip)));
        assertEquals(1, handler.movesRequired(game));
        game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.Single)));
        assertEquals(1, handler.movesRequired(game));
    }

    @Test
    public void testFireAndMissWithSharedIntel() {
        game.getFeatures().add(GameFeature.SharedIntel);

        final GridCoordinate coordinate = new GridCoordinate(7, 6);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));
        String expectedTurnPlayer = "You fired at 100000000000000000000000 (7,6) and missed.";
        String expectedTargetPlayer = "200000000000000000000000 fired at (7,6) and missed.";
        String expectedOtherPlayer = "200000000000000000000000 fired at 100000000000000000000000 (7,6) and missed.";
        GridCellState turnPlayerView = GridCellState.KnownByMiss;
        GridCellState otherPlayerView = GridCellState.KnownByOtherMiss;
        checkActionLogAndViews(coordinate, expectedTurnPlayer, expectedTargetPlayer, expectedOtherPlayer, turnPlayerView, otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            assertEquals(ss.getShip().getGridSize(), ss.getHealthRemaining());
            assertTrue(ss.getShipSegmentHit().stream().noneMatch(hit -> hit));
        });
        assertEquals(0, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
    }

    private void checkActionLogAndViews(final GridCoordinate coordinate,
                                        final String expectedTurnPlayer,
                                        final String expectedTargetPlayer,
                                        final String expectedOtherPlayer,
                                        final GridCellState turnPlayerView,
                                        final GridCellState otherPlayerView) {
        checkActionLogAndViews(coordinate, expectedTurnPlayer, TBActionLogEntry.TBActionType.Fired, expectedTargetPlayer, TBActionLogEntry.TBActionType.Fired, expectedOtherPlayer, TBActionLogEntry.TBActionType.Fired, turnPlayerView, otherPlayerView);
    }

    private void checkActionLogAndViews(
            final GridCoordinate coordinate,
            final String expectedTurnPlayer,
            final TBActionLogEntry.TBActionType turnPlayerAction,
            final String expectedTargetPlayer,
            final TBActionLogEntry.TBActionType targetPlayerAction,
            final String expectedOtherPlayer,
            final TBActionLogEntry.TBActionType otherPlayerAction,
            final GridCellState turnPlayerView,
            final GridCellState otherPlayerView) {
        assertEquals(expectedTurnPlayer, getLastEntry(game, PTWO).getDescription());
        assertEquals(turnPlayerAction, getLastEntry(game, PTWO).getActionType());
        assertEquals(expectedTargetPlayer, getLastEntry(game, PONE).getDescription());
        assertEquals(targetPlayerAction, getLastEntry(game, PONE).getActionType());
        assertEquals(expectedOtherPlayer, getLastEntry(game, PTHREE).getDescription());
        assertEquals(otherPlayerAction, getLastEntry(game, PTHREE).getActionType());
        assertEquals(expectedOtherPlayer, getLastEntry(game, PFOUR).getDescription());
        assertEquals(otherPlayerAction, getLastEntry(game, PFOUR).getActionType());

        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().forEach((id, grid) -> {
            if (id.equals(PTWO.getId())) {
                assertEquals(turnPlayerView, grid.get(coordinate));
            } else {
                if (turnPlayerView.equals(GridCellState.KnownByRehit)) {
                    GridCellState gridCellState = grid.get(coordinate);
                    assertTrue(gridCellState == otherPlayerView || gridCellState == GridCellState.KnownByHit);
                } else {
                    assertEquals(otherPlayerView, grid.get(coordinate));
                }
            }
        });
        game.getPlayerDetails().entrySet().stream().filter(e -> !PONE.getId().equals(e.getKey())).forEach(e -> {
            if (e.getKey().equals(PTWO.getId())) {
                assertEquals(turnPlayerView, e.getValue().getOpponentGrids().get(PONE.getId()).get(coordinate));
            } else {
                if (turnPlayerView.equals(GridCellState.KnownByRehit)) {
                    GridCellState gridCellState = e.getValue().getOpponentGrids().get(PONE.getId()).get(coordinate);
                    assertTrue(gridCellState == otherPlayerView || gridCellState == GridCellState.KnownByHit);
                } else {
                    assertEquals(otherPlayerView, e.getValue().getOpponentGrids().get(PONE.getId()).get(coordinate));
                }
            }
        });
    }

    @Test
    public void testFireAndMissWithIsolatedIntel() {
        game.getFeatures().add(GameFeature.IsolatedIntel);

        final GridCoordinate coordinate = new GridCoordinate(7, 6);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "You fired at 100000000000000000000000 (7,6) and missed.";
        String expectedTargetPlayer = "200000000000000000000000 fired at (7,6) and missed.";
        String expectedOtherPlayer = "200000000000000000000000 fired at 100000000000000000000000.";
        GridCellState turnPlayerView = GridCellState.KnownByMiss;
        GridCellState otherPlayerView = GridCellState.Unknown;
        checkActionLogAndViews(coordinate, expectedTurnPlayer, expectedTargetPlayer, expectedOtherPlayer, turnPlayerView, otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            assertEquals(ss.getShip().getGridSize(), ss.getHealthRemaining());
            assertTrue(ss.getShipSegmentHit().stream().noneMatch(hit -> hit));
        });
        assertEquals(0, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
    }

    @Test
    public void testFireAndHitWithSharedIntel() {
        game.getFeatures().add(GameFeature.SharedIntel);

        final GridCoordinate coordinate = new GridCoordinate(7, 7);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "You fired at 100000000000000000000000 (7,7) and hit!";
        String expectedTargetPlayer = "200000000000000000000000 fired at (7,7) and hit your Destroyer!";
        String expectedOtherPlayer = "200000000000000000000000 fired at 100000000000000000000000 (7,7) and hit!";
        GridCellState turnPlayerView = GridCellState.KnownByHit;
        GridCellState otherPlayerView = GridCellState.KnownByOtherHit;
        checkActionLogAndViews(coordinate, expectedTurnPlayer, expectedTargetPlayer, expectedOtherPlayer, turnPlayerView, otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            if (Ship.Destroyer.equals(ss.getShip())) {
                assertEquals(1, ss.getHealthRemaining());
                assertEquals(Arrays.asList(true, false), ss.getShipSegmentHit());

            } else {
                assertEquals(ss.getShip().getGridSize(), ss.getHealthRemaining());
                assertTrue(ss.getShipSegmentHit().stream().noneMatch(hit -> hit));
            }
        });
        assertEquals(TBGameScorer.SCORE_FOR_HIT, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
    }

    @Test
    public void testFireAndHitWithIsolatedIntel() {
        game.getFeatures().add(GameFeature.IsolatedIntel);

        final GridCoordinate coordinate = new GridCoordinate(7, 7);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "You fired at 100000000000000000000000 (7,7) and hit!";
        String expectedTargetPlayer = "200000000000000000000000 fired at (7,7) and hit your Destroyer!";
        String expectedOtherPlayer = "200000000000000000000000 fired at 100000000000000000000000.";
        GridCellState turnPlayerView = GridCellState.KnownByHit;
        GridCellState otherPlayerView = GridCellState.Unknown;
        checkActionLogAndViews(coordinate, expectedTurnPlayer, expectedTargetPlayer, expectedOtherPlayer, turnPlayerView, otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            if (Ship.Destroyer.equals(ss.getShip())) {
                assertEquals(1, ss.getHealthRemaining());
                assertEquals(Arrays.asList(true, false), ss.getShipSegmentHit());

            } else {
                assertEquals(ss.getShip().getGridSize(), ss.getHealthRemaining());
                assertTrue(ss.getShipSegmentHit().stream().noneMatch(hit -> hit));
            }
        });
        assertEquals(TBGameScorer.SCORE_FOR_HIT, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
    }

    @Test
    public void testFireAndReHitWithSharedIntel() {
        game.getFeatures().add(GameFeature.SharedIntel);

        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Destroyer.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(1);
                    ss.getShipSegmentHit().set(0, true);
                });
        final GridCoordinate coordinate = new GridCoordinate(7, 7);
        game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).set(coordinate, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).set(coordinate, GridCellState.KnownByHit);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "You fired at 100000000000000000000000 (7,7) and hit an already damaged area!";
        String expectedTargetPlayer = "200000000000000000000000 fired at (7,7) and re-hit your Destroyer!";
        String expectedOtherPlayer = "200000000000000000000000 fired at 100000000000000000000000 (7,7) and re-hit a damaged area!";
        GridCellState turnPlayerView = GridCellState.KnownByRehit;
        GridCellState otherPlayerView = GridCellState.KnownByOtherHit;
        checkActionLogAndViews(coordinate, expectedTurnPlayer, expectedTargetPlayer, expectedOtherPlayer, turnPlayerView, otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            if (Ship.Destroyer.equals(ss.getShip())) {
                assertEquals(1, ss.getHealthRemaining());
                assertEquals(Arrays.asList(true, false), ss.getShipSegmentHit());

            } else {
                assertEquals(ss.getShip().getGridSize(), ss.getHealthRemaining());
                assertTrue(ss.getShipSegmentHit().stream().noneMatch(hit -> hit));
            }
        });

        assertEquals(0, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
    }

    @Test
    public void testFireAndReHitWithIsolatedIntel() {
        game.getFeatures().add(GameFeature.IsolatedIntel);

        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Destroyer.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(1);
                    ss.getShipSegmentHit().set(0, true);
                });
        final GridCoordinate coordinate = new GridCoordinate(7, 7);
        game.getPlayerDetails().get(PFOUR.getId()).getOpponentGrids().get(PONE.getId()).set(coordinate, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PFOUR.getId()).set(coordinate, GridCellState.KnownByHit);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "You fired at 100000000000000000000000 (7,7) and hit an already damaged area!";
        String expectedTargetPlayer = "200000000000000000000000 fired at (7,7) and re-hit your Destroyer!";
        String expectedOtherPlayer = "200000000000000000000000 fired at 100000000000000000000000.";
        GridCellState turnPlayerView = GridCellState.KnownByRehit;
        GridCellState otherPlayerView = GridCellState.Unknown;
        checkActionLogAndViews(coordinate, expectedTurnPlayer, expectedTargetPlayer, expectedOtherPlayer, turnPlayerView, otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            if (Ship.Destroyer.equals(ss.getShip())) {
                assertEquals(1, ss.getHealthRemaining());
                assertEquals(Arrays.asList(true, false), ss.getShipSegmentHit());

            } else {
                assertEquals(ss.getShip().getGridSize(), ss.getHealthRemaining());
                assertTrue(ss.getShipSegmentHit().stream().noneMatch(hit -> hit));
            }
        });
        assertEquals(0, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
    }

    @Test
    public void testFireAndSinkShipWithSharedIntel() {
        game.getFeatures().add(GameFeature.SharedIntel);

        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Destroyer.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(1);
                    ss.getShipSegmentHit().set(1, true);
                });
        final GridCoordinate coordinate = new GridCoordinate(7, 7);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "You sunk a Destroyer for 100000000000000000000000!";
        String expectedTargetPlayer = "200000000000000000000000 sunk your Destroyer!";
        String expectedOtherPlayer = "200000000000000000000000 sunk a Destroyer for 100000000000000000000000!";
        GridCellState turnPlayerView = GridCellState.KnownByHit;
        GridCellState otherPlayerView = GridCellState.KnownByOtherHit;
        checkActionLogAndViews(
                coordinate,
                expectedTurnPlayer,
                TBActionLogEntry.TBActionType.Sunk,
                expectedTargetPlayer,
                TBActionLogEntry.TBActionType.Sunk,
                expectedOtherPlayer,
                TBActionLogEntry.TBActionType.Sunk,
                turnPlayerView,
                otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            if (Ship.Destroyer.equals(ss.getShip())) {
                assertEquals(0, ss.getHealthRemaining());
                assertEquals(Arrays.asList(true, true), ss.getShipSegmentHit());

            } else {
                assertEquals(ss.getShip().getGridSize(), ss.getHealthRemaining());
                assertTrue(ss.getShipSegmentHit().stream().noneMatch(hit -> hit));
            }
        });

//        assertEquals("You fired at 100000000000000000000000 (7,7) and hit!", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 fired at (7,7) and hit your Destroyer!", game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 fired at 100000000000000000000000 (7,7) and hit!", game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 fired at 100000000000000000000000 (7,7) and hit!", game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-2).getActionType());
        assertEquals(TBGameScorer.SCORE_FOR_HIT, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
        assertEquals(TBGameScorer.SCORE_FOR_SINK, game.getPlayerDetails().get(PTWO.getId()).getScoreFromSinks());
    }

    @Test
    public void testFireAndSinkShipWithIsolatedIntel() {
        game.getFeatures().add(GameFeature.IsolatedIntel);

        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Destroyer.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(1);
                    ss.getShipSegmentHit().set(1, true);
                });
        final GridCoordinate coordinate = new GridCoordinate(7, 7);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "You sunk a Destroyer for 100000000000000000000000!";
        String expectedTargetPlayer = "200000000000000000000000 sunk your Destroyer!";
        String expectedOtherPlayer = "200000000000000000000000 fired at 100000000000000000000000.";
        GridCellState turnPlayerView = GridCellState.KnownByHit;
        GridCellState otherPlayerView = GridCellState.Unknown;
        checkActionLogAndViews(
                coordinate,
                expectedTurnPlayer,
                TBActionLogEntry.TBActionType.Sunk,
                expectedTargetPlayer,
                TBActionLogEntry.TBActionType.Sunk,
                expectedOtherPlayer,
                TBActionLogEntry.TBActionType.Fired,
                turnPlayerView,
                otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            if (Ship.Destroyer.equals(ss.getShip())) {
                assertEquals(0, ss.getHealthRemaining());
                assertEquals(Arrays.asList(true, true), ss.getShipSegmentHit());

            } else {
                assertEquals(ss.getShip().getGridSize(), ss.getHealthRemaining());
                assertTrue(ss.getShipSegmentHit().stream().noneMatch(hit -> hit));
            }
        });

//        assertEquals("You fired at 100000000000000000000000 (7,7) and hit!", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 fired at (7,7) and hit your Destroyer!", game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 fired at 100000000000000000000000.", game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-1).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-1).getActionType());
//        assertEquals("200000000000000000000000 fired at 100000000000000000000000.", game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-1).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-1).getActionType());
//        assertEquals("You sunk a Destroyer for 100000000000000000000000!", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-1).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Sunk, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-1).getActionType());
//        assertEquals("200000000000000000000000 sunk your Destroyer!", game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-1).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Sunk, game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-1).getActionType());
        assertEquals(TBGameScorer.SCORE_FOR_HIT, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
        assertEquals(TBGameScorer.SCORE_FOR_SINK, game.getPlayerDetails().get(PTWO.getId()).getScoreFromSinks());
    }

    @Test
    public void testFireAndSinkShipAndEndPlayerWithSharedIntel() {
        game.getFeatures().add(GameFeature.SharedIntel);

        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Destroyer.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(1);
                    ss.getShipSegmentHit().set(1, true);
                });
        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> !Ship.Destroyer.equals(ss.getShip()))
                .forEach(ss -> ss.setHealthRemaining(0));
        final GridCoordinate coordinate = new GridCoordinate(7, 7);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "100000000000000000000000 has been defeated!";
        String expectedTargetPlayer = "100000000000000000000000 has been defeated!";
        String expectedOtherPlayer = "100000000000000000000000 has been defeated!";
        GridCellState turnPlayerView = GridCellState.KnownByHit;
        GridCellState otherPlayerView = GridCellState.KnownByOtherHit;
        checkActionLogAndViews(
                coordinate,
                expectedTurnPlayer,
                TBActionLogEntry.TBActionType.Defeated,
                expectedTargetPlayer,
                TBActionLogEntry.TBActionType.Defeated,
                expectedOtherPlayer,
                TBActionLogEntry.TBActionType.Defeated,
                turnPlayerView,
                otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            if (Ship.Destroyer.equals(ss.getShip())) {
                assertEquals(0, ss.getHealthRemaining());
                assertEquals(Arrays.asList(true, true), ss.getShipSegmentHit());
            }
        });

//        assertEquals("You fired at 100000000000000000000000 (7,7) and hit!", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-3).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-3).getActionType());
//        assertEquals("200000000000000000000000 fired at (7,7) and hit your Destroyer!", game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-3).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-3).getActionType());
//        assertEquals("200000000000000000000000 fired at 100000000000000000000000 (7,7) and hit!", game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-3).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-3).getActionType());
//        assertEquals("200000000000000000000000 fired at 100000000000000000000000 (7,7) and hit!", game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-3).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-3).getActionType());
//        assertEquals("You sunk a Destroyer for 100000000000000000000000!", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Sunk, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 sunk your Destroyer!", game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Sunk, game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 sunk a Destroyer for 100000000000000000000000!", game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Sunk, game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 sunk a Destroyer for 100000000000000000000000!", game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Sunk, game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-2).getActionType());
        assertEquals(TBGameScorer.SCORE_FOR_HIT, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
        assertEquals(TBGameScorer.SCORE_FOR_SINK, game.getPlayerDetails().get(PTWO.getId()).getScoreFromSinks());
    }

    @Test
    public void testFireAndSinkShipAndEndPlayerWithIsolatedIntel() {
        game.getFeatures().add(GameFeature.IsolatedIntel);

        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> Ship.Destroyer.equals(ss.getShip()))
                .forEach(ss -> {
                    ss.setHealthRemaining(1);
                    ss.getShipSegmentHit().set(1, true);
                });
        game.getPlayerDetails().get(PONE.getId()).getShipStates()
                .stream()
                .filter(ss -> !Ship.Destroyer.equals(ss.getShip()))
                .forEach(ss -> ss.setHealthRemaining(0));

        final GridCoordinate coordinate = new GridCoordinate(7, 7);
        assertEquals(game, handler.playMove(PTWO, game, PONE, coordinate));

        String expectedTurnPlayer = "100000000000000000000000 has been defeated!";
        String expectedTargetPlayer = "100000000000000000000000 has been defeated!";
        String expectedOtherPlayer = "100000000000000000000000 has been defeated!";
        GridCellState turnPlayerView = GridCellState.KnownByHit;
        GridCellState otherPlayerView = GridCellState.Unknown;
        checkActionLogAndViews(
                coordinate,
                expectedTurnPlayer,
                TBActionLogEntry.TBActionType.Defeated,
                expectedTargetPlayer,
                TBActionLogEntry.TBActionType.Defeated,
                expectedOtherPlayer,
                TBActionLogEntry.TBActionType.Defeated,
                turnPlayerView,
                otherPlayerView);
        game.getPlayerDetails().get(PONE.getId()).getShipStates().forEach(ss -> {
            if (Ship.Destroyer.equals(ss.getShip())) {
                assertEquals(0, ss.getHealthRemaining());
                assertEquals(Arrays.asList(true, true), ss.getShipSegmentHit());
            }
        });

//        assertEquals("You fired at 100000000000000000000000 (7,7) and hit!", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-3).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-3).getActionType());
//        assertEquals("200000000000000000000000 fired at (7,7) and hit your Destroyer!", game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-3).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-3).getActionType());
//        assertEquals("200000000000000000000000 fired at 100000000000000000000000.", game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PTHREE.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 fired at 100000000000000000000000.", game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Fired, game.getPlayerDetails().get(PFOUR.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("You sunk a Destroyer for 100000000000000000000000!", game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Sunk, game.getPlayerDetails().get(PTWO.getId()).getActionLog().get(-2).getActionType());
//        assertEquals("200000000000000000000000 sunk your Destroyer!", game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-2).getDescription());
//        assertEquals(TBActionLogEntry.TBActionType.Sunk, game.getPlayerDetails().get(PONE.getId()).getActionLog().get(-2).getActionType());
        assertEquals(TBGameScorer.SCORE_FOR_HIT, game.getPlayerDetails().get(PTWO.getId()).getScoreFromHits());
        assertEquals(TBGameScorer.SCORE_FOR_SINK, game.getPlayerDetails().get(PTWO.getId()).getScoreFromSinks());
    }
}
