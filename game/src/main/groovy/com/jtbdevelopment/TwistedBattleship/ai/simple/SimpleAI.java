package com.jtbdevelopment.TwistedBattleship.ai.simple;

import com.jtbdevelopment.TwistedBattleship.ai.AI;
import com.jtbdevelopment.TwistedBattleship.ai.WeightedTarget;
import com.jtbdevelopment.TwistedBattleship.ai.common.AIActionHandlers;
import com.jtbdevelopment.TwistedBattleship.ai.common.RandomizedSetup;
import com.jtbdevelopment.TwistedBattleship.rest.Target;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 9/18/15
 * Time: 6:58 AM
 */
@SuppressWarnings("unused")
@Component
public class SimpleAI implements AI {
    @Autowired
    private SimpleAIPlayerCreator playerCreator;
    @Autowired
    private AIActionHandlers aiActionHandler;
    @Autowired
    private GridCircleUtil gridCircleUtil;
    @Autowired
    private RandomizedSetup randomizedSetup;
    private Random random = new Random();

    @Override
    public List<MongoPlayer> getPlayers() {
        return playerCreator.getPlayers();
    }

    public void setup(final TBGame game, final MongoPlayer player) {
        randomizedSetup.setup(game, player);
    }

    public void playOneMove(final TBGame game, final MongoPlayer player) {

        TBPlayerState myState = game.getPlayerDetails().get(player.getId());
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
                                throw new RuntimeException("Unable to take any action!");
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean didCruiseMissile(final TBGame game, final MongoPlayer player, final TBPlayerState myState) {
        if (game.getMovesForSpecials() <= game.getRemainingMoves() && myState.getCruiseMissilesRemaining() > 0) {
            List<WeightedTarget> targets = myState.getOpponentGrids().entrySet()
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
                                        return state.equals(GridCellState.KnownByHit) || state.equals(GridCellState.KnownShip);
                                    })
                                    .map(coordinate -> {
                                        WeightedTarget target = new WeightedTarget();
                                        target.setPlayer(e.getKey());
                                        target.setCoordinate(coordinate);
                                        return target;
                                    })
                                    .collect(Collectors.toList()).stream()
                    )
                    .collect(Collectors.toList());

            if (targets.size() == 0) {
                return false;
            }

            WeightedTarget target = targets.get(random.nextInt(targets.size()));
            aiActionHandler.getCruiseMissileHandler().handleAction(player.getId(), game.getId(), target);
            return true;
        }

        return false;
    }

    private boolean didRepair(final TBGame game, final MongoPlayer player, final TBPlayerState myState) {
        if (game.getMovesForSpecials() <= game.getRemainingMoves() && myState.getEmergencyRepairsRemaining() > 0) {
            boolean perShip = game.getFeatures().contains(GameFeature.PerShip);
            final Set<Ship> allowedShips = perShip ?
                    new HashSet<>(Arrays.asList(Ship.Carrier, Ship.Battleship)) :
                    new HashSet<>(Arrays.asList(Ship.Carrier, Ship.Battleship, Ship.Submarine, Ship.Cruiser));

            Optional<ShipState> mostDamaged = myState.getShipStates().stream()
                    .filter(ss -> ss.getHealthRemaining() > 0)
                    .filter(ss -> allowedShips.contains(ss.getShip()))
                    .filter(ss -> ss.getHealthRemaining() < ss.getShip().getGridSize() - 2)
                    .min((a, b) -> (b.getShip().getGridSize() - b.getHealthRemaining()) - (a.getShip().getGridSize() - a.getHealthRemaining()));

            if (mostDamaged.isPresent()) {
                Target target = new Target();
                target.setCoordinate(mostDamaged.get().getShipGridCells().get(0));
                target.setPlayer(player.getMd5());
                aiActionHandler.getRepairShipHandler().handleAction(player.getId(), game.getId(), target);
                return true;
            }
        }
        return false;
    }

    private boolean didEvasive(final TBGame game, final MongoPlayer player, final TBPlayerState myState) {
        if (game.getMovesForSpecials() <= game.getRemainingMoves() && myState.getEvasiveManeuversRemaining() > 0) {
            Optional<ShipState> mostDamaged = myState.getShipStates().stream()
                    .filter(ss -> ss.getHealthRemaining() > 0)
                    .filter(ss -> ss.getShip().getGridSize() > 2)
                    .filter(ss -> ss.getHealthRemaining() < ss.getShip().getGridSize() - 2)
                    .min((a, b) -> (b.getShip().getGridSize() - b.getHealthRemaining()) - (a.getShip().getGridSize() - a.getHealthRemaining()));

            if (mostDamaged.isPresent()) {
                Target target = new Target();
                target.setCoordinate(mostDamaged.get().getShipGridCells().get(0));
                target.setPlayer(player.getMd5());
                aiActionHandler.getEvasiveManeuverHandler().handleAction(player.getId(), game.getId(), target);
                return true;
            }
        }

        return false;
    }

    private boolean didSpy(final TBGame game, final MongoPlayer player, TBPlayerState myState) {
        if (game.getMovesForSpecials() <= game.getRemainingMoves() && myState.getSpysRemaining() > 0) {
            List<WeightedTarget> weightedTargets = game.getPlayerDetails().entrySet().stream()
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
                                                        return 2;
                                                    default:
                                                        return 1;
                                                }
                                            })
                                            .sum();
                                    WeightedTarget target = new WeightedTarget();
                                    target.setPlayer(md5);
                                    target.setCoordinate(center);
                                    target.setWeight(currentValue);
                                    return target;
                                })
                                .collect(Collectors.toList()).stream();

                    })
                    .collect(Collectors.toList());
            if (!weightedTargets.isEmpty()) {
                aiActionHandler.getSpyHandler().handleAction(player.getId(), game.getId(), getRandomBestTarget(weightedTargets));
                return true;
            }

        }

        return false;
    }

    private WeightedTarget getRandomBestTarget(final List<WeightedTarget> weightedTargets) {
        weightedTargets.sort((a, b) -> b.getWeight() - a.getWeight());
        List<WeightedTarget> bestTargets = weightedTargets.stream().filter(t -> t.getWeight() == weightedTargets.get(0).getWeight()).collect(Collectors.toList());
        return bestTargets.get(random.nextInt(bestTargets.size()));
    }

    private boolean didECM(final TBGame game, final MongoPlayer player, final TBPlayerState myState) {
        if (game.getMovesForSpecials() <= game.getRemainingMoves() && myState.getEcmsRemaining() > 0) {
            List<WeightedTarget> weightedTargets = game.getPlayerDetails().entrySet().stream()
                    .filter(e -> e.getValue().isAlive())
                    .flatMap(e -> myState.getOpponentViews().entrySet().stream().filter(myview -> e.getKey().equals(myview.getKey())))
                    .flatMap(view -> view.getValue().stream()
                            .map(center -> {
                                int currentValue = gridCircleUtil.computeCircleCoordinates(game, center)
                                        .stream()
                                        .mapToInt(coordinate -> {
                                            switch (view.getValue().get(coordinate)) {
                                                case KnownShip:
                                                    return 4;
                                                case KnownEmpty:
                                                case KnownByRehit:
                                                case KnownByHit:
                                                case KnownByOtherMiss:
                                                case KnownByOtherHit:
                                                case KnownByMiss:
                                                    return 2;
                                                case Unknown:
                                                    return 0;
                                                default:
                                                    return 1;
                                            }
                                        })
                                        .sum();
                                WeightedTarget target = new WeightedTarget();
                                target.setPlayer(player.getMd5());
                                target.setCoordinate(center);
                                target.setWeight(currentValue);
                                return target;
                            }).collect(Collectors.toList()).stream()
                    )
                    .collect(Collectors.toList());
            if (!weightedTargets.isEmpty()) {
                aiActionHandler.getEcmHandler().handleAction(player.getId(), game.getId(), getRandomBestTarget(weightedTargets));
                return true;
            }

        }
        return false;
    }

    private boolean didFire(final TBGame game, final MongoPlayer player, final TBPlayerState myState) {
        List<WeightedTarget> weightedTargets = game.getPlayerDetails().entrySet().stream()
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
                                            currentValue = -1;
                                            break;
                                        case KnownShip:
                                            currentValue = 100;
                                            break;
                                        case Unknown:
                                            currentValue = 25;
                                            break;
                                        default:
                                            currentValue = 50;
                                            break;

                                    }
                                    WeightedTarget target = new WeightedTarget();
                                    target.setPlayer(md5);
                                    target.setCoordinate(coordinate);
                                    target.setWeight(currentValue);
                                    return target;
                                }).collect(Collectors.toList()).stream())
                .collect(Collectors.toList());

        if (!weightedTargets.isEmpty()) {
            aiActionHandler.getFireAtCoordinateHandler().handleAction(player.getId(), game.getId(), getRandomBestTarget(weightedTargets));
            return true;
        }

        return false;
    }

    private String getMD5ForObjectId(TBGame game, ObjectId id) {
        return game.getPlayers().stream().filter(p -> p.getId().equals(id)).findFirst().get().getMd5();
    }

}
