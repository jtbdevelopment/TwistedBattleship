package com.jtbdevelopment.TwistedBattleship.state

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.mongo.state.AbstractMongoMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 4/1/15
 * Time: 10:06 PM
 */
@Document(collection = "game")
@CompileStatic
@CompoundIndexes([
        @CompoundIndex(name = "player_phase", def = "{'players._id': 1, 'gamePhase': 1, 'lastUpdate': 1}"),
])
class TBGame extends AbstractMongoMultiPlayerGame<GameFeature> {
    Map<ObjectId, TBPlayerState> playerDetails = [:]
    List<Ship> startingShips = []
    //  TODO support non-symmetrical
    int gridSize
    ObjectId currentPlayer
    int remainingMoves
    int movesForSpecials
    String generalMessage = ""
}
