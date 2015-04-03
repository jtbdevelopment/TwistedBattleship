package com.jtbdevelopment.TwistedBattleship.player.reset

import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.publish.PlayerPublisher
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

/**
 * Date: 2/11/15
 * Time: 7:16 PM
 */
class PlayerFreeGameResetTest extends GroovyTestCase {
    PlayerFreeGameReset freeGameReset = new PlayerFreeGameReset()

    void testResetsAndPublishes() {
        boolean reset = false;
        boolean published = false;


        freeGameReset.mongoOperations = [
                updateMulti: {
                    Query q, Update u, Class c ->
                        assert q.toString() == 'Query: { "gameSpecificPlayerAttributes.freeGamesUsedToday" : { "$gt" : 0}}, Fields: null, Sort: null'
                        assert u.toString() == '{ "$set" : { "gameSpecificPlayerAttributes.freeGamesUsedToday" : 0}}'
                        assert MongoPlayer.class.is(c)
                        reset = true
                        null
                }
        ] as MongoOperations
        freeGameReset.playerPublisher = [
                publishAll: {
                    published = true
                }
        ] as PlayerPublisher
        freeGameReset.resetFreeGames()
        assert reset
        assert published
    }
}
