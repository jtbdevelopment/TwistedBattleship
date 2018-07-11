package com.jtbdevelopment.TwistedBattleship.ai.regular;

import com.jtbdevelopment.TwistedBattleship.ai.AI;
import com.jtbdevelopment.TwistedBattleship.ai.WeightedTarget;
import com.jtbdevelopment.TwistedBattleship.ai.common.AIActionHandlers;
import com.jtbdevelopment.TwistedBattleship.ai.common.RandomizedSetup;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.*;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.Player;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState.*;

/**
 * Date: 9/18/15
 * Time: 6:58 AM
 */
@Component
public class RegularAI implements AI {
    private Set<GridCellState> includeInFireValue = new HashSet<>(Arrays.asList(KnownShip, KnownByHit, KnownByOtherHit, KnownByRehit));
    @Autowired
    private RegularAIPlayerCreator playerCreator;
    @Autowired
    private AIActionHandlers aiActionHandler;
    @Autowired
    private GridCircleUtil gridCircleUtil;
    @Autowired
    private RandomizedSetup randomizedSetup;
    @Autowired
    private ConsolidateGridViews consolidateGridViews;
    private Random random = new Random();
    private int spyPerUnknown = 3;
    private int spyPerObscured = 1;
    private int ecmPerKnownSingleMove = 3;
    private int ecmPerKnownMultiMove = 8;
    private int repairPerDmgPointSingleMove = 8;
    private int repairPerDmgPointMultiMove = 3;
    private int repairMinDamage = 2;
    private int movePerDmgPointSingleMove = 8;
    private int movePerDmgPointMultiMove = 3;
    private int moveMinDamage = 2;
    private int fireBaseKnownShip = 25;
    private int fireBaseUnknown = 10;
    private int fireBaseObscured = 15;
    private int fireKnownAdjacentShip = 25;
    private int fireKnownDoubleAdjacentShip = 25;
    private int cruiseKnownHitOrShip = 200;

    @Override
    public List<MongoPlayer> getPlayers() {
        return playerCreator.getPlayers();
    }

    public void setup(final TBGame game, final MongoPlayer player) {
        randomizedSetup.setup(game, player);
    }

    public void playOneMove(final TBGame game, final MongoPlayer player) {
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

        TBPlayerState myState = game.getPlayerDetails().get(player.getId());
        List<WeightedTarget> targets = new LinkedList<>();
        targets.addAll(computeSpy(game, myState));
        targets.addAll(computeRepair(game, player, myState));
        targets.addAll(computeEvasiveMove(game, player, myState));
        targets.addAll(computeECM(game, player, myState));
        targets.addAll(computeFireTargets(game, myState));
        targets.addAll(computeCruiseMissileTargets(game, myState));

        if (targets.isEmpty()) {
            throw new RuntimeException("Could not think of a move!");
        }

        WeightedTarget target = getRandomBestTarget(targets);
        switch (target.getAction()) {
            case CruiseMissile:
                aiActionHandler.getCruiseMissileHandler().handleAction(player.getId(), game.getId(), target);
                break;
            case ECM:
                aiActionHandler.getEcmHandler().handleAction(player.getId(), game.getId(), target);
                break;
            case Spy:
                aiActionHandler.getSpyHandler().handleAction(player.getId(), game.getId(), target);
                break;
            case Fire:
                aiActionHandler.getFireAtCoordinateHandler().handleAction(player.getId(), game.getId(), target);
                break;
            case Repair:
                aiActionHandler.getRepairShipHandler().handleAction(player.getId(), game.getId(), target);
                break;
            case Move:
                aiActionHandler.getEvasiveManeuverHandler().handleAction(player.getId(), game.getId(), target);
                break;
        }

    }

    private WeightedTarget getRandomBestTarget(final List<WeightedTarget> weightedTargets) {
        weightedTargets.sort((a, b) -> b.getWeight() - a.getWeight());
        List<WeightedTarget> bestTargets = weightedTargets.stream().filter(t -> t.getWeight() == weightedTargets.get(0).getWeight()).collect(Collectors.toList());
        return bestTargets.get(random.nextInt(bestTargets.size()));
    }

    private List<WeightedTarget> computeRepair(final TBGame game, final Player player, final TBPlayerState myState) {
        if (myState.getEmergencyRepairsRemaining() == 0 || game.getRemainingMoves() < game.getMovesForSpecials()) {
            return new ArrayList<>();
        }

        final int perPoint = game.getFeatures().contains(GameFeature.PerShip) ? repairPerDmgPointMultiMove : repairPerDmgPointSingleMove;
        return myState.getShipStates().stream()
                .filter(ss -> ss.getHealthRemaining() > 0)
                .filter(ss -> ss.getHealthRemaining() < ss.getShip().getGridSize() - repairMinDamage)
                .map(ss -> {
                    WeightedTarget weightedTarget = new WeightedTarget();
                    weightedTarget.setAction(WeightedTarget.Action.Repair);
                    weightedTarget.setPlayer(player.getMd5());
                    weightedTarget.setCoordinate(ss.getShipGridCells().get(0));
                    weightedTarget.setWeight((ss.getShip().getGridSize() - ss.getHealthRemaining()) * perPoint);
                    return weightedTarget;
                })
                .collect(Collectors.toList());
    }

    private List<WeightedTarget> computeEvasiveMove(final TBGame game, final Player player, final TBPlayerState myState) {
        if (myState.getEmergencyRepairsRemaining() == 0 || game.getRemainingMoves() < game.getMovesForSpecials()) {
            return new ArrayList<>();
        }

        final int perPoint = game.getFeatures().contains(GameFeature.PerShip) ? movePerDmgPointMultiMove : movePerDmgPointSingleMove;
        return myState.getShipStates().stream()
                .filter(ss -> ss.getHealthRemaining() > 0)
                .filter(ss -> ss.getHealthRemaining() < ss.getShip().getGridSize() - moveMinDamage)
                .map(ss -> {
                    WeightedTarget weightedTarget = new WeightedTarget();
                    weightedTarget.setAction(WeightedTarget.Action.Move);
                    weightedTarget.setPlayer(player.getMd5());
                    weightedTarget.setCoordinate(ss.getShipGridCells().get(0));
                    weightedTarget.setWeight((ss.getShip().getGridSize() - ss.getHealthRemaining()) * perPoint);
                    return weightedTarget;
                })
                .collect(Collectors.toList());
    }

    private String getMD5ForObjectId(TBGame game, ObjectId id) {
        return game.getPlayers().stream().filter(p -> p.getId().equals(id)).findFirst().get().getMd5();
    }

    private List<WeightedTarget> computeSpy(final TBGame game, final TBPlayerState myState) {
        if (myState.getSpysRemaining() == 0 || game.getRemainingMoves() < game.getMovesForSpecials()) {
            return new ArrayList<>();
        }

        return game.getPlayerDetails().entrySet().stream()
                .filter(e -> e.getValue().isAlive())
                .flatMap(e -> myState.getOpponentGrids().entrySet().stream().filter(myview -> e.getKey().equals(myview.getKey())))
                .flatMap(view -> {
                    String md5 = getMD5ForObjectId(game, view.getKey());
                    return view.getValue().stream()
                            .map(center -> {
                                int currentValue = gridCircleUtil.computeCircleCoordinates(game, center)
                                        .stream()
                                        .mapToInt(coordinate -> {
                                            switch (view.getValue().get(coordinate)) {
                                                case KnownEmpty:
                                                case KnownByRehit:
                                                case KnownByHit:
                                                case KnownByOtherMiss:
                                                case KnownByOtherHit:
                                                case KnownByMiss:
                                                case KnownShip:
                                                    return 0;
                                                case Unknown:
                                                    return spyPerUnknown;
                                                default:
                                                    return spyPerObscured;
                                            }
                                        })
                                        .sum();
                                WeightedTarget target = new WeightedTarget();
                                target.setAction(WeightedTarget.Action.Spy);
                                target.setPlayer(md5);
                                target.setCoordinate(center);
                                target.setWeight(currentValue);
                                return target;
                            })
                            .collect(Collectors.toList()).stream();

                })
                .collect(Collectors.toList());
    }

    private List<WeightedTarget> computeECM(final TBGame game, final MongoPlayer player, final TBPlayerState myState) {
        if (myState.getEcmsRemaining() == 0 || game.getRemainingMoves() < game.getMovesForSpecials()) {
            return new ArrayList<>();
        }
        final int perPoint = game.getFeatures().contains(GameFeature.PerShip) ? ecmPerKnownMultiMove : ecmPerKnownSingleMove;
        List<Grid> activeOpponentViewsOfMe = game.getPlayerDetails().entrySet().stream()
                .filter(e -> e.getValue().isAlive())
                .map(e -> e.getValue().getOpponentViews())
                .map(e -> e.get(player.getId()))
                .collect(Collectors.toList());
        Grid consolidatedViewOfMe = consolidateGridViews.createConsolidatedView(game, activeOpponentViewsOfMe);
        return consolidatedViewOfMe.stream()
                .map(center -> {
                    int currentValue = gridCircleUtil.computeCircleCoordinates(game, center)
                            .stream()
                            .mapToInt(coordinate -> {
                                switch (consolidatedViewOfMe.get(coordinate)) {
                                    case KnownShip:
                                    case KnownByRehit:
                                    case KnownByHit:
                                    case KnownByOtherHit:
                                        return myState.getCoordinateShipMap().get(coordinate).getHealthRemaining() > 0 ? perPoint : 0;
                                    default:
                                        return 0;
                                }
                            })
                            .sum();
                    WeightedTarget target = new WeightedTarget();
                    target.setAction(WeightedTarget.Action.ECM);
                    target.setPlayer(player.getMd5());
                    target.setCoordinate(center);
                    target.setWeight(currentValue);
                    return target;
                })
                .collect(Collectors.toList());
    }

    private List<WeightedTarget> computeCruiseMissileTargets(final TBGame game, final TBPlayerState myState) {
        if (myState.getCruiseMissilesRemaining() == 0 || game.getRemainingMoves() < game.getMovesForSpecials()) {
            return new ArrayList<>();
        }
        return myState.getOpponentGrids().entrySet()
                .stream()
                .filter(e -> game.getPlayerDetails().get(e.getKey()).isAlive())
                .collect(
                        Collectors.toMap(
                                e -> getMD5ForObjectId(game, e.getKey()),
                                Map.Entry::getValue))
                .entrySet()
                .stream()
                .flatMap(e ->
                        e.getValue().stream()
                                .filter(coordinate -> {
                                    GridCellState state = e.getValue().get(coordinate);
                                    return state.equals(GridCellState.KnownByHit) || state.equals(KnownShip);
                                })
                                .map(coordinate -> {
                                    WeightedTarget target = new WeightedTarget();
                                    target.setPlayer(e.getKey());
                                    target.setCoordinate(coordinate);
                                    target.setWeight(cruiseKnownHitOrShip);
                                    return target;
                                })
                                .collect(Collectors.toList()).stream()
                )
                .collect(Collectors.toList());
    }

    private List<WeightedTarget> computeFireTargets(final TBGame game, final TBPlayerState myState) {
        return game.getPlayerDetails().entrySet().stream()
                .filter(e -> e.getValue().isAlive())
                .flatMap(e -> myState.getOpponentGrids().entrySet().stream().filter(myview -> e.getKey().equals(myview.getKey())))
                .flatMap(view ->
                        view.getValue()
                                .stream()
                                .map(coordinate -> {
                                    String md5 = getMD5ForObjectId(game, view.getKey());
                                    int currentValue;
                                    switch (view.getValue().get(coordinate)) {
                                        case KnownEmpty:
                                        case KnownByRehit:
                                        case KnownByHit:
                                        case KnownByOtherMiss:
                                        case KnownByOtherHit:
                                        case KnownByMiss:
                                            currentValue = 0;
                                            break;
                                        case KnownShip:
                                            currentValue = fireBaseKnownShip;
                                            break;
                                        case Unknown:
                                            currentValue = fireBaseUnknown;
                                            break;
                                        default:
                                            currentValue = fireBaseObscured;
                                            break;

                                    }

                                    currentValue += IntStream.range(-1, 2).map(rowAdjustment -> {
                                        int addRow = 0;
                                        if (rowAdjustment == 0) {
                                            addRow += IntStream.range(-1, 2)
                                                    .map(colAdjustment -> {
                                                        if (colAdjustment == 0) {
                                                            return 0;
                                                        }
                                                        int addCol = 0;
                                                        if (includeInFireValue.contains(view.getValue().get(coordinate.add(rowAdjustment, colAdjustment)))) {
                                                            addCol += fireKnownAdjacentShip;
                                                        }
                                                        GridCoordinate nextAdjacent = coordinate.add(rowAdjustment, colAdjustment * 2);
                                                        if (nextAdjacent.isValidCoordinate(game)
                                                                && includeInFireValue.contains(view.getValue().get(nextAdjacent))) {
                                                            addCol += fireKnownDoubleAdjacentShip;
                                                        }
                                                        return addCol;
                                                    }).sum();
                                        } else {
                                            GridCoordinate adjacent = coordinate.add(rowAdjustment, 0);
                                            if (adjacent.isValidCoordinate(game)
                                                    && includeInFireValue.contains(view.getValue().get(adjacent))) {
                                                addRow += fireKnownAdjacentShip;
                                            }
                                            GridCoordinate nextAdjacent = coordinate.add(rowAdjustment * 2, 0);
                                            if (nextAdjacent.isValidCoordinate(game)
                                                    && includeInFireValue.contains(view.getValue().get(nextAdjacent))) {
                                                addRow += fireKnownDoubleAdjacentShip;
                                            }
                                        }

                                        return addRow;
                                    }).sum();
                                    WeightedTarget target = new WeightedTarget();
                                    target.setPlayer(md5);
                                    target.setAction(WeightedTarget.Action.Fire);
                                    target.setCoordinate(coordinate);
                                    target.setWeight(currentValue);
                                    return target;
                                }).collect(Collectors.toList()).stream())
                .collect(Collectors.toList());
    }

}
