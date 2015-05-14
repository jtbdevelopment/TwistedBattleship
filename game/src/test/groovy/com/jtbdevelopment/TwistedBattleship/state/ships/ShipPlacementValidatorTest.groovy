package com.jtbdevelopment.TwistedBattleship.state.ships

import com.jtbdevelopment.TwistedBattleship.exceptions.NotAllShipsSetupException
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipPlacementsNotValidException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil

/**
 * Date: 4/30/15
 * Time: 8:46 PM
 */
class ShipPlacementValidatorTest extends GroovyTestCase {
    ShipPlacementValidator validator = new ShipPlacementValidator()
    TBGame game = new TBGame(features: [GameFeature.Grid15x15])

    @Override
    protected void setUp() throws Exception {
        validator.gridSizeUtil = new GridSizeUtil()
    }

    void testShipPositionsValid1() {
        validator.validateShipPlacementsForGame(game, [
                (Ship.Battleship): new ShipState(Ship.Battleship,
                        new TreeSet([
                                new GridCoordinate(0, 0),
                                new GridCoordinate(0, 1),
                                new GridCoordinate(0, 2),
                                new GridCoordinate(0, 3)
                        ])
                ),
                (Ship.Carrier)   : new ShipState(Ship.Carrier,
                        new TreeSet([
                                new GridCoordinate(1, 0),
                                new GridCoordinate(1, 1),
                                new GridCoordinate(1, 2),
                                new GridCoordinate(1, 3),
                                new GridCoordinate(1, 4)
                        ])
                ),
                (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                        new TreeSet([
                                new GridCoordinate(2, 0),
                                new GridCoordinate(2, 1),
                                new GridCoordinate(2, 2),
                        ])
                ),
                (Ship.Submarine) : new ShipState(Ship.Submarine,
                        new TreeSet([
                                new GridCoordinate(3, 0),
                                new GridCoordinate(3, 1),
                                new GridCoordinate(3, 2),
                        ])
                ),
                (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                        new TreeSet([
                                new GridCoordinate(0, 4),
                                new GridCoordinate(0, 5)
                        ])
                ),
        ])
    }

    void testShipPositionsValid2() {
        validator.validateShipPlacementsForGame(game, [
                (Ship.Battleship): new ShipState(Ship.Battleship,
                        new TreeSet([
                                new GridCoordinate(0, 0),
                                new GridCoordinate(1, 0),
                                new GridCoordinate(2, 0),
                                new GridCoordinate(3, 0)
                        ])
                ),
                (Ship.Carrier)   : new ShipState(Ship.Carrier,
                        new TreeSet([
                                new GridCoordinate(0, 1),
                                new GridCoordinate(1, 1),
                                new GridCoordinate(2, 1),
                                new GridCoordinate(3, 1),
                                new GridCoordinate(4, 1)
                        ])
                ),
                (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                        new TreeSet([
                                new GridCoordinate(0, 2),
                                new GridCoordinate(1, 2),
                                new GridCoordinate(2, 2),
                        ])
                ),
                (Ship.Submarine) : new ShipState(Ship.Submarine,
                        new TreeSet([
                                new GridCoordinate(0, 3),
                                new GridCoordinate(0, 4),
                                new GridCoordinate(0, 5),
                        ])
                ),
                (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                        new TreeSet([
                                new GridCoordinate(4, 0),
                                new GridCoordinate(5, 0),
                        ])
                ),
        ])
    }

    void testShipPositionsValid3() {
        validator.validateShipPlacementsForGame(
                game,
                [
                        (Ship.Battleship): new ShipState(Ship.Battleship,
                                new TreeSet([
                                        new GridCoordinate(0, 0),
                                        new GridCoordinate(1, 0),
                                        new GridCoordinate(2, 0),
                                        new GridCoordinate(3, 0)
                                ])
                        ),
                        (Ship.Carrier)   : new ShipState(Ship.Carrier,
                                new TreeSet([
                                        new GridCoordinate(0, 1),
                                        new GridCoordinate(0, 2),
                                        new GridCoordinate(0, 3),
                                        new GridCoordinate(0, 4),
                                        new GridCoordinate(0, 5)
                                ])
                        ),
                        (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                                new TreeSet([
                                        new GridCoordinate(0, 6),
                                        new GridCoordinate(1, 6),
                                        new GridCoordinate(2, 6),
                                ])
                        ),
                        (Ship.Submarine) : new ShipState(Ship.Submarine,
                                new TreeSet([
                                        new GridCoordinate(0, 7),
                                        new GridCoordinate(0, 8),
                                        new GridCoordinate(0, 9),
                                ])
                        ),
                        (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                                new TreeSet([
                                        new GridCoordinate(0, 10),
                                        new GridCoordinate(1, 10)
                                ])
                        ),
                ]
        )
    }

    void testShipMissingCoordinates() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            4,
                            [
                                    new GridCoordinate(0, 0),
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(2, 0),       // missing one
                            ],
                            [false, false, false, false]
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(0, 1),
                                    new GridCoordinate(0, 2),
                                    new GridCoordinate(0, 3),
                                    new GridCoordinate(0, 4),
                                    new GridCoordinate(0, 5)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(0, 6),
                                    new GridCoordinate(1, 6),
                                    new GridCoordinate(2, 6),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(0, 7),
                                    new GridCoordinate(0, 8),
                                    new GridCoordinate(0, 9),
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(0, 10),
                                    new GridCoordinate(1, 10)
                            ])
                    ),
            ])
        })
    }

    void testShipExtraCoordinates() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            4,
                            [
                                    new GridCoordinate(0, 0),
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(2, 0),
                                    new GridCoordinate(3, 0),
                                    new GridCoordinate(4, 0),                          //  Extra
                            ],
                            [false, false, false, false]
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(0, 1),
                                    new GridCoordinate(0, 2),
                                    new GridCoordinate(0, 3),
                                    new GridCoordinate(0, 4),
                                    new GridCoordinate(0, 5)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(0, 6),
                                    new GridCoordinate(1, 6),
                                    new GridCoordinate(2, 6),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(0, 7),
                                    new GridCoordinate(0, 8),
                                    new GridCoordinate(0, 9),
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(0, 10),
                                    new GridCoordinate(1, 10)
                            ])
                    ),
            ])
        })
    }

    void testShipPositionsOutsideBoundsPositive() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            new TreeSet([
                                    new GridCoordinate(0, 0),
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(2, 0),
                                    new GridCoordinate(3, 0)
                            ])
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(0, 1),
                                    new GridCoordinate(1, 1),
                                    new GridCoordinate(2, 1),
                                    new GridCoordinate(3, 1),
                                    new GridCoordinate(4, 1)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(0, 2),
                                    new GridCoordinate(1, 2),
                                    new GridCoordinate(2, 2),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(13, 3),
                                    new GridCoordinate(14, 4),
                                    new GridCoordinate(15, 5),      //outside
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(4, 0),
                                    new GridCoordinate(5, 0)
                            ])
                    ),
            ])
        })
    }

    void testShipPositionsOutsideBoundsNegative() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            new TreeSet([
                                    new GridCoordinate(0, 0),
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(2, 0),
                                    new GridCoordinate(3, 0)
                            ])
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(0, 1),
                                    new GridCoordinate(1, 1),
                                    new GridCoordinate(2, 1),
                                    new GridCoordinate(3, 1),
                                    new GridCoordinate(4, 1)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(0, 2),
                                    new GridCoordinate(1, 2),
                                    new GridCoordinate(2, 2),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(-1, 3),               // outside
                                    new GridCoordinate(0, 3),
                                    new GridCoordinate(1, 3),
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(4, 0),
                                    new GridCoordinate(5, 0)
                            ])
                    ),
            ])
        })
    }

    void testNotAllShipsInList() {
        shouldFail(NotAllShipsSetupException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Carrier)  : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(0, 1),
                                    new GridCoordinate(1, 1),
                                    new GridCoordinate(2, 1),
                                    new GridCoordinate(3, 1),
                                    new GridCoordinate(4, 1)
                            ])
                    ),
                    (Ship.Cruiser)  : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(0, 2),
                                    new GridCoordinate(1, 2),
                                    new GridCoordinate(2, 2),
                            ])
                    ),
                    (Ship.Submarine): new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(0, 3),
                                    new GridCoordinate(15, 3),
                                    new GridCoordinate(0, 3),
                            ])
                    ),
                    (Ship.Destroyer): new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(4, 0),
                                    new GridCoordinate(5, 0)
                            ])
                    ),
            ])
        })
    }

    void testShipPositionsOverlap() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            new TreeSet([
                                    new GridCoordinate(0, 0),
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(2, 0),
                                    new GridCoordinate(3, 0)  //Overlap
                            ])
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(0, 1),
                                    new GridCoordinate(1, 1),
                                    new GridCoordinate(2, 1),
                                    new GridCoordinate(3, 1),
                                    new GridCoordinate(4, 1)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(0, 2),
                                    new GridCoordinate(1, 2),
                                    new GridCoordinate(2, 2),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(0, 3),
                                    new GridCoordinate(0, 4),
                                    new GridCoordinate(0, 5),
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(3, 0), //Overlap
                                    new GridCoordinate(4, 0)
                            ])
                    ),
            ])
        })
    }

    void testShipPositionsNonContiguousVertical() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            new TreeSet([
                                    new GridCoordinate(0, 0),
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(2, 11),       //  non-contig
                                    new GridCoordinate(3, 0)
                            ])
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(0, 1),
                                    new GridCoordinate(1, 1),
                                    new GridCoordinate(2, 1),
                                    new GridCoordinate(3, 1),
                                    new GridCoordinate(4, 1)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(0, 2),
                                    new GridCoordinate(1, 2),
                                    new GridCoordinate(2, 2),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(0, 4),
                                    new GridCoordinate(0, 5),
                                    new GridCoordinate(0, 6),
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(4, 0),
                                    new GridCoordinate(5, 0)
                            ])
                    ),
            ])
        })
    }

    void testShipPositionsNonContiguousVertical2() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            new TreeSet([
                                    new GridCoordinate(0, 0),
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(2, 0),
                                    new GridCoordinate(10, 0)       // non-contig
                            ])
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(0, 1),
                                    new GridCoordinate(1, 1),
                                    new GridCoordinate(2, 1),
                                    new GridCoordinate(3, 1),
                                    new GridCoordinate(4, 1)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(0, 2),
                                    new GridCoordinate(1, 2),
                                    new GridCoordinate(2, 2),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(0, 4),
                                    new GridCoordinate(0, 5),
                                    new GridCoordinate(0, 6),
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(4, 0),
                                    new GridCoordinate(5, 0)
                            ])
                    ),
            ])
        })
    }

    void testShipPositionsNonContiguousHorizontal() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            new TreeSet([
                                    new GridCoordinate(10, 0),
                                    new GridCoordinate(10, 1),
                                    new GridCoordinate(10, 2),
                                    new GridCoordinate(11, 3)            //  non-contig
                            ])
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(1, 1),
                                    new GridCoordinate(1, 2),
                                    new GridCoordinate(1, 3),
                                    new GridCoordinate(1, 4)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(2, 0),
                                    new GridCoordinate(2, 1),
                                    new GridCoordinate(2, 2),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(3, 0),
                                    new GridCoordinate(3, 1),
                                    new GridCoordinate(3, 2),
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(0, 4),
                                    new GridCoordinate(0, 5)
                            ])
                    ),
            ])
        })
    }

    void testShipPositionsNonContiguousHorizontal2() {
        shouldFail(ShipPlacementsNotValidException.class, {
            validator.validateShipPlacementsForGame(game, [
                    (Ship.Battleship): new ShipState(Ship.Battleship,
                            new TreeSet([
                                    new GridCoordinate(10, 0),
                                    new GridCoordinate(10, 1),
                                    new GridCoordinate(10, 2),
                                    new GridCoordinate(10, 5)            //  non-contig
                            ])
                    ),
                    (Ship.Carrier)   : new ShipState(Ship.Carrier,
                            new TreeSet([
                                    new GridCoordinate(1, 0),
                                    new GridCoordinate(1, 1),
                                    new GridCoordinate(1, 2),
                                    new GridCoordinate(1, 3),
                                    new GridCoordinate(1, 4)
                            ])
                    ),
                    (Ship.Cruiser)   : new ShipState(Ship.Cruiser,
                            new TreeSet([
                                    new GridCoordinate(2, 0),
                                    new GridCoordinate(2, 1),
                                    new GridCoordinate(2, 2),
                            ])
                    ),
                    (Ship.Submarine) : new ShipState(Ship.Submarine,
                            new TreeSet([
                                    new GridCoordinate(3, 0),
                                    new GridCoordinate(3, 1),
                                    new GridCoordinate(3, 2),
                            ])
                    ),
                    (Ship.Destroyer) : new ShipState(Ship.Destroyer,
                            new TreeSet([
                                    new GridCoordinate(0, 4),
                                    new GridCoordinate(0, 5)
                            ])
                    ),
            ])
        })
    }
}
