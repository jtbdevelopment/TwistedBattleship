package com.jtbdevelopment.TwistedBattleship.ai.common

import com.jtbdevelopment.TwistedBattleship.rest.handlers.SetupShipsHandler
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 9/29/15
 * Time: 6:50 AM
 */
@Component
@CompileStatic
class RandomizedSetup {
    @Autowired
    SetupShipsHandler setupShipsHandler

    private Random random = new Random()

    void setup(final TBGame game, final Player player) {
        Set<GridCoordinate> used = [] as Set
        List<ShipState> shipStates = (List<ShipState>) game.startingShips.collect {
            Ship ship ->
                boolean horizontal = random.nextInt(100) < 50
                TreeSet<GridCoordinate> set = new TreeSet<>()
                if (horizontal) {
                    boolean ok = false
                    while (!ok) {
                        int row = random.nextInt(game.gridSize)
                        int startCol = random.nextInt(game.gridSize - ship.gridSize)
                        set = new TreeSet<GridCoordinate>((startCol..(startCol + ship.gridSize - 1)).collect {
                            int col ->
                                new GridCoordinate(row, col)
                        })
                        ok = set.find {
                            used.contains(it)
                        } == null
                    }
                } else {
                    boolean ok = false
                    while (!ok) {
                        int startRow = random.nextInt(game.gridSize - ship.gridSize)
                        int col = random.nextInt(game.gridSize)
                        set = new TreeSet<GridCoordinate>((startRow..(startRow + ship.gridSize - 1)).collect {
                            int row ->
                                new GridCoordinate(row, col)
                        })
                        ok = set.find {
                            used.contains(it)
                        } == null
                    }
                }
                used.addAll(set)
                return new ShipState(ship, set)
        }
        setupShipsHandler.handleAction(player.id, game.id, shipStates)
    }
}
