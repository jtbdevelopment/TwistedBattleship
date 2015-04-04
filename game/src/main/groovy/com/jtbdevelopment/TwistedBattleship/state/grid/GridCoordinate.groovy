package com.jtbdevelopment.TwistedBattleship.state.grid

import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:33 PM
 */
@CompileStatic
class GridCoordinate {
    int x, y

    GridCoordinate(final int x, final int y) {
        this.x = x
        this.y = y
    }
}
