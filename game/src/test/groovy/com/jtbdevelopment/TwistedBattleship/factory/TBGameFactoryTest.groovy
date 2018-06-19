package com.jtbdevelopment.TwistedBattleship.factory

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import groovy.transform.CompileStatic
import org.bson.types.ObjectId

/**
 * Date: 4/5/2015
 * Time: 2:42 PM
 */
@CompileStatic
class TBGameFactoryTest extends MongoGameCoreTestCase {
    TBGameFactory gameFactory = new TBGameFactory(null, null)

    void testNewGame() {
        assert gameFactory.newGame() instanceof TBGame
    }

    void testCopyFromPreviousGame() {
        TBGame previousGame = new TBGame(id: new ObjectId())
        previousGame.features = [GameFeature.CruiseMissileEnabled, GameFeature.ECMDisabled] as Set;
        TBGame newGame = new TBGame(features: new HashSet<>(previousGame.features))

        gameFactory.copyFromPreviousGame(previousGame, newGame)

        assert newGame.previousId == previousGame.id
        assert newGame.features == previousGame.features
    }

    void testCopyFromPreviousGameAddsMissingDefaults() {
        TBGame previousGame = new TBGame(id: new ObjectId())
        previousGame.features = [GameFeature.SpyDisabled, GameFeature.ECMDisabled] as Set;
        TBGame newGame = new TBGame(features: new HashSet<>(previousGame.features))

        gameFactory.copyFromPreviousGame(previousGame, newGame)

        assert newGame.previousId == previousGame.id
        assert newGame.features.containsAll(previousGame.features)
        assert newGame.features.contains(GameFeature.CruiseMissileDisabled)
        assert newGame.features.size() == (previousGame.features.size() + 1)
    }
}
