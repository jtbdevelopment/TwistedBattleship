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
}
