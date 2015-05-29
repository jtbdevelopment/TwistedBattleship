package com.jtbdevelopment.TwistedBattleship.state.grid

import com.jtbdevelopment.TwistedBattleship.state.TBGame

/**
 * Date: 5/20/15
 * Time: 7:05 AM
 */
class GridCircleUtilTest extends GroovyTestCase {
    GridCircleUtil util = new GridCircleUtil()

    void testSmallGrid() {
        assert [
                new GridCoordinate(2, 2),
                new GridCoordinate(2, 3),
                new GridCoordinate(2, 4),
                new GridCoordinate(3, 3),
                new GridCoordinate(1, 3),
        ] as Set == util.computeCircleCoordinates(new TBGame(gridSize: 10), new GridCoordinate(2, 3))
    }

    void testMediumGrid() {
        assert [
                new GridCoordinate(2, 2),
                new GridCoordinate(2, 3),
                new GridCoordinate(2, 4),
                new GridCoordinate(3, 3),
                new GridCoordinate(1, 3),
                new GridCoordinate(2, 1),
                new GridCoordinate(2, 5),
                new GridCoordinate(4, 3),
                new GridCoordinate(0, 3),
                new GridCoordinate(3, 4),
                new GridCoordinate(3, 2),
                new GridCoordinate(1, 2),
                new GridCoordinate(1, 4)
        ] as Set == util.computeCircleCoordinates(new TBGame(gridSize: 15), new GridCoordinate(2, 3))
    }

    void testLargeGrid() {
        assert [
                new GridCoordinate(2, 2),
                new GridCoordinate(2, 3),
                new GridCoordinate(2, 4),
                new GridCoordinate(3, 3),
                new GridCoordinate(1, 3),
                new GridCoordinate(2, 1),
                new GridCoordinate(2, 5),
                new GridCoordinate(4, 3),
                new GridCoordinate(0, 3),
                new GridCoordinate(3, 4),
                new GridCoordinate(3, 2),
                new GridCoordinate(1, 2),
                new GridCoordinate(1, 4),
                new GridCoordinate(4, 5),
                new GridCoordinate(4, 1),
                new GridCoordinate(0, 1),
                new GridCoordinate(0, 5),
                new GridCoordinate(4, 4),
                new GridCoordinate(3, 5),
                new GridCoordinate(4, 2),
                new GridCoordinate(4, 2),
                new GridCoordinate(1, 5),
                new GridCoordinate(0, 2),
                new GridCoordinate(1, 1),
                new GridCoordinate(0, 4),
                new GridCoordinate(3, 1)
        ] as Set == util.computeCircleCoordinates(new TBGame(gridSize: 20), new GridCoordinate(2, 3))
    }

    void testDrawGrid() {
/*
        int size = 15
        TBGame game = new TBGame(features: [GameFeature.Grid15x15], gridSize: size)
        Grid grid = new Grid(size)
        GridCoordinate start = new GridCoordinate(2, 1)
        GridSizeUtil util = new GridSizeUtil()
        def x = GridCircleUtil.CIRCLE_OFFSETS.findAll { it.key <= size }.collectMany {
            int k, List<GridCoordinate> adjust ->
                adjust.collect {
                    GridCoordinate adjustCoord ->
                        start.add(adjustCoord)
                }
        }.findAll { GridCoordinate it -> util.isValidCoordinate(game, it) }.each {
            GridCoordinate coordinate ->
                grid.set(coordinate, GridCellState.KnownShip)
        }
        (0..(size - 1)).each {
            int row ->
                (0..(size - 1)).each {
                    int col ->
                        if (row == start.row && col == start.column) {
                            print "C "
                        } else {
                            if (grid.get(row, col) == GridCellState.Unknown) {
                                print "U "
                            } else {
                                print "X "
                            }
                        }
                }
                println ""
        }
*/
    }
}
