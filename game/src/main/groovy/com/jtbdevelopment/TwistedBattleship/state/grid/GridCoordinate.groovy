package com.jtbdevelopment.TwistedBattleship.state.grid

import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:33 PM
 */
@CompileStatic
class GridCoordinate {
    int row, column

    GridCoordinate(final int row, final int column) {
        this.row = row
        this.column = column
    }

    boolean equals(final o) {
        if (this.is(o)) return true
        if (!(o instanceof GridCoordinate)) return false

        final GridCoordinate that = (GridCoordinate) o

        if (row != that.row) return false
        if (column != that.column) return false

        return true
    }

    int hashCode() {
        int result
        result = row
        result = 31 * result + column
        return result
    }

}
