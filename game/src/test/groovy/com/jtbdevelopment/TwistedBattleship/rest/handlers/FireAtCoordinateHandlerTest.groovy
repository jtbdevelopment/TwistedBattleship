package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.scoring.TBGameScorer
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNull

/**
 * Date: 5/12/15
 * Time: 11:50 AM
 */
class FireAtCoordinateHandlerTest extends AbstractBaseHandlerTest {
    FireAtCoordinateHandler handler = new FireAtCoordinateHandler(null, null, null, null, null, null)

    @Test
    void testTargetSelf() {
        assertFalse handler.targetSelf()
    }

    @Test
    void testAlwaysCostsOne() {
        assert 1 == handler.movesRequired(new TBGame(features: [GameFeature.PerShip]))
        assert 1 == handler.movesRequired(new TBGame(features: [GameFeature.Single]))
    }

    @Test
    void testFireAndMissWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        def coordinate = new GridCoordinate(7, 6)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,6) and missed." == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 fired at (7,6) and missed." == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 fired at 1 (7,6) and missed." == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired at 1 (7,6) and missed." == game.playerDetails[PFOUR.id].actionLog[-1].description
        game.playerDetails[PONE.id].shipStates.each {
            assert it.ship.gridSize == it.healthRemaining
            assertNull it.shipSegmentHit.find { it }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            if (it.key == PTWO.id) {
                assert GridCellState.KnownByMiss == it.value.get(coordinate)
            } else {
                assert GridCellState.KnownByOtherMiss == it.value.get(coordinate)
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            if (it.key == PTWO.id) {
                assert GridCellState.KnownByMiss == it.value.opponentGrids[PONE.id].get(coordinate)
            } else {
                assert GridCellState.KnownByOtherMiss == it.value.opponentGrids[PONE.id].get(coordinate)
            }
        }
        assert 0 == game.playerDetails[PTWO.id].scoreFromHits
    }

    @Test
    void testFireAndMissWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        def coordinate = new GridCoordinate(7, 6)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,6) and missed." == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 fired at (7,6) and missed." == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 fired at 1." == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired at 1." == game.playerDetails[PFOUR.id].actionLog[-1].description
        game.playerDetails[PONE.id].shipStates.each {
            assert it.ship.gridSize == it.healthRemaining
            assertNull it.shipSegmentHit.find { it }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            if (it.key == PTWO.id) {
                assert GridCellState.KnownByMiss == it.value.get(coordinate)
            } else {
                assert GridCellState.Unknown == it.value.get(coordinate)
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            if (it.key == PTWO.id) {
                assert GridCellState.KnownByMiss == it.value.opponentGrids[PONE.id].get(coordinate)
            } else {
                assert GridCellState.Unknown == it.value.opponentGrids[PONE.id].get(coordinate)
            }
        }
        assert 0 == game.playerDetails[PTWO.id].scoreFromHits
    }

    @Test
    void testFireAndHitWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,7) and hit!" == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 fired at (7,7) and hit your Destroyer!" == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 fired at 1 (7,7) and hit!" == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired at 1 (7,7) and hit!" == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        game.playerDetails[PONE.id].shipStates.each {
            if (it.ship == Ship.Destroyer) {
                assert 1 == it.healthRemaining
                assert [true, false] == it.shipSegmentHit
            } else {
                assert it.ship.gridSize == it.healthRemaining
                assertNull it.shipSegmentHit.find { it }
            }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            if (it.key == PTWO.id) {
                assert GridCellState.KnownByHit == it.value.get(coordinate)
            } else {
                assert GridCellState.KnownByOtherHit == it.value.get(coordinate)
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            if (it.key == PTWO.id) {
                assert GridCellState.KnownByHit == it.value.opponentGrids[PONE.id].get(coordinate)
            } else {
                assert GridCellState.KnownByOtherHit == it.value.opponentGrids[PONE.id].get(coordinate)
            }
        }
        assert TBGameScorer.SCORE_FOR_HIT == game.playerDetails[PTWO.id].scoreFromHits
    }

    @Test
    void testFireAndHitWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,7) and hit!" == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 fired at (7,7) and hit your Destroyer!" == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 fired at 1." == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired at 1." == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        game.playerDetails[PONE.id].shipStates.each {
            if (it.ship == Ship.Destroyer) {
                assert 1 == it.healthRemaining
                assert [true, false] == it.shipSegmentHit
            } else {
                assert it.ship.gridSize == it.healthRemaining
                assertNull it.shipSegmentHit.find { it }
            }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            if (it.key == PTWO.id) {
                assert GridCellState.KnownByHit == it.value.get(coordinate)
            } else {
                assert GridCellState.Unknown == it.value.get(coordinate)
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            if (it.key == PTWO.id) {
                assert GridCellState.KnownByHit == it.value.opponentGrids[PONE.id].get(coordinate)
            } else {
                assert GridCellState.Unknown == it.value.opponentGrids[PONE.id].get(coordinate)
            }
        }
        assert TBGameScorer.SCORE_FOR_HIT == game.playerDetails[PTWO.id].scoreFromHits
    }

    @Test
    void testFireAndReHitWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.shipSegmentHit[0] = true
        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        game.playerDetails[PFOUR.id].opponentGrids[PONE.id].set(coordinate, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PFOUR.id].set(coordinate, GridCellState.KnownByHit)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,7) and hit an already damaged area!" == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 fired at (7,7) and re-hit your Destroyer!" == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 fired at 1 (7,7) and re-hit a damaged area!" == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired at 1 (7,7) and re-hit a damaged area!" == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        game.playerDetails[PONE.id].shipStates.each {
            if (it.ship == Ship.Destroyer) {
                assert 1 == it.healthRemaining
                assert [true, false] == it.shipSegmentHit
            } else {
                assert it.ship.gridSize == it.healthRemaining
                assertNull it.shipSegmentHit.find { it }
            }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByRehit == it.value.get(coordinate)
                    break
                case PFOUR.id:
                    assert GridCellState.KnownByHit == it.value.get(coordinate)
                    break
                default:
                    assert GridCellState.KnownByOtherHit == it.value.get(coordinate)
                    break
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByRehit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
                case PFOUR.id:
                    assert GridCellState.KnownByHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
                default:
                    assert GridCellState.KnownByOtherHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
            }
        }
        assert 0 == game.playerDetails[PTWO.id].scoreFromHits
    }

    @Test
    void testFireAndReHitWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.shipSegmentHit[0] = true
        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        game.playerDetails[PFOUR.id].opponentGrids[PONE.id].set(coordinate, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PFOUR.id].set(coordinate, GridCellState.KnownByHit)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,7) and hit an already damaged area!" == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 fired at (7,7) and re-hit your Destroyer!" == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 fired at 1." == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired at 1." == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        game.playerDetails[PONE.id].shipStates.each {
            if (it.ship == Ship.Destroyer) {
                assert 1 == it.healthRemaining
                assert [true, false] == it.shipSegmentHit
            } else {
                assert it.ship.gridSize == it.healthRemaining
                assertNull it.shipSegmentHit.find { it }
            }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByRehit == it.value.get(coordinate)
                    break
                case PFOUR.id:
                    assert GridCellState.KnownByHit == it.value.get(coordinate)
                    break
                default:
                    assert GridCellState.Unknown == it.value.get(coordinate)
                    break
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByRehit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
                case PFOUR.id:
                    assert GridCellState.KnownByHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
                default:
                    assert GridCellState.Unknown == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
            }
        }
        assert 0 == game.playerDetails[PTWO.id].scoreFromHits
    }

    @Test
    void testFireAndSinkShipWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.shipSegmentHit[1] = true
        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,7) and hit!" == game.playerDetails[PTWO.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-2].actionType
        assert "2 fired at (7,7) and hit your Destroyer!" == game.playerDetails[PONE.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-2].actionType
        assert "2 fired at 1 (7,7) and hit!" == game.playerDetails[PTHREE.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-2].actionType
        assert "2 fired at 1 (7,7) and hit!" == game.playerDetails[PFOUR.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PFOUR.id].actionLog[-2].actionType
        assert "You sunk a Destroyer for 1!" == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 sunk your Destroyer!" == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "2 sunk a Destroyer for 1!" == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 sunk a Destroyer for 1!" == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        game.playerDetails[PONE.id].shipStates.each {
            if (it.ship == Ship.Destroyer) {
                assert 0 == it.healthRemaining
                assert [true, true] == it.shipSegmentHit
            } else {
                assert it.ship.gridSize == it.healthRemaining
                assertNull it.shipSegmentHit.find { it }
            }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByHit == it.value.get(coordinate)
                    break
                default:
                    assert GridCellState.KnownByOtherHit == it.value.get(coordinate)
                    break
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
                default:
                    assert GridCellState.KnownByOtherHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
            }
        }
        assert TBGameScorer.SCORE_FOR_HIT == game.playerDetails[PTWO.id].scoreFromHits
        assert TBGameScorer.SCORE_FOR_SINK == game.playerDetails[PTWO.id].scoreFromSinks
    }

    @Test
    void testFireAndSinkShipWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.shipSegmentHit[1] = true
        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,7) and hit!" == game.playerDetails[PTWO.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-2].actionType
        assert "2 fired at (7,7) and hit your Destroyer!" == game.playerDetails[PONE.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-2].actionType
        assert "2 fired at 1." == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "2 fired at 1." == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        assert "You sunk a Destroyer for 1!" == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "2 sunk your Destroyer!" == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PONE.id].actionLog[-1].actionType
        game.playerDetails[PONE.id].shipStates.each {
            if (it.ship == Ship.Destroyer) {
                assert 0 == it.healthRemaining
                assert [true, true] == it.shipSegmentHit
            } else {
                assert it.ship.gridSize == it.healthRemaining
                assertNull it.shipSegmentHit.find { it }
            }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByHit == it.value.get(coordinate)
                    break
                default:
                    assert GridCellState.Unknown == it.value.get(coordinate)
                    break
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
                default:
                    assert GridCellState.Unknown == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
            }
        }
        assert TBGameScorer.SCORE_FOR_HIT == game.playerDetails[PTWO.id].scoreFromHits
        assert TBGameScorer.SCORE_FOR_SINK == game.playerDetails[PTWO.id].scoreFromSinks
    }

    @Test
    void testFireAndSinkShipAndEndPlayerWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        Ship.values().each {
            Ship ship ->
                if (ship != Ship.Destroyer) {
                    def state = game.playerDetails[PONE.id].shipStates.find { it.ship == ship }
                    state.healthRemaining = 0
                    (1..ship.gridSize).each {
                        int index ->
                            state.shipSegmentHit[index - 1] = true
                    }
                }
        }
        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.shipSegmentHit[1] = true
        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,7) and hit!" == game.playerDetails[PTWO.id].actionLog[-3].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-3].actionType
        assert "2 fired at (7,7) and hit your Destroyer!" == game.playerDetails[PONE.id].actionLog[-3].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-3].actionType
        assert "2 fired at 1 (7,7) and hit!" == game.playerDetails[PTHREE.id].actionLog[-3].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-3].actionType
        assert "2 fired at 1 (7,7) and hit!" == game.playerDetails[PFOUR.id].actionLog[-3].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PFOUR.id].actionLog[-3].actionType
        assert "You sunk a Destroyer for 1!" == game.playerDetails[PTWO.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PTWO.id].actionLog[-2].actionType
        assert "2 sunk your Destroyer!" == game.playerDetails[PONE.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PONE.id].actionLog[-2].actionType
        assert "2 sunk a Destroyer for 1!" == game.playerDetails[PTHREE.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PTHREE.id].actionLog[-2].actionType
        assert "2 sunk a Destroyer for 1!" == game.playerDetails[PFOUR.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PFOUR.id].actionLog[-2].actionType
        assert "1 has been defeated!" == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Defeated == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "1 has been defeated!" == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Defeated == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "1 has been defeated!" == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Defeated == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "1 has been defeated!" == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Defeated == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        game.playerDetails[PONE.id].shipStates.each {
            if (it.ship == Ship.Destroyer) {
                assert 0 == it.healthRemaining
                assert [true, true] == it.shipSegmentHit
            } else {
                assert 0 == it.healthRemaining
                assertNull it.shipSegmentHit.find { !it }
            }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByHit == it.value.get(coordinate)
                    break
                default:
                    assert GridCellState.KnownByOtherHit == it.value.get(coordinate)
                    break
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
                default:
                    assert GridCellState.KnownByOtherHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
            }
        }
        assert TBGameScorer.SCORE_FOR_HIT == game.playerDetails[PTWO.id].scoreFromHits
        assert TBGameScorer.SCORE_FOR_SINK == game.playerDetails[PTWO.id].scoreFromSinks
    }

    @Test
    void testFireAndSinkShipAndEndPlayerWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        Ship.values().each {
            Ship ship ->
                if (ship != Ship.Destroyer) {
                    def state = game.playerDetails[PONE.id].shipStates.find { it.ship == ship }
                    state.healthRemaining = 0
                    (1..ship.gridSize).each {
                        int index ->
                            state.shipSegmentHit[index - 1] = true
                    }
                }
        }
        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.shipSegmentHit[1] = true
        game.playerDetails[PONE.id].shipStates.find { it.ship == Ship.Destroyer }.healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert "You fired at 1 (7,7) and hit!" == game.playerDetails[PTWO.id].actionLog[-3].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[-3].actionType
        assert "2 fired at (7,7) and hit your Destroyer!" == game.playerDetails[PONE.id].actionLog[-3].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[-3].actionType
        assert "2 fired at 1." == game.playerDetails[PTHREE.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTHREE.id].actionLog[-2].actionType
        assert "2 fired at 1." == game.playerDetails[PFOUR.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PFOUR.id].actionLog[-2].actionType
        assert "You sunk a Destroyer for 1!" == game.playerDetails[PTWO.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PTWO.id].actionLog[-2].actionType
        assert "2 sunk your Destroyer!" == game.playerDetails[PONE.id].actionLog[-2].description
        assert TBActionLogEntry.TBActionType.Sunk == game.playerDetails[PONE.id].actionLog[-2].actionType
        assert "1 has been defeated!" == game.playerDetails[PTWO.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Defeated == game.playerDetails[PTWO.id].actionLog[-1].actionType
        assert "1 has been defeated!" == game.playerDetails[PONE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Defeated == game.playerDetails[PONE.id].actionLog[-1].actionType
        assert "1 has been defeated!" == game.playerDetails[PTHREE.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Defeated == game.playerDetails[PTHREE.id].actionLog[-1].actionType
        assert "1 has been defeated!" == game.playerDetails[PFOUR.id].actionLog[-1].description
        assert TBActionLogEntry.TBActionType.Defeated == game.playerDetails[PFOUR.id].actionLog[-1].actionType
        game.playerDetails[PONE.id].shipStates.each {
            if (it.ship == Ship.Destroyer) {
                assert 0 == it.healthRemaining
                assert [true, true] == it.shipSegmentHit
            } else {
                assert 0 == it.healthRemaining
                assertNull it.shipSegmentHit.find { !it }
            }
        }
        game.playerDetails[PONE.id].opponentViews.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByHit == it.value.get(coordinate)
                    break
                default:
                    assert GridCellState.Unknown == it.value.get(coordinate)
                    break
            }
        }
        game.playerDetails.findAll { it.key != PONE.id }.each {
            switch (it.key) {
                case PTWO.id:
                    assert GridCellState.KnownByHit == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
                default:
                    assert GridCellState.Unknown == it.value.opponentGrids[PONE.id].get(coordinate)
                    break
            }
        }
        assert TBGameScorer.SCORE_FOR_HIT == game.playerDetails[PTWO.id].scoreFromHits
        assert TBGameScorer.SCORE_FOR_SINK == game.playerDetails[PTWO.id].scoreFromSinks
    }
}
