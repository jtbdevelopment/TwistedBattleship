package com.jtbdevelopment.TwistedBattleship.factory

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import org.bson.types.ObjectId

/**
 * Date: 4/5/2015
 * Time: 2:42 PM
 */
class TBGameFactoryTest extends MongoGameCoreTestCase {
    TBGameFactory gameFactory = new TBGameFactory()
    void testNewGame() {
        assert gameFactory.newGame() instanceof  TBGame
    }

    void testCopyFromPreviousGame() {
        TBGame previousGame = new TBGame(id: new ObjectId())
        TBGame newGame = new TBGame()

        gameFactory.copyFromPreviousGame(previousGame, newGame)
        assert newGame.previousId == previousGame.id
    }
}
