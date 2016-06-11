package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.players.Player
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
class PlayerGameStateInitializer implements GameInitializer<TBGame> {
    final int order = LATE_ORDER

    @Override
    void initializeGame(final TBGame game) {
        int specialMoves = game.players.size() - 1

        List<ShipState> defaultShipPlacements = game.startingShips.withIndex().collect {
            Ship ship, int index ->
                def coordinates = (1..ship.gridSize).collect {
                    int gridIndex ->
                        new GridCoordinate(index, gridIndex)
                } as SortedSet
                new ShipState(ship, coordinates)
        }

        game.players.each {
            Player<ObjectId> p ->
                Set<Player> opponents = new HashSet<Player>(game.players)
                opponents.remove(p)
                game.playerDetails[p.id] = new TBPlayerState(
                        startingShips: game.startingShips,
                        shipStates: new LinkedList<ShipState>(defaultShipPlacements),
                        ecmsRemaining: game.features.contains(GameFeature.ECMEnabled) ? specialMoves : 0,
                        cruiseMissilesRemaining: game.features.contains(GameFeature.CruiseMissileEnabled) ? 1 : 0,
                        emergencyRepairsRemaining: game.features.contains(GameFeature.EREnabled) ? specialMoves : 0,
                        evasiveManeuversRemaining: game.features.contains(GameFeature.EMEnabled) ? specialMoves : 0,
                        spysRemaining: game.features.contains(GameFeature.SpyEnabled) ? specialMoves : 0,
                        opponentGrids: opponents.collectEntries { Player o ->
                            [(o.id), new Grid(game.gridSize)]
                        },
                        opponentViews: opponents.collectEntries { Player o ->
                            [(o.id), new Grid(game.gridSize)]
                        }
                )
        }
    }
}
