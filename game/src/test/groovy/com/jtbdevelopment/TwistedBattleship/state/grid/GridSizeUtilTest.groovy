package com.jtbdevelopment.TwistedBattleship.state.grid

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame

/**
 * Date: 4/2/15
 * Time: 5:17 PM
 */
class GridSizeUtilTest extends GroovyTestCase {
    GridSizeUtil gridSizeUtil = new GridSizeUtil()

    void testValidGridSize() {
        assert 10 == gridSizeUtil.getSize(new TBGame(features: [GameFeature.Grid10x10]))
        assert 15 == gridSizeUtil.getSize(new TBGame(features: [GameFeature.Grid15x15]))
        assert 20 == gridSizeUtil.getSize(new TBGame(features: [GameFeature.Grid20x20]))
    }

    void testInvalidGridSize() {
        shouldFail(IllegalArgumentException.class, {
            gridSizeUtil.getSize(new TBGame(features: []))
        })
    }

    void testInvalidGridSizes() {
        [GameFeature.Grid20x20, GameFeature.Grid10x10, GameFeature.Grid15x15].each {
            GameFeature size ->
                TBGame game = new TBGame(features: [size])
                int max = gridSizeUtil.getSize(game)
                [
                        new GridCoordinate(-1, 0),
                        new GridCoordinate(0, -1),
                        new GridCoordinate(0, max),
                        new GridCoordinate(max, 0),
                        new GridCoordinate(0, max + 1),
                        new GridCoordinate(max + 1, 0),
                ].each {
                    assertFalse gridSizeUtil.isValidCoordinate(game, it)
                }

        }
    }

    void testValidGridSizes() {
        [GameFeature.Grid20x20, GameFeature.Grid10x10, GameFeature.Grid15x15].each {
            GameFeature size ->
                TBGame game = new TBGame(features: [size])
                game.gridSize = gridSizeUtil.getSize(game)
                int max = gridSizeUtil.getSize(game)
                (0..max - 1).each {
                    int row ->
                        (0..max - 1).each {
                            int col ->
                                assert gridSizeUtil.isValidCoordinate(game, new GridCoordinate(row, col))
                        }
                }

        }
    }
}
