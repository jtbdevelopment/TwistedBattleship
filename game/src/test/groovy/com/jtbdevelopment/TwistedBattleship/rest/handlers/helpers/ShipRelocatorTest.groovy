package com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoRoomForEmergencyManeuverException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState

/**
 * Date: 5/27/15
 * Time: 6:43 AM
 */
class ShipRelocatorTest extends GroovyTestCase {
    ShipRelocator relocator = new ShipRelocator()

    Set<GridCoordinate> otherCoords = [new GridCoordinate(5, 5), new GridCoordinate(6, 6), new GridCoordinate(7, 7)] as Set
    ShipState shipState = new ShipState(Ship.Battleship, new TreeSet<GridCoordinate>([
            new GridCoordinate(1, 1),
            new GridCoordinate(2, 2),
            new GridCoordinate(3, 3)
    ]))
    TBGame game = [];
    TBPlayerState playerState = [
            getCoordinateShipMap: {
                Map<GridCoordinate, ShipState> results = [:]
                otherCoords.each { results.put(it, null) }
                shipState.shipGridCells.each { results.put(it, null) }
                results
            }
    ] as TBPlayerState

    void testSimpleCaseOnFirstAttemptWithRotateAndMoveByRows() {
        List<GridCoordinate> newcoords = [new GridCoordinate(-1, -1), new GridCoordinate(-15, 15)]
        List<GridCoordinate> rotated = [new GridCoordinate(4, 4), new GridCoordinate(3, 3)]
        relocator.generator = [
                nextBoolean: {
                    true
                },
                nextInt    : {
                    int max ->
                        assert max == 5
                        return 2
                }
        ] as Random
        relocator.rotater = [
                rotateShip: {
                    ShipState s ->
                        assert shipState.is(s)
                        return rotated
                }
        ] as ShipRotater
        relocator.calculator = [
                relocateShip: {
                    TBGame g, ShipState ss, List<GridCoordinate> ic, Set<GridCoordinate> osc, boolean rows, List<Integer> t ->
                        assert game.is(g)
                        assert shipState.is(ss)
                        assert rotated.is(ic)
                        assert otherCoords == osc
                        assert rows
                        assert [2, -2, 1, -1, 0] == t
                        return newcoords
                }
        ] as ShipRelocatorCalculator

        assert newcoords.is(relocator.relocateShip(game, playerState, shipState))
    }

    void testSimpleCaseOnFirstAttemptWithoutRotateAndMoveByCols() {
        List<GridCoordinate> newcoords = [new GridCoordinate(-1, -1), new GridCoordinate(-15, 15)]
        relocator.generator = [
                nextBoolean: {
                    false
                },
                nextInt    : {
                    int max ->
                        assert max == 5
                        return 4
                }
        ] as Random
        relocator.calculator = [
                relocateShip: {
                    TBGame g, ShipState ss, List<GridCoordinate> ic, Set<GridCoordinate> osc, boolean rows, List<Integer> t ->
                        assert game.is(g)
                        assert shipState.is(ss)
                        assert shipState.shipGridCells == ic
                        assert otherCoords == osc
                        assertFalse rows
                        assert [2, -1, -2, 1, 0] == t
                        return newcoords
                }
        ] as ShipRelocatorCalculator

        assert newcoords.is(relocator.relocateShip(game, playerState, shipState))
    }

    void testTrysAllVariantsWithInitiallySetToRotateAndMoveByRows() {
        List<GridCoordinate> rotated = [new GridCoordinate(4, 4), new GridCoordinate(3, 3)]
        relocator.generator = [
                nextBoolean: {
                    true
                },
                nextInt    : {
                    int max ->
                        assert max == 5
                        return 5
                }
        ] as Random
        relocator.rotater = [
                rotateShip: {
                    ShipState s ->
                        assert shipState.is(s)
                        return rotated
                }
        ] as ShipRotater
        int count = 0
        relocator.calculator = [
                relocateShip: {
                    TBGame g, ShipState ss, List<GridCoordinate> ic, Set<GridCoordinate> osc, boolean rows, List<Integer> t ->
                        assert game.is(g)
                        assert shipState.is(ss)
                        assert otherCoords == osc
                        assert [0, 1, -2, -1, 2] == t
                        ++count
                        switch (count) {
                            case 1:
                                assert rows
                                assert rotated.is(ic)
                                break
                            case 2:
                                assertFalse rows
                                assert rotated.is(ic)
                                break;
                            case 3:
                                assert rows
                                assert shipState.shipGridCells == ic
                                break;
                            case 4:
                                assertFalse rows
                                assert shipState.shipGridCells == ic
                                break;
                            default:
                                fail("unexpected count" + count)
                        }
                        null
                }
        ] as ShipRelocatorCalculator

        shouldFail(NoRoomForEmergencyManeuverException.class, {
            relocator.relocateShip(game, playerState, shipState)
        })
        assert 4 == count
    }

    void testTrysAllVariantsWithInitiallySetToNoRotateAndMoveByCols() {
        List<GridCoordinate> rotated = [new GridCoordinate(4, 4), new GridCoordinate(3, 3)]
        relocator.generator = [
                nextBoolean: {
                    false
                },
                nextInt    : {
                    int max ->
                        assert max == 5
                        return 5
                }
        ] as Random
        relocator.rotater = [
                rotateShip: {
                    ShipState s ->
                        assert shipState.is(s)
                        return rotated
                }
        ] as ShipRotater
        int count = 0
        relocator.calculator = [
                relocateShip: {
                    TBGame g, ShipState ss, List<GridCoordinate> ic, Set<GridCoordinate> osc, boolean rows, List<Integer> t ->
                        assert game.is(g)
                        assert shipState.is(ss)
                        assert otherCoords == osc
                        assert [0, 1, -2, -1, 2] == t
                        ++count
                        switch (count) {
                            case 3:
                                assertFalse rows
                                assert rotated.is(ic)
                                break
                            case 4:
                                assert rows
                                assert rotated.is(ic)
                                break;
                            case 1:
                                assertFalse rows
                                assert shipState.shipGridCells == ic
                                break;
                            case 2:
                                assert rows
                                assert shipState.shipGridCells == ic
                                break;
                            default:
                                fail("unexpected count" + count)
                        }
                        null
                }
        ] as ShipRelocatorCalculator

        shouldFail(NoRoomForEmergencyManeuverException.class, {
            relocator.relocateShip(game, playerState, shipState)
        })
        assert 4 == count
    }
}
