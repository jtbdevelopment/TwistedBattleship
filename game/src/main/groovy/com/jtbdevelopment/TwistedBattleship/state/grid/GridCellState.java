package com.jtbdevelopment.TwistedBattleship.state.grid;

/**
 * Date: 4/2/15
 * Time: 6:56 AM
 */
public enum GridCellState {
    Unknown(0),

    ObscuredEmpty(10),
    ObscuredShip(10),
    ObscuredOtherHit(20),
    ObscuredOtherMiss(20),
    ObscuredRehit(25),
    ObscuredMiss(30),
    ObscuredHit(30),

    RevealedShip(35),
    KnownEmpty(40),
    KnownShip(40),

    HiddenHit(45),
    KnownByOtherHit(50),
    KnownByOtherMiss(50),
    KnownByRehit(55),
    KnownByMiss(60),
    KnownByHit(60);

    private int rank;

    GridCellState(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
