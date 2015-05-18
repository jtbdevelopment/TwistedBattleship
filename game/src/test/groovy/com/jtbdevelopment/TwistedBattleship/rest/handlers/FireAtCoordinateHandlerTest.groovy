package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.factory.gameinitializers.PlayerGameStateInitializer
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import com.jtbdevelopment.TwistedBattleship.state.scoring.TBGameScorer
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipPlacementValidator
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase

/**
 * Date: 5/12/15
 * Time: 11:50 AM
 */
class FireAtCoordinateHandlerTest extends MongoGameCoreTestCase {
    FireAtCoordinateHandler handler = new FireAtCoordinateHandler()
    TBGame game

    @Override
    protected void setUp() throws Exception {
        game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.ActionsPerTurn, GameFeature.CriticalDisabled, GameFeature.ECMEnabled, GameFeature.EMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled],
                players: [PONE, PTWO, PTHREE, PFOUR])
        new PlayerGameStateInitializer(util: new GridSizeUtil()).initializeGame(game)
        game.gamePhase = GamePhase.Setup
        new SetupShipsHandler(shipPlacementValidator: new ShipPlacementValidator(gridSizeUtil: new GridSizeUtil())).handleActionInternal(
                PONE,
                game,
                [
                        (Ship.Carrier)   : new ShipState(Ship.Carrier, [
                                new GridCoordinate(0, 0),
                                new GridCoordinate(1, 0),
                                new GridCoordinate(2, 0),
                                new GridCoordinate(3, 0),
                                new GridCoordinate(4, 0),
                        ] as SortedSet),
                        (Ship.Battleship): new ShipState(Ship.Battleship, [
                                new GridCoordinate(0, 14),
                                new GridCoordinate(1, 14),
                                new GridCoordinate(2, 14),
                                new GridCoordinate(3, 14),
                        ] as SortedSet),
                        (Ship.Cruiser)   : new ShipState(Ship.Cruiser, [
                                new GridCoordinate(14, 14),
                                new GridCoordinate(13, 14),
                                new GridCoordinate(12, 14),
                        ] as SortedSet),
                        (Ship.Submarine) : new ShipState(Ship.Submarine, [
                                new GridCoordinate(14, 0),
                                new GridCoordinate(13, 0),
                                new GridCoordinate(12, 0),
                        ] as SortedSet),
                        (Ship.Destroyer) : new ShipState(Ship.Destroyer, [
                                new GridCoordinate(7, 7),
                                new GridCoordinate(7, 8),
                        ] as SortedSet),
                ]
        )
    }

    void testTargetSelf() {
        assertFalse handler.targetSelf()
    }

    void testAlwaysCostsOne() {
        assert 1 == handler.movesRequired(new TBGame(features: [GameFeature.PerShip]))
        assert 1 == handler.movesRequired(new TBGame(features: [GameFeature.Single]))
    }

    void testFireAndMissWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        def coordinate = new GridCoordinate(7, 6)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "No enemy at (7,6)."
        assert game.playerDetails[PONE.id].lastActionMessage == "2 missed at (7,6)."
        game.playerDetails[PONE.id].shipStates.each {
            assert it.key.gridSize == it.value.healthRemaining
            assertNull it.value.shipSegmentHit.find { it }
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
        assert "" == game.generalMessage
    }

    void testFireAndMissWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        def coordinate = new GridCoordinate(7, 6)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "No enemy at (7,6)."
        assert game.playerDetails[PONE.id].lastActionMessage == "2 missed at (7,6)."
        game.playerDetails[PONE.id].shipStates.each {
            assert it.key.gridSize == it.value.healthRemaining
            assertNull it.value.shipSegmentHit.find { it }
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
        assert "" == game.generalMessage
    }

    void testFireAndHitWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "Direct hit at (7,7)!"
        assert game.playerDetails[PONE.id].lastActionMessage == "2 hit your Destroyer at (7,7)!"
        game.playerDetails[PONE.id].shipStates.each {
            if (it.key == Ship.Destroyer) {
                assert 1 == it.value.healthRemaining
                assert [true, false] == it.value.shipSegmentHit
            } else {
                assert it.key.gridSize == it.value.healthRemaining
                assertNull it.value.shipSegmentHit.find { it }
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
        assert "" == game.generalMessage
    }

    void testFireAndHitWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "Direct hit at (7,7)!"
        assert game.playerDetails[PONE.id].lastActionMessage == "2 hit your Destroyer at (7,7)!"
        game.playerDetails[PONE.id].shipStates.each {
            if (it.key == Ship.Destroyer) {
                assert 1 == it.value.healthRemaining
                assert [true, false] == it.value.shipSegmentHit
            } else {
                assert it.key.gridSize == it.value.healthRemaining
                assertNull it.value.shipSegmentHit.find { it }
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
        assert "" == game.generalMessage
    }

    void testFireAndReHitWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].shipSegmentHit[0] = true
        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        game.playerDetails[PFOUR.id].opponentGrids[PONE.id].set(coordinate, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PFOUR.id].set(coordinate, GridCellState.KnownByHit)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "Damaged area hit again at (7,7)."
        assert game.playerDetails[PONE.id].lastActionMessage == "2 re-hit your Destroyer at (7,7)."
        game.playerDetails[PONE.id].shipStates.each {
            if (it.key == Ship.Destroyer) {
                assert 1 == it.value.healthRemaining
                assert [true, false] == it.value.shipSegmentHit
            } else {
                assert it.key.gridSize == it.value.healthRemaining
                assertNull it.value.shipSegmentHit.find { it }
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
        assert "" == game.generalMessage
    }

    void testFireAndReHitWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].shipSegmentHit[0] = true
        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        game.playerDetails[PFOUR.id].opponentGrids[PONE.id].set(coordinate, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PFOUR.id].set(coordinate, GridCellState.KnownByHit)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "Damaged area hit again at (7,7)."
        assert game.playerDetails[PONE.id].lastActionMessage == "2 re-hit your Destroyer at (7,7)."
        game.playerDetails[PONE.id].shipStates.each {
            if (it.key == Ship.Destroyer) {
                assert 1 == it.value.healthRemaining
                assert [true, false] == it.value.shipSegmentHit
            } else {
                assert it.key.gridSize == it.value.healthRemaining
                assertNull it.value.shipSegmentHit.find { it }
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
        assert "" == game.generalMessage
    }

    void testFireAndSinkShipWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].shipSegmentHit[1] = true
        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "You sunk a Destroyer!"
        assert game.playerDetails[PONE.id].lastActionMessage == "2 sunk your Destroyer!"
        game.playerDetails[PONE.id].shipStates.each {
            if (it.key == Ship.Destroyer) {
                assert 0 == it.value.healthRemaining
                assert [true, true] == it.value.shipSegmentHit
            } else {
                assert it.key.gridSize == it.value.healthRemaining
                assertNull it.value.shipSegmentHit.find { it }
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
        assert "2 sunk 1's Destroyer!" == game.generalMessage
    }

    void testFireAndSinkShipWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].shipSegmentHit[1] = true
        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "You sunk a Destroyer!"
        assert game.playerDetails[PONE.id].lastActionMessage == "2 sunk your Destroyer!"
        game.playerDetails[PONE.id].shipStates.each {
            if (it.key == Ship.Destroyer) {
                assert 0 == it.value.healthRemaining
                assert [true, true] == it.value.shipSegmentHit
            } else {
                assert it.key.gridSize == it.value.healthRemaining
                assertNull it.value.shipSegmentHit.find { it }
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
        assert "2 sunk 1's Destroyer!" == game.generalMessage
    }

    void testFireAndSinkShipAndEndPlayerWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        Ship.values().each {
            Ship ship ->
                if (ship != Ship.Destroyer) {
                    def state = game.playerDetails[PONE.id].shipStates[ship]
                    state.healthRemaining = 0
                    (1..ship.gridSize).each {
                        int index ->
                            state.shipSegmentHit[index - 1] = true
                    }
                }
        }
        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].shipSegmentHit[1] = true
        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "You sunk a Destroyer!"
        assert game.playerDetails[PONE.id].lastActionMessage == "2 sunk your Destroyer!"
        game.playerDetails[PONE.id].shipStates.each {
            if (it.key == Ship.Destroyer) {
                assert 0 == it.value.healthRemaining
                assert [true, true] == it.value.shipSegmentHit
            } else {
                assert 0 == it.value.healthRemaining
                assertNull it.value.shipSegmentHit.find { !it }
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
        assert "2 has defeated 1!" == game.generalMessage
    }

    void testFireAndSinkShipAndEndPlayerWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        Ship.values().each {
            Ship ship ->
                if (ship != Ship.Destroyer) {
                    def state = game.playerDetails[PONE.id].shipStates[ship]
                    state.healthRemaining = 0
                    (1..ship.gridSize).each {
                        int index ->
                            state.shipSegmentHit[index - 1] = true
                    }
                }
        }
        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].shipSegmentHit[1] = true
        game.playerDetails[PONE.id].shipStates[Ship.Destroyer].healthRemaining = 1
        def coordinate = new GridCoordinate(7, 7)
        assert game.is(handler.playMove(PTWO, game, PONE, coordinate))
        assert game.playerDetails[PTWO.id].lastActionMessage == "You sunk a Destroyer!"
        assert game.playerDetails[PONE.id].lastActionMessage == "2 sunk your Destroyer!"
        game.playerDetails[PONE.id].shipStates.each {
            if (it.key == Ship.Destroyer) {
                assert 0 == it.value.healthRemaining
                assert [true, true] == it.value.shipSegmentHit
            } else {
                assert 0 == it.value.healthRemaining
                assertNull it.value.shipSegmentHit.find { !it }
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
        assert "2 has defeated 1!" == game.generalMessage
    }
}
