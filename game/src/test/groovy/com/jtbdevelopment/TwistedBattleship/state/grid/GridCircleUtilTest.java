package com.jtbdevelopment.TwistedBattleship.state.grid;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/20/15
 * Time: 7:05 AM
 */
public class GridCircleUtilTest {
    private GridCircleUtil util = new GridCircleUtil();

    @Test
    public void testSmallGrid() {
        TBGame game = new TBGame();

        game.setGridSize(10);
        assertEquals(new HashSet<>(Arrays.asList(
                new GridCoordinate(2, 2),
                new GridCoordinate(2, 3),
                new GridCoordinate(2, 4),
                new GridCoordinate(3, 3),
                new GridCoordinate(1, 3))),
                util.computeCircleCoordinates(game, new GridCoordinate(2, 3)));
    }

    @Test
    public void testMediumGrid() {
        TBGame game = new TBGame();
        game.setGridSize(15);
        assertEquals(new HashSet<>(Arrays.asList(
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
                new GridCoordinate(1, 4))),
                util.computeCircleCoordinates(game, new GridCoordinate(2, 3)));
    }

    @Test
    public void testLargeGrid() {
        TBGame game = new TBGame();
        game.setGridSize(20);
        assertEquals(new HashSet<>(Arrays.asList(
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
                new GridCoordinate(3, 1))),
                util.computeCircleCoordinates(game, new GridCoordinate(2, 3)));
    }

    @Test
    public void testDrawGrid() {
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
