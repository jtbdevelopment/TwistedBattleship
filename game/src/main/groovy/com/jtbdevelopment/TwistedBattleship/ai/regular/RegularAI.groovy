package com.jtbdevelopment.TwistedBattleship.ai.regular

import com.jtbdevelopment.TwistedBattleship.ai.AI
import com.jtbdevelopment.TwistedBattleship.ai.WeightedTarget
import com.jtbdevelopment.TwistedBattleship.ai.common.RandomizedSetup
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.rest.handlers.*
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.*
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
class RegularAI implements AI {
    @Autowired
    RegularAIPlayerCreator playerCreator

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
    GridCircleUtil gridCircleUtil

    @Autowired
    RandomizedSetup randomizedSetup

    @Autowired
    ConsolidateGridViews consolidateGridViews

    private Random random = new Random()

    @Override
    List<Player> getPlayers() {
        return playerCreator.players
    }

    void setup(final TBGame game, final Player player) {
        randomizedSetup.setup(game, player)
    }

    private int spyPerUnknown = 3
    private int spyPerObscured = 1
    private int ecmPerKnownSingleMove = 3
    private int ecmPerKnownMultiMove = 8
    private int repairPerDmgPointSingleMove = 8
    private int repairPerDmgPointMultiMove = 3
    private int repairMinDamage = 2
    private int movePerDmgPointSingleMove = 8
    private int movePerDmgPointMultiMove = 3
    private int moveMinDamage = 2
    private int fireBaseKnownShip = 25
    private int fireBaseUnknown = 10
    private int fireBaseObscured = 15
    private int fireKnownAdjacentShip = 25
    private int fireKnownDoubleAdjacentShip = 25

    void playOneMove(final TBGame game, final Player player) {
        //  Pure Points System
        //  Spy - 3 points per unknown, 1 point per obscured
        //  ECM - 3 points per known ship if ship alive, 8 per single ove
        //  Repair - min 2 dmg points, alive - (3 points per dmg per ship, 8 points per single move) * ships dead
        //  Move - min 2 dmg points, alive - (3 points per dmg per ship, 8 points per single move) * ships dead
        //  Fire
        //      KnownHit, KnownEmpty, KnownRehit, KnownOtherHit, KnownMisses - 0
        //      Unknown - 10, +25 for each adjacent known ship/hit cell, +25 for each adjacent to adjacent in same direction
        //      Obscured - 15, +25 for each adjacent aboves
        //      KnownShip - 25, +25 for each adjacent known ship/hit cell, +25 for each adjacent to adjacent in the same direction
        //         Max Value - middle aircraft with ships in cross = 25 + 50 + 50 + 50 + 50 = 125

        TBPlayerState myState = game.playerDetails[(ObjectId) player.id]
        List<WeightedTarget> targets = []
        targets += computeSpy(game, player, myState)
        targets += computeRepair(game, player, myState)
        targets += computeEvasiveMove(game, player, myState)
        targets += computeECM(game, player, myState)
        targets += computeFireTargets(game, player, myState)

        List<WeightedTarget> bestTargets = targets.sort {
            WeightedTarget t1, WeightedTarget t2 ->
                return t2.weight - t1.weight
        }.findAll {
            WeightedTarget t ->
                t.weight == targets[0].weight
        }.toList()

        if (bestTargets.isEmpty()) {
            throw new RuntimeException("Could not think of a move!")
        }

        WeightedTarget target = bestTargets[random.nextInt(bestTargets.size())]
        switch (target.action) {
            case WeightedTarget.Action.ECM:
                ecmHandler.handleAction(
                        player.id,
                        game.id,
                        new Target(player: target.player, coordinate: target.coordinate))
                break;
            case WeightedTarget.Action.Spy:
                spyHandler.handleAction(
                        player.id,
                        game.id,
                        new Target(player: target.player, coordinate: target.coordinate))
                break;
            case WeightedTarget.Action.Fire:
                fireAtCoordinateHandler.handleAction(
                        player.id,
                        game.id,
                        new Target(player: target.player, coordinate: target.coordinate))
                break;
            case WeightedTarget.Action.Repair:
                repairShipHandler.handleAction(
                        player.id,
                        game.id,
                        new Target(player: target.player, coordinate: target.coordinate))
                break;
            case WeightedTarget.Action.Move:
                evasiveManeuverHandler.handleAction(
                        player.id,
                        game.id,
                        new Target(player: target.player, coordinate: target.coordinate))
                break;
        }

    }

    private List<WeightedTarget> computeRepair(final TBGame game, final Player player, final TBPlayerState myState) {
        if (myState.emergencyRepairsRemaining == 0 || game.remainingMoves < game.movesForSpecials) {
            return [];
        }
        int perPoint = game.features.contains(GameFeature.PerShip) ? repairPerDmgPointMultiMove : repairPerDmgPointSingleMove
        myState.shipStates.findAll {
            ShipState state ->
                (state.healthRemaining > 0) && (state.healthRemaining <= (state.ship.gridSize - repairMinDamage))
        }.collect {
            ShipState state ->
                new WeightedTarget(
                        action: WeightedTarget.Action.Repair,
                        player: player.md5,
                        coordinate: state.shipGridCells[0],
                        weight: state.shipSegmentHit.findAll { it }.size() * perPoint
                )
        }
    }

    private List<WeightedTarget> computeEvasiveMove(
            final TBGame game, final Player player, final TBPlayerState myState) {
        if (myState.emergencyRepairsRemaining == 0 || game.remainingMoves < game.movesForSpecials) {
            return [];
        }
        int perPoint = game.features.contains(GameFeature.PerShip) ? movePerDmgPointMultiMove : movePerDmgPointSingleMove
        myState.shipStates.findAll {
            ShipState state ->
                (state.healthRemaining > 0) && (state.healthRemaining <= (state.ship.gridSize - moveMinDamage))
        }.collect {
            ShipState state ->
                new WeightedTarget(
                        action: WeightedTarget.Action.Repair,
                        player: player.md5,
                        coordinate: state.shipGridCells[0],
                        weight: state.shipSegmentHit.findAll { it }.size() * perPoint
                )
        }
    }

    private List<WeightedTarget> computeSpy(final TBGame game, final Player player, final TBPlayerState myState) {
        if (myState.spysRemaining == 0 || game.remainingMoves < game.movesForSpecials) {
            return [];
        }
        myState.opponentGrids.findAll {
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
                                    currentValue += spyPerUnknown
                                    break
                                default:
                                    //  All the obscured
                                    currentValue += spyPerObscured
                                    break
                            }
                        }
                        weightedTargets.add(new WeightedTarget(
                                action: WeightedTarget.Action.Spy,
                                player: md5,
                                coordinate: new GridCoordinate(row, col),
                                weight: currentValue))
                    }
                }
                weightedTargets
        }.toList()
    }

    private List<WeightedTarget> computeECM(final TBGame game, final Player player, final TBPlayerState myState) {
        if (myState.ecmsRemaining == 0 || game.remainingMoves < game.movesForSpecials) {
            return [];
        }
        Grid consolidatedView = consolidateGridViews.createConsolidatedView(
                game,
                myState.opponentViews.findAll { game.playerDetails[it.key].alive }.values())

        int perPoint = game.features.contains(GameFeature.PerShip) ? ecmPerKnownMultiMove : ecmPerKnownSingleMove
        List<WeightedTarget> weightedTargets = []
        for (int row = 0; row < game.gridSize; ++row) {
            for (int col = 0; col < game.gridSize; ++col) {
                int currentValue = 0;

                def ecmCoordinate = new GridCoordinate(row, col)
                gridCircleUtil.computeCircleCoordinates(game, ecmCoordinate).each {
                    switch (consolidatedView.get(it)) {
                        case GridCellState.KnownShip:
                        case GridCellState.KnownByRehit:
                        case GridCellState.KnownByHit:
                        case GridCellState.KnownByOtherHit:
                            currentValue += myState.coordinateShipMap[it].healthRemaining > 0 ? perPoint : 0
                            break;
                        default:
                            break
                    }
                }
                weightedTargets.add(new WeightedTarget(
                        action: WeightedTarget.Action.ECM,
                        player: player.md5,
                        coordinate: ecmCoordinate,
                        weight: currentValue
                ))
            }
        }
        weightedTargets
    }

    private List<WeightedTarget> computeFireTargets(
            final TBGame game, final Player player, final TBPlayerState myState) {
        myState.opponentGrids.findAll {
            game.playerDetails[it.key].alive
        }.collectMany {
            ObjectId opponent, Grid grid ->
                List<WeightedTarget> weightedTargets = []
                String md5 = game.players.find { it.id == opponent }.md5
                for (int row = 0; row < game.gridSize; ++row) {
                    for (int col = 0; col < game.gridSize; ++col) {
                        int currentValue = 0;
                        switch (grid.get(row, col)) {
                            case GridCellState.KnownEmpty:
                            case GridCellState.KnownByRehit:
                            case GridCellState.KnownByHit:
                            case GridCellState.KnownByOtherMiss:
                            case GridCellState.KnownByOtherHit:
                            case GridCellState.KnownByMiss:
                                break
                            case GridCellState.KnownShip:
                                currentValue = fireBaseKnownShip
                                break
                            case GridCellState.Unknown:
                                currentValue = fireBaseUnknown
                                break
                            default:
                                currentValue = fireBaseObscured
                                break
                        }
                        if (currentValue > 0) {
                            (-1..1).each {
                                int addRow ->
                                    switch (addRow) {
                                        case 0:
                                            (-1..1).each {
                                                int addCol ->
                                                    switch (addCol) {
                                                        case 0:
                                                            break;
                                                        default:
                                                            GridCoordinate adjacent = new GridCoordinate(row, col + addCol)
                                                            if (adjacent.isValidCoordinate(game)) {
                                                                switch (grid.get(adjacent)) {
                                                                    case GridCellState.KnownShip:
                                                                    case GridCellState.KnownByHit:
                                                                    case GridCellState.KnownByOtherHit:
                                                                    case GridCellState.KnownByRehit:
                                                                        currentValue += fireKnownAdjacentShip
                                                                        GridCoordinate nextAdjacent = new GridCoordinate(row, col + (addCol * 2))
                                                                        if (nextAdjacent.isValidCoordinate(game)) {
                                                                            switch (grid.get(nextAdjacent)) {
                                                                                case GridCellState.KnownShip:
                                                                                case GridCellState.KnownByHit:
                                                                                case GridCellState.KnownByOtherHit:
                                                                                case GridCellState.KnownByRehit:
                                                                                    currentValue += fireKnownDoubleAdjacentShip
                                                                                    break
                                                                                default:
                                                                                    break
                                                                            }
                                                                        }
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }
                                                            }
                                                            break;
                                                    }
                                            }
                                            break;
                                        default:
                                            GridCoordinate adjacent = new GridCoordinate(row + addRow, col)
                                            if (adjacent.isValidCoordinate(game)) {
                                                switch (grid.get(adjacent)) {
                                                    case GridCellState.KnownShip:
                                                    case GridCellState.KnownByHit:
                                                    case GridCellState.KnownByOtherHit:
                                                    case GridCellState.KnownByRehit:
                                                        currentValue += fireKnownAdjacentShip
                                                        GridCoordinate nextAdjacent = new GridCoordinate(row + (addRow * 2), col)
                                                        if (nextAdjacent.isValidCoordinate(game)) {
                                                            switch (grid.get(nextAdjacent)) {
                                                                case GridCellState.KnownShip:
                                                                case GridCellState.KnownByHit:
                                                                case GridCellState.KnownByOtherHit:
                                                                case GridCellState.KnownByRehit:
                                                                    currentValue += fireKnownDoubleAdjacentShip
                                                                    break
                                                                default:
                                                                    break
                                                            }
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                            break;
                                    }
                            }
                        }
                        weightedTargets.add(new WeightedTarget(
                                action: WeightedTarget.Action.Fire,
                                player: md5,
                                coordinate: new GridCoordinate(row, col),
                                weight: currentValue))
                    }
                }
                weightedTargets
        }.toList()
    }

}
