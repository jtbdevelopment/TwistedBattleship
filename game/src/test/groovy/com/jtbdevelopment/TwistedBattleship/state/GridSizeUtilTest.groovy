package com.jtbdevelopment.TwistedBattleship.state
/**
 * Date: 4/2/15
 * Time: 5:17 PM
 */
class GridSizeUtilTest extends GroovyTestCase {
    GridSizeUtil gridSizeUtil = new GridSizeUtil()

    void testValidGridSize() {
        assert 10 == gridSizeUtil.getSize(GameFeatures.Grid10x10)
        assert 15 == gridSizeUtil.getSize(GameFeatures.Grid15x15)
        assert 20 == gridSizeUtil.getSize(GameFeatures.Grid20x20)
    }

    void testInvalidGridSize() {
        shouldFail(IllegalArgumentException.class, {
            gridSizeUtil.getSize(null)
        })

        shouldFail(IllegalArgumentException.class, {
            gridSizeUtil.getSize(GameFeatures.ActionsPerTurn)
        })

        shouldFail(IllegalArgumentException.class, {
            gridSizeUtil.getSize(GameFeatures.GridSize)
        })
    }
}
