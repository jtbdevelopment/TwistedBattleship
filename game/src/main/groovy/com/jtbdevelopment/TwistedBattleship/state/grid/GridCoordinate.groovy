package com.jtbdevelopment.TwistedBattleship.state.grid

import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:33 PM
 */
@CompileStatic
class GridCoordinate implements Comparable<GridCoordinate>, Serializable {
    int row, column

    @SuppressWarnings("unused")
    protected GridCoordinate() {

    }

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

    @Override
    int compareTo(final GridCoordinate o) {
        if (this.row == o.row) {
            if (this.column == o.column) {
                return 0
            }
            return (this.column - o.column)
        }
        return (this.row - o.row)
    }
}
