package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@CompileStatic
class PlayerStateInitializer implements GameInitializer<TBGame> {
    @Autowired
    GridSizeUtil util
    @Override
    void initializeGame(final TBGame game) {
        int players = game.players.size()
        GameFeature size = game.features.find{ GameFeature it -> it.group == GameFeature.GridSize}
        int sizeValue = util.getSize(size)
        game.players.each {
            Player<ObjectId> p ->
                Set<Player> opponents = new HashSet<Player>(game.players)
                opponents.remove(p)
                game.playerDetails[p.id] = new TBPlayerState(
                        ecmsRemaining: game.features.contains(GameFeature.ECMEnabled) ? players : 0,
                        emergencyRepairsRemaining: game.features.contains(GameFeature.EREnabled) ? players : 0,
                        evasiveManeuversRemaining: game.features.contains(GameFeature.EMEnabled) ? players : 0,
                        spysRemaining: game.features.contains(GameFeature.SpyEnabled) ? players : 0,
                        opponentGrids: opponents.collectEntries{Player o ->
                            [(o.id), new Grid(sizeValue)]
                        },
                        opponentViews: opponents.collectEntries{Player o ->
                            [(o.id), new Grid(sizeValue)]
                        }
                )
        }
    }
}
