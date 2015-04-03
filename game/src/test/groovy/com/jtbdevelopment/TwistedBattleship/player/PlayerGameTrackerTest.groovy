package com.jtbdevelopment.TwistedBattleship.player

import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerPayLevel
import com.jtbdevelopment.games.publish.PlayerPublisher
import com.jtbdevelopment.games.tracking.PlayerGameEligibility
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

/**
 * Date: 2/10/15
 * Time: 6:42 AM
 */
class PlayerGameTrackerTest extends MongoGameCoreTestCase {
    public static
    final String FREETOPLAYFREEGAMEFIND = "Query: { \"_id\" : { \"\$oid\" : \"100000000000000000000000\"} , \"gameSpecificPlayerAttributes.freeGamesUsedToday\" : { \"\$lt\" : " + TBPlayerAttributes.DEFAULT_FREE_GAMES_PER_DAY + "}}, Fields: null, Sort: null"
    public static
    final String UPDATEFREEPLAYED = "{ \"\$inc\" : { \"gameSpecificPlayerAttributes.freeGamesUsedToday\" : 1}}"
    public static
    final String FRRETOPLAYPREMIUMFIND = "Query: { \"_id\" : { \"\$oid\" : \"100000000000000000000000\"} , \"gameSpecificPlayerAttributes.freeGamesUsedToday\" : { \"\$lt\" : " + TBPlayerAttributes.DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY + "}}, Fields: null, Sort: null"
    PlayerGameTracker tracker = new PlayerGameTracker()

    void testRegularEligibilityWithPlayerHavingFreeGames() {
        int callNumber = 0
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.payLevel = PlayerPayLevel.FreeToPlay
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 0, availablePurchasedGames: 5)
        MongoPlayer output = (MongoPlayer) input.clone()
        output.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 1, availablePurchasedGames: 5)
        tracker.mongoOperations = [
                findAndModify: {
                    Query query, Update update, FindAndModifyOptions options, Class entityClass ->
                        ++callNumber
                        if (callNumber == 1) {
                            return simulateFreePlayUpdate(query, FREETOPLAYFREEGAMEFIND, update, options, entityClass, output)
                        }
                        fail("Invalid call number")
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        assert p.is(output)
                        published = true
                }
        ] as PlayerPublisher
        PlayerGameEligibilityResult result = tracker.getGameEligibility(input)
        assertNotNull result
        assert result.eligibility == PlayerGameEligibility.FreeGameUsed
        assert result.player.is(output)
        assert published
    }

    void testRegularEligibilityWithPlayerHavingNoFreeGamesButHasPaid() {
        int callNumber = 0
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.payLevel = PlayerPayLevel.FreeToPlay
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 10, availablePurchasedGames: 5)
        MongoPlayer output = (MongoPlayer) input.clone()
        output.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 10, availablePurchasedGames: 4)
        tracker.mongoOperations = [
                findAndModify: {
                    Query query, Update update, FindAndModifyOptions options, Class entityClass ->
                        ++callNumber
                        if (callNumber == 1) {
                            return simulateFreePlayUpdate(query, FREETOPLAYFREEGAMEFIND, update, options, entityClass, null)
                        }
                        if (callNumber == 2) {
                            return simulatePaidGameUpdate(query, update, options, entityClass, output)
                        }
                        fail("Invalid call number")
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        assert p.is(output)
                        published = true
                }
        ] as PlayerPublisher
        PlayerGameEligibilityResult result = tracker.getGameEligibility(input)
        assertNotNull result
        assert result.eligibility == PlayerGameEligibility.PaidGameUsed
        assert result.player.is(output)
        assert published
    }

    void testRegularEligibilityWithPlayerHavingNoGames() {
        int callNumber = 0
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.payLevel = PlayerPayLevel.FreeToPlay
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 10, availablePurchasedGames: 0)
        tracker.mongoOperations = [
                findAndModify: {
                    Query query, Update update, FindAndModifyOptions options, Class entityClass ->
                        ++callNumber
                        if (callNumber == 1) {
                            return simulateFreePlayUpdate(query, FREETOPLAYFREEGAMEFIND, update, options, entityClass, null)
                        }
                        if (callNumber == 2) {
                            return simulatePaidGameUpdate(query, update, options, entityClass, null)
                        }
                        fail("Invalid call number")
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        fail('publish should not have happened')
                }
        ] as PlayerPublisher
        PlayerGameEligibilityResult result = tracker.getGameEligibility(input)
        assertNotNull result
        assert result.eligibility == PlayerGameEligibility.NoGamesAvailable
        assert result.player.is(input)
        assertFalse published
    }

    void testPremiumEligibilityWithPlayerHavingFreeGames() {
        int callNumber = 0
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.payLevel = PlayerPayLevel.PremiumPlayer
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 0, availablePurchasedGames: 5)
        MongoPlayer output = (MongoPlayer) input.clone()
        output.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 1, availablePurchasedGames: 5)
        tracker.mongoOperations = [
                findAndModify: {
                    Query query, Update update, FindAndModifyOptions options, Class entityClass ->
                        ++callNumber
                        if (callNumber == 1) {
                            return simulateFreePlayUpdate(query, FRRETOPLAYPREMIUMFIND, update, options, entityClass, output)
                        }
                        fail("Invalid call number")
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        assert p.is(output)
                        published = true
                }
        ] as PlayerPublisher
        PlayerGameEligibilityResult result = tracker.getGameEligibility(input)
        assertNotNull result
        assert result.eligibility == PlayerGameEligibility.FreeGameUsed
        assert result.player.is(output)
        assert published
    }

    void testPremiumEligibilityWithPlayerHavingNoFreeGamesButHasPaid() {
        int callNumber = 0
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.payLevel = PlayerPayLevel.PremiumPlayer
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 25, availablePurchasedGames: 5)
        MongoPlayer output = (MongoPlayer) input.clone()
        output.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 25, availablePurchasedGames: 4)
        tracker.mongoOperations = [
                findAndModify: {
                    Query query, Update update, FindAndModifyOptions options, Class entityClass ->
                        ++callNumber
                        if (callNumber == 1) {
                            return simulateFreePlayUpdate(query, FRRETOPLAYPREMIUMFIND, update, options, entityClass, null)
                        }
                        if (callNumber == 2) {
                            return simulatePaidGameUpdate(query, update, options, entityClass, output)
                        }
                        fail("Invalid call number")
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        assert p.is(output)
                        published = true
                }
        ] as PlayerPublisher
        PlayerGameEligibilityResult result = tracker.getGameEligibility(input)
        assertNotNull result
        assert result.eligibility == PlayerGameEligibility.PaidGameUsed
        assert result.player.is(output)
        assert published
    }

    void testPremiumEligibilityWithPlayerHavingNoGames() {
        int callNumber = 0
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.payLevel = PlayerPayLevel.PremiumPlayer
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 25, availablePurchasedGames: 0)
        tracker.mongoOperations = [
                findAndModify: {
                    Query query, Update update, FindAndModifyOptions options, Class entityClass ->
                        ++callNumber
                        if (callNumber == 1) {
                            return simulateFreePlayUpdate(query, FRRETOPLAYPREMIUMFIND, update, options, entityClass, null)
                        }
                        if (callNumber == 2) {
                            return simulatePaidGameUpdate(query, update, options, entityClass, null)
                        }
                        fail("Invalid call number")
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        fail('publish should not have happened')
                }
        ] as PlayerPublisher
        PlayerGameEligibilityResult result = tracker.getGameEligibility(input)
        assertNotNull result
        assert result.eligibility == PlayerGameEligibility.NoGamesAvailable
        assert result.player.is(input)
        assertFalse published
    }

    void testRevertingFreeGameUsage() {
        int callNumber = 0
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 1, availablePurchasedGames: 5)
        PlayerGameEligibilityResult result = new PlayerGameEligibilityResult(
                eligibility: PlayerGameEligibility.FreeGameUsed,
                player: input
        )
        MongoPlayer output = (MongoPlayer) input.clone()
        output.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 0, availablePurchasedGames: 5)
        tracker.mongoOperations = [
                findAndModify: {
                    Query query, Update update, FindAndModifyOptions options, Class entityClass ->
                        ++callNumber
                        if (callNumber == 1) {
                            return simulateFreeGameRevert(query, update, options, entityClass, output)
                        }
                        fail("Invalid call number")
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        assert p.is(output)
                        published = true
                }
        ] as PlayerPublisher
        tracker.revertGameEligibility(result)
        assert published
    }

    void testRevertingPaidGameUsage() {
        int callNumber = 0
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 1, availablePurchasedGames: 5)
        PlayerGameEligibilityResult result = new PlayerGameEligibilityResult(
                eligibility: PlayerGameEligibility.PaidGameUsed,
                player: input
        )
        MongoPlayer output = (MongoPlayer) input.clone()
        output.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 0, availablePurchasedGames: 5)
        tracker.mongoOperations = [
                findAndModify: {
                    Query query, Update update, FindAndModifyOptions options, Class entityClass ->
                        ++callNumber
                        if (callNumber == 1) {
                            return simulatePaidGameRevert(query, update, options, entityClass, output)
                        }
                        fail("Invalid call number")
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        assert p.is(output)
                        published = true
                }
        ] as PlayerPublisher
        tracker.revertGameEligibility(result)
        assert published
    }

    void testRevertingNotEligibleGameDoesNothing() {
        boolean published = false
        MongoPlayer input = (MongoPlayer) PONE.clone();
        input.gameSpecificPlayerAttributes = new TBPlayerAttributes(freeGamesUsedToday: 1, availablePurchasedGames: 5)
        PlayerGameEligibilityResult result = new PlayerGameEligibilityResult(
                eligibility: PlayerGameEligibility.NoGamesAvailable,
                player: input
        )
        tracker.mongoOperations = [
                findAndModify: {
                    fail('should not be called')
                }
        ] as MongoOperations
        tracker.playerPublisher = [
                publish: {
                    Player p ->
                        fail('should not be called')
                }
        ] as PlayerPublisher
        tracker.revertGameEligibility(result)
        assertFalse published
    }

    protected
    static MongoPlayer simulateFreePlayUpdate(Query query, String expectedQuery, Update update, FindAndModifyOptions options, Class entityClass, MongoPlayer output) {
        assert query.toString() == expectedQuery
        assert update.toString() == UPDATEFREEPLAYED
        assert options.returnNew
        assertFalse options.remove
        assertFalse options.upsert
        assert entityClass.is(MongoPlayer.class)
        return output
    }

    protected
    static MongoPlayer simulatePaidGameUpdate(Query query, Update update, FindAndModifyOptions options, Class entityClass, MongoPlayer output) {
        assert query.toString() == "Query: { \"_id\" : { \"\$oid\" : \"100000000000000000000000\"} , \"gameSpecificPlayerAttributes.availablePurchasedGames\" : { \"\$gt\" : 0}}, Fields: null, Sort: null"
        assert update.toString() == "{ \"\$inc\" : { \"gameSpecificPlayerAttributes.availablePurchasedGames\" : -1}}"
        assert options.returnNew
        assertFalse options.remove
        assertFalse options.upsert
        assert entityClass.is(MongoPlayer.class)
        return output
    }

    protected
    static MongoPlayer simulateFreeGameRevert(Query query, Update update, FindAndModifyOptions options, Class entityClass, MongoPlayer output) {
        assert query.toString() == "Query: { \"_id\" : { \"\$oid\" : \"100000000000000000000000\"}}, Fields: null, Sort: null"
        assert update.toString() == "{ \"\$inc\" : { \"gameSpecificPlayerAttributes.freeGamesUsedToday\" : -1}}"
        assert options.returnNew
        assertFalse options.remove
        assertFalse options.upsert
        assert entityClass.is(MongoPlayer.class)
        return output
    }

    protected
    static MongoPlayer simulatePaidGameRevert(Query query, Update update, FindAndModifyOptions options, Class entityClass, MongoPlayer output) {
        assert query.toString() == "Query: { \"_id\" : { \"\$oid\" : \"100000000000000000000000\"}}, Fields: null, Sort: null"
        assert update.toString() == "{ \"\$inc\" : { \"gameSpecificPlayerAttributes.availablePurchasedGames\" : 1}}"
        assert options.returnNew
        assertFalse options.remove
        assertFalse options.upsert
        assert entityClass.is(MongoPlayer.class)
        return output
    }
}
