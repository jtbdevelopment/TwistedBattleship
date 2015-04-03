package com.jtbdevelopment.TwistedBattleship.state.grid

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 7:07 AM
 */
@CompileStatic
class Grid {
    final Table<Integer, Integer, GridCellState> table

    public Grid(final int size) {
        table = HashBasedTable.<Integer, Integer, GridCellState>create(size, size)
    }
}
