package com.jtbdevelopment.TwistedBattleship.state;

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.games.mongo.state.AbstractMongoMultiPlayerGame;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 4/1/15
 * Time: 10:06 PM
 */
@Document(collection = "game")
@CompoundIndexes({@CompoundIndex(name = "player_phase", def = "{'players._id': 1, 'gamePhase': 1, 'lastUpdate': 1}")})
public class TBGame extends AbstractMongoMultiPlayerGame<GameFeature> {
    private Map<ObjectId, TBPlayerState> playerDetails = new HashMap<>();
    private List<Ship> startingShips = new ArrayList<>();
    private int gridSize;
    private ObjectId currentPlayer;
    private int remainingMoves;
    private int movesForSpecials;

    public Map<ObjectId, TBPlayerState> getPlayerDetails() {
        return playerDetails;
    }

    public void setPlayerDetails(Map<ObjectId, TBPlayerState> playerDetails) {
        this.playerDetails = playerDetails;
    }

    public List<Ship> getStartingShips() {
        return startingShips;
    }

    public void setStartingShips(List<Ship> startingShips) {
        this.startingShips = startingShips;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public ObjectId getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(ObjectId currentPlayer) {
        this.currentPlayer = currentPlayer;
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
