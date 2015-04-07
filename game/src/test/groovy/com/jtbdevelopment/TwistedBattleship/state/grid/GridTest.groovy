package com.jtbdevelopment.TwistedBattleship.state.grid

/**
 * Date: 4/6/15
 * Time: 7:13 PM
 */
class GridTest extends GroovyTestCase {
    void testInitialization() {
        Grid grid = new Grid(5)
        assert grid.table.size() == 25
        (0..4).each {
            int r->
            (0..4).each {
                int c ->
                    assert grid.table.get(r, c) == GridCellState.Unknown
            }
        }
    }
}
