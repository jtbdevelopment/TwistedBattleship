package com.jtbdevelopment.TwistedBattleship.state.grid

import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 6:56 AM
 */
@CompileStatic
enum GridCellState {
    //  States for viewing other map
    Unknown(0),

    ObscuredEmpty(10), // previously spied
    ObscuredShip(10),  // previously spied

    ObscuredOtherHit(20),
    ObscuredRehit(25),
    ObscuredMiss(30),
    ObscuredHit(30),

    KnownEmpty(40),  // Spied
    KnownShip(40),   // Spied

    KnownByOtherHit(50),
    KnownByRehit(55),
    KnownByMiss(60),
    KnownByHit(60)

    int rank

    GridCellState(int rank) {
        this.rank = rank
    }
}