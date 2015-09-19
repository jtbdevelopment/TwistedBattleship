package com.jtbdevelopment.TwistedBattleship.ai.simple

import com.jtbdevelopment.TwistedBattleship.ai.AI
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.rest.handlers.*
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 9/18/15
 * Time: 6:58 AM
 */
@CompileStatic
@Component
class SimpleAI implements AI {
    @Autowired
    SimpleAIPlayerCreator playerCreator

    @Autowired
    RepairShipHandler repairShipHandler

    @Autowired
    EvasiveManeuverHandler evasiveManeuverHandler

    @Autowired
    FireAtCoordinateHandler fireAtCoordinateHandler

    @Autowired
    SpyHandler spyHandler

    @Autowired
    SetupShipsHandler setupShipsHandler

    private Random random = new Random()

    @Override
    Player getPlayer() {
        return playerCreator.player
    }

    void setup(final TBGame game) {
        //  TODO - better
        int row = 0
        Map<Ship, ShipState> shipStateMap = (Map<Ship, ShipState>) Ship.values().collectEntries {
            Ship ship ->
                row++
                [(ship): new ShipState(ship, new TreeSet<GridCoordinate>((1..ship.gridSize).collect {
                    int col ->
                        new GridCoordinate(row, col)
                }))]
        }
        setupShipsHandler.handleAction(playerCreator.player.id, game.id, shipStateMap)
    }

    //  TODO - ECM rule?
    void playOneMove(final TBGame game) {
        TBPlayerState myState = game.playerDetails[(ObjectId) playerCreator.player.id]
        //  PerShip
        //  If carrier or battleship has 2 or more dmg points and repair available - play repair
        //  Single
        //  If non-destroyer has 2 or more dmg points and repair available - play repair
        if (!didRepair(game, myState)) {
            //  If anything other than destroyer has a hit on it and evasive available - evasive
            if (!didEvasive(game, myState)) {
                //  If spy available - use it
                if (!didSpy(game, myState)) {
                    //  Fire
                    if (!didFire(game)) {
                        //  TODO -??
                    }
                }
            }

        }
    }

    private boolean didRepair(final TBGame game, final TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.emergencyRepairsRemaining > 0) {
            boolean perShip = game.features.contains(GameFeature.PerShip)
            Set<Ship> allowedShips = perShip ?
                    [Ship.Carrier, Ship.Battleship] as Set :
                    [Ship.Carrier, Ship.Battleship, Ship.Submarine, Ship.Cruiser] as Set
            def damagedShip = myState.shipStates.values().findAll {
                ShipState state ->
                    allowedShips.contains(state.ship) && (state.healthRemaining < state.ship.gridSize - 2)
            }.sort {
                ShipState one, ShipState two ->
                    return ((two.ship.gridSize - one.ship.gridSize) * 10) + (two.healthRemaining - one.healthRemaining)
            }.find {
                true
            }
            if (damagedShip) {
                repairShipHandler.handleAction(
                        playerCreator.player.id,
                        game.id,
                        new Target(player: playerCreator.player.md5, coordinate: damagedShip.shipGridCells[0]))
                return true
            }
        }

        return false
    }

    private boolean didEvasive(final TBGame game, final TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.evasiveManeuversRemaining > 0) {
            def damagedShip = myState.shipStates.values().findAll {
                ShipState state ->
                    state.ship.gridSize > 2 && (state.healthRemaining < state.ship.gridSize - 2)
            }.sort {
                ShipState one, ShipState two ->
                    return ((two.ship.gridSize - one.ship.gridSize) * 10) + (one.healthRemaining - two.healthRemaining)
            }.find {
                true
            }
            if (damagedShip) {
                evasiveManeuverHandler.handleAction(
                        playerCreator.player.id,
                        game.id,
                        new Target(player: playerCreator.player.md5, coordinate: damagedShip.shipGridCells[0]))
                return true
            }
        }

        return false
    }

    private boolean didSpy(final TBGame game, TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.spysRemaining > 0) {
            //  TODO
        }
        return false
    }

    private boolean didFire(final TBGame game) {
        Player target = findActiveOpponent(game)

        Grid opponent = game.playerDetails[(ObjectId) playerCreator.player.id].opponentGrids[(ObjectId) target.id]
        GridCoordinate bestTarget = new GridCoordinate(0, 0)
        int bestTargetValue = -1
        for (int row = 0; row < game.gridSize; ++row) {
            for (int col = 0; col < game.gridSize; ++col) {
                int currentValue;
                switch (opponent.get(row, col)) {
                    case GridCellState.KnownEmpty:
                    case GridCellState.KnownByRehit:
                    case GridCellState.KnownByHit:
                    case GridCellState.KnownByOtherMiss:
                    case GridCellState.KnownByOtherHit:
                    case GridCellState.KnownByMiss:
                        currentValue = -1
                        break
                    case GridCellState.KnownShip:
                        currentValue = 100
                        break
                    case GridCellState.Unknown:
                        currentValue = 25
                        break
                    default:
                        currentValue = 50
                        break
                }
                if (currentValue > bestTargetValue) {
                    bestTarget = new GridCoordinate(row, col)
                    bestTargetValue = currentValue
                }
            }
        }
        fireAtCoordinateHandler.handleAction(
                playerCreator.player.id,
                game.id,
                new Target(player: target.md5, coordinate: bestTarget))

        return true
    }

    protected Player findActiveOpponent(TBGame game) {
        List<Player> targets = game.playerDetails.findAll {
            ObjectId id, TBPlayerState state ->
                state.activeShipsRemaining > 0 && id != playerCreator.player.id
        }.collect {
            ObjectId id, TBPlayerState state ->
                game.players.find {
                    id == it.id
                }
        }.sort()
        return targets[random.nextInt(targets.size())]
    }
}
