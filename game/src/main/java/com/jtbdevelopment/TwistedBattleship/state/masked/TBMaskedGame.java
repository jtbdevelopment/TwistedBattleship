package com.jtbdevelopment.TwistedBattleship.state.masked;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 4/2/15
 * Time: 6:36 PM
 */
@SuppressWarnings("unused")
public class TBMaskedGame extends AbstractMaskedMultiPlayerGame<GameFeature> {
    private Map<String, Boolean> playersSetup = new HashMap<>();
    private Map<String, Boolean> playersAlive = new HashMap<>();
    private Map<String, Integer> playersScore = new HashMap<>();
    private List<Ship> startingShips = new ArrayList<>();
    private TBMaskedPlayerState maskedPlayersState;
    private String currentPlayer;
    private String winningPlayer;
    private int gridSize;
    private int remainingMoves;
    private int movesForSpecials;

    public Map<String, Boolean> getPlayersSetup() {
        return playersSetup;
    }

    public void setPlayersSetup(Map<String, Boolean> playersSetup) {
        this.playersSetup = playersSetup;
    }

    public Map<String, Boolean> getPlayersAlive() {
        return playersAlive;
    }

    public void setPlayersAlive(Map<String, Boolean> playersAlive) {
        this.playersAlive = playersAlive;
    }

    public Map<String, Integer> getPlayersScore() {
        return playersScore;
    }

    public void setPlayersScore(Map<String, Integer> playersScore) {
        this.playersScore = playersScore;
    }

    public List<Ship> getStartingShips() {
        return startingShips;
    }

    public void setStartingShips(List<Ship> startingShips) {
        this.startingShips = startingShips;
    }

    public TBMaskedPlayerState getMaskedPlayersState() {
        return maskedPlayersState;
    }

    public void setMaskedPlayersState(TBMaskedPlayerState maskedPlayersState) {
        this.maskedPlayersState = maskedPlayersState;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getWinningPlayer() {
        return winningPlayer;
    }

    public void setWinningPlayer(String winningPlayer) {
        this.winningPlayer = winningPlayer;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public int getRemainingMoves() {
        return remainingMoves;
    }

    public void setRemainingMoves(int remainingMoves) {
        this.remainingMoves = remainingMoves;
    }

    public int getMovesForSpecials() {
        return movesForSpecials;
    }

    public void setMovesForSpecials(int movesForSpecials) {
        this.movesForSpecials = movesForSpecials;
    }
}
