package com.jtbdevelopment.TwistedBattleship.ai.simple

import com.jtbdevelopment.TwistedBattleship.ai.AI
import com.jtbdevelopment.TwistedBattleship.ai.WeightedTarget
import com.jtbdevelopment.TwistedBattleship.ai.common.AIActionHandlers
import com.jtbdevelopment.TwistedBattleship.ai.common.RandomizedSetup
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 9/18/15
 * Time: 6:58 AM
 */
@SuppressWarnings("GroovyUnusedDeclaration")
@CompileStatic
@Component
class SimpleAI implements AI {
    @Autowired
    SimpleAIPlayerCreator playerCreator

    @Autowired
    AIActionHandlers aiActionHandler

    @Autowired
    GridCircleUtil gridCircleUtil

    @Autowired
    RandomizedSetup randomizedSetup

    private Random random = new Random()

    @Override
    List<Player> getPlayers() {
        return playerCreator.players
    }

    void setup(final TBGame game, final Player player) {
        randomizedSetup.setup(game, player)
    }

    void playOneMove(final TBGame game, final Player player) {
        TBPlayerState myState = game.playerDetails[(ObjectId) player.id]
        // if we hit something cruise missile it
        if (!didCruiseMissile(game, player, myState)) {
            //  PerShip
            //  If carrier or battleship has 2 or more dmg points and repair available - play repair
            //  Single
            //  If non-destroyer has 2 or more dmg points and repair available - play repair
            if (!didRepair(game, player, myState)) {
                //  If anything other than destroyer has a hit on it and evasive available - evasive
                if (!didEvasive(game, player, myState)) {
                    //  If spy available - use it
                    if (!didSpy(game, player, myState)) {
                        //  If we can get at least 3 known details eliminated ecm
                        if (!didECM(game, player, myState)) {
                            //  Fire
                            if (!didFire(game, player, myState)) {
                                throw new RuntimeException("Unable to take any action!")
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean didCruiseMissile(final TBGame game, final Player player, final TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.cruiseMissilesRemaining > 0) {
            List<WeightedTarget> targets = myState.opponentGrids.findAll {
                game.playerDetails[it.key].alive
            }.collectMany {
                ObjectId opponent, Grid grid ->
                    String md5 = game.players.find { it.id == opponent }.md5
                    List<WeightedTarget> weightedTargets = []
                    for (int row = 0; row < game.gridSize; ++row) {
                        for (int col = 0; col < game.gridSize; ++col) {
                            GridCoordinate coordinate = new GridCoordinate(row, col);
                            switch (grid.get(coordinate)) {
                                case GridCellState.KnownByHit:
                                case GridCellState.KnownShip:
                                    weightedTargets.add(new WeightedTarget(
                                            player: md5,
                                            coordinate: coordinate
                                    ))
                                    break;
                            }
                        }
                    }
                    weightedTargets
            }.toList()

            if (targets.size() == 0) {
                return false;
            }

            WeightedTarget target = targets[random.nextInt(targets.size())]

            aiActionHandler.cruiseMissileHandler.handleAction(
                    player.id,
                    game.id,
                    target)

            return true
        }

        return false
    }

    private boolean didRepair(final TBGame game, final Player player, final TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.emergencyRepairsRemaining > 0) {
            boolean perShip = game.features.contains(GameFeature.PerShip)
            Set<Ship> allowedShips = perShip ?
                    [Ship.Carrier, Ship.Battleship] as Set :
                    [Ship.Carrier, Ship.Battleship, Ship.Submarine, Ship.Cruiser] as Set
            def damagedShip = myState.shipStates.findAll {
                ShipState state ->
                    allowedShips.contains(state.ship) &&
                            state.healthRemaining < (state.ship.gridSize - 2) &&
                            state.healthRemaining > 0
            }.sort {
                ShipState one, ShipState two ->
                    return ((two.ship.gridSize - one.ship.gridSize) * 10) + (two.healthRemaining - one.healthRemaining)
            }.find {
                true
            }
            if (damagedShip) {
                aiActionHandler.repairShipHandler.handleAction(
                        player.id,
                        game.id,
                        new Target(player: player.md5, coordinate: damagedShip.shipGridCells[0]))
                return true
            }
        }

        return false
    }

    private boolean didEvasive(final TBGame game, final Player player, final TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.evasiveManeuversRemaining > 0) {
            def damagedShip = myState.shipStates.findAll {
                ShipState state ->
                    state.ship.gridSize > 2 &&
                            state.healthRemaining < (state.ship.gridSize - 2) &&
                            state.healthRemaining > 0
            }.sort {
                ShipState one, ShipState two ->
                    return ((two.ship.gridSize - one.ship.gridSize) * 10) + (one.healthRemaining - two.healthRemaining)
            }.find {
                true
            }
            if (damagedShip) {
                aiActionHandler.evasiveManeuverHandler.handleAction(
                        player.id,
                        game.id,
                        new Target(player: player.md5, coordinate: damagedShip.shipGridCells[0]))
                return true
            }
        }

        return false
    }

    private boolean didSpy(final TBGame game, final Player player, TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.spysRemaining > 0) {
            List<WeightedTarget> targets = myState.opponentGrids.findAll {
                game.playerDetails[it.key].alive
            }.collectMany {
                ObjectId opponent, Grid grid ->
                    String md5 = game.players.find { it.id == opponent }.md5
                    List<WeightedTarget> weightedTargets = []
                    for (int row = 0; row < game.gridSize; ++row) {
                        for (int col = 0; col < game.gridSize; ++col) {
                            int currentValue = 0;
                            gridCircleUtil.computeCircleCoordinates(game, new GridCoordinate(row, col)).each {
                                switch (grid.get(it)) {
                                    case GridCellState.KnownEmpty:
                                    case GridCellState.KnownByRehit:
                                    case GridCellState.KnownByHit:
                                    case GridCellState.KnownByOtherMiss:
                                    case GridCellState.KnownByOtherHit:
                                    case GridCellState.KnownByMiss:
                                    case GridCellState.KnownShip:
                                        break
                                    case GridCellState.Unknown:
                                        currentValue += 2
                                        break
                                    default:
                                        //  All the obscured
                                        currentValue += 1
                                        break
                                }
                            }
                            weightedTargets.add(new WeightedTarget(
                                    player: md5,
                                    coordinate: new GridCoordinate(row, col),
                                    weight: currentValue))
                        }
                    }
                    weightedTargets
            }.toList()

            List<WeightedTarget> bestTargets = targets.sort {
                WeightedTarget t1, WeightedTarget t2 ->
                    return t2.weight - t1.weight
            }.findAll {
                WeightedTarget t ->
                    t.weight == targets[0].weight
            }.toList()

            WeightedTarget target = bestTargets[random.nextInt(bestTargets.size())]

            aiActionHandler.spyHandler.handleAction(
                    player.id,
                    game.id,
                    target)

            return true
        }
        return false
    }

    private boolean didECM(final TBGame game, final Player player, final TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.ecmsRemaining > 0) {
            List<WeightedTarget> targets = myState.opponentViews.findAll {
                game.playerDetails[it.key].alive
            }.collectMany {
                ObjectId opponent, Grid grid ->
                    List<WeightedTarget> weightedTargets = []
                    for (int row = 0; row < game.gridSize; ++row) {
                        for (int col = 0; col < game.gridSize; ++col) {
                            int currentValue = 0;
                            gridCircleUtil.computeCircleCoordinates(game, new GridCoordinate(row, col)).each {
                                switch (grid.get(it)) {
                                    case GridCellState.KnownShip:
                                        currentValue += 4
                                        break
                                    case GridCellState.KnownEmpty:
                                    case GridCellState.KnownByRehit:
                                    case GridCellState.KnownByHit:
                                    case GridCellState.KnownByOtherMiss:
                                    case GridCellState.KnownByOtherHit:
                                    case GridCellState.KnownByMiss:
                                        currentValue += 2
                                        break
                                    case GridCellState.Unknown:
                                        break
                                    default:
                                        //  All the obscured
                                        currentValue += 1
                                        break
                                }
                            }
                            if (currentValue >= 6) {
                                weightedTargets.add(new WeightedTarget(
                                        player: player.md5,
                                        coordinate: new GridCoordinate(row, col),
                                        weight: currentValue))
                            }
                        }
                    }
                    weightedTargets
            }.toList()

            if (targets.size() == 0) {
                return false
            }

            List<WeightedTarget> bestTargets = targets.sort {
                WeightedTarget t1, WeightedTarget t2 ->
                    return t2.weight - t1.weight
            }.findAll {
                WeightedTarget t ->
                    t.weight == targets[0].weight
            }.toList()

            WeightedTarget target = bestTargets[random.nextInt(bestTargets.size())]

            aiActionHandler.ecmHandler.handleAction(
                    player.id,
                    game.id,
                    target)

            return true
        }
        return false
    }

    private boolean didFire(final TBGame game, final Player player, final TBPlayerState myState) {
        List<WeightedTarget> targets = myState.opponentGrids.findAll {
            game.playerDetails[it.key].alive
        }.collectMany {
            ObjectId opponent, Grid grid ->
                List<WeightedTarget> weightedTargets = []
                String md5 = game.players.find { it.id == opponent }.md5
                for (int row = 0; row < game.gridSize; ++row) {
                    for (int col = 0; col < game.gridSize; ++col) {
                        int currentValue;
                        switch (grid.get(row, col)) {
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
                        weightedTargets.add(new WeightedTarget(
                                player: md5,
                                coordinate: new GridCoordinate(row, col),
                                weight: currentValue))
                    }
                }
                weightedTargets
        }.toList()

        List<WeightedTarget> bestTargets = targets.sort {
            WeightedTarget t1, WeightedTarget t2 ->
                return t2.weight - t1.weight
        }.findAll {
            WeightedTarget t ->
                t.weight == targets[0].weight
        }.toList()

        WeightedTarget target = bestTargets[random.nextInt(bestTargets.size())]

        aiActionHandler.fireAtCoordinateHandler.handleAction(
                player.id,
                game.id,
                target)

        return true
    }

}
