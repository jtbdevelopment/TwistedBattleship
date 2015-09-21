package com.jtbdevelopment.TwistedBattleship.ai.simple

import com.jtbdevelopment.TwistedBattleship.ai.AI
import com.jtbdevelopment.TwistedBattleship.ai.WeightedTarget
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.rest.handlers.*
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
    RepairShipHandler repairShipHandler

    @Autowired
    EvasiveManeuverHandler evasiveManeuverHandler

    @Autowired
    FireAtCoordinateHandler fireAtCoordinateHandler

    @Autowired
    SpyHandler spyHandler

    @Autowired
    ECMHandler ecmHandler

    @Autowired
    SetupShipsHandler setupShipsHandler

    @Autowired
    GridCircleUtil gridCircleUtil

    private Random random = new Random()

    @Override
    Player getPlayer() {
        return playerCreator.player
    }

    void setup(final TBGame game) {
        Set<GridCoordinate> used = [] as Set
        List<ShipState> shipStates = (List<ShipState>) game.startingShips.collect {
            Ship ship ->
                boolean horizontal = random.nextInt(100) < 50
                TreeSet<GridCoordinate> set
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
        setupShipsHandler.handleAction(playerCreator.player.id, game.id, shipStates)
    }

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
                    //  If we can get at least 3 known details eliminated ecm
                    if (!didECM(game, myState)) {
                        //  Fire
                        if (!didFire(game, myState)) {
                            throw new RuntimeException("Unable to take any action!")
                        }
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
            List<WeightedTarget> targets = myState.opponentGrids.collectMany {
                ObjectId opponent, Grid grid ->
                    List<WeightedTarget> weightedTargets = []
                    for (int row = 0; row < game.gridSize; ++row) {
                        for (int col = 0; col < game.gridSize; ++col) {
                            int currentValue = 0;
                            gridCircleUtil.computeCircleCoordinates(game, new GridCoordinate(row, col)).each {
                                switch (grid.get(row, col)) {
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
                                    player: opponent,
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

            spyHandler.handleAction(
                    playerCreator.player.id,
                    game.id,
                    new Target(player: game.players.find { it.id == target.player }.md5, coordinate: target.coordinate))

            return true
        }
        return false
    }

    private boolean didECM(final TBGame game, TBPlayerState myState) {
        if (game.movesForSpecials <= game.remainingMoves && myState.ecmsRemaining > 0) {
            List<WeightedTarget> targets = myState.opponentViews.collectMany {
                ObjectId opponent, Grid grid ->
                    List<WeightedTarget> weightedTargets = []
                    for (int row = 0; row < game.gridSize; ++row) {
                        for (int col = 0; col < game.gridSize; ++col) {
                            int currentValue = 0;
                            gridCircleUtil.computeCircleCoordinates(game, new GridCoordinate(row, col)).each {
                                switch (grid.get(row, col)) {
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
                                        player: opponent,
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

            ecmHandler.handleAction(
                    playerCreator.player.id,
                    game.id,
                    new Target(player: player.md5, coordinate: target.coordinate))

            return true
        }
        return false
    }

    private boolean didFire(final TBGame game, final TBPlayerState myState) {
        List<WeightedTarget> targets = myState.opponentGrids.collectMany {
            ObjectId opponent, Grid grid ->
                List<WeightedTarget> weightedTargets = []
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
                                player: opponent,
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

        fireAtCoordinateHandler.handleAction(
                playerCreator.player.id,
                game.id,
                new Target(player: game.players.find { it.id == target.player }.md5, coordinate: target.coordinate))

        return true
    }

}
