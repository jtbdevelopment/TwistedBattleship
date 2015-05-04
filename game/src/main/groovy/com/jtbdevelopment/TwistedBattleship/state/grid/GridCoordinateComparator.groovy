package com.jtbdevelopment.TwistedBattleship.state.grid

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 5/1/15
 * Time: 7:08 PM
 */
@CompileStatic
@Component
class GridCoordinateComparator implements Comparator<GridCoordinate> {
    @Override
    int compare(final GridCoordinate o1, final GridCoordinate o2) {
        if (o1.row == o2.row) {
            if (o1.column == o2.column) {
                return 0
            }
            return (o1.column - o2.column)
        }
        return (o1.row - o2.row)
    }
}
