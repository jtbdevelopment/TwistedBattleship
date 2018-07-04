package com.jtbdevelopment.TwistedBattleship.state;

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.*;

/**
 * Date: 4/2/15
 * Time: 6:40 AM
 */
@SuppressWarnings("WeakerAccess")
public class TBPlayerState implements Serializable {
    private List<Ship> startingShips = new ArrayList<>();
    private List<ShipState> shipStates = new ArrayList<>();
    @Transient
    private Map<GridCoordinate, ShipState> coordinateShipMap = null;
    private int spysRemaining = 0;
    private int cruiseMissilesRemaining = 0;
    private int evasiveManeuversRemaining = 0;
    private int ecmsRemaining = 0;
    private int emergencyRepairsRemaining = 0;
    private int scoreFromHits = 0;
    private int scoreFromSinks = 0;
    private int scoreFromLiving = 0;
    private boolean setup = false;
    private Map<ObjectId, Grid> opponentGrids = new HashMap<>();
    private Map<ObjectId, Grid> opponentViews = new HashMap<>();
    private List<TBActionLogEntry> actionLog = new LinkedList<>();

    public int getTotalScore() {
        return scoreFromHits + scoreFromSinks + scoreFromLiving;
    }

    @SuppressWarnings("unused")
    public void setTotalScore(final int totalScore) {
        //  ignore
    }

    public boolean getAlive() {
        return isAlive();
    }

    public boolean isAlive() {
        return getActiveShipsRemaining() > 0;
    }

    @SuppressWarnings("unused")
    public void setAlive(final boolean alive) {
        // ignore
    }

    public int getActiveShipsRemaining() {
        return (int) shipStates.stream().filter(s -> s.getHealthRemaining() > 0).count();
    }

    @SuppressWarnings("unused")
    public void setActiveShipsRemaining(final int remaining) {
        //  ignore
    }

    public List<ShipState> getShipStates() {
        return shipStates;
    }

    public void setShipStates(final List<ShipState> shipStates) {
        this.shipStates = shipStates;
        this.shipStates.sort(Comparator.comparing(ShipState::getShip));
        computeCoordinateShipMap();
    }

    @Transient
    public Map<GridCoordinate, ShipState> getCoordinateShipMap() {
        if (coordinateShipMap == null || coordinateShipMap.isEmpty()) {
            computeCoordinateShipMap();
        }

        return coordinateShipMap;
    }

    @SuppressWarnings("unused")
    public void setCoordinateShipMap(final Map<GridCoordinate, ShipState> coordinateShipMap) {
        //  ignore
    }

    private void computeCoordinateShipMap() {
        Map<GridCoordinate, ShipState> map = new HashMap<>();
        shipStates.forEach(state -> state.getShipGridCells().forEach(coordinate -> map.put(coordinate, state)));
        this.coordinateShipMap = map;
    }

    public List<Ship> getStartingShips() {
        return startingShips;
    }

    public void setStartingShips(final List<Ship> startingShips) {
        this.startingShips = startingShips;
        Collections.sort(this.startingShips);
    }

    public int getSpysRemaining() {
        return spysRemaining;
    }

    public void setSpysRemaining(int spysRemaining) {
        this.spysRemaining = spysRemaining;
    }

    public int getCruiseMissilesRemaining() {
        return cruiseMissilesRemaining;
    }

    public void setCruiseMissilesRemaining(int cruiseMissilesRemaining) {
        this.cruiseMissilesRemaining = cruiseMissilesRemaining;
    }

    public int getEvasiveManeuversRemaining() {
        return evasiveManeuversRemaining;
    }

    public void setEvasiveManeuversRemaining(int evasiveManeuversRemaining) {
        this.evasiveManeuversRemaining = evasiveManeuversRemaining;
    }

    public int getEcmsRemaining() {
        return ecmsRemaining;
    }

    public void setEcmsRemaining(int ecmsRemaining) {
        this.ecmsRemaining = ecmsRemaining;
    }

    public int getEmergencyRepairsRemaining() {
        return emergencyRepairsRemaining;
    }

    public void setEmergencyRepairsRemaining(int emergencyRepairsRemaining) {
        this.emergencyRepairsRemaining = emergencyRepairsRemaining;
    }

    public int getScoreFromHits() {
        return scoreFromHits;
    }

    public void setScoreFromHits(int scoreFromHits) {
        this.scoreFromHits = scoreFromHits;
    }

    public int getScoreFromSinks() {
        return scoreFromSinks;
    }

    public void setScoreFromSinks(int scoreFromSinks) {
        this.scoreFromSinks = scoreFromSinks;
    }

    public int getScoreFromLiving() {
        return scoreFromLiving;
    }

    public void setScoreFromLiving(int scoreFromLiving) {
        this.scoreFromLiving = scoreFromLiving;
    }

    public boolean getSetup() {
        return setup;
    }

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(final boolean isSetup) {
        this.setup = isSetup;
    }

    public Map<ObjectId, Grid> getOpponentGrids() {
        return opponentGrids;
    }

    public void setOpponentGrids(Map<ObjectId, Grid> opponentGrids) {
        this.opponentGrids = opponentGrids;
    }

    public Map<ObjectId, Grid> getOpponentViews() {
        return opponentViews;
    }

    public void setOpponentViews(Map<ObjectId, Grid> opponentViews) {
        this.opponentViews = opponentViews;
    }

    public List<TBActionLogEntry> getActionLog() {
        return actionLog;
    }

    public void setActionLog(List<TBActionLogEntry> actionLog) {
        this.actionLog = actionLog;
    }
}
