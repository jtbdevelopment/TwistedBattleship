package com.jtbdevelopment.TwistedBattleship.state.grid

import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:56 AM
 */
@CompileStatic
enum GridCellState {
    Unknown,
    KnownEmpty,
    KnownShip,
    KnownByMiss,
    KnownByHit,
}